/*
 * Copyright (C) 2017-2018 Artuto
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.artuto.endless.storage.data.managers;

import ch.qos.logback.classic.Logger;
import me.artuto.endless.Bot;
import me.artuto.endless.Endless;
import me.artuto.endless.core.entities.Poll;
import me.artuto.endless.handlers.PollHandler;
import net.dv8tion.jda.bot.sharding.ShardManager;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Artuto
 */

public class PollsDataManager
{
    private final Bot bot;
    private final Connection connection;
    private final Logger LOG = Endless.getLog(PollsDataManager.class);

    public PollsDataManager(Bot bot)
    {
        this.bot = bot;
        this.connection = bot.db.getConnection();
    }

    public void createPoll(long endTime, long guildId, long msgId, long tcId)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM POLLS",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                results.moveToInsertRow();
                results.updateLong("channel_id", tcId);
                results.updateLong("end_time", endTime);
                results.updateLong("guild_id", guildId);
                results.updateLong("msg_id", msgId);
                results.insertRow();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while creating a poll.", e);
        }
    }

    private void deletePoll(Poll poll)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM POLLS WHERE channel_id = ? AND msg_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, poll.getTextChannelId());
            statement.setLong(2, poll.getMessageId());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    results.deleteRow();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while deleting a poll.", e);
        }
    }

    public void updatePolls(ShardManager shardManager)
    {
        for(Poll poll : getPolls())
        {
            if(OffsetDateTime.now().isAfter(poll.getEndTime()))
            {
                deletePoll(poll);
                PollHandler.sendResults(poll, shardManager);
            }
        }
    }

    private List<Poll> getPolls()
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM POLLS",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            List<Poll> polls = new LinkedList<>();

            try(ResultSet results = statement.executeQuery())
            {
                while(results.next())
                    polls.add(bot.endlessBuilder.entityBuilder.createPoll(results));
                return polls;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while deleting a poll.", e);
            return Collections.emptyList();
        }
    }
}

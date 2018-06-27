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

import me.artuto.endless.core.entities.Poll;
import me.artuto.endless.core.entities.impl.PollImpl;
import me.artuto.endless.handlers.PollHandler;
import me.artuto.endless.storage.data.Database;
import net.dv8tion.jda.bot.sharding.ShardManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Artuto
 */

public class PollsDataManager
{
    private final Connection connection;

    public PollsDataManager(Database db)
    {
        this.connection = db.getConnection();
    }

    public void createPoll(long endTime, long guildId, long msgId, long tcId)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery("SELECT * FROM POLLS"))
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
            Database.LOG.error("Error while creating a poll.", e);
        }
    }

    public void deletePoll(long msgId, long tcId)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM POLLS WHERE channel_id = %s AND msg_id = %s", tcId, msgId)))
            {
                if(results.next())
                    results.deleteRow();
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while deleting a poll.", e);
        }
    }

    public List<Poll> getPolls()
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            List<Poll> polls = new LinkedList<>();

            try(ResultSet results = statement.executeQuery("SELECT * FROM POLLS"))
            {
                while(results.next())
                    polls.add(new PollImpl(results.getLong("end_time"), results.getLong("guild_id"),
                            results.getLong("msg_id"), results.getLong("tc_id")));
                return polls;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while deleting a poll.", e);
            return Collections.emptyList();
        }
    }

    public void updatePolls(ShardManager shardManager)
    {
        for(Poll poll : getPolls())
        {
            if(OffsetDateTime.now().isAfter(poll.getEndTime()))
            {
                deletePoll(poll.getMessageId(), poll.getTextChannelId());
                PollHandler.sendResults(poll, shardManager);
            }
        }
    }
}

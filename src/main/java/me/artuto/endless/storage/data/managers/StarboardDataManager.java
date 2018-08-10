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
import me.artuto.endless.core.entities.StarboardMessage;
import net.dv8tion.jda.core.entities.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StarboardDataManager
{
    private final Bot bot;
    private final Connection connection;
    private final Logger LOG = Endless.getLog(StarboardDataManager.class);

    public StarboardDataManager(Bot bot)
    {
        this.bot = bot;
        this.connection = bot.db.getConnection();
    }

    public void addMessage(Message msg, int amount)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM STARBOARD",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(!(results.next()))
                {
                    results.moveToInsertRow();
                    results.updateLong("msg_id", msg.getIdLong());
                    results.updateLong("tc_id", msg.getTextChannel().getIdLong());
                    results.updateLong("guild_id", msg.getGuild().getIdLong());
                    results.updateInt("star_amount", amount);
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while adding a message to the starboard. Message ID: {} TC ID: {}", msg.getIdLong(), msg.getTextChannel().getIdLong(), e);
        }
    }

    public void setStarboardMessageId(Message msg, long starboardMsg)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM STARBOARD WHERE msg_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, msg.getIdLong());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                {
                    results.updateLong("starboard_msg_id", starboardMsg);
                    results.updateRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while setting the starboard message ID. Message ID: {} TC ID: {} Starboard Message ID: ",
                    msg.getIdLong(), msg.getTextChannel().getIdLong(), starboardMsg, e);
        }
    }

    public void updateCount(long msg, int amount)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM STARBOARD WHERE msg_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, msg);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                {
                    results.updateInt("star_amount", amount);
                    results.updateRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while updating the count of stars of a message. Message ID: {}", msg, e);
        }
    }

    public StarboardMessage getStarboardMessage(long msg)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM STARBOARD WHERE msg_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, msg);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    return bot.endlessBuilder.entityBuilder.createStarboardMessage(results);
                else
                    return null;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting a message from the starboard. Message ID: {}", msg, e);
            return null;
        }
    }

    public void deleteMessage(long msg, long starboardMsg)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM STARBOARD WHERE msg_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, msg);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    results.deleteRow();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while deleting a message from the starboard. Message ID: {} Starboard Message ID: {}", msg, starboardMsg, e);
        }
    }
}

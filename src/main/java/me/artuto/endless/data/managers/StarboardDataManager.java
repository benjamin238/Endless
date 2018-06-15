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

package me.artuto.endless.data.managers;

import me.artuto.endless.data.Database;
import me.artuto.endless.core.entities.StarboardMessage;
import me.artuto.endless.core.entities.impl.StarboardMessageImpl;
import net.dv8tion.jda.core.entities.Message;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StarboardDataManager
{
    private final Connection connection;

    public StarboardDataManager(Database db)
    {
        connection = db.getConnection();
    }

    public boolean addMessage(Message msg, Integer amount)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM STARBOARD WHERE msg_id = %s", msg.getId())))
            {
                results.moveToInsertRow();
                results.updateLong("msg_id", msg.getIdLong());
                results.updateLong("tc_id", msg.getTextChannel().getIdLong());
                results.updateLong("guild_id", msg.getGuild().getIdLong());
                results.updateInt("star_amount", amount);
                results.insertRow();
                return true;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while adding a message to the starboard. Message ID: "+msg.getId()+" TC ID: "+msg.getTextChannel().getId(), e);
            return false;
        }
    }

    public boolean setStarboardMessageId(Message msg, Long starboardMsg)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT msg_id, starboard_msg_id FROM STARBOARD WHERE msg_id = %s", msg.getId())))
            {
                if(results.next())
                {
                    results.updateLong("starboard_msg_id", starboardMsg);
                    results.updateRow();
                    return true;
                }
                else return false;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the starboard message ID. Message ID: "+msg.getId()+". TC ID: "+msg.getTextChannel().getId()+". Starboard Message ID: "+starboardMsg, e);
            return false;
        }
    }

    public boolean updateCount(Long msg, Integer amount)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM STARBOARD WHERE msg_id = %s", msg)))
            {
                if(results.next())
                {
                    results.updateInt("star_amount", amount);
                    results.updateRow();
                    return true;
                }
                else return false;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while updating the count of stars of a message. Message ID: "+msg, e);
            return false;
        }
    }

    public StarboardMessage getStarboardMessage(Long message)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT msg_id, tc_id, guild_id, star_amount, starboard_msg_id FROM STARBOARD WHERE msg_id = %s", message)))
            {
                if(results.next())
                    return new StarboardMessageImpl(results.getLong("msg_id"), results.getLong("tc_id"), results.getLong("guild_id"), results.getInt("star_amount"), results.getLong("starboard_msg_id"));
                else return null;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting a message from the starboard. Message ID: "+message, e);
            return null;
        }
    }

    public void deleteMessage(Long msg, Long starboardMsg)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM STARBOARD WHERE msg_id = \"%s\"", msg)))
            {
                if(results.next())
                {
                    results.updateLong("msg_id", 0);
                    results.updateRow();
                }
            }
            statement.executeUpdate(String.format("DELETE FROM STARBOARD WHERE starboard_msg_id = \"%s\"", starboardMsg));
            statement.closeOnCompletion();
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while deleting a message from the starboard. Message ID: "+msg+". Starboard Message ID: "+starboardMsg, e);
        }
    }
}

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

import me.artuto.endless.Const;
import me.artuto.endless.data.Database;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class BlacklistDataManager
{
    private Connection connection;

    public BlacklistDataManager(Database db)
    {
        connection = db.getConnection();
    }

    public boolean isBlacklisted(long id)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM BLACKLISTED_ENTITIES WHERE id = %s", id)))
            {
                return results.next();
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while checking if the specified ID is blacklisted. ID: "+id, e);
            return false;
        }
    }

    public void addBlacklist(long id, String reason, Const.BlacklistType type)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM BLACKLISTED_ENTITIES WHERE id = %s", id)))
            {
                if(results.next())
                {
                    results.updateLong("id", id);
                    results.updateString("reason", reason);
                    results.updateString("type", type.name());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("id", id);
                    results.updateString("reason", reason);
                    results.updateString("type", type.name());
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while adding the specified ID to the blacklisted entities list. ID: "+id, e);
        }
    }

    public void removeBlacklist(long id)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM BLACKLISTED_ENTITIES WHERE id = %s", id)))
            {
                if(results.next())
                    results.deleteRow();
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while removing the specified ID from the blacklisted entities list. ID: "+id, e);
        }
    }

    public List<Guild> getBlacklistedGuilds(JDA jda)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            List<Guild> guilds;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM BLACKLISTED_ENTITIES WHERE type = %s", Const.BlacklistType.GUILD.name())))
            {
                guilds = new LinkedList<>();
                while(results.next())
                {
                    long id = results.getLong("id");
                    Guild guild = jda.getGuildCache().getElementById(id);

                    if(!(guild==null))
                        guilds.add(guild);
                }
            }
            return guilds;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error whiile getting the blacklisted guilds list.", e);
            return null;
        }
    }

    public List<User> getBlacklistedUsers(JDA jda)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            List<User> users;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM BLACKLISTED_ENTITIES WHERE type = \"%s\"", Const.BlacklistType.USER.name())))
            {
                users = new LinkedList<>();
                while(results.next())
                {
                    long id = results.getLong("id");
                    jda.retrieveUserById(id).queue(users::add, e -> removeBlacklist(id));
                }
            }
            return users;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the blacklisted users list.", e);
            return null;
        }
    }

    public List<Long> getBlacklistedGuildsRaw()
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            List<Long> guilds;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM BLACKLISTED_ENTITIES WHERE type = %s", Const.BlacklistType.GUILD.name())))
            {
                guilds = new LinkedList<>();
                while(results.next())
                    guilds.add(results.getLong("id"));
            }
            return guilds;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the raw blacklisted guilds list.", e);
            return null;
        }
    }

    public List<Long> getBlacklistedUsersRaw()
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            List<Long> users;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM BLACKLISTED_ENTITIES WHERE type = %s", Const.BlacklistType.USER.name())))
            {
                users = new LinkedList<>();
                while(results.next())
                    users.add(results.getLong("id"));
            }
            return users;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the raw blacklisted users list.", e);
            return null;
        }
    }
}

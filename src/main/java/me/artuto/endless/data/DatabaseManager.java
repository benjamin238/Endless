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

package me.artuto.endless.data;

import me.artuto.endless.entities.GuildSettings;
import me.artuto.endless.entities.impl.GuildSettingsImpl;
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DatabaseManager
{
    private final Connection connection;
    private final Logger LOG = LoggerFactory.getLogger("MySQL Database");
    private final GuildSettings DEFAULT = new GuildSettingsImpl(0L, 0L, 0L, "", 0L, "", 0L, 0, null, 0L);

    public DatabaseManager(String host, String user, String pass) throws SQLException
    {
        connection = DriverManager.getConnection(host, user, pass);
    }

    //Connection getter

    Connection getConnection()
    {
        return connection;
    }

    //Guild settings

    public GuildSettings getSettings(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            GuildSettings gs;
            Set<String> prefixes = new HashSet<>();
            String array;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getIdLong())))
            {
                if(results.next())
                {
                    array = results.getString("prefixes");

                    if(!(array == null))
                    {
                        for(Object prefix : new JSONArray(results.getString("prefixes")))
                            prefixes.add(prefix.toString());
                    }

                    gs = new GuildSettingsImpl(results.getLong("modlog_id"), results.getLong("serverlog_id"),
                            results.getLong("welcome_id"), results.getString("welcome_msg"),
                            results.getLong("leave_id"), results.getString("leave_msg"),
                            results.getLong("starboard_id"), results.getInt("starboard_count"),
                            prefixes, results.getLong("muted_role_id"));
                }
                else gs = DEFAULT;
            }
            return gs;
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return DEFAULT;
        }
    }

    public GuildSettings getSettings(long guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            GuildSettings gs;
            Set<String> prefixes = new HashSet<>();
            String array;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild)))
            {
                if(results.next())
                {
                    array = results.getString("prefixes");

                    if(!(array == null)) for(Object prefix : new JSONArray(results.getString("prefixes")))
                        prefixes.add(prefix.toString());

                    gs = new GuildSettingsImpl(results.getLong("modlog_id"), results.getLong("serverlog_id"),
                            results.getLong("welcome_id"), results.getString("welcome_msg"),
                            results.getLong("leave_id"), results.getString("leave_msg"),
                            results.getLong("starboard_id"), results.getInt("starboard_count"),
                            prefixes, results.getLong("muted_role_id"));
                }
                else gs = DEFAULT;
            }
            return gs;
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return DEFAULT;
        }
    }

    public boolean hasSettings(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                return results.next();
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return false;
        }
    }

    public boolean hasSettings(long guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild)))
            {
                return results.next();
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return false;
        }
    }

    public void shutdown()
    {
        try
        {
            connection.close();
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }
}

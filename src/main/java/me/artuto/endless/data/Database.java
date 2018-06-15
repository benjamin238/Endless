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

import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.core.entities.impl.GuildSettingsImpl;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Database
{
    private final Connection connection;
    public static final Logger LOG = LoggerFactory.getLogger(Database.class);
    private final GuildSettings DEFAULT = new GuildSettingsImpl(null, 0, 0,null,
            0L, 0L, 0L,0L,
            0L, 0L, null, null);

    public Database(String host, String user, String pass) throws SQLException
    {
        connection = DriverManager.getConnection(host, user, pass);
    }

    //Connection getter
    public Connection getConnection()
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
            List<Role> roleMeRoles = new LinkedList<>();
            Set<String> prefixes = new HashSet<>();
            String array;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getIdLong())))
            {
                if(results.next())
                {
                    array = results.getString("prefixes");

                    if(!(array == null))
                    {
                        for(Object prefix : new JSONArray(array))
                            prefixes.add(prefix.toString());
                    }

                    array = results.getString("roleme_roles");

                    if(!(array == null))
                    {
                        for(Object preRole : new JSONArray(array))
                        {
                            Role role = guild.getRoleById(preRole.toString());

                            if(!(role==null))
                                roleMeRoles.add(role);
                        }
                    }

                    gs = new GuildSettingsImpl(prefixes, results.getInt("ban_delete_days"), results.getInt("starboard_count"), roleMeRoles,
                            results.getLong("leave_id"), results.getLong("modlog_id"), results.getLong("muted_role_id"), results.getLong("serverlog_id"),
                            results.getLong("starboard_id"), results.getLong("welcome_id"), results.getString("leave_msg"), results.getString("welcome_msg"));
                }
                else gs = DEFAULT;
            }
            return gs;
        }
        catch(SQLException e)
        {
            LOG.warn("Error while getting the settings of a guild. ID: "+guild.getId(), e);
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
            LOG.warn("Error while checking the settings of a guild. ID: "+guild.getId(), e);
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
            LOG.warn("Error while checking the settings of a guild. ID: "+guild, e);
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
            LOG.warn("Unexpected error while shutdown process!", e);
        }
    }
}

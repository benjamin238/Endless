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

package me.artuto.endless.storage.data;

import me.artuto.endless.Bot;
import me.artuto.endless.core.entities.*;
import me.artuto.endless.core.entities.impl.EndlessCoreImpl;
import me.artuto.endless.core.entities.impl.GuildSettingsImpl;
import me.artuto.endless.core.entities.impl.IgnoreImpl;
import me.artuto.endless.core.entities.impl.ProfileImpl;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Database
{
    public final List<Long> toDelete = new LinkedList<>();
    private final Bot bot;
    private final Connection connection;

    public static final Logger LOG = LoggerFactory.getLogger(Database.class);

    public GuildSettings createDefaultSettings(Guild guild)
    {
        return new GuildSettingsImpl(true, new HashSet<>(), guild, 0, 0, new LinkedList<>(), new LinkedList<>(),
                new LinkedList<>(), new LinkedList<>(), 0L, 0L, 0L, 0L, 0L,
                0L, 0L, 0L, Room.Mode.NO_CREATION, null, "\u2B50", null, null, EntityBuilder.DEFAULT_TZ);
    }

    public Profile createDefaultProfile(User user)
    {
        return new ProfileImpl(0, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, user);
    }

    public Database(Bot bot, String host, String user, String pass) throws SQLException
    {
        this.bot = bot;
        this.connection = DriverManager.getConnection(host, user, pass);
    }

    // Connection getter
    public Connection getConnection()
    {
        return connection;
    }

    // Guild settings
    public GuildSettings getSettings(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            GuildSettings gs;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getIdLong())))
            {
                if(results.next())
                    return bot.endlessBuilder.entityBuilder.createGuildSettings(guild, results);
                else
                    gs = createDefaultSettings(guild);
            }
            return gs;
        }
        catch(SQLException e)
        {
            LOG.warn("Error while getting the settings of a guild. ID: "+guild.getId(), e);
            return createDefaultSettings(guild);
        }
    }

    public List<Guild> getGuildsThatHaveSettings(JDA jda)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery("SELECT * FROM GUILD_SETTINGS"))
            {
                Guild guild;
                List<Guild> guilds = new LinkedList<>();
                while(results.next())
                {
                    long id = results.getLong("guild_id");
                    guild = jda.getGuildById(id);
                    if(!(guild==null))
                        guilds.add(guild);
                    else
                        toDelete.add(id);
                }
                return guilds;
            }
        }
        catch(SQLException e)
        {
            LOG.warn("Error while getting settings", e);
            return Collections.emptyList();
        }
    }

    public List<Ignore> getIgnoresForGuild(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM IGNORES WHERE GUILD_ID = %s", guild.getIdLong())))
            {
                List<Ignore> ignores = new LinkedList<>();
                while(results.next())
                    ignores.add(bot.endlessBuilder.entityBuilder.createIgnore(results));
                return ignores;
            }
        }
        catch(SQLException e)
        {
            LOG.warn("Error while getting the list of ignored entities of a guild. ID: "+guild.getIdLong(), e);
            return Collections.emptyList();
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

    public void deleteSettings(long guild)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = ?");
            statement.setLong(1, guild);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    results.deleteRow();
            }
        }
        catch(SQLException e)
        {
            LOG.warn("Error while deleting the settings of a guild. ID: {}", guild, e);
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

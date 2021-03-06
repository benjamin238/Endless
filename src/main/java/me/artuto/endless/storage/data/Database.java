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
import me.artuto.endless.Endless;
import me.artuto.endless.core.entities.*;
import me.artuto.endless.core.entities.impl.GuildSettingsImpl;
import me.artuto.endless.core.entities.impl.ProfileImpl;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;

import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Database
{
    public final List<Long> toDelete = new LinkedList<>();
    private final Bot bot;
    private final Connection connection;
    private final Logger LOG = Endless.getLog(Database.class);

    public GuildSettings createDefaultSettings(Guild guild)
    {
        return new GuildSettingsImpl(true, true, false, new HashSet<>(), guild, 0, 0, 100, new LinkedList<>(), new LinkedList<>(),
                new LinkedList<>(), new LinkedList<>(), 0L, 0L, 0L, 0L, 0L, 0L,
                0L, 0L, 0L, 0L, 0L, Room.Mode.NO_CREATION, null, "\u2B50", null, null, EntityBuilder.DEFAULT_TZ);
    }

    public Profile createDefaultProfile(User user)
    {
        return new ProfileImpl(null, null, null, null, null, null, null, null,
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
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM GUILD_SETTINGS WHERE guild_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, guild.getIdLong());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    return bot.endlessBuilder.entityBuilder.createGuildSettings(guild, results);
                else
                    return createDefaultSettings(guild);
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting the settings of a guild. ID: {}", guild.getId(), e);
            return createDefaultSettings(guild);
        }
    }

    public List<Guild> getGuildsThatHaveSettings(JDA jda)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM GUILD_SETTINGS", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
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
            LOG.error("Error while getting settings", e);
            return Collections.emptyList();
        }
    }

    public List<Ignore> getIgnoresForGuild(Guild guild)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM IGNORES WHERE guild_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, guild.getIdLong());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                List<Ignore> ignores = new LinkedList<>();
                while(results.next())
                    ignores.add(bot.endlessBuilder.entityBuilder.createIgnore(results));
                return ignores;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting the list of ignored entities of a guild. ID: {} ", guild.getIdLong(), e);
            return Collections.emptyList();
        }
    }

    public boolean hasSettings(long guild)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM GUILD_SETTINGS WHERE guild_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, guild);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                return results.next();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while checking the settings of a guild. ID: {}", guild, e);
            return false;
        }
    }

    public void deleteSettings(long guild)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
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
            LOG.error("Error while deleting the settings of a guild. ID: {}", guild, e);
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
            LOG.error("Unexpected error while shutdown process!", e);
        }
    }
}

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
import me.artuto.endless.core.entities.BlacklistType;
import me.artuto.endless.core.entities.impl.EndlessCoreImpl;
import me.artuto.endless.core.entities.Blacklist;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.sql.*;
import java.util.*;

public class BlacklistDataManager
{
    private final Bot bot;
    private final Connection connection;
    private final Logger LOG = Endless.getLog(BlacklistDataManager.class);

    public BlacklistDataManager(Bot bot)
    {
        this.bot = bot;
        this.connection = bot.db.getConnection();
    }

    private Blacklist getBlacklist(long id)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM BLACKLISTED_ENTITIES WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, id);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    return bot.endlessBuilder.entityBuilder.createBlacklist(results);
                else
                    return null;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while checking if the specified ID is blacklisted. ID: {}", id, e);
            return null;
        }
    }

    public void addBlacklist(BlacklistType type, long id, long time, String reason)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM BLACKLISTED_ENTITIES WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, id);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                {
                    results.updateLong("id", id);
                    results.updateString("reason", reason.equals("[no reason provided]")?null:reason);
                    results.updateLong("time", time);
                    results.updateString("type", type.name());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("id", id);
                    results.updateString("reason", reason);
                    results.updateLong("time", time);
                    results.updateString("type", type.name());
                    results.insertRow();
                }
                Blacklist blacklist = bot.endless.getBlacklist(id);
                if(blacklist==null)
                    ((EndlessCoreImpl)bot.endless).addBlacklist(getBlacklist(id));
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while adding the specified ID to the blacklisted entities list. ID: {}", id, e);
        }
    }

    public void removeBlacklist(long id)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM BLACKLISTED_ENTITIES WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, id);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                {
                    results.deleteRow();
                    Blacklist blacklist = bot.endless.getBlacklist(id);
                    if(!(blacklist==null))
                        ((EndlessCoreImpl)bot.endless).removeBlacklist(blacklist);
                }
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while removing the specified ID from the blacklisted entities list. ID: {}", id, e);
        }
    }

    public Map<Guild, Blacklist> getBlacklistedGuilds(JDA jda)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM BLACKLISTED_ENTITIES WHERE type = ?");
            statement.setString(1, BlacklistType.GUILD.name());
            statement.closeOnCompletion();
            Map<Guild, Blacklist> map;

            try(ResultSet results = statement.executeQuery())
            {
                map = new HashMap<>();
                while(results.next())
                {
                    long id = results.getLong("id");
                    Guild guild = jda.getGuildById(id);

                    if(!(guild==null))
                        map.put(guild, getBlacklist(id));
                }
            }
            return map;
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting the blacklisted guilds map.", e);
            return null;
        }
    }

    public Map<User, Blacklist> getBlacklistedUsers(JDA jda)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM BLACKLISTED_ENTITIES WHERE type = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, BlacklistType.USER.name());
            statement.closeOnCompletion();
            Map<User, Blacklist> map;

            try(ResultSet results = statement.executeQuery())
            {
                map = new HashMap<>();
                while(results.next())
                {
                    long id = results.getLong("id");
                    jda.retrieveUserById(id).queue(user -> map.put(user, getBlacklist(id)), e -> removeBlacklist(id));
                }
            }
            return map;
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting the blacklisted users map.", e);
            return null;
        }
    }
}

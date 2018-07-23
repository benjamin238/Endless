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
import me.artuto.endless.core.entities.PunishmentType;
import me.artuto.endless.core.entities.Punishment;
import me.artuto.endless.core.entities.TempPunishment;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Artuto
 */

public class PunishmentsDataManager
{
    private final Bot bot;
    private final Connection connection;
    private final Logger LOG = Endless.getLog(PunishmentsDataManager.class);

    public PunishmentsDataManager(Bot bot)
    {
        this.bot = bot;
        this.connection = bot.db.getConnection();
    }

    public void addPunishment(long user, long guild, PunishmentType type)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PUNISHMENTS",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                results.moveToInsertRow();
                results.updateLong("user_id", user);
                results.updateLong("guild_id", guild);
                results.updateString("type", type.name());
                results.insertRow();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while adding a punishment. User ID: {} Guild ID: {}", user, guild, e);
        }
    }

    public void removePunishment(long user, long guild, PunishmentType type)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PUNISHMENTS WHERE user_id = ? AND guild_id = ? AND type = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, user);
            statement.setLong(2, guild);
            statement.setString(3, type.name());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    results.deleteRow();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while removing a punishment. User ID: {} Guild ID: {}", user, guild, e);
        }
    }

    public void addTempPunishment(long user, long guild, long time, PunishmentType type)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PUNISHMENTS",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                results.moveToInsertRow();
                results.updateLong("user_id", user);
                results.updateLong("guild_id", guild);
                results.updateLong("time", time);
                results.updateString("type", type.name());
                results.insertRow();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while adding a temporal punishment. User ID: {} Guild ID: {}", user, guild, e);
        }
    }

    public Punishment getPunishment(long user, long guild, PunishmentType type)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PUNISHMENTS WHERE user_id = ? AND guild_id = ? AND type = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, user);
            statement.setLong(2, guild);
            statement.setString(3, type.name());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    return bot.endlessBuilder.entityBuilder.createPunishment(results);
                else
                    return null;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting a punishment. User ID: {} Guild ID: {}", user, guild, e);
            return null;
        }
    }

    public TempPunishment getTempPunishment(long user, long guild, PunishmentType type)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PUNISHMENTS WHERE user_id = ? AND guild_id = ? AND type = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, user);
            statement.setLong(2, guild);
            statement.setString(3, type.name());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    return bot.endlessBuilder.entityBuilder.createTempPunishment(results);
                else
                    return null;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting a temporal punishment. User ID: {} Guild ID: {}", user, guild, e);
            return null;
        }
    }

    public List<Punishment> getPunishments(PunishmentType type)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PUNISHMENTS WHERE type = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, type.name());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                List<Punishment> list = new LinkedList<>();

                while(results.next())
                    list.add(bot.endlessBuilder.entityBuilder.createPunishment(results));
                return list;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting the list of punishments.", e);
            return null;
        }
    }

    public List<TempPunishment> getTempPunishments(PunishmentType type)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PUNISHMENTS WHERE type = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, type.name());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                List<TempPunishment> list = new LinkedList<>();

                while(results.next())
                    list.add(bot.endlessBuilder.entityBuilder.createTempPunishment(results));
                return list;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting the list of temporal punishments.", e);
            return null;
        }
    }

    public void updateTempPunishments(PunishmentType type, ShardManager shardManager)
    {
        for(TempPunishment p : getTempPunishments(type))
        {
            if(OffsetDateTime.now().isAfter(p.getExpiryTime()))
            {
                removePunishment(p.getUserId(), p.getGuildId(), p.getType());
                Guild guild = shardManager.getGuildById(p.getGuildId());

                if(!(guild==null))
                {
                     Member member = guild.getMemberById(p.getUserId());
                     Role mutedRole = GuildUtils.getMutedRole(guild);
                     if(!(member==null) && guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES) &&
                             ChecksUtil.canMemberInteract(guild.getSelfMember(), mutedRole))
                         guild.getController().removeSingleRoleFromMember(member, mutedRole).reason("[Tempumute finished]").queue();
                }
            }
        }
    }
}

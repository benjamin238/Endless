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
import me.artuto.endless.core.entities.Punishment;
import me.artuto.endless.core.entities.TempPunishment;
import me.artuto.endless.core.entities.impl.PunishmentImpl;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Artuto
 */

public class PunishmentsDataManager
{
    private final Connection connection;

    public PunishmentsDataManager(Database db)
    {
        this.connection = db.getConnection();
    }

    public void addPunishment(long user, long guild, Const.PunishmentType type)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery("SELECT * FROM PUNISHMENTS"))
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
            Database.LOG.error("Error while adding a punishment. User ID: "+user+". Guild ID: "+guild, e);
        }
    }

    public void removePunishment(long user, long guild, Const.PunishmentType type)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM PUNISHMENTS WHERE user_id = %s AND guild_id = %s AND type = \"%s\"", user, guild, type.name())))
            {
                if(results.next())
                {
                    results.updateInt("user_id", 0);
                    results.updateRow();
                }
            }
            statement.executeUpdate(String.format("DELETE FROM PUNISHMENTS WHERE user_id = %s AND guild_id = %s AND type = \"%s\"", user, guild, type.name()));
            statement.closeOnCompletion();
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while adding a punishment. User ID: "+user+". Guild ID: "+guild, e);
        }
    }

    public void addTempPunishment(long user, long guild, long time, Const.PunishmentType type)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery("SELECT * FROM PUNISHMENTS"))
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
            Database.LOG.error("Error while adding a temporal punishment. User ID: "+user+". Guild ID: "+guild, e);
        }
    }

    public Punishment getPunishment(long user, long guild, Const.PunishmentType type)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM PUNISHMENTS WHERE user_id = %s AND guild_id = %s AND type = \"%s\"", user, guild, type.name())))
            {
                if(results.next())
                    return new PunishmentImpl(type, guild, user, null);
                else
                    return null;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting a punishment. User ID: "+user+". Guild ID: "+guild, e);
            return null;
        }
    }

    public Punishment getTempPunishment(long user, long guild, Const.PunishmentType type)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM PUNISHMENTS WHERE user_id = %s AND guild_id = %s AND type = \"%s\"", user, guild, type.name())))
            {
                if(results.next())
                {
                    Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                    gmt.setTimeInMillis(results.getLong("time"));
                    return new PunishmentImpl(type, guild, user, OffsetDateTime.ofInstant(gmt.toInstant(), gmt.getTimeZone().toZoneId()));
                }
                else return null;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting a temporal punishment. User ID: "+user+". Guild ID: "+guild, e);
            return null;
        }
    }

    public List<Punishment> getPunishments(Const.PunishmentType type)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM PUNISHMENTS WHERE type = \"%s\"", type.name())))
            {
                List<Punishment> list = new LinkedList<>();

                while(results.next())
                    list.add(new PunishmentImpl(type, results.getLong("guild_id"), results.getLong("user_id"), null));
                return list;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the list of punishments.", e);
            return null;
        }
    }

    public List<TempPunishment> getTempPunishments(Const.PunishmentType type)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM PUNISHMENTS WHERE type = \"%s\"", type.name())))
            {
                List<TempPunishment> list = new LinkedList<>();

                while(results.next())
                {
                    Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                    gmt.setTimeInMillis(results.getLong("time"));
                    list.add(new PunishmentImpl(type, results.getLong("guild_id"), results.getLong("user_id"),
                           OffsetDateTime.ofInstant(gmt.toInstant(), gmt.getTimeZone().toZoneId())));
                }
                return list;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the list of temporal punishments.", e);
            return null;
        }
    }

    public void updateTempPunishments(Const.PunishmentType type, JDA jda)
    {
        for(TempPunishment p : getTempPunishments(type))
        {
            if(OffsetDateTime.now().isAfter(p.getExpiryTime()))
            {
                removePunishment(p.getUserId(), p.getGuildId(), p.getType());
                Guild guild = jda.getGuildById(p.getGuildId());

                if(!(guild==null))
                {
                     Member member = guild.getMemberById(p.getUserId());
                     Role mutedRole = GuildUtils.getMutedRole(guild);
                     if(!(member==null) && guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES) && ChecksUtil.canMemberInteract(guild.getSelfMember(), mutedRole))
                         guild.getController().removeSingleRoleFromMember(member, mutedRole).reason("[Tempumute finished]").queue();
                }
            }
        }
    }
}

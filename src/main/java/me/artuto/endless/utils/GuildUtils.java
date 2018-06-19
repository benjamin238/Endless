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

package me.artuto.endless.utils;

import me.artuto.endless.Bot;
import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.core.entities.ParsedAuditLog;
import me.artuto.endless.core.entities.impl.ParsedAuditLogImpl;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.audit.AuditLogChange;
import net.dv8tion.jda.core.audit.AuditLogEntry;
import net.dv8tion.jda.core.audit.AuditLogKey;
import net.dv8tion.jda.core.entities.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GuildUtils
{
    private static Bot bot;

    public GuildUtils(Bot bot)
    {
        GuildUtils.bot = bot;
    }

    public static Collection<String> getPrefixes(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettingsById(guild.getIdLong());
        if(settings==null)
            return Collections.emptyList();
        else
            return settings.getPrefixes();
    }

    public static int getBanDeleteDays(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettingsById(guild.getIdLong());
        if(settings==null)
            return 0;
        else
            return settings.getBanDeleteDays();
    }

    public static int getStarboardCount(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettingsById(guild.getIdLong());
        if(settings==null)
            return 0;
        else
            return settings.getStarboardCount();
    }

    public static List<Role> getRoleMeRoles(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettingsById(guild.getIdLong());
        if(settings==null)
            return Collections.emptyList();
        else
            return settings.getRoleMeRoles();
    }

    public static Role getMutedRole(Guild guild)
    {
        return guild.getRolesByName("Muted", true).stream().findFirst().orElse(guild.getRoleById(bot.db.getSettings(guild).getMutedRole()));
    }

    public static String getLeaveMessage(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettingsById(guild.getIdLong());
        if(settings==null)
            return null;
        else
            return settings.getLeaveMsg();
    }

    public static String getWelcomeMessage(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettingsById(guild.getIdLong());
        if(settings==null)
            return null;
        else
            return settings.getWelcomeMsg();
    }

    public static TextChannel getLeaveChannel(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettingsById(guild.getIdLong());
        if(settings==null)
            return null;
        else
            return guild.getTextChannelById(settings.getLeaveChannel());
    }

    public static TextChannel getModlogChannel(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettingsById(guild.getIdLong());
        if(settings==null)
            return null;
        else
            return guild.getTextChannelById(settings.getModlog());
    }

    public static TextChannel getServerlogChannel(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettingsById(guild.getIdLong());
        if(settings==null)
            return null;
        else
            return guild.getTextChannelById(settings.getServerlog());
    }

    public static TextChannel getStarboardChannel(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettingsById(guild.getIdLong());
        if(settings==null)
            return null;
        else
            return guild.getTextChannelById(settings.getStarboard());
    }

    public static TextChannel getWelcomeChannel(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettingsById(guild.getIdLong());
        if(settings==null)
            return null;
        else
            return guild.getTextChannelById(settings.getWelcomeChannel());
    }

    public static ParsedAuditLog getAuditLog(AuditLogEntry entry, AuditLogKey key)
    {
        JDA jda = entry.getJDA();
        User author = entry.getUser();
        User target = jda.getUserById(entry.getTargetIdLong());

        if(!(key==null))
        {
            AuditLogChange change = entry.getChangeByKey(key);
            if(!(change==null))
                return new ParsedAuditLogImpl(key, change.getNewValue(), change.getOldValue(), entry.getReason(), author, target);
            else
                return null;
        }
        else
            return new ParsedAuditLogImpl(null, null, null, entry.getReason(), author, target);
    }
}

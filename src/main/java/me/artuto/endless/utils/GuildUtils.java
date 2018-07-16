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
import me.artuto.endless.core.entities.Ignore;
import me.artuto.endless.core.entities.ParsedAuditLog;
import me.artuto.endless.core.entities.impl.EndlessCoreImpl;
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
    public static Bot bot;

    public GuildUtils(Bot bot)
    {
        GuildUtils.bot = bot;
    }

    public static Collection<String> getPrefixes(Guild guild)
    {
        if(bot.endless==null)
            return Collections.emptySet();

        GuildSettings settings = bot.endless.getGuildSettings(guild);
        return settings.getPrefixes();
    }

    public static int getBanDeleteDays(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettings(guild);
        return settings.getBanDeleteDays();
    }

    public static int getStarboardCount(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettings(guild);
        return settings.getStarboardCount();
    }

    public static List<Ignore> getIgnoredEntities(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettings(guild);
        return settings.getIgnoredEntities();
    }

    public static List<Role> getRoleMeRoles(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettings(guild);
        return settings.getRoleMeRoles();
    }

    public static Role getAdminRole(Guild guild)
    {
        return guild.getRolesByName("Admin", true)
                .stream().findFirst().orElse(guild.getRoleById(bot.endless.getGuildSettings(guild).getAdminRole()));
    }

    public static Role getModRole(Guild guild)
    {
        return guild.getRolesByName("Moderator", true)
                .stream().findFirst().orElse(guild.getRoleById(bot.endless.getGuildSettings(guild).getModRole()));
    }

    public static Role getMutedRole(Guild guild)
    {
        return guild.getRolesByName("Muted", true)
                .stream().findFirst().orElse(guild.getRoleById(bot.endless.getGuildSettings(guild).getMutedRole()));
    }

    public static String getLeaveMessage(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettings(guild);
        return settings.getLeaveMsg();
    }

    public static String getWelcomeMessage(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettings(guild);
        return settings.getWelcomeMsg();
    }

    public static TextChannel getLeaveChannel(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettings(guild);
        return guild.getTextChannelById(settings.getLeaveChannel());
    }

    public static TextChannel getModlogChannel(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettings(guild);
        return guild.getTextChannelById(settings.getModlog());
    }

    public static TextChannel getServerlogChannel(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettings(guild);
        return guild.getTextChannelById(settings.getServerlog());
    }

    public static TextChannel getStarboardChannel(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettings(guild);
        return guild.getTextChannelById(settings.getStarboard());
    }

    public static TextChannel getWelcomeChannel(Guild guild)
    {
        GuildSettings settings = bot.endless.getGuildSettings(guild);
        return guild.getTextChannelById(settings.getWelcomeChannel());
    }

    public static ParsedAuditLog getAuditLog(AuditLogEntry entry, AuditLogKey key)
    {
        return bot.endlessBuilder.entityBuilder.createParsedAuditLog(entry, key);
    }
}

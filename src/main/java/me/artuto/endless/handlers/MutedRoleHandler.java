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

package me.artuto.endless.handlers;

import me.artuto.endless.Action;
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.core.entities.ParsedAuditLog;
import me.artuto.endless.core.entities.Punishment;
import me.artuto.endless.core.entities.TempPunishment;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audit.ActionType;
import net.dv8tion.jda.core.audit.AuditLogEntry;
import net.dv8tion.jda.core.audit.AuditLogKey;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Artuto
 */

public class MutedRoleHandler
{
    public static void checkRoleAdd(GuildMemberRoleAddEvent event)
    {
        if(!(Bot.getInstance().dataEnabled))
            return;

        Guild guild = event.getGuild();
        Role mutedRole = GuildUtils.getMutedRole(guild);

        if(!(event.getRoles().contains(mutedRole)))
            return;

        if(!(ChecksUtil.hasPermission(guild.getSelfMember(), null, Permission.VIEW_AUDIT_LOGS)))
            return;

        guild.getAuditLogs().type(ActionType.MEMBER_ROLE_UPDATE).limit(1).queue(preEntries -> {
            List<AuditLogEntry> entries = preEntries.stream().filter(ale -> ale.getTargetIdLong()==event.getUser().getIdLong()).collect(Collectors.toList());

            if(entries.isEmpty())
                return;

            ParsedAuditLog parsedAuditLog = GuildUtils.getAuditLog(entries.get(0), AuditLogKey.MEMBER_ROLES_ADD);
            if(parsedAuditLog==null)
                return;

            String reason = parsedAuditLog.getReason();
            User author = parsedAuditLog.getAuthor();
            User target = parsedAuditLog.getTarget();

            if(author.isBot())
                return;

            Bot.getInstance().modlog.logManual(Action.MANUAL_MUTE, guild, OffsetDateTime.now(), reason, author, target);
            Bot.getInstance().pdm.addPunishment(target.getIdLong(), guild.getIdLong(), Const.PunishmentType.MUTE);
        }, e -> {});
    }

    public static void checkRoleRemove(GuildMemberRoleRemoveEvent event)
    {
        if(!(Bot.getInstance().dataEnabled))
            return;

        Guild guild = event.getGuild();
        Role mutedRole = GuildUtils.getMutedRole(guild);

        if(!(event.getRoles().contains(mutedRole)))
            return;

        if(!(ChecksUtil.hasPermission(guild.getSelfMember(), null, Permission.VIEW_AUDIT_LOGS)))
            return;

        guild.getAuditLogs().type(ActionType.MEMBER_ROLE_UPDATE).limit(1).queue(entries -> {
            if(entries.isEmpty())
                return;

            ParsedAuditLog parsedAuditLog = GuildUtils.getAuditLog(entries.get(0), AuditLogKey.MEMBER_ROLES_REMOVE);
            if(parsedAuditLog==null)
                return;

            String reason = parsedAuditLog.getReason();
            User author = parsedAuditLog.getAuthor();
            User target = parsedAuditLog.getTarget();

            Bot.getInstance().modlog.logManual(Action.MANUAL_UNMUTE, guild, OffsetDateTime.now(), reason, author, target);
            Bot.getInstance().pdm.removePunishment(target.getIdLong(), guild.getIdLong(), Const.PunishmentType.MUTE);
            Bot.getInstance().pdm.removePunishment(target.getIdLong(), guild.getIdLong(), Const.PunishmentType.TEMPMUTE);
        }, e -> {});
    }

    public static void checkJoin(GuildMemberJoinEvent event)
    {
        if(!(Bot.getInstance().dataEnabled))
            return;

        Guild guild = event.getGuild();
        Punishment punishment = Bot.getInstance().pdm.getPunishment(event.getUser().getIdLong(), event.getGuild().getIdLong(), Const.PunishmentType.MUTE);
        TempPunishment tempPunishment = (TempPunishment)Bot.getInstance().pdm.getPunishment(event.getUser().getIdLong(), event.getGuild().getIdLong(), Const.PunishmentType.TEMPMUTE);

        if(!(punishment==null))
        {
            Role mutedRole = GuildUtils.getMutedRole(event.getGuild());

            if(!(mutedRole==null) && ChecksUtil.hasPermission(guild.getSelfMember(), null, Permission.MANAGE_ROLES) && ChecksUtil.canMemberInteract(guild.getSelfMember(), mutedRole))
                guild.getController().addSingleRoleToMember(event.getMember(), mutedRole).reason("[Mute restore]").queue(s -> {}, e -> {});
        }
        else if(!(tempPunishment==null))
        {
            Role mutedRole = GuildUtils.getMutedRole(event.getGuild());

            if(!(mutedRole==null) && ChecksUtil.hasPermission(guild.getSelfMember(), null, Permission.MANAGE_ROLES) && ChecksUtil.canMemberInteract(guild.getSelfMember(), mutedRole))
                event.getGuild().getController().addSingleRoleToMember(event.getMember(), mutedRole).reason("[Mute restore]").queue(s -> {}, e-> {});
        }
    }
}

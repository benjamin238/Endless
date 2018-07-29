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

package me.artuto.endless.commands.moderation;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Action;
import me.artuto.endless.Bot;
import me.artuto.endless.Endless;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.core.entities.PunishmentType;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Artuto
 */

public class MuteCmd extends EndlessCommand
{
    private final Bot bot;

    public MuteCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "mute";
        this.help = "Mutes the specified user";
        this.arguments = "<@user|ID|nickname|username> for [time] for [reason]";
        this.category = Categories.MODERATION;
        this.botPerms = new Permission[]{Permission.MANAGE_ROLES};
        this.userPerms = new Permission[]{Permission.MANAGE_ROLES};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        if(!(bot.dataEnabled))
        {
            event.replyError("Endless is running on No-data mode.");
            return;
        }

        int time;
        Role mutedRole;
        String[] args = ArgsUtils.splitWithReasonAndTime(2, event.getArgs(), " for ");
        time = Integer.valueOf(args[1]);
        String query = args[0];
        String reason = args[2];

        Member target = ArgsUtils.findMember(event, query);
        User author = event.getAuthor();
        if(target==null)
            return;

        if(!(ChecksUtil.canMemberInteract(event.getSelfMember(), target)))
        {
            event.replyError("I can't mute the specified user!");
            return;
        }
        if(!(ChecksUtil.canMemberInteract(event.getMember(), target)))
        {
            event.replyError("You can't mute the specified user!");
            return;
        }

        String username = "**"+target.getUser().getName()+"**#"+target.getUser().getDiscriminator();
        mutedRole = GuildUtils.getMutedRole(event.getGuild());
        int minutes = time/60;
        Instant unmuteTime = Instant.now().plus(minutes, ChronoUnit.MINUTES);
        PunishmentType type = time==0?PunishmentType.MUTE:PunishmentType.TEMPMUTE;
        if(time<0)
        {
            event.replyError("The time cannot be negative!");
            return;
        }
        else if(time==0)
            minutes = 0;
        else if(time>60)
            minutes = (int)Math.round(time/60.0);
        else
            minutes = 1;
        int fMins = minutes;

        if(mutedRole==null)
            event.replyError("No muted role set! Please set one using `e!config mutedrole <role>` or let the me create one for you using `e!setup mutedrole`");
        else
        {
            if(!(ChecksUtil.canMemberInteract(event.getSelfMember(), mutedRole)))
            {
                event.replyError("I can't interact with the Muted role!");
                return;
            }
            if(type==PunishmentType.MUTE && (!(bot.pdm.getPunishment(target.getUser().getIdLong(), event.getGuild().getIdLong(), PunishmentType.TEMPMUTE)==null)))
                bot.pdm.removePunishment(target.getUser().getIdLong(), event.getGuild().getIdLong(), PunishmentType.TEMPMUTE);
            else if(type==PunishmentType.TEMPMUTE && (!(bot.pdm.getPunishment(target.getUser().getIdLong(), event.getGuild().getIdLong(), PunishmentType.MUTE)==null)))
            {
                event.replyWarning("This user is already muted!");
                return;
            }
            else if(!(bot.pdm.getPunishment(target.getUser().getIdLong(), event.getGuild().getIdLong(), type)==null))
            {
                event.replyWarning("This user is already muted!");
                return;
            }

            event.getGuild().getController().addSingleRoleToMember(target, mutedRole).reason(author.getName()+"#"+author.getDiscriminator()+": "+reason)
                    .queue(s -> {
                        event.replySuccess(String.format("Successfully muted %s", username));
                        if(fMins>0)
                        {
                            bot.modlog.logTemp(Action.TEMP_MUTE, event, fMins, OffsetDateTime.now(), reason, target.getUser());
                            bot.pdm.addTempPunishment(target.getUser().getIdLong(), event.getGuild().getIdLong(), unmuteTime.toEpochMilli(),
                                    PunishmentType.TEMPMUTE);
                        }
                        else
                        {
                            bot.modlog.logGeneral(Action.MUTE, event, OffsetDateTime.now(), reason, target.getUser());
                            bot.pdm.addPunishment(target.getUser().getIdLong(), event.getGuild().getIdLong(), PunishmentType.MUTE);
                        }
            }, e -> {
                        event.replyError(String.format("An error happened when muting %s", username));
                        Endless.LOG.error("Could not mute user {} in guild {}", target.getUser().getId(), event.getGuild().getId(), e);
                    });
        }
    }
}

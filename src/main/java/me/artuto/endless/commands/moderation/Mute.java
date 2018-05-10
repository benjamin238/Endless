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
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Const;
import me.artuto.endless.Messages;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.data.GuildSettingsDataManager;
import me.artuto.endless.data.PunishmentsDataManager;
import me.artuto.endless.loader.Config;
import me.artuto.endless.logging.ModLogging;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author Artuto
 */

public class Mute extends EndlessCommand
{
    private final Config config;
    private final GuildSettingsDataManager gsdm;
    private final ModLogging modlog;
    private final PunishmentsDataManager tpdm;

    public Mute(Config config, GuildSettingsDataManager gsdm, ModLogging modlog, PunishmentsDataManager tpdm)
    {
        this.config = config;
        this.gsdm = gsdm;
        this.modlog = modlog;
        this.tpdm = tpdm;
        this.name = "mute";
        this.help = "Mutes the specified user";
        this.arguments = "<@user|ID|nickname|username> for [time] for [reason]";
        this.category = Categories.MODERATION;
        this.botPerms = new Permission[]{Permission.MANAGE_ROLES};
        this.userPerms = new Permission[]{Permission.MANAGE_ROLES};
        this.ownerCommand = false;
        this.guildCommand = true;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        Member member;
        User author = event.getAuthor();
        Role mutedRole;
        String[] args = event.getArgs().split(" for ", 2);
        String target;
        String reason = "";
        int time;

        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Invalid Syntax: "+event.getClient().getPrefix()+"mute <@user|ID|nickname|username> for [reason]");
            return;
        }

        try
        {
            target = args[0].trim();
            time = ArgsUtils.parseTime(args[1].trim());

            try
            {
                if(time==0) reason = args[1].trim();
                else reason = args[1].trim().split(" for ", 2)[1].trim();
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                reason = "[no reason specified]";
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            target = event.getArgs().trim();
            time = 0;
        }

        List<Member> list = FinderUtil.findMembers(target, event.getGuild());

        if(list.isEmpty())
        {
            event.replyWarning("I was not able to found a user with the provided arguments: '"+target+"'");
            return;
        }
        else if(list.size()>1)
        {
            event.replyWarning(FormatUtil.listOfMembers(list, target));
            return;
        }
        else member = list.get(0);

        if(!event.getSelfMember().canInteract(member))
        {
            event.replyError("I can't mute the specified user!");
            return;
        }

        if(!event.getMember().canInteract(member))
        {
            event.replyError("You can't mute the specified user!");
            return;
        }

        String username = "**"+member.getUser().getName()+"#"+member.getUser().getDiscriminator()+"**";
        mutedRole = GuildUtils.getMutedRole(event.getGuild());
        int fTime = time;
        String fReason = reason;
        Instant unmuteTime = Instant.now().plus(time, ChronoUnit.MINUTES);
        Const.PunishmentType type = time==0?Const.PunishmentType.MUTE:Const.PunishmentType.TEMPMUTE;

        if(mutedRole==null)
            event.replyError("No muted role set! Please set one using `e!config mutedrole <role>` or let the me create one for you using `e!setup mutedrole` ");
        else
        {
            if(!(event.getSelfMember().canInteract(mutedRole)))
            {
                event.replyError("I can't interact with the Muted role!");
                return;
            }

            if(!(tpdm.getPunishment(member.getUser().getIdLong(), event.getGuild().getIdLong(), type)==null))
            {
                event.replyWarning("This user is already muted!");
                return;
            }

            if(tpdm.addTempPunishment(member.getUser(), event.getGuild(), unmuteTime.getEpochSecond(), type))
            {
                event.getGuild().getController().addSingleRoleToMember(member, mutedRole).reason("["+author.getName()+"#"+author.getDiscriminator()+"]: "+reason).queue(s -> {
                    event.replySuccess(Messages.MUTE_SUCCESS+username);
                    if(fTime==0)
                        modlog.logMute(author, member, fReason, event.getGuild(), event.getTextChannel());
                    else
                        modlog.logTempMute(author, member, fReason, event.getGuild(), event.getTextChannel(), fTime);
                }, e -> event.replyError(Messages.MUTE_ERROR+username));
            }
            else
                event.replyError("Something has gone wrong while muting the specified user, please contact the bot owner.");
        }
    }
}

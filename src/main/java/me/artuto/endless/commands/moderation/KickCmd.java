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
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.ChecksUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.time.OffsetDateTime;

/**
 * @author Artuto
 */

public class KickCmd extends EndlessCommand
{
    private final Bot bot;

    public KickCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "kick";
        this.help = "Kicks the specified user";
        this.arguments = "<@user|ID|nickname|username> for [reason]";
        this.category = Categories.MODERATION;
        this.botPerms = new Permission[]{Permission.KICK_MEMBERS};
        this.userPerms = new Permission[]{Permission.KICK_MEMBERS};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        String[] args = ArgsUtils.splitWithReason(2, event.getArgs(), " for ");
        String query = args[0];
        String reason = args[1];

        Member target = ArgsUtils.findMember(event, query);
        User author = event.getAuthor();
        if(target==null)
            return;

        if(!(ChecksUtil.canMemberInteract(event.getSelfMember(), target)))
        {
            event.replyError("I can't kick the specified user!");
            return;
        }
        if(!(ChecksUtil.canMemberInteract(event.getMember(), target)))
        {
            event.replyError("You can't kick the specified user!");
            return;
        }

        String username = "**"+target.getUser().getName()+"**#"+target.getUser().getDiscriminator();
        event.getGuild().getController().kick(target).reason(author.getName()+"#"+author.getDiscriminator()+": "+reason).queue(s -> {
            event.replySuccess(String.format("Successfully kicked user %s", username));
            bot.modlog.logGeneral(Action.KICK, event, OffsetDateTime.now(), reason, target.getUser());
        }, e -> {
            event.replyError(String.format("An error happened when kicking %s", username));
            Endless.LOG.error("Could not kick user {} in guild {}", target.getUser().getId(), event.getGuild().getId(), e);
        });
    }
}

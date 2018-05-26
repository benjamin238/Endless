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
import me.artuto.endless.Bot;
import me.artuto.endless.Messages;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.utils.Checks;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

/**
 * @author Artuto
 */

public class SoftbanCmd extends EndlessCommand
{
    private final Bot bot;

    public SoftbanCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "softban";
        this.help = "Softbans the specified user";
        this.arguments = "<@user|ID|niokname|username> for [reason]";
        this.category = Categories.MODERATION;
        this.botPerms = new Permission[]{Permission.BAN_MEMBERS};
        this.userPerms = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        Member member;
        User author = event.getAuthor();
        String target;
        String reason;

        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Invalid Syntax: "+event.getClient().getPrefix()+"softban <@user|ID|nickname|username> for [reason]");
            return;
        }

        try
        {
            String[] args = event.getArgs().split(" for ", 2);
            target = args[0];
            reason = args[1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            target = event.getArgs();
            reason = "[no reason specified]";
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

        if(!(Checks.canMemberInteract(event.getSelfMember(), member)))
        {
            event.replyError("I can't softban the specified user!");
            return;
        }

        if(!(Checks.canMemberInteract(event.getMember(), member)))
        {
            event.replyError("You can't softban the specified user!");
            return;
        }

        String username = "**"+member.getUser().getName()+"#"+member.getUser().getDiscriminator()+"**";
        String fReason = reason;

        event.getGuild().getController().ban(member, 1).reason("[SOFTBAN - 1 DAY]["+author.getName()+"#"+author.getDiscriminator()+"]: "+reason).queue(s ->
                event.getGuild().getController().unban(member.getUser()).reason("[SOFTBAN - 1 DAY]["+author.getName()+"#"+author.getDiscriminator()+"]: "+fReason).queue(s2 -> {
            event.replySuccess(Messages.SOFTBAN_SUCCESS+username);
            bot.modlog.logSoftban(event.getAuthor(), member, fReason, event.getGuild(), event.getTextChannel());
        }, e -> event.replyError("Error while unbanning "+username)), e -> event.replyError(Messages.SOFTBAN_ERROR+username));
    }
}

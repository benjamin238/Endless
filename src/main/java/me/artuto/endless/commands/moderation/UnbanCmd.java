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
import me.artuto.endless.Action;
import me.artuto.endless.Bot;
import me.artuto.endless.Messages;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author Artuto
 */

public class UnbanCmd extends EndlessCommand
{
    private final Bot bot;

    public UnbanCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "unban";
        this.help = "Unbans the specified user";
        this.arguments = "<@user|ID|username> for [reason]";
        this.category = Categories.MODERATION;
        this.botPerms = new Permission[]{Permission.BAN_MEMBERS};
        this.userPerms = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        User author;
        User user;
        author = event.getAuthor();
        String target;
        String reason;

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

        List<User> list = FinderUtil.findBannedUsers(target, event.getGuild());

        if(list.isEmpty())
        {
            event.replyWarning("I was not able to found a user with the provided arguments: '"+target+"'");
            return;
        }
        else if(list.size()>1)
        {
            event.replyWarning(FormatUtil.listOfUsers(list, target));
            return;
        }
        else user = list.get(0);

        String username = "**"+user.getName()+"#"+user.getDiscriminator()+"**";
        String fReason = reason;

        event.getGuild().getController().unban(user).reason("["+author.getName()+"#"+author.getDiscriminator()+"]: "+reason).queue(s -> {
            event.replySuccess(Messages.UNBAN_SUCCESS+username);
            bot.modlog.logGeneral(Action.UNBAN, event, OffsetDateTime.now(), fReason, user);
        }, e -> event.replyError(Messages.UNBAN_ERROR+username));
    }
}

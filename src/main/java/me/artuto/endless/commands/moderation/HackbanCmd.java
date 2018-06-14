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
import me.artuto.endless.Bot;
import me.artuto.endless.Messages;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

public class HackbanCmd extends EndlessCommand
{
    private final Bot bot;

    public HackbanCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "hackban";
        this.help = "Hackbans the specified user";
        this.arguments = "<ID> for [reason]";
        this.category = Categories.MODERATION;
        this.botPerms = new Permission[]{Permission.BAN_MEMBERS};
        this.userPerms = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        User author = event.getAuthor();
        User user;
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

        try
        {
            user = event.getJDA().retrieveUserById(target).complete();
        }
        catch(Exception e)
        {
            event.replyError("That ID isn't valid!");
            return;
        }

        String username = "**"+user.getName()+"**#**"+user.getDiscriminator()+"**";
        String fReason = reason;

        if(event.getGuild().getMembers().contains(event.getGuild().getMemberById(target)))
            event.replyWarning("This user is on this Guild! Please use `"+event.getClient().getPrefix()+"ban` instead.");
        else
        {
            event.getGuild().getController().ban(user, bot.gsdm.getBanDeleteDays(event.getGuild())).reason("["+author.getName()+"#"+author.getDiscriminator()+"]: "+reason).queue(s -> {
                event.replySuccess(Messages.HACKBAN_SUCCESS+username);
                bot.modlog.logHackban(event.getAuthor(), user, fReason, event.getGuild(), event.getTextChannel());
            }, e -> event.replyError(Messages.HACKBAN_ERROR+username));
        }
    }
}

/*
 * Copyright (C) 2017 Artu
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

package me.artuto.endless.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import java.util.List;
import me.artuto.endless.Messages;
import me.artuto.endless.utils.FinderUtil2;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.utils.ModLogging;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;

/**
 *
 * @author Artu
 */

public class Unban extends Command
{
    public Unban()
    {
        this.name = "unban";
        this.help = "Unbans the specified user";
        this.arguments = "@user | ID | nickname | username";
        this.category = new Command.Category("Moderation");
        this.botPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.ownerCommand = false;
        this.guildOnly = true;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        Member member;
        User author;
        User user;
        author = event.getAuthor();
        String target;
        String reason;
        
        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Invalid Syntax: "+event.getClient().getPrefix()+"unban @user | ID | nickname | username for *reason*");
            return;
        }

        try
        {
            String[] args = event.getArgs().split(" for ");
            target = args[0];
            reason = args[1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            event.replyWarning("Invalid Syntax: "+event.getClient().getPrefix()+"unban @user | ID | nickname | username for *reason*");
            return;
        }
              
        List<User> list = FinderUtil2.findBannedUsers(target, event.getGuild());
            
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
    	else
        {
            user = list.get(0);
        }
        
        String success = user.getAsMention();
        
        try
        {
            event.getGuild().getController().unban(user).reason("["+author.getName()+"#"+author.getDiscriminator()+"]: "+reason).queue();
            event.replySuccess(Messages.UNBAN_SUCCESS+success);

            ModLogging.logUnban(event.getAuthor(), user, reason, event.getGuild(), event.getTextChannel(), event.getMessage());
        }
        catch(Exception e)
        {
            event.replyError(Messages.UNBAN_ERROR+user.getAsMention());
            SimpleLog.getLog("Unban").fatal(e);
            e.printStackTrace();
        }
    }
    
}

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

package me.artuto.endless.commands.moderation;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import java.util.List;
import me.artuto.endless.Messages;
import me.artuto.endless.utils.FinderUtil2;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

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
        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Invalid Syntax: "+event.getClient().getPrefix()+"unban @user | ID | nickname | username for *reason*");
            return;
        }
    
        String args = event.getArgs();
        String[] targetpre = args.split(" for ");
        String target = targetpre[0];
        String reason = targetpre[1];
        User user;
        User author;
        author = event.getAuthor();
        
        if(event.getArgs().isEmpty())
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
        
        String success = "**"+user.getName()+"#"+user.getDiscriminator()+"** with reason **"+reason+"**";
        
        try
        {
            event.getGuild().getController().unban(user).reason("["+author.getName()+"#"+author.getDiscriminator()+"]: "+reason).queue();
            event.replySuccess(Messages.UNBAN_SUCCESS+success);
        }
        catch(Exception e)
        {
            event.replyError(Messages.UNBAN_ERROR+user.getName()+"#"+user.getDiscriminator()+"**");
        }
    }
    
}

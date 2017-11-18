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
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.loader.Config;
import me.artuto.endless.utils.FinderUtil;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.logging.ModLogging;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artu
 */

public class Unban extends Command
{
    private final ModLogging modlog;
    private final Config config;

    public Unban(ModLogging modlog, Config config)
    {
        this.modlog = modlog;
        this.config = config;
        this.name = "unban";
        this.help = "Unbans the specified user";
        this.arguments = "<@user|ID|username> for [reason]";
        this.category = Categories.MODERATION;
        this.botPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.ownerCommand = false;
        this.guildOnly = true;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
        User author;
        User user;
        author = event.getAuthor();
        String target;
        String reason;
        
        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Invalid Syntax: "+event.getClient().getPrefix()+"unban <@user|ID|username> for [reason]");
            return;
        }

        try
        {
            String[] args = event.getArgs().split(" for", 2);
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
    	else
            user = list.get(0);
        
        String username = "**"+user.getName()+"#"+user.getDiscriminator()+"**";
        
        try
        {
            event.getGuild().getController().unban(user).reason("["+author.getName()+"#"+author.getDiscriminator()+"]: "+reason).complete();
            event.replySuccess(Messages.UNBAN_SUCCESS+username);
            modlog.logUnban(event.getAuthor(), user, reason, event.getGuild(), event.getTextChannel());
        }
        catch(Exception e)
        {
            event.replyError(Messages.UNBAN_ERROR+username);
            LoggerFactory.getLogger("Unban Command").error(e.toString());
            if(config.isDebugEnabled())
                e.printStackTrace();
        }
    }
    
}

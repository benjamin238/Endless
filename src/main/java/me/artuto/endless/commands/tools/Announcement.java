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

package me.artuto.endless.commands.tools;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

/**
 * @author Artuto
 */

public class Announcement extends EndlessCommand
{
    public Announcement()
    {
        this.name = "announcement";
        this.help = "Sends an announcement to the desired channel";
        this.arguments = "<message> | [channel] | [role to ping|everyone|here]";
        this.aliases = new String[]{"announce"};
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MANAGE_ROLES};
        this.userPerms = new Permission[]{Permission.MANAGE_ROLES, Permission.MANAGE_SERVER};
        this.ownerCommand = false;
        this.guildCommand = true;
    }

     @Override
     public void executeCommand(CommandEvent event)
     {
         String args = event.getArgs();

         if(args.isEmpty())
         {
             event.replyWarning("Invalid Syntax! e!announcement "+arguments+"\nDon't forget the pipes (|)!");
             return;
         }

         boolean ping = true;
         Role role;
         String[] splittedArgs = args.split(" \\| ", 2);
         String[] splittedArgs2;
         String message;
         String preChannel;
         String preRole;
         TextChannel tc;

         try
         {
             message = splittedArgs[0].trim();
             splittedArgs2 = splittedArgs[1].trim().trim().split(" \\| ", 2);
             try
             {
                 preChannel = splittedArgs2[0].trim();

                 List<TextChannel> tcList = FinderUtil.findTextChannels(preChannel, event.getGuild());
                 if(tcList.isEmpty())
                 {
                     event.replyWarning("I was not able to found a text channel with the provided arguments: '"+preChannel+"'");
                     return;
                 }
                 else if(tcList.size()>1)
                 {
                     event.replyWarning(FormatUtil.listOfTcChannels(tcList, event.getArgs()));
                     return;
                 }
                 else tc = tcList.get(0);
             }
             catch(ArrayIndexOutOfBoundsException e)
             {
                 tc = null;
             }

             try
             {
                 preRole = splittedArgs2[1].trim();

                 List<Role> rList = FinderUtil.findRoles(preRole, event.getGuild());
                 if(rList.isEmpty())
                 {
                     event.replyWarning("I was not able to found a role with the provided arguments: '"+preRole+"'");
                     return;
                 }
                 else if(rList.size()>1)
                 {
                     event.replyWarning(FormatUtil.listOfRoles(rList, event.getArgs()));
                     return;
                 }
                 else role = rList.get(0);
             }
             catch(ArrayIndexOutOfBoundsException e)
             {
                 role = null;
             }
         }
         catch(ArrayIndexOutOfBoundsException e)
         {
             message = splittedArgs[0].trim();
             role = null;
             tc = null;
         }

         if(tc==null)
             tc = event.getTextChannel();
         if(role==null)
             ping = false;

         if(!(tc.canTalk()))
         {
             event.replyError("I can't talk in the specified channel!");
             return;
         }

         if(!(tc.canTalk(event.getMember())))
         {
             event.replyError("You can't talk in the specified channel!");
             return;
         }

         if(ping)
         {
             if(!(event.getSelfMember().canInteract(role)))
             {
                 event.replyError("I can't interact with the specified role!");
                 return;
             }

             if(!(event.getMember().canInteract(role)))
             {
                 event.replyError("You can't interact with the specified role!");
                 return;
             }

             if(role.isPublicRole() && !(event.getSelfMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)))
             {
                 event.replyError("I can't mention everyone!");
                 return;
             }

             if(role.isPublicRole() && !(event.getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)))
             {
                 event.replyError("You can't mention everyone!");
                 return;
             }

             if(!(role.isMentionable()))
             {
                 Role fRole = role;
                 String fMessage = message;
                 TextChannel fTc = tc;

                 role.getManager().setMentionable(true).queue(s -> fTc.sendMessage(fRole.getAsMention()+" "+fMessage).queue(s2 -> fRole.getManager().setMentionable(false).queue(s3 ->
                         event.replySuccess("Successfully sent the announcement!"),
                         e -> event.replyError("Error while setting the role back to no mentionable!")),
                         e -> event.replyError("Error while sending the announcement!")), e -> event.replyError("Error while setting the role to mentionable!"));
             }
             else
             {
                 tc.sendMessage(role.getAsMention()+" "+message).queue(s -> event.replySuccess("Successfully sent the announcement!"), e ->
                 event.replyError("Error while sending the announcement!"));
             }
         }
		 else
		 {
			tc.sendMessage(message).queue(s -> event.replySuccess("Successfully sent the announcement!"), e ->
                 event.replyError("Error while sending the announcement!"));
		 }
     }
}

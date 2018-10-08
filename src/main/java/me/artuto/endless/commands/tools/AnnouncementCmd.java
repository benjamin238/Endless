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

import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author Artuto
 */

public class AnnouncementCmd extends EndlessCommand
{
    public AnnouncementCmd()
    {
        this.name = "announcement";
        this.help = "Sends an announcement to the desired channel";
        this.arguments = "<message> | [channel] | [role to ping|everyone|here]";
        this.aliases = new String[]{"announce"};
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MANAGE_ROLES};
        this.userPerms = new Permission[]{Permission.MANAGE_ROLES, Permission.MANAGE_SERVER};
    }

     @Override
     public void executeCommand(EndlessCommandEvent event)
     {
         String args = event.getArgs();

         boolean ping;
         Role role;
         String[] splittedArgs = args.split(" \\| ", 2);
         String[] splittedArgs2;
         String message;
         String channelQuery;
         String roleQuery;
         TextChannel tc;

         try
         {
             message = splittedArgs[0].trim();
             splittedArgs2 = splittedArgs[1].trim().split(" \\| ", 2);
             try
             {
                 channelQuery = splittedArgs2[0].trim();
                 tc = ArgsUtils.findTextChannel(event, channelQuery);
                 if(tc==null)
                     return;
             }
             catch(ArrayIndexOutOfBoundsException e) {tc = null;}

             try
             {
                 roleQuery = splittedArgs2[1].trim();
                 role = ArgsUtils.findRole(event, roleQuery);
                 if(role==null)
                     return;
             }
             catch(ArrayIndexOutOfBoundsException e) {role = null;}
         }
         catch(ArrayIndexOutOfBoundsException e)
         {
             message = splittedArgs[0].trim();
             role = null;
             tc = null;
         }

         tc = tc==null?event.getTextChannel():tc;
         ping = !(role==null);

         if(!(tc.canTalk()))
         {
             event.replyError("command.announcement.cantTalk.bot");
             return;
         }
         if(!(tc.canTalk(event.getMember())))
         {
             event.replyError("command.announcement.cantTalk.executor");
             return;
         }

         if(ping)
         {
             if(!(ChecksUtil.canMemberInteract(event.getSelfMember(), role)))
             {
                 event.replyError("core.error.cantInteract.role.bot");
                 return;
             }
             if(!(ChecksUtil.canMemberInteract(event.getMember(), role)))
             {
                 event.replyError("core.error.cantInteract.role.executor");
                 return;
             }

             if(role.isPublicRole() && !(ChecksUtil.hasPermission(event.getSelfMember(), tc, Permission.MESSAGE_MENTION_EVERYONE)))
             {
                 event.replyError("command.announcement.cantPing.bot");
                 return;
             }
             if(role.isPublicRole() && !(ChecksUtil.hasPermission(event.getMember(), tc, Permission.MESSAGE_MENTION_EVERYONE)))
             {
                 event.replyError("command.announcement.cantPing.executor");
                 return;
             }

             if(!(role.isMentionable()))
             {
                 Role fRole = role;
                 String fMessage = message;
                 TextChannel fTc = tc;

                 role.getManager().setMentionable(true).queue(s -> fTc.sendMessage(fRole.getAsMention()+" "+fMessage)
                         .queue(s2 -> fRole.getManager().setMentionable(false).queue(s3 ->
                                         event.replySuccess("command.announcement.success"),
                                 e -> event.replyError("command.announcement.error.noMentionable")),
                                 e -> event.replyError("command.announcement.error.sending")),
                         e -> event.replyError("command.announcement.error.mentionable"));
             }
             else
             {
                 tc.sendMessage(role.getAsMention()+" "+FormatUtil.sanitize(message))
                         .queue(s -> event.replySuccess("command.announcement.success"), e ->
                                 event.replyError("command.announcement.error.sending"));
             }
         }
		 else
		 {
			tc.sendMessage(FormatUtil.sanitize(message))
                    .queue(s -> event.replySuccess("command.announcement.success"), e ->
                            event.replyError("command.announcement.error.sending"));
		 }
     }
}

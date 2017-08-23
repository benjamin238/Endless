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
import com.jagrosh.jdautilities.utils.FinderUtil;
import java.awt.Color;
import java.time.Instant;
import java.util.List;

import me.artuto.endless.Messages;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.utils.ModLogging;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.SimpleLog;

/**
 *
 * @author Artu
 */

public class Ban extends Command
{
    public Ban()
    {
        this.name = "ban";
        this.help = "Bans the specified user";
        this.arguments = "@user | ID | nickname | username";
        this.category = new Command.Category("Moderation");
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
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
            event.replyWarning("Invalid Syntax: "+event.getClient().getPrefix()+"ban @user | ID | nickname | username for *reason*");
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
            event.replyWarning("Invalid Syntax: "+event.getClient().getPrefix()+"ban @user | ID | nickname | username for *reason*");
            return;
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
    	else
        {
            member = list.get(0);
        }
    
        if(!event.getSelfMember().canInteract(member))
        {
            event.replyError("I can't ban the specified user!");
            return;
        }
        
        if(!event.getMember().canInteract(member))
        {
            event.replyError("You can't ban the specified user!");
            return;
        }
        
        String success = member.getAsMention();
              
        try
        {
            builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
            builder.setTitle("Ban");
            builder.setDescription("You were banned on the guild **"+event.getGuild().getName()+"** by **"
                +event.getAuthor().getName()+"#"+event.getAuthor().getDiscriminator()+"**\n"
                + "They gave the following reason: **"+reason+"**\n");
            builder.setFooter("Time", null);
            builder.setTimestamp(Instant.now());
            builder.setColor(Color.RED);
            builder.setThumbnail(event.getGuild().getIconUrl());
           
            if(!member.getUser().isBot())
            {
               member.getUser().openPrivateChannel().queue(s -> s.sendMessage(new MessageBuilder().setEmbed(builder.build()).build()).queue(
                    (d) -> event.replySuccess(Messages.KICK_SUCCESS+success),
                    (e) -> event.replyWarning(Messages.KICK_NODM+success)));
            }
            else
            {
               event.replySuccess(Messages.KICK_SUCCESS+success);
            }
            
            event.getGuild().getController().ban(member, 0).reason("["+author.getName()+"#"+author.getDiscriminator()+"]: "+reason).queue();

            ModLogging.logBan(event.getAuthor(), member, reason, event.getGuild(), event.getTextChannel(), event.getMessage());
        }
        catch(Exception e)
        {
            event.replyError(Messages.BAN_ERROR+member.getUser().getName()+"#"+member.getUser().getDiscriminator()+"**");
            SimpleLog.getLog("Ban").fatal(e);
            e.printStackTrace();
        }
    }
}

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
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.logging.ModLogging;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;

/**
 *
 * @author Artu
 */

public class Kick extends Command
{
    private final ModLogging modlog;

    public Kick(ModLogging modlog)
    {
        this.modlog = modlog;
        this.name = "kick";
        this.help = "Kicks the specified user";
        this.arguments = "<@user|ID|nickname|username> for [reason]";
        this.category = Categories.MODERATION;
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
        author = event.getAuthor();
        String target;
        String reason;
        
        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Invalid Syntax: "+event.getClient().getPrefix()+"kick <@user|ID|nickname|username> for [reason]");
            return;
        }

        try
        {
            String[] args = event.getArgs().split(" for", 2);
            target = args[0].trim();
            reason = args[1].trim();
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
    	else
        {
            member = list.get(0);
        }       
    
        if(!event.getSelfMember().canInteract(member))
        {
            event.replyError("I can't kick the specified user!");
            return;
        }
        
        if(!event.getMember().canInteract(member))
        {
            event.replyError("You can't kick the specified user!");
            return;
        }
        
        String success = "**"+member.getUser().getName()+"#"+member.getUser().getDiscriminator()+"**";
        
        try
        {
            builder.setColor(Color.YELLOW);
            builder.setThumbnail(event.getGuild().getIconUrl());
            builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
            builder.setTitle("Kick");
            builder.setDescription("You were kicked on the guild **"+event.getGuild().getName()+"** by **"
                    +event.getAuthor().getName()+"#"+event.getAuthor().getDiscriminator()+"**\n"
                    + "They gave the following reason: **"+reason+"**\n");
            builder.setFooter("Time", null);
            builder.setTimestamp(Instant.now());
            
            if(!member.getUser().isBot())
            {
               member.getUser().openPrivateChannel().queue(s -> s.sendMessage(new MessageBuilder().setEmbed(builder.build()).build()).queue(
                    (d) -> event.replySuccess(Messages.KICK_SUCCESS+success),
                    (e) -> event.replyWarning(Messages.KICK_NODM+success)));
            }
            else
            {
               event.replySuccess(Messages.KICK_SUCCESS+"**"+member.getUser().getName()+"#"+member.getUser().getDiscriminator()+"**");
            }
            
           event.getGuild().getController().kick(member).reason("["+author.getName()+"#"+author.getDiscriminator()+"]: "+reason).queue();

           modlog.logKick(event.getAuthor(), member, reason, event.getGuild(), event.getTextChannel());
        }
        catch(Exception e)
        {
            event.replyError(Messages.KICK_ERROR+member.getAsMention());
            SimpleLog.getLog("Kick").fatal(e);
            e.printStackTrace();
        }
    }
}

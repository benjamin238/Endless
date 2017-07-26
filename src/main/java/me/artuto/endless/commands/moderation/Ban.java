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
import java.awt.Color;
import java.time.Instant;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

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
        this.arguments = "@user or ID";
        this.category = new Command.Category("Moderation");
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.ownerCommand = false;
        this.guildOnly = true;
    }
    
    @Override
    protected void execute(CommandEvent event) throws ErrorResponseException
    {
        EmbedBuilder builder = new EmbedBuilder();
        String reason = event.getArgs().replaceAll("<@!?\\d+>", "");
        Member member;
        if(event.getMessage().getMentionedUsers().isEmpty())
        {
            try
    	    {
    		member = event.getGuild().getMemberById(event.getArgs());
    	    } 
            catch(Exception e)    		
    	   {
    		member = null;
    	    }
    	}
       
    	else
        {
            member = event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0));
        }
    	
    	if(member==null)
    	{
    		event.reply(event.getClient().getError()+" I wasn't able to find the user "+event.getArgs());
    		return;
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
        
        try
        {
            event.getGuild().getController().ban(member, 0).reason(reason).queue();
            event.replySuccess("Banned user **"+member.getUser().getName()+"#"+member.getUser().getDiscriminator()+"** with reason **"+reason+"**");
            
            builder.setColor(Color.RED);
            builder.setThumbnail(event.getGuild().getIconUrl());
            builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
            builder.setTitle("Ban");
            builder.setDescription("You were banned on the guild **"+event.getGuild().getName()+"** by **"
                +event.getAuthor().getName()+"#"+event.getAuthor().getDiscriminator()+"**\n"
                + "They gave the following reason: **"+reason+"**\n");
            builder.setFooter("Time", null);
            builder.setTimestamp(Instant.now());
           
            member.getUser().openPrivateChannel().queue(s -> s.sendMessage(new MessageBuilder().setEmbed(builder.build()).build()).queue(null, (e) -> 
                   event.replyWarning("I was not able to DM the user due they has DM on Mutual Guilds off!")));
                
            }
            catch(Exception e)
            {
                   
            }  
        }
}

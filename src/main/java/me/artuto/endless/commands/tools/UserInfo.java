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

package me.artuto.endless.commands.tools;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;

/**
 *
 * @author Artu
 */

public class UserInfo extends Command
{
    public UserInfo()
    {
        this.name = "user";
        this.help = "Shows info about the specified user";
        this.arguments= "@user or ID";
        this.category = new Command.Category("Tools");
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = true;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
    	EmbedBuilder builder = new EmbedBuilder();
    	Member member;
        
        if(event.getArgs().isEmpty())
        {
            member = event.getMessage().getMember();
        }
        else
        {
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
        }
        
        Color color;
        
        if(event.isFromType(ChannelType.PRIVATE))
        {
            color = Color.decode("#33ff00");
        }
        else
        {
            color = event.getGuild().getSelfMember().getColor();
        }
    	    	
        String title=(member.getUser().isBot()?":information_source: Information about the bot **"+member.getUser().getName()+"**"+"#"+"**"+member.getUser().getDiscriminator()+"**":":information_source: Information about the user **"+member.getUser().getName()+"**"+"#"+"**"+member.getUser().getDiscriminator()+"**");
        
        StringBuilder rolesbldr = new StringBuilder();
        member.getRoles().forEach(r -> rolesbldr.append(" ").append(r.getAsMention()));
        		
        builder.addField(":1234: ID: ", "**"+member.getUser().getId()+"**", true);
    	builder.addField(":busts_in_silhouette: Nickname: ", (member.getNickname()==null ? "None" : "**"+member.getNickname()+"**"), true);
    	builder.addField(":hammer: Roles: ", rolesbldr.toString(), false);
    	builder.addField("<:online:334859814410911745> Status: ", "**"+member.getOnlineStatus().name()+"**"+(member.getGame()==null?"":" ("
      		        + (member.getGame().getType()==Game.GameType.TWITCH?"On Live at [*"+member.getGame().getName()+"*]"
	                    : "Playing **"+member.getGame().getName()+"**")+")"+""), false);
    	builder.addField(":calendar_spiral: Account Creation Date: ", "**"+member.getUser().getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", true);
    	builder.addField(":calendar_spiral: Guild Join Date: ", "**"+member.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", true);    
	builder.setThumbnail(member.getUser().getEffectiveAvatarUrl());
    	builder.setColor(color);
        event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();    		   
    }
}

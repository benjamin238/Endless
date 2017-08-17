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
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import me.artuto.endless.utils.FormatUtil;

/**
 *
 * @author Artu
 */

public class UserInfo extends Command
{
    public UserInfo()
    {
        this.name = "user";
        this.aliases = new String[]{"member", "userinfo", "i", "info", "memberinfo", "whois"};
        this.help = "Shows info about the specified user";
        this.arguments= "@user | ID | nickname | username";
        this.category = new Command.Category("Tools");
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = true;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
        String ranks = null;
        String roles = null;
        String emote = null;
        String status = null;
    	EmbedBuilder builder = new EmbedBuilder();
    	Member member = null;
                
        if(event.getArgs().isEmpty())
        {
            member = event.getMessage().getMember();
        }
        else
        {
            List<Member> list = FinderUtil.findMembers(event.getArgs(), event.getGuild());
            
            if(list.isEmpty())
            {
                event.replyWarning("I was not able to found a user with the provided arguments: '"+event.getArgs()+"'");
                return;
            }
            else if(list.size()>1)
            {
                event.replyWarning(FormatUtil.listOfMembers(list, event.getArgs()));
                return;
            }
    	    else
            {
                member = list.get(0);
            }
        }
        
        StringBuilder rolesbldr = new StringBuilder();
        member.getRoles().forEach(r -> rolesbldr.append(" ").append(r.getAsMention()));
              
        if(member.getOnlineStatus().toString().equals("ONLINE"))
        {
            emote = "<:online:334859814410911745>";
            status = "Online ";
        }
        else if(member.getOnlineStatus().toString().equals("IDLE"))
        {
            emote = "<:away:334859813869584384>";
            status = "Away ";
        }
        else if(member.getOnlineStatus().toString().equals("DO_NOT_DISTURB"))
        {
            emote = "<:dnd:334859814029099008>";
            status = "Do Not Disturb ";
        }
        else if(member.getOnlineStatus().toString().equals("INVISIBLE"))
        {
            emote = "<:invisible:334859814410649601>";
            status = "Invisible ";
        }
        else if(member.getOnlineStatus().toString().equals("OFFLINE"))
        {
            emote = "<:offline:334859814423232514>";
            status = "Offline ";
        }
        else if(member.getOnlineStatus().toString().equals("UNKNOWN"))
        {
            emote = ":interrobang:";
            status = "Unknown ";
        }
        
        if(rolesbldr.toString().isEmpty())
        {
            roles = "None";
        }
        else
        {
            roles = rolesbldr.toString();
        }

        if(!(member.getUser().getAvatarId()==null))
        {
            if(member.getUser().getAvatarId().startsWith("a_"))
            {
                ranks = "<:nitro:334859814566101004>";
            }
            else
            {
                ranks = "";
            }
        }

        String title=(member.getUser().isBot()?":information_source: Information about the bot **"+member.getUser().getName()+"**"+"#"+"**"+member.getUser().getDiscriminator()+"** <:bot:334859813915983872>":":information_source: Information about the user **"+member.getUser().getName()+"**"+"#"+"**"+member.getUser().getDiscriminator()+"** "+ranks);
        
        try
        {	
            builder.addField(":1234: ID: ", "**"+member.getUser().getId()+"**", true);
    	    builder.addField(":bust_in_silhouette: Nickname: ", (member.getNickname()==null ? "None" : "**"+member.getNickname()+"**"), true);
    	    builder.addField(":hammer: Roles: ", roles, false);
    	    builder.addField(emote+" Status: ", status+(member.getGame()==null?"":" ("
      	       	            + (member.getGame().getType()==Game.GameType.TWITCH?"On Live at [*"+member.getGame().getName()+"*]"
	                        : "Playing **"+member.getGame().getName()+"**")+")"+""), true);
    	    builder.addField(":calendar_spiral: Account Creation Date: ", "**"+member.getUser().getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", true);
    	    builder.addField(":calendar_spiral: Guild Join Date: ", "**"+member.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", true);    
	        builder.setThumbnail(member.getUser().getEffectiveAvatarUrl());
    	    builder.setColor(member.getColor());
            event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue(); 
        }
    	catch(Exception e)
        {
            event.replyError("Something went wrong when getting the role info: \n```"+e+"```");
        }   		   
    }
}
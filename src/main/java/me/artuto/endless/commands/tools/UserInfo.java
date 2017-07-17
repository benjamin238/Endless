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
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
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
    	if(event.getMessage().getMentionedUsers().isEmpty())
    	{
    		try
    		{
    			member = event.getGuild().getMemberById(event.getArgs());
    		} catch(Exception e) 
    		    		    		
    		{
    			member = null;
    		}
    	}
    	else
    		member = event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0));
    	if(member==null)
    	{
    		event.reply(event.getClient().getError()+" I wasn't able to find the user "+event.getArgs());
    		return;
    	}
    	
        String roles="";
        roles = member.getRoles().stream().map((rol) -> rol.getName()).filter((r) -> (!r.equalsIgnoreCase("@everyone"))).map((r) -> "`, `"+r).reduce(roles, String::concat);
    	
        String title=(member.getUser().isBot()?":information_source: Information about the bot **"+member.getUser().getName()+"**"+"#"+"**"+member.getUser().getDiscriminator()+"**":":information_source: Information about the user **"+member.getUser().getName()+"**"+"#"+"**"+member.getUser().getDiscriminator()+"**");
        		
    	if(roles.isEmpty())
    		roles="None";
    	else
    		roles=roles.substring(3)+"`";
               builder.addField(":1234: ID: ", "**"+member.getUser().getId()+"**", false);
    	       builder.addField(":busts_in_silhouette: Nickname: ", (member.getNickname()==null ? "None" : "**"+member.getNickname()+"**"), false);
    	       builder.addField(":hammer: Roles: ", roles, false);
    	       builder.addField("<:online:313956277808005120> Status: ", "**"+member.getOnlineStatus().name()+"**"+(member.getGame()==null?"":" ("
      				+ (member.getGame().getType()==Game.GameType.TWITCH?"On Live at [*"+member.getGame().getName()+"*]"
					   : "Playing **"+member.getGame().getName()+"**")+")"+""), false);
    	       builder.addField(":calendar_spiral: Account Creation Date: ", "**"+member.getUser().getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", false);
    	       builder.addField(":calendar_spiral: Guild Join Date: ", "**"+member.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", false);    
	       builder.setThumbnail(member.getUser().getEffectiveAvatarUrl());
    	       builder.setColor(member.getColor());
               event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();    		   
    }
}

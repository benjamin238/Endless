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
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

/**
 *
 * @author Artu
 */

public class GuildInfo extends Command
{
    public GuildInfo()
    {
        this.name = "guild";
        this.help = "Shows info about the current guild";
        this.category = new Command.Category("Tools");
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = true;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
    	Guild guild;
    	guild = event.getGuild();
       
        Member owner;        
        owner = guild.getOwner();
        
        String roles="";
        roles = guild.getRoles().stream().map((rol) -> rol.getName()).filter((r) -> (!r.equalsIgnoreCase("@everyone"))).map((r) -> "`, `"+r).reduce(roles, String::concat);
    	
    	String title =":information_source: Information about the guild **"+guild.getName()+"**";
        
    	if(roles.isEmpty())
    		roles="@everyone";
    	else
    		roles=roles.substring(3)+"`";
        
    	EmbedBuilder builder = new EmbedBuilder();
        builder.addField(":1234: ID: ", "**"+guild.getId()+"**", false);
        builder.addField(":bust_in_silhouette: Owner: ", "**"+owner.getUser().getName()+"**#**"+owner.getUser().getDiscriminator()+"**", false);
        builder.addField(":map: Region: ", "**"+guild.getRegion()+"**", false);
        builder.addField(":one: User count: ", "**"+guild.getMembers().size()+"**", false);
        builder.addField(":hammer: Roles: ", roles, false);
        builder.addField(":speech_left: Text Channels: ", "**"+guild.getTextChannels().size()+"**", true);
        builder.addField(":speaker: Voice Channels: ", "**"+guild.getVoiceChannels().size()+"**", true);
        builder.addField(":speech_balloon: Default Channel: ", "**"+guild.getPublicChannel().getName()+"**", true);
        builder.addField(":date: Creation Date: ", "**"+guild.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", true);
        builder.addField(":vertical_traffic_light: Verification level: ", "**"+guild.getVerificationLevel()+"**", true);
        builder.addField(":envelope: Default Notification level: ", "**"+guild.getDefaultNotificationLevel()+"**", true);
        builder.addField(":wrench: Explicit Content Filter level: ", "**"+guild.getExplicitContentLevel()+"**", true);
    	builder.setThumbnail(guild.getIconUrl());
        builder.setColor(guild.getSelfMember().getColor());
    	event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();
    }
}

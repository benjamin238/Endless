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
import net.dv8tion.jda.core.entities.User;

/**
 *
 * @author Artu
 */

public class GuildInfo extends Command
{
    public GuildInfo()
    {
        this.name = "guild";
        this.aliases = new String[]{"server", "serverinfo", "guildinfo", "ginfo", "sinfo"};
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
    	String title =":information_source: Information about the guild **"+guild.getName()+"**";
        
        long botCount = guild.getMembers().stream().filter(u -> u.getUser().isBot()).count();
        
    	EmbedBuilder builder = new EmbedBuilder();
        builder.addField(":1234: ID: ", "**"+guild.getId()+"**", true);
        builder.addField(":bust_in_silhouette: Owner: ", "**"+owner.getUser().getName()+"**#**"+owner.getUser().getDiscriminator()+"**", true);
        builder.addField(":map: Region: ", "**"+guild.getRegion()+"**", true);
        builder.addField(":one: User count: ", "**"+guild.getMembers().size()+"** (**"+botCount+"** bots)", true);

    	builder.setThumbnail(guild.getIconUrl());
        builder.setColor(guild.getSelfMember().getColor());
    	event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();
    }
}

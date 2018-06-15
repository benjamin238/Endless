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

import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.utils.FinderUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.time.format.DateTimeFormatter;

/**
 * @author Artuto
 */

public class GuildInfoCmd extends EndlessCommand
{
    public GuildInfoCmd()
    {
        this.name = "guild";
        this.aliases = new String[]{"server", "serverinfo", "guildinfo", "ginfo", "sinfo"};
        this.help = "Shows info about the current guild";
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        String roles;
        String voicech;
        Guild guild = event.getGuild();
        Member owner;
        owner = guild.getOwner();
        String title = ":information_source: Information about the guild **"+guild.getName()+"**";
        long botCount = guild.getMembers().stream().filter(u -> u.getUser().isBot()).count();

        StringBuilder rolesbldr = new StringBuilder();
        guild.getRoles().forEach(r -> rolesbldr.append(" ").append(r.getAsMention()));

        StringBuilder textchbldr = new StringBuilder();
        guild.getTextChannels().forEach(tc -> textchbldr.append(" ").append(tc.getAsMention()));

        StringBuilder voicechbldr = new StringBuilder();
        guild.getVoiceChannels().forEach(vc -> voicechbldr.append(" ").append(vc.getName()));

        if(rolesbldr.toString().isEmpty())
            roles = "**None**";
        else
            roles = rolesbldr.toString();

        if(voicechbldr.toString().isEmpty())
            voicech = "**None**";
        else
            voicech = voicechbldr.toString();

        builder.addField(":1234: ID: ", "**"+guild.getId()+"**", true);
        builder.addField(":bust_in_silhouette: Owner: ", "**"+owner.getUser().getName()+"**#**"+owner.getUser().getDiscriminator()+"**", true);
        builder.addField(":map: Region: ", "**"+guild.getRegion()+"**", true);
        builder.addField(":one: User count: ", "**"+guild.getMembers().size()+"** (**"+botCount+"** bots)", true);
        builder.addField(":hammer: Roles: ", roles, false);
        builder.addField(":speech_left: Text Channels: ", textchbldr.toString(), false);
        builder.addField(":speaker: Voice Channels: ", voicech, false);
        builder.addField(":speech_balloon: Default Channel: ", FinderUtil.getDefaultChannel(guild).getAsMention(), true);
        builder.addField(":date: Creation Date: ", "**"+guild.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", true);
        builder.addField(":vertical_traffic_light: Verification level: ", "**"+guild.getVerificationLevel()+"**", true);
        builder.addField(":envelope: Default Notification level: ", "**"+guild.getDefaultNotificationLevel()+"**", true);
        builder.addField(":wrench: Explicit Content Filter level: ", "**"+guild.getExplicitContentLevel()+"**", true);
        builder.setThumbnail(guild.getIconUrl());
        builder.setColor(guild.getSelfMember().getColor());
        event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();
    }
}

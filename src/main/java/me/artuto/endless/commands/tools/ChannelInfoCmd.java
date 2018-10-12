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

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Const;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * @author Artuto
 */

public class ChannelInfoCmd extends EndlessCommand
{
    public ChannelInfoCmd()
    {
        this.name = "channel";
        this.aliases = new String[]{"textchannel", "voicechannel", "tcinfo", "vcinfo", "category", "channelinfo"};
        this.help = "Shows info about the specified channel or category";
        this.arguments = "<channel|category>";
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        Channel channel;
        if(event.getArgs().isEmpty())
            channel = event.getTextChannel();
        else
            channel = ArgsUtils.findChannel(event, event.getArgs());
        Guild guild = event.getGuild();
        if(channel==null)
            return;

        EmbedBuilder eb = new EmbedBuilder();
        MessageBuilder mb = new MessageBuilder();
        StringBuilder sb = new StringBuilder();

        int position;
        if(channel instanceof TextChannel)
            position = guild.getTextChannels().indexOf(channel);
        else if(channel instanceof VoiceChannel)
            position = guild.getVoiceChannels().indexOf(channel);
        else
            position = guild.getCategories().indexOf(channel);

        sb.append(Const.LINE_START).append(" ID: **").append(channel.getId()).append("**\n");
        sb.append(Const.LINE_START).append(" Position: **").append(position).append("**\n");
        sb.append(Const.LINE_START).append(" Creation: **").append(channel.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**\n");
        sb.append(Const.LINE_START).append(" Users: **").append(channel.getMembers().size());
        if(channel instanceof VoiceChannel)
        {
            VoiceChannel vc = (VoiceChannel)channel;
            sb.append("/").append(vc.getUserLimit()==0?"Unlimited":vc.getUserLimit()).append("**\n");
            sb.append(Const.LINE_START).append(" Bitrate: **").append(vc.getBitrate()).append("**kbps\n");
        }
        else
            sb.append("**\n");

        if(channel instanceof TextChannel)
        {
            TextChannel tc = (TextChannel)channel;
            if(!(tc.getTopic()==null || tc.getTopic().isEmpty()))
                eb.addField("Topic:", tc.getTopic(), false);
        }
        else if(channel instanceof net.dv8tion.jda.core.entities.Category)
        {
            net.dv8tion.jda.core.entities.Category category = (net.dv8tion.jda.core.entities.Category)channel;
            if(!(category.getTextChannels().isEmpty()))
                eb.addField("Text Channels:", category.getTextChannels().stream().map(IMentionable::getAsMention)
                        .collect(Collectors.joining(", ")), false);
            if(!(category.getVoiceChannels().isEmpty()))
                eb.addField("Voice Channels:", category.getVoiceChannels().stream().map(Channel::getName)
                        .collect(Collectors.joining(", ")), false);
        }

        String name = channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName();
        String title = ":tv: Information about **"+name+"**";
        eb.setColor(event.getSelfMember().getColor()).setDescription(sb.toString());
        event.reply(mb.setContent(title).setEmbed(eb.build()).build());
    }
}

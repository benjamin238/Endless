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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.RichPresence;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author Artuto
 */

public class RPCCmd extends EndlessCommand
{
    public RPCCmd()
    {
        this.name = "rpc";
        this.aliases = new String[]{"gameinfo", "rpcinfo"};
        this.help = "Shows RPC info about the specified user";
        this.arguments = "[user]";
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        Member member;
        if(event.getArgs().isEmpty())
            member = event.getMember();
        else
            member = ArgsUtils.findMember(event, event.getArgs());

        if(member==null)
            return;

        Game game = member.getGame();
        if(game==null)
        {
            event.replyWarning("This member is not playing anything!");
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        MessageBuilder mb = new MessageBuilder();
        StringBuilder sb = new StringBuilder();

        sb.append(Const.LINE_START).append(" Name: **").append(game.getName()).append("**");
        if(!(game.getUrl()==null))
            sb.append("\n").append(Const.LINE_START).append("Link: **").append("[").append(game.getUrl()).append("](Link)**");

        if(game.isRich())
        {
            RichPresence rpc = game.asRichPresence();
            sb.append("\n").append(Const.LINE_START).append(" Details: **").append(rpc.getDetails()).append("**\n")
                    .append(Const.LINE_START).append(" State: **").append(rpc.getState()).append("**");
            if(!(rpc.getTimestamps()==null))
            {
                RichPresence.Timestamps timestamps = rpc.getTimestamps();
                if(!(timestamps.getStartTime()==null))
                {
                    sb.append("\n").append(Const.LINE_START).append(" Start Time: **").append(OffsetDateTime.ofInstant(timestamps.getStartTime(),
                            ZoneId.systemDefault()).format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**");
                }
                if(!(timestamps.getEndTime()==null))
                {
                    sb.append("\n").append(Const.LINE_START).append(" End Time: **").append(OffsetDateTime.ofInstant(timestamps.getEndTime(),
                            ZoneId.systemDefault()).format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**");
                }
            }
            if(!(rpc.getLargeImage()==null))
                builder.setThumbnail(rpc.getLargeImage().getUrl());
        }

        builder.setDescription(sb).setColor(member.getColor());
        event.reply(mb.setContent(event.getClient().getSuccess()+" Info about the game of **"+member.getUser().getName()+"**#**"
                +member.getUser().getDiscriminator()+"**").setEmbed(builder.build()).build());
    }
}

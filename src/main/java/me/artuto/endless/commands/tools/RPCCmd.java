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

import me.artuto.endless.Const;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.RichPresence;
import net.dv8tion.jda.core.entities.User;

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
    protected void executeCommand(EndlessCommandEvent event)
    {
        Member member = event.getArgs().isEmpty()?event.getMember():ArgsUtils.findMember(event, event.getArgs());
        if(member==null)
            return;

        Game game = member.getGame();
        if(game==null)
        {
            event.replyWarning("command.rpc.notPlaying");
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        MessageBuilder mb = new MessageBuilder();
        StringBuilder sb = new StringBuilder();

        sb.append(Const.LINE_START).append(" **").append(event.localize("command.rpc.name")).append(": **").append(game.getName()).append("**");
        if(!(game.getUrl()==null))
        {
            sb.append("\n").append(Const.LINE_START).append(" **").append(event.localize("command.rpc.link")).append(": **[").append(game.getName())
                    .append("](").append(game.getUrl()).append(")**");
        }

        if(game.isRich())
        {
            RichPresence rpc = game.asRichPresence();
            sb.append("\n").append(Const.LINE_START).append(" **").append(event.localize("command.rpc.details")).append(": **")
                    .append(rpc.getDetails()).append("**\n")
                    .append(Const.LINE_START).append(" **").append(event.localize("command.rpc.state")).append(": **").append(rpc.getState()).append("**");
            if(!(rpc.getTimestamps()==null))
            {
                RichPresence.Timestamps timestamps = rpc.getTimestamps();
                if(!(timestamps.getStartTime()==null))
                {
                    sb.append("\n").append(Const.LINE_START).append(" **").append(event.localize("command.rpc.startTime"))
                            .append(": **").append(OffsetDateTime.ofInstant(timestamps.getStartTime(),
                            ZoneId.systemDefault()).format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**");
                }
                if(!(timestamps.getEndTime()==null))
                {
                    sb.append("\n").append(Const.LINE_START).append(" **").append(event.localize("command.rpc.endTime"))
                            .append(": **").append(OffsetDateTime.ofInstant(timestamps.getEndTime(),
                            ZoneId.systemDefault()).format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**");
                }
            }
            if(!(rpc.getLargeImage()==null))
                builder.setThumbnail(rpc.getLargeImage().getUrl());
        }

        builder.setDescription(sb).setColor(member.getColor());
        User user = member.getUser();
        event.reply(mb.setContent(event.getClient().getSuccess()+" "+event.localize("command.rpc.title",
                user.getName()+"#"+user.getDiscriminator())).setEmbed(builder.build()).build());
    }
}

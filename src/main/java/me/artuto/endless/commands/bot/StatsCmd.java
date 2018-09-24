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

package me.artuto.endless.commands.bot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import me.artuto.endless.Const;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.utils.MiscUtils;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Artuto
 */

public class StatsCmd extends EndlessCommand
{
    public StatsCmd()
    {
        this.name = "stats";
        this.help = "Shows useful information of the bot";
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.category = Categories.BOT;
        this.guildOnly = false;
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        Color color = event.isFromType(ChannelType.TEXT)?event.getSelfMember().getColor():Color.decode("#33ff00");
        CommandClient client = event.getClient();
        EmbedBuilder builder = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();
        Runtime runtime = Runtime.getRuntime();
        ShardManager shardManager = event.getJDA().asBot().getShardManager();

        long channels = shardManager.getTextChannelCache().size()+shardManager.getVoiceChannelCache().size();
        long guilds = shardManager.getGuildCache().size();
        long users = shardManager.getUserCache().size();

        long totalRamMb = runtime.totalMemory()/(1024*1024);
        long usedRamMb = (runtime.totalMemory() - runtime.freeMemory())/(1024*1024);

        long commandsRan = 0;
        for(Command command : client.getCommands())
            commandsRan += client.getCommandUses(command);

        long audioConnections = shardManager.getGuilds().stream().filter(g -> g.getSelfMember().getVoiceState().inVoiceChannel()).count();

        sb.append(Const.LINE_START).append(" Channels: **").append(channels).append("**\n")
                .append(Const.LINE_START).append(" Guilds: **").append(guilds).append("**\n")
                .append(Const.LINE_START).append(" Users: **").append(users).append("**\n")
                .append(Const.LINE_START).append(" RAM Usage: **").append(usedRamMb).append("**MB / **").append(totalRamMb).append("**MB\n")
                .append(Const.LINE_START).append(" Commands Ran: **").append(commandsRan).append("**\n")
                .append(Const.LINE_START).append(" Last startup: ").append(FormatUtil.formatTimeFromSeconds(event.getClient().getStartTime()
                .until(OffsetDateTime.now(), ChronoUnit.SECONDS))).append(" ago\n")
                .append(Const.LINE_START).append(" Active Audio Connections **").append(audioConnections).append("**");

        builder.setColor(color);
        builder.setDescription(sb);
        builder.setThumbnail(MiscUtils.getImageUrl("png", null, event.getSelfUser().getEffectiveAvatarUrl()));
        event.reply(new MessageBuilder().setContent(Const.ENDLESS+" **Endless** Stats:").setEmbed(builder.build()).build());
    }
}

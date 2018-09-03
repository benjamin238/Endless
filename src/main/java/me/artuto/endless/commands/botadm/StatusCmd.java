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

package me.artuto.endless.commands.botadm;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Const;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Artuto
 */

public class StatusCmd extends EndlessCommand
{
    public StatusCmd()
    {
        this.name = "status";
        this.aliases = new String[]{"debug"};
        this.help = "Shows the status of the bot";
        this.category = Categories.BOTADM;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
        this.ownerCommand = true;
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        Runtime runtime = Runtime.getRuntime();
        ShardManager shardManager = event.getJDA().asBot().getShardManager();
        long totalRamMb = runtime.totalMemory()/(1024*1024);
        long usedRamMb = (runtime.totalMemory() - runtime.freeMemory())/(1024*1024);
        StringBuilder sb = new StringBuilder(Const.ENDLESS).append(" **Endless** status:");
        sb.append("\nLast startup: ").append(FormatUtil.formatTimeFromSeconds(event.getClient().getStartTime()
                .until(OffsetDateTime.now(), ChronoUnit.SECONDS))).append(" ago");
        sb.append("\nGuilds: **").append(shardManager.getGuildCache().size()).append("**");
        sb.append("\nRAM: **").append(usedRamMb).append("**MB / **").append(totalRamMb).append("**MB");
        sb.append("\nBot Average Ping: **").append(Math.round(shardManager.getAveragePing())).append("**ms");
        sb.append("\nCurrent Shard: **").append((event.getJDA().getShardInfo().getShardId()+1)).append("**");
        sb.append("\nShard Statuses: ```diff");

        shardManager.getShards().forEach(shard -> sb.append("\n").append((shard.getStatus()==JDA.Status.CONNECTED?"+":"-")+" ").append(shard.getShardInfo().getShardId()+1).append(": ").append(shard.getStatus())
                .append(" - ").append(shard.getGuildCache().size()).append(" guilds"));
        sb.append("```");

        event.reply(sb.toString());
    }
}

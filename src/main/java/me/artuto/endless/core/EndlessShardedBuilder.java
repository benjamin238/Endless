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

package me.artuto.endless.core;

import me.artuto.endless.Bot;
import me.artuto.endless.core.entities.impl.EndlessShardedImpl;
import net.dv8tion.jda.bot.sharding.ShardManager;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Artuto
 */

public class EndlessShardedBuilder
{
    private final Bot bot;
    private final ShardManager shardManager;

    private final List<EndlessCore> shards;

    public EndlessShardedBuilder(Bot bot, ShardManager shardManager)
    {
        this.bot = bot;
        this.shardManager = shardManager;
        this.shards = new LinkedList<>();
    }

    public void addShard(EndlessCore shard)
    {
        shards.add(shard);
    }

    public EndlessSharded build()
    {
        return new EndlessShardedImpl(bot, shardManager, shards);
    }
}

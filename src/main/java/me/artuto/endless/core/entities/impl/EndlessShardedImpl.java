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

package me.artuto.endless.core.entities.impl;

import me.artuto.endless.Bot;
import me.artuto.endless.core.EndlessCore;
import me.artuto.endless.core.EndlessSharded;
import me.artuto.endless.core.entities.GuildSettings;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Artuto
 */

public class EndlessShardedImpl implements EndlessSharded
{
    private final Bot bot;
    private final List<EndlessCore> shards;
    private final ShardManager shardManager;

    private final List<GuildSettings> guildSettings;
    private final Map<JDA, EndlessCore> shardsMap;

    public EndlessShardedImpl(Bot bot, ShardManager shardManager, List<EndlessCore> shards)
    {
        this.bot = bot;
        this.shardManager = shardManager;
        this.shards = shards;

        this.guildSettings = new LinkedList<>();
        this.shardsMap = new HashMap<>();

        shards.forEach(shard -> {
            guildSettings.addAll(shard.getGuildSettings());
            shardsMap.put(shard.getJDA(), shard);
        });
    }

    @Override
    public Bot getBot()
    {
        return bot;
    }

    @Override
    public EndlessCore getShard(JDA jda)
    {
        return shardsMap.get(jda);
    }

    @Nullable
    @Override
    public GuildSettings getGuildSettingsById(long id)
    {
        Guild guild = shardManager.getGuildById(id);
        if(!(guild==null))
            return guildSettings.stream().filter(gs -> gs.getGuild().getIdLong()==id).findFirst().orElse(null);
        else
            return null;
    }

    @Nullable
    @Override
    public GuildSettings getGuildSettingsById(String id)
    {
        Guild guild = shardManager.getGuildById(id);
        if(!(guild==null))
            return guildSettings.stream().filter(gs -> gs.getGuild().getId().equals(id)).findFirst().orElse(null);
        else
            return null;
    }

    @Override
    public List<GuildSettings> getGuildSettings()
    {
        return Collections.unmodifiableList(guildSettings);
    }

    @Override
    public List<EndlessCore> getShards()
    {
        return Collections.unmodifiableList(shards);
    }

    @Override
    public ShardManager getShardManager()
    {
        return shardManager;
    }
}

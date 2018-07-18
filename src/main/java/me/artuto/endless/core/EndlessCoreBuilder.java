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

import com.jagrosh.jdautilities.command.CommandClient;
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.Endless;
import me.artuto.endless.core.entities.EntityBuilder;
import me.artuto.endless.core.entities.impl.EndlessCoreImpl;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.managers.Presence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Artuto
 */

public class EndlessCoreBuilder
{
    public final EntityBuilder entityBuilder;
    private final Bot bot;

    private final EndlessCoreImpl impl;

    public EndlessCoreBuilder(Bot bot, CommandClient client, ShardManager shardManager)
    {
        this.bot = bot;
        this.entityBuilder = new EntityBuilder(bot);

        this.impl = new EndlessCoreImpl(bot, client, entityBuilder, shardManager);
    }

    public void addShard(JDA shard)
    {
        impl.addShard(shard);
        impl.makeCacheForShard(shard);
    }

    public EndlessCore build()
    {
        impl.finishSetup();
        impl.getShards().forEach(this::updateGame);
        bot.initialized = true;
        Endless.LOG.info("Endless is ready!");
        return impl;
    }

    private void updateGame(JDA shard)
    {
        JDA.ShardInfo shardInfo = shard.getShardInfo();
        Presence presence = shard.getPresence();

        if(!(bot.maintenance))
            presence.setPresence(bot.config.getStatus(), Game.playing("Type "+bot.config.getPrefix()+"help | Version "
                    +Const.VERSION+" | On "+shard.getGuildCache().size()+" Guilds | "+shard.getUserCache().size()+
                    " Users | Shard "+(shardInfo.getShardId()+1)));
        else
            presence.setPresence(OnlineStatus.DO_NOT_DISTURB, Game.playing("Maintenance mode enabled | Shard "
                    +(shardInfo.getShardId()+1)));
    }
}

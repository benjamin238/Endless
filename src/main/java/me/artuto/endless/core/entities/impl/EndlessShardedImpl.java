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

import ch.qos.logback.classic.Logger;
import me.artuto.endless.Bot;
import me.artuto.endless.core.EndlessCore;
import me.artuto.endless.core.EndlessSharded;
import me.artuto.endless.core.entities.Blacklist;
import me.artuto.endless.core.entities.GlobalTag;
import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.core.entities.LocalTag;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Artuto
 */

public class EndlessShardedImpl implements EndlessSharded
{
    private final Logger LOG = (Logger)LoggerFactory.getLogger(EndlessShardedImpl.class);

    private final Bot bot;
    private final List<EndlessCore> shards;
    private final Map<JDA, EndlessCore> shardMap;
    private final ShardManager shardManager;

    private final List<Blacklist> blacklists;
    private final Map<Long, Blacklist> blacklistMap;

    private final List<GuildSettings> guildSettings;
    private final Map<Guild, GuildSettings> guildSettingsMap;

    private final List<GlobalTag> globalTags;
    private final List<LocalTag> localTags;
    private final Map<String, GlobalTag> globalTagMap;
    private final Map<String, LocalTag> localTagMap;

    public EndlessShardedImpl(Bot bot, ShardManager shardManager, List<EndlessCore> shards)
    {
        this.bot = bot;
        this.shardManager = shardManager;
        this.shards = shards;
        this.shardMap = new HashMap<>();

        this.blacklists = new LinkedList<>();
        this.blacklistMap = new HashMap<>();

        this.guildSettings = new LinkedList<>();
        this.guildSettingsMap = new HashMap<>();

        this.globalTags = new LinkedList<>();
        this.globalTagMap = new HashMap<>();
        this.localTags = new LinkedList<>();
        this.localTagMap = new HashMap<>();
    }

    @Override
    public Bot getBot()
    {
        return bot;
    }

    @Override
    public Blacklist getBlacklist(long id)
    {
        return blacklistMap.get(id);
    }

    @Override
    public EndlessCore getShard(JDA jda)
    {
        return shardMap.get(jda);
    }

    @Override
    public GlobalTag getGlobalTag(String name)
    {
        return globalTagMap.get(name);
    }

    @Override
    public GuildSettings getGuildSettings(Guild guild)
    {
        return guildSettingsMap.getOrDefault(guild, bot.db.createDefault(guild));
    }

    @Override
    public GuildSettings getGuildSettingsById(long id)
    {
        Guild guild = shardManager.getGuildById(id);
        return guildSettingsMap.getOrDefault(guild, bot.db.createDefault(guild));
    }

    @Override
    public GuildSettings getGuildSettingsById(String id)
    {
        Guild guild = shardManager.getGuildById(id);
        return guildSettingsMap.getOrDefault(guild, bot.db.createDefault(guild));
    }

    public List<Blacklist> getBlacklists()
    {
        return Collections.unmodifiableList(blacklists);
    }

    @Override
    public List<EndlessCore> getShards()
    {
        return Collections.unmodifiableList(shards);
    }

    @Override
    public List<GlobalTag> getGlobalTags()
    {
        return Collections.unmodifiableList(globalTags);
    }

    @Override
    public List<GuildSettings> getGuildSettings()
    {
        return Collections.unmodifiableList(guildSettings);
    }

    @Override
    public List<LocalTag> getLocalTags()
    {
        return Collections.unmodifiableList(localTags);
    }

    @Override
    public LocalTag getLocalTag(long guildId, String name)
    {
        return localTagMap.get(guildId+":"+name);
    }

    @Override
    public ShardManager getShardManager()
    {
        return shardManager;
    }

    public void addBlacklist(Blacklist blacklist)
    {
        blacklists.add(blacklist);
        blacklistMap.put(blacklist.getId(), blacklist);
    }

    public void addGlobalTag(GlobalTag tag)
    {
        globalTags.add(tag);
        globalTagMap.put(tag.getName(), tag);
    }

    public void addLocalTag(LocalTag tag)
    {
        localTags.add(tag);
        localTagMap.put(tag.getGuildId()+":"+tag.getName(), tag);
    }

    public void addSettings(Guild guild, GuildSettings settings)
    {
        guildSettings.add(settings);
        guildSettingsMap.put(guild, settings);
    }

    public void makeCache()
    {
        shards.forEach(shard -> {
            bot.bdm.getBlacklistedGuilds(shard.getJDA()).forEach((g, b) -> {
                blacklists.add(b);
                blacklistMap.put(g.getIdLong(), b);
            });
            bot.bdm.getBlacklistedUsers(shard.getJDA()).forEach((u, b) -> {
                blacklists.add(b);
                blacklistMap.put(u.getIdLong(), b);
            });
            LOG.debug("Cached {} Blacklists", blacklists.size());

            guildSettings.addAll(shard.getGuildSettings());
            guildSettings.forEach(gs -> guildSettingsMap.put(gs.getGuild(), gs));

            globalTags.addAll(shard.getGlobalTags());
            globalTags.forEach(gTag -> globalTagMap.put(gTag.getName(), gTag));
            localTags.addAll(shard.getLocalTags());
            localTags.forEach(lTag -> localTagMap.put(lTag.getGuildId()+":"+lTag.getName(), lTag));

            shardMap.put(shard.getJDA(), shard);
            long totalCache = blacklists.size()+guildSettings.size()+globalTags.size()+localTags.size();

            LOG.debug("Successfully cached {} across {} Shards", totalCache, shards.size());
        });
    }

    public void removeBlacklist(Blacklist blacklist)
    {
        blacklists.remove(blacklistMap.remove(blacklist.getId()));
    }

    public void removeGlobalTag(String name)
    {
        globalTags.remove(globalTagMap.remove(name));
    }

    public void removeLocalTag(long guild, String name)
    {
        localTags.remove(localTagMap.remove(guild+":"+name));
    }
}

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
import com.jagrosh.jdautilities.command.CommandClient;
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.Endless;
import me.artuto.endless.core.EndlessCore;
import me.artuto.endless.core.entities.*;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Artuto
 */

public class EndlessCoreImpl implements EndlessCore
{
    private final Logger LOG = Endless.getLog(this.getClass());

    private final Bot bot;
    private final CommandClient client;
    private final EntityBuilder entityBuilder;
    private final List<JDA> shards;
    private final Map<Integer, JDA> shardMap;
    private final ShardManager shardManager;

    private final List<Blacklist> blacklists;
    private final Map<Long, Blacklist> blacklistMap;

    private final List<EndlessUser> users;
    private final Map<Long, EndlessUser> userMap;

    private final List<GuildSettings> guildSettings;
    private final Map<Long, GuildSettings> guildSettingsMap;

    private final List<Tag> globalTags;
    private final List<LocalTag> localTags;
    private final Map<String, Tag> globalTagMap;
    private final Map<String, LocalTag> localTagMap;

    public EndlessCoreImpl(Bot bot, CommandClient client, EntityBuilder entityBuilder, ShardManager shardManager)
    {
        this.bot = bot;
        this.client = client;
        this.entityBuilder = entityBuilder;
        this.shardManager = shardManager;
        this.shards = new LinkedList<>();
        this.shardMap = new HashMap<>();

        this.blacklists = new LinkedList<>();
        this.blacklistMap = new HashMap<>();

        this.users = new LinkedList<>();
        this.userMap = new HashMap<>();

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
    public CommandClient getClient()
    {
        return client;
    }

    @Override
    public Blacklist getBlacklist(long id)
    {
        return blacklistMap.get(id);
    }

    @Override
    public EndlessUser getUser(long id)
    {
        return userMap.get(id);
    }

    @Override
    public GuildSettings getGuildSettings(Guild guild)
    {
        return guildSettingsMap.getOrDefault(guild.getIdLong(), bot.db.createDefaultSettings(guild));
    }

    @Override
    public GuildSettings getGuildSettingsById(long id)
    {
        Guild guild = shardManager.getGuildById(id);
        return guildSettingsMap.getOrDefault(id, bot.db.createDefaultSettings(guild));
    }

    @Override
    public GuildSettings getGuildSettingsById(String id)
    {
        Guild guild = shardManager.getGuildById(id);
        return guildSettingsMap.getOrDefault(guild.getIdLong(), bot.db.createDefaultSettings(guild));
    }

    @Override
    public Ignore getIgnore(Guild guild, long entity)
    {
        return getGuildSettings(guild).getIgnoredEntities().stream().filter(ignore ->
                ignore.getEntityId()==entity).findFirst().orElse(null);
    }

    @Override
    public JDA getShard(int id)
    {
        return shardMap.get(id);
    }

    @Override
    public List<Blacklist> getBlacklists()
    {
        return Collections.unmodifiableList(blacklists);
    }

    @Override
    public List<Blacklist> getGuildBlacklists()
    {
        return Collections.unmodifiableList(blacklists.stream().filter(b ->
                b.getType()==Const.BlacklistType.GUILD).collect(Collectors.toList()));
    }

    @Override
    public List<Blacklist> getUserBlacklists()
    {
        return Collections.unmodifiableList(blacklists.stream().filter(b ->
                b.getType()==Const.BlacklistType.USER).collect(Collectors.toList()));
    }

    @Override
    public List<EndlessUser> getUsers()
    {
        return Collections.unmodifiableList(users);
    }

    @Override
    public List<GuildSettings> getGuildSettings()
    {
        return Collections.unmodifiableList(guildSettings);
    }

    @Override
    public List<JDA> getShards()
    {
        return Collections.unmodifiableList(shards);
    }

    @Override
    public List<LocalTag> getLocalTags()
    {
        return Collections.unmodifiableList(localTags);
    }

    @Override
    public List<Tag> getGlobalTags()
    {
        return Collections.unmodifiableList(globalTags);
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

    @Override
    public Tag getGlobalTag(String name)
    {
        return globalTagMap.get(name);
    }

    public EntityBuilder getEntityBuilder()
    {
        return entityBuilder;
    }

    public void addBlacklist(Blacklist blacklist)
    {
        blacklistMap.put(blacklist.getId(), blacklist);
        blacklists.add(blacklist);
    }

    public void addUser(EndlessUser user)
    {
        userMap.put(user.getUserId(), user);
        users.add(user);
    }

    public void addGlobalTag(Tag tag)
    {
        globalTagMap.put(tag.getName(), tag);
        globalTags.add(tag);
    }

    public void addLocalTag(LocalTag tag)
    {
        localTagMap.put(tag.getGuildId()+":"+tag.getName(), tag);
        localTags.add(tag);
    }

    public void addSettings(Guild guild, GuildSettings settings)
    {
        guildSettingsMap.put(guild.getIdLong(), settings);
        guildSettings.add(settings);
    }

    public void addShard(JDA jda)
    {
        shardMap.put(jda.getShardInfo().getShardId(), jda);
        shards.add(jda);
    }

    public void finishSetup()
    {
        long totalCache = blacklists.size()+guildSettings.size()+globalTags.size()+localTags.size();
        LOG.debug("Successfully cached {} across {} Shards", totalCache, shards.size());
    }

    public void makeCacheForShard(JDA shard)
    {
        if(!(bot.dataEnabled))
            return;

        LOG.debug("Starting cache creation for shard "+shard.getShardInfo().getShardId()+"...");

        // Cache Guild Blacklists
        bot.bdm.getBlacklistedGuilds(shard).forEach((g, b) -> {
            blacklists.add(b);
            blacklistMap.put(g.getIdLong(), b);
        });

        // Cache User Blacklists
        bot.bdm.getBlacklistedUsers(shard).forEach((u, b) -> {
            blacklists.add(b);
            blacklistMap.put(u.getIdLong(), b);
        });
        LOG.debug("Cached {} Blacklists", blacklists.size());

        // Cache Guild Settings
        bot.db.getGuildsThatHaveSettings(shard).forEach(g -> {
            GuildSettings settings = bot.db.getSettings(g);
            addSettings(g, settings);
        });
        LOG.debug("Cached {} Guild Settings", guildSettings.size());

        bot.tdm.getGlobalTags().forEach(this::addGlobalTag);
        LOG.debug("Cached {} Global tags", globalTags.size());

        shardManager.getGuilds().forEach(guild -> bot.tdm.getLocalTagsForGuild(guild).forEach(this::addLocalTag));
        LOG.debug("Cached {} Local tags", localTags.size());
    }

    public void removeBlacklist(Blacklist blacklist)
    {
        blacklists.remove(blacklistMap.remove(blacklist.getId()));
    }

    public void removeUser(EndlessUser user)
    {
        users.remove(userMap.remove(user.getUserId()));
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

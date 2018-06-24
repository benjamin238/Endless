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
import me.artuto.endless.core.EndlessCore;
import me.artuto.endless.core.entities.GlobalTag;
import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.core.entities.LocalTag;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Artuto
 */

public class EndlessCoreImpl implements EndlessCore
{
    private final Logger LOG = (Logger)LoggerFactory.getLogger(EndlessCore.class);

    private final Bot bot;
    private final CommandClient client;
    private final JDA jda;

    private final List<GuildSettings> guildSettings;
    private final Map<Guild, GuildSettings> guildSettingsMap;

    private final List<GlobalTag> globalTags;
    private final List<LocalTag> localTags;
    private final Map<String, GlobalTag> globalTagMap;
    private final Map<String, LocalTag> localTagMap;

    public EndlessCoreImpl(Bot bot, CommandClient client, JDA jda)
    {
        this.bot = bot;
        this.client = client;
        this.jda = jda;

        this.guildSettings = new LinkedList<>();
        this.guildSettingsMap = new HashMap<>();

        this.globalTags = new LinkedList<>();
        this.globalTagMap = new HashMap<>();
        this.localTags = new LinkedList<>();
        this.localTagMap = new HashMap<>();

        guildSettings.forEach(gs -> guildSettingsMap.put(gs.getGuild(), gs));
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
        Guild guild = jda.getGuildById(id);
        return guildSettingsMap.getOrDefault(guild, bot.db.createDefault(guild));
    }

    @Override
    public GuildSettings getGuildSettingsById(String id)
    {
        Guild guild = jda.getGuildById(id);
        return guildSettingsMap.getOrDefault(guild, bot.db.createDefault(guild));
    }

    @Override
    public JDA getJDA()
    {
        return jda;
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
    public String toString()
    {
        return "EndlessShard: "+jda.getShardInfo().getShardString();
    }

    public void makeCache()
    {
        LOG.debug("Starting cache creation for shard "+jda.getShardInfo().getShardId()+"...");

        for(Guild guild : bot.db.getGuildsThatHaveSettings(jda))
        {
            GuildSettings settings = bot.db.getSettings(guild);
            addSettings(guild, settings);
        }
        LOG.debug("Cached {} Guild Settings", guildSettings.size());

        for(GlobalTag tag : bot.tdm.getGlobalTags())
            addGlobalTag(tag);
        LOG.debug("Cached {} Global tags", globalTags.size());

        jda.getGuilds().forEach(guild -> {
            for(LocalTag tag : bot.tdm.getLocalTagsForGuild(guild))
                addLocalTag(tag);
        });
        LOG.debug("Cached {} Local tags", localTags.size());

        LOG.debug("Successfully cached all needed entities for shard "+jda.getShardInfo().getShardId()+".");
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

    public void removeGlobalTag(String name)
    {
        globalTags.remove(globalTagMap.remove(name));
    }

    public void removeLocalTag(long guild, String name)
    {
        localTags.remove(localTagMap.remove(guild+":"+name));
    }
}

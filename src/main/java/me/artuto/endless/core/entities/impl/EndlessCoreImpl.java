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
import me.artuto.endless.Endless;
import me.artuto.endless.core.EndlessCore;
import me.artuto.endless.core.entities.GuildSettings;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Artuto
 */

public class EndlessCoreImpl implements EndlessCore
{
    private boolean initialized = false;
    private final Logger LOG = (Logger)LoggerFactory.getLogger(EndlessCore.class);

    protected final Bot bot;
    protected final CommandClient client;
    protected final List<GuildSettings> guildSettings;
    protected final ShardManager jda;

    public EndlessCoreImpl(Bot bot, CommandClient client, ShardManager jda)
    {
        this.bot = bot;
        this.client = client;
        this.jda = jda;
        this.guildSettings = new LinkedList<>();
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

    @Nullable
    @Override
    public GuildSettings getGuildSettingsById(long id)
    {
        Guild guild = jda.getGuildById(id);
        if(!(guild==null))
            return bot.db.getSettings(guild);
        else
            return null;
    }

    @Nullable
    @Override
    public GuildSettings getGuildSettingsById(String id)
    {
        Guild guild = jda.getGuildById(id);
        if(!(guild==null))
            return bot.db.getSettings(guild);
        else
            return null;
    }

    @Override
    public List<GuildSettings> getGuildSettings()
    {
        return Collections.unmodifiableList(guildSettings);
    }

    @Override
    public ShardManager getShardManager()
    {
        return jda;
    }

    public void makeCache()
    {
        if(initialized)
            throw new RuntimeException("This Endless instance is initialized already!");

        LOG.debug("Starting cache creation...");
        for(Guild guild : bot.db.getGuildsThatHaveSettings(jda))
            guildSettings.add(bot.db.getSettings(guild));
        LOG.debug("Cached {} Guild Settings", guildSettings.size());

        initialized = true;
        LOG.debug("Successfully cached all needed entities.");
    }

    public void updateSettingsCache(Guild guild)
    {
        LOG.debug("Requested cache update of settings for Guild {}", guild.getIdLong());
        GuildSettings settings = getGuildSettingsById(guild.getIdLong());

        if(!(settings==null))
            guildSettings.remove(settings);
        guildSettings.add(bot.db.getSettings(guild));

        LOG.debug("Successfully updated settings cache for Guild {}", guild.getIdLong());
    }

    public void updateInstances()
    {
        bot.gsdm.endlessImpl = this;
    }
}

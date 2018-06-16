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
import me.artuto.endless.core.hooks.EndlessListener;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Guild;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Artuto
 */

public class EndlessCoreImpl implements EndlessCore
{
    private final Logger LOG = Endless.LOG;

    protected final Bot bot;
    protected final CommandClient client;
    protected final EndlessListener listener;
    protected final List<GuildSettings> guildSettings;
    protected final ShardManager jda;

    public EndlessCoreImpl(Bot bot, CommandClient client, ShardManager jda, EndlessListener listener)
    {
        this.bot = bot;
        this.client = client;
        this.jda = jda;
        this.guildSettings = new LinkedList<>();
        this.listener = listener;
    }

    @Override
    public CommandClient getClient()
    {
        return client;
    }

    @Override
    public EndlessListener getListener()
    {
        return listener;
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

    public void makeCache()
    {
        LOG.debug("Starting cache creation...");

        for(Guild guild : bot.db.getGuildsThatHaveSettings(jda))
        {
            guildSettings.add(bot.db.getSettings(guild));
            LOG.debug(String.format("Cached %s Guild Settings", guildSettings.size()));
        }
    }
}

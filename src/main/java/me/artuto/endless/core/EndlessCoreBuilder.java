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
import me.artuto.endless.bootloader.EndlessLoader;
import me.artuto.endless.core.entities.impl.EndlessCoreImpl;
import net.dv8tion.jda.bot.sharding.ShardManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Artuto
 */

public class EndlessCoreBuilder
{
    protected final EndlessLoader loader;

    protected Bot bot;
    protected CommandClient client;
    protected List<Object> listeners;
    protected ShardManager jda;

    public EndlessCoreBuilder(Bot bot)
    {
        this.bot = bot;
        this.loader = new EndlessLoader(bot);
    }

    public EndlessCoreBuilder addListeners(Object... listeners)
    {
        this.listeners = new LinkedList<>();
        Collections.addAll(this.listeners, listeners);
        return this;
    }

    public EndlessCoreBuilder setCommandClient(CommandClient client)
    {
        this.client = client;
        return this;
    }

    public EndlessCoreBuilder setShardmanager(ShardManager manager)
    {
        this.jda = manager;
        return this;
    }

    public EndlessCore build()
    {
        return new EndlessCoreImpl(client, jda, listeners);
    }
}

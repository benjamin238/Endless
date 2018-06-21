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
import me.artuto.endless.core.entities.impl.EndlessCoreImpl;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.managers.Presence;

/**
 * @author Artuto
 */

public class EndlessCoreBuilder
{
    protected final Bot bot;
    protected CommandClient client;
    protected final JDA jda;

    public EndlessCoreBuilder(Bot bot, JDA jda)
    {
        this.bot = bot;
        this.jda = jda;
    }

    public EndlessCoreBuilder setCommandClient(CommandClient client)
    {
        this.client = client;
        return this;
    }

    public EndlessCore build()
    {
        EndlessCoreImpl impl = new EndlessCoreImpl(bot, client, jda);

        impl.updateInstances();
        impl.makeCache();

        return impl;
    }
}

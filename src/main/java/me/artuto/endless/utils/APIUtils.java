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

package me.artuto.endless.utils;

import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Artuto
 */

public class APIUtils
{
    private final Config config;
    private final JDA jda;

    public APIUtils(Config config, JDA jda)
    {
        this.config = config;
        this.jda = jda;
    }

    public List<Guild> getGuildsForUser(Long id)
    {
        User user = jda.getUserById(id);

        if(!(user == null)) return user.getMutualGuilds();
        else return null;
    }

    public List<Guild> getManagedGuildsForUser(Long id)
    {
        List<Guild> guilds = new LinkedList<>();
        User user = jda.getUserById(id);

        if(!(user == null))
            user.getMutualGuilds().stream().filter(g -> Checks.hasPermission(g.getMember(user), null, Permission.MANAGE_SERVER)).forEach(g -> guilds.add(g));

        return guilds;
    }

    public Guild getGuild(Long id)
    {
        return jda.getGuildById(id);
    }
}

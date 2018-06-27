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

package me.artuto.endless.handlers;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.core.entities.Ignore;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

/**
 * @author Artuto
 */

public class IgnoreHandler
{
    private final Bot bot;

    public IgnoreHandler(Bot bot)
    {
        this.bot = bot;
    }

    public boolean handleIgnore(CommandEvent event)
    {
        Guild guild = event.getGuild();
        List<Role> roles = event.getMember().getRoles();
        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();

        for(Role role : roles)
        {
            if(!(bot.endless.getIgnore(guild, role.getIdLong())==null) && !(role.isPublicRole()))
                return false;
        }
        if(!(bot.endless.getIgnore(guild, tc.getIdLong())==null))
            return false;
        else
            return bot.endless.getIgnore(guild, user.getIdLong())==null;
    }
}

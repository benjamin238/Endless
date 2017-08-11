/*
 * Copyright (C) 2017 Artu
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


import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.data.Settings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.awt.*;

/**
 *
 * @author Artu
 */

public class ModLogging 
{
    private static Bot bot;
    private Settings settings;

    public ModLogging(Bot b)
    {
        this.bot = b;
    }

    public static void logBan(User author, Member target, String reason, Guild guild, Message message)
    {
        Settings settings = bot.getSettings(guild);
        TextChannel tc = guild.getTextChannelById(settings.getModLogId());
        
        if(settings.getModLogId()==0 || !tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
        {
            return;
        }
        else
        {
            tc.sendMessage("`Ban` :hammer: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") banned **"+target.getUser().getName()+"**#**"+target.getUser().getDiscriminator()+"** ("+target.getUser().getId()+")\n"
                    + "`Reason:` *"+reason+"*").queue();
        }
    }
}

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

    public ModLogging(Bot bot)
    {
        this.bot = bot;
        this.settings = settings;
    }

    public static void logBan(User author, Member target, String reason, Guild guild, Message message, CommandEvent event)
    {
        Settings settings = bot.getSettings(guild);
        TextChannel tc = event.getJDA().getTextChannelById(bot.getSettings(guild).getModLogId());

            tc.sendMessage(new EmbedBuilder()
                    .setAuthor(author.getName(), null, author.getAvatarUrl())
                    .setTitle("A Memeber was banned!")
                    .setDescription(author.getName()+"#"+author.getDiscriminator()+" banned "+target.getUser().getName()+"#"+target.getUser().getDiscriminator()+"\n with the reason: " +
                            reason)
                    .setTimestamp(message.getCreationTime())
                    .setColor(Color.RED)
                    .build()).queue();
            SimpleLog.getLog("DEBUG MODLOG").info("Modlog sent");
    }
}

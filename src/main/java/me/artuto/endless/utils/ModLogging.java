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


import me.artuto.endless.data.Settings;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 *
 * @author Artu
 */

public class ModLogging 
{
    private final Settings settings;
    
    public ModLogging(Settings settings)
    {
        this.settings = settings;
    }
        
    public void logBan(Message msg)
    {
        TextChannel tc = settings.getModLogId();
        if(tc==null || !tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_EMBED_LINKS))
            return;
        tc.sendMessage(new EmbedBuilder()
                .setColor(tc.getGuild().getSelfMember().getColor())
                .setTimestamp(msg.getCreationTime())
                .setFooter(msg.getAuthor().getName()+"#"+msg.getAuthor().getDiscriminator()+" | #"+msg.getTextChannel().getName(), msg.getAuthor().getEffectiveAvatarUrl())
                .setDescription(msg.getRawContent())
                .build()).queue();
    }
}

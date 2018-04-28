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

package me.artuto.endless.logging;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import me.artuto.endless.utils.StringUtils;
import me.artuto.endless.utils.TimeUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Artuto
 */

public class CommandLogging implements CommandListener
{
    @Override
    public void onCommand(CommandEvent event, Command command)
    {
        TextChannel commandLog = event.getJDA().getTextChannelById(439269675109580810L);
        User author = event.getAuthor();
        Guild guild = event.getGuild();
        
        // If is the help command is null
        if(command==null) return;

        if(event.isOwner()) return;

        commandLog.sendMessage("`"+TimeUtils.getTimeAndDate()+"` :keyboard: **"+author.getName()+"#"+author.getDiscriminator()+"** " +
                "(ID: "+author.getId()+") used the command `"+command.getName()+"` (`"+event.getMessage().getContentStripped()+"`) in **"+guild.getName()+"** (ID: "+guild.getId()+")").queue();
    }
}

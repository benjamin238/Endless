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

package me.artuto.endless.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import java.util.List;
import me.artuto.endless.Bot;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 *
 * @author Artu
 */

public class ModLog extends Command
{
    private final Bot bot;
    
    public ModLog(Bot bot)
    {
        this.bot = bot;
        this.name = "modlog";
        this.aliases = new String[]{"banlog", "kicklog", "banslog", "kickslog"};
        this.help = "Sets the modlog channel";
        this.arguments = "#channel | Channel ID | Channel name";
        this.category = new Command.Category("Settings");
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.ownerCommand = false;
        this.guildOnly = true;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
        if(event.getArgs().isEmpty())
        {
            event.reply(event.getClient().getError()+" Please include a text channel or NONE");
        }
        else if(event.getArgs().equalsIgnoreCase("none"))
        {
            bot.clearModLogChannel(event.getGuild());
            event.reply(event.getClient().getSuccess()+" Modlogging disabled");
        }
        else
        {
            List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
            if(list.isEmpty())
                event.reply(event.getClient().getWarning()+" No Text Channels found matching \""+event.getArgs()+"\"");
            else if (list.size()>1)
                event.reply(event.getClient().getWarning()+FormatUtil.listOfTcChannels(list, event.getArgs()));
            else
            {
                bot.setModLogChannel(list.get(0));
                event.reply(event.getClient().getSuccess()+" Modlogging actions will be logged in "+list.get(0).getAsMention());
            }
        }
        
    }
}

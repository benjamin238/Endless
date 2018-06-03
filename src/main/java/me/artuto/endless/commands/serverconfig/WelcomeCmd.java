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

package me.artuto.endless.commands.serverconfig;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

public class WelcomeCmd extends EndlessCommand
{
    private final Bot bot;

    public WelcomeCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "welcome";
        this.children = new Command[]{new Change()};
        this.aliases = new String[]{"welcomemessage", "welcomemsg"};
        this.help = "Changes or shows the welcome message";
        this.category = Categories.SERVER_CONFIG;
        this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        Guild guild = event.getGuild();
        String msg = bot.gsdm.getWelcomeMessage(guild);

        if(!(msg == null)) event.replySuccess("Welcome message at **"+guild.getName()+"**: `"+msg+"`");
        else event.replyError("No message configured!");
    }

    private class Change extends EndlessCommand
    {
        Change()
        {
            this.name = "change";
            this.help = "Changes the welcome message";
            this.aliases = new String[]{"set"};
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a new welcome message or `none` to disable it.");
                return;
            }

            if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setWelcomeMessage(event.getGuild(), null);
                event.replySuccess("Successfully removed welcome message");
            }
            else
            {
                bot.gsdm.setWelcomeMessage(event.getGuild(), event.getArgs());
                event.replySuccess("Welcome message configured.");
            }
        }
    }
}

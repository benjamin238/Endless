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
import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.core.entities.GuildSettings;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

/**
 * @author Artuto
 */

public class WelcomeDmCmd extends EndlessCommand
{
    private final Bot bot;

    public WelcomeDmCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "welcomedm";
        this.children = new Command[]{new ChangeCmd()};
        this.help = "Changes or shows the message sent in DMs to new members";
        this.category = Categories.SERVER_CONFIG;
        this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        Guild guild = event.getGuild();
        GuildSettings gs = event.getClient().getSettingsFor(guild);

        if(!(gs.getWelcomeDM()==null))
            event.replySuccess("command.welcome.dm.set", guild.getName(), gs.getWelcomeDM());
        else
            event.replyError("command.welcome.dm.unset", event.getClient().getPrefix());
    }

    private class ChangeCmd extends EndlessCommand
    {
        ChangeCmd()
        {
            this.name = "change";
            this.help = "Changes the message that will be sent to members that join.";
            this.aliases = new String[]{"set"};
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Specify a new welcome DM or `none` to disable it.";
            this.parent = WelcomeDmCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setWelcomeDm(event.getGuild(), null);
                event.replySuccess("command.welcome.dm.set.removed");
            }
            else
            {
                if(event.getArgs().length()>400)
                {
                    event.replyError("command.welcome.dm.set.length");
                    return;
                }

                bot.gsdm.setWelcomeDm(event.getGuild(), event.getArgs());
                event.replySuccess("command.welcome.dm.set.set");
            }
        }
    }
}

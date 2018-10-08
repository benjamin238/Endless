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

import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;

/**
 * @author Artuto
 */

public class SetDJCmd extends EndlessCommand
{
    private final Bot bot;

    public SetDJCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "setdj";
        this.help = "Sets the DJ role.";
        this.arguments = "<Role|Role ID|@Role>";
        this.category = Categories.SERVER_CONFIG;
        this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        this.needsArgumentsMessage = "Please include a role or NONE";
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        if(event.getArgs().equalsIgnoreCase("none"))
        {
            bot.gsdm.setDJRole(event.getGuild(), null);
            event.replySuccess("command.setdj.disabled");
        }
        else
        {
            Role role = ArgsUtils.findRole(event, event.getArgs());
            if(role==null)
                return;

            bot.gsdm.setDJRole(event.getGuild(), role);
            event.replySuccess("command.setdj.set", role.getName());
        }
    }
}

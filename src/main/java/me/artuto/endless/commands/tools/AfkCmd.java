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

package me.artuto.endless.commands.tools;

import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.storage.tempdata.AfkManager;
import net.dv8tion.jda.core.entities.User;

public class AfkCmd extends EndlessCommand
{
    public AfkCmd()
    {
        this.name = "afk";
        this.help = "Mark yourself as afk with a message";
        this.arguments = "[message]";
        this.category = Categories.TOOLS;
        this.guildOnly = false;
        this.needsArguments = false;
    }

    protected void executeCommand(EndlessCommandEvent event)
    {
        User user = event.getAuthor();

        AfkManager.setAfk(user.getIdLong(), event.getArgs().isEmpty()?null:event.getArgs());
        event.replySuccess("command.afk", user.getAsMention());
    }
}

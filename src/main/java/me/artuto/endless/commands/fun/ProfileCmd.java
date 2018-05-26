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

package me.artuto.endless.commands.fun;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import net.dv8tion.jda.core.Permission;

public class ProfileCmd extends EndlessCommand
{
    private final Bot bot;

    public ProfileCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "profile";
        this.aliases = new String[]{"p"};
        this.help = "Displays or edits the profile of the specified user";
        this.arguments = "<user>";
        this.category = Categories.FUN;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        event.reply("Not available yet...");
    }
}

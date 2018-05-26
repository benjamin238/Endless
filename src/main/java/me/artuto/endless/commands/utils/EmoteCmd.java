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

package me.artuto.endless.commands.utils;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;

/**
 * @author Artuto
 */

public class EmoteCmd extends EndlessCommand
{
    public EmoteCmd()
    {
        this.name = "emote";
        this.aliases = new String[]{"emoji"};
        this.help = "Get the info of a specified emote.";
        this.arguments = "<emote>";
        this.category = Categories.UTILS;
        this.guildOnly = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {

    }
}

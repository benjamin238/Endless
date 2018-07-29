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
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;

/**
 * @author Artuto
 */

public class ChooseCmd extends EndlessCommand
{
    public ChooseCmd()
    {
        this.name = "choose";
        this.aliases = new String[]{"pickone"};
        this.help = "Chooses between the given options.";
        this.arguments = "<option 1> <option 2> ...";
        this.category = Categories.FUN;
        this.guildOnly = false;
        this.needsArgumentsMessage = "You didn't give me any choices!";
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        String[] options = event.getArgs().split("\\s+");

        if(options.length==1)
            event.replyWarning("You only gave me one option: `"+options[0]+"`");
        else
            event.reply("I choose `"+options[(int) (Math.random()*options.length)]+"`");
    }
}

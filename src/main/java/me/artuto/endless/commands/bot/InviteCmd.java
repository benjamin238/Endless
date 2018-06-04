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

package me.artuto.endless.commands.bot;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import net.dv8tion.jda.core.Permission;

/**
 * @author Artuto
 */

public class InviteCmd extends EndlessCommand
{
    public InviteCmd()
    {
        this.name = "invite";
        this.help = "Shows the bot invite";
        this.category = Categories.BOT;
        this.guildOnly = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        event.reply("So you want invite Endless to your server? Here you have it:\n"+":link: **<https://discordapp.com/api/oauth2/authorize?client_id="+event.getSelfUser().getId()+"&scope=bot&permissions=8>**");
    }
}
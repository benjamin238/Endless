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
import net.dv8tion.jda.core.entities.MessageChannel;

/**
 * @author Artuto
 */

public class Ping extends EndlessCommand
{
    public Ping()
    {
        this.name = "ping";
        this.help = "Pong!";
        this.category = Categories.BOT;
        this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guild = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        MessageChannel channel = event.getChannel();
        long time = System.currentTimeMillis();
        channel.sendMessage("Ping!").queue((message) -> message.editMessageFormat("Pong! - "+"Discord API Ping: "+event.getJDA().getPing()+"ms - Ping: %dms", System.currentTimeMillis()-time).queue());
    }
}

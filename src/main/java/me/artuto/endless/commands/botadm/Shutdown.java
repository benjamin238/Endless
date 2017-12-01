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

package me.artuto.endless.commands.botadm;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.DatabaseManager;
import net.dv8tion.jda.core.Permission;

/**
 *
 * @author Artu
 */

public class Shutdown extends Command
{
    private final DatabaseManager db;

    public Shutdown(DatabaseManager db)
    {
        this.db = db;
        this.name = "shutdown";
        this.aliases = new String[]{"quit", "exit", "close", "terminate"};
        this.help = "Turns Off the bot";
        this.category = Categories.BOTADM;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.reactSuccess();
        db.shutdown();
        event.getJDA().shutdown();
    }
}
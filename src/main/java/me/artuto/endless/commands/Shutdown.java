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

package me.artuto.endless.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.artuto.endless.management.Optimizer;
import net.dv8tion.jda.core.Permission;

/**
 *
 * @author Artu
 */

public class Shutdown extends Command
{
    public Shutdown()
    {
        this.name = "shutdown";
        this.help = "Turns OFf the bot";
        this.category = new Command.Category("Bot Administration");
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
        if(!(event.isOwner()) && !(event.isCoOwner()))
        {
            event.reactSuccess();
            try
            {
                Thread.sleep(5000);
            }
            catch (InterruptedException ignored) {}

            event.reply("Not really... "+event.getAuthor().getAsMention());

            return;   
        }
        
        event.reactSuccess();
        try
        {
            Thread.sleep(2000);
            Optimizer.shutdown();
        }
        catch (InterruptedException e)
        {
            event.replyError("An error happened when closing the bot, check the console for more information.");
            e.printStackTrace();
        }
        event.getJDA().shutdown();
    }
    
    
}

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
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;

/**
 *
 * @author Artu
 */

public class BotCPanel extends Command
{
    public BotCPanel()
    {
        this.name = "bot";
        this.help = "Controls ";
        this.category = new Command.Category("Bot Administration");
        this.children = new Command[]{new Status()};
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        if(!(event.isOwner()) || event.isCoOwner())
        {
            event.replyError("Sorry, but you don't have access to this command! Only Bot owners!");
            return;
        }
        if(event.getArgs().isEmpty())
        {
            event.replyError("Please execute a valid subcommand!");
            return;
        }
    }
    
    private class Status extends Command
    {
        public Status()
        {
            this.name = "status";
            this.help = "Sets the Online Status (OnlineStatus) of the bot.";
            this.category = new Command.Category("Bot Administration");
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildOnly = false;
       }
        
       @Override
       protected void execute(CommandEvent event)
       {
            if(!(event.isOwner()) || event.isCoOwner())
            {
                event.replyError("Sorry, but you don't have access to this command! Only Bot owners!");
                return;
                
            }
            if(event.getArgs().isEmpty())
            {
                event.replyError("Please provide me a valid OnlineStatus!");
                return;
            }
            if(event.getArgs().equals("help"))
            {
                event.replyInDM("Help for subcommand `bot status`\n"
                        + "Valid options: `ONLINE`, `DO_NOT_DISTURB`, `INVISIBLE`, `AWAY`");
                event.reactSuccess();
                return;
            }
            
            try
            {
               String status = event.getArgs();
               event.getJDA().getPresence().setStatus(OnlineStatus.valueOf(status));
               event.replySuccess("Changed status to "+event.getJDA().getPresence().getStatus()+" without error!");
            }
            catch(Exception e)
            {
                event.replyError("Error when changing the status! Check the Bot console for more information.");
                e.printStackTrace();
            }
       }
    }
    
    /*private class Game extends Command
    {
        public Game()
        {
            this.name = "game";
            this.help = "Sets the Game (Game.of) of the bot.";
            this.category = new Command.Category("Bot Administration");
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildOnly = false;
       }
        
       @Override
       protected void execute(CommandEvent event)
       {
            if(!(event.isOwner()) || event.isCoOwner())
            {
                event.replyError("Sorry, but you don't have access to this command! Only Bot owners!");
                return;
                
            }
            if(event.getArgs().isEmpty())
            {
                event.replySuccess("Game cleaned.");
            }
            
            try
            {
               String status = event.getArgs();
               event.getJDA().getPresence().setGame(Game.of(event.getArgs()));
               event.replySuccess("Changed game to "+event.getJDA().getPresence().getGame()+" without error!");
            }
            catch(Exception e)
            {
                event.replyError("Error when changing the status! Check the Bot console for more information.");
                e.printStackTrace();
            }
       }
    }*/
}


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

package me.artuto.endless.commands.botadm;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Const;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;

/**
 * @author Artuto
 */

public class BotCPanel extends EndlessCommand
{
    public BotCPanel()
    {
        this.name = "bot";
        this.help = "Controls the status, game, optimized the bot and other useful things.";
        this.category = Categories.BOTADM;
        this.children = new Command[]{new Status(), new Playing(), new DefaultGameUpdate(), new Optimize()};
        this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = true;
        this.guildCommand = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        String prefix = event.getClient().getPrefix();

        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Please choose a subcommand:\n"+"- `"+prefix+"bot status`: Sets the Online Status (OnlineStatus) of the bot.\n"+"- `"+prefix+"bot game`: Sets the Game (Game.of) of the bot.\n"+"- `"+prefix+"bot updategame`: Updates the default game.\n"+"- `"+prefix+"bot optimize`: Optimizes the Bot's RAM usage. Use with caution.\n");
        }
        else if(!(event.getArgs().contains("status")) || !(event.getArgs().contains("game") || !(event.getArgs().contains("updategame"))) || !(event.getArgs().contains("optimize")))
        {
            event.replyWarning("Please choose a subcommand:\n"+"- `"+prefix+"bot status`: Sets the Online Status (OnlineStatus) of the bot.\n"+"- `"+prefix+"bot game`: Sets the Game (Game.of) of the bot.\n"+"- `"+prefix+"bot updategame`: Updates the default game.\n"+"- `"+prefix+"bot optimize`: Optimizes the Bot's RAM usage. Use with caution.\n");
        }
    }

    private class Status extends EndlessCommand
    {
        Status()
        {
            this.name = "status";
            this.help = "Sets the Online Status (OnlineStatus) of the bot.";
            this.category = Categories.BOTADM;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = true;
            this.guildCommand = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyError("Please provide me a valid OnlineStatus!");
                return;
            }

            if(event.getArgs().equals("help"))
            {
                event.replyInDm("Help for subcommand `bot status`\n"+"Valid options: `ONLINE`, `DO_NOT_DISTURB`, `INVISIBLE`, `AWAY`");
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

    private class Playing extends EndlessCommand
    {
        Playing()
        {
            this.name = "game";
            this.help = "Sets the Game (Game.of) of the bot.";
            this.category = Categories.BOTADM;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = true;
            this.guildCommand = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                try
                {
                    event.getJDA().getPresence().setGame(null);
                    event.replySuccess("Game cleaned.");
                }
                catch(Exception e)
                {
                    event.replyError("Error when cleaning the game! Check the Bot console for more information.");
                    e.printStackTrace();
                }
            }
            else
            {
                try
                {
                    event.getJDA().getPresence().setGame(Game.playing(event.getArgs()));
                    event.replySuccess("Changed game to "+event.getJDA().getPresence().getGame().getName()+" without error!");
                }
                catch(Exception e)
                {
                    event.replyError("Error when changing the game! Check the Bot console for more information.");
                    e.printStackTrace();
                }
            }
        }
    }

    private class DefaultGameUpdate extends EndlessCommand
    {
        DefaultGameUpdate()
        {
            this.name = "updategame";
            this.help = "Updates the default game.";
            this.category = Categories.BOTADM;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = true;
            this.guildCommand = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            try
            {
                event.getJDA().getPresence().setGame(Game.playing("Type "+event.getClient().getPrefix()+"help | Version "+Const.VERSION+" | On "+event.getJDA().getGuilds().size()+" Guilds | "+event.getJDA().getUsers().size()+" Users | "+event.getJDA().getTextChannels().size()+" Channels"));
                event.replySuccess("Game updated.");
            }
            catch(Exception e)
            {
                event.replyError("Error when updating the game! Check the Bot console for more information.");
                e.printStackTrace();
            }
        }
    }

    private class Optimize extends EndlessCommand
    {
        Optimize()
        {
            this.name = "optimize";
            this.help = "Optimizes the Bot's RAM usage. Use with caution.";
            this.category = Categories.BOTADM;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = true;
            this.guildCommand = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            try
            {
                System.gc();
                event.reactSuccess();
            }
            catch(Exception e)
            {
                event.replyError("Error when optimizing the bot! Check the Bot console for more information.");
                e.printStackTrace();
            }
        }
    }
}


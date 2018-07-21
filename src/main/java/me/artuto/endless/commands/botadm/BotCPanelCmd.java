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
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.managers.Presence;

/**
 * @author Artuto
 */

public class BotCPanelCmd extends EndlessCommand
{
    public BotCPanelCmd()
    {
        this.name = "bot";
        this.help = "Controls the status, game, optimized the bot and other useful things.";
        this.category = Categories.BOTADM;
        this.children = new Command[]{new StatusCmd(), new PlayingCmd(), new DefaultGameUpdateCmd(), new OptimizeCmd()};
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        event.reply(Const.ENDLESS+" **Endless Control Panel** "+Const.ENDLESS+"\n" +
                "Please use a subcommand.");
    }

    private class StatusCmd extends EndlessCommand
    {
        StatusCmd()
        {
            this.name = "status";
            this.help = "Sets the Online Status (OnlineStatus) of the bot.";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Please provide me a valid OnlineStatus!";
            this.parent = BotCPanelCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            JDA jda = event.getJDA();
            jda.asBot().getShardManager().getShards().forEach(shard -> {
                try
                {
                    String status = event.getArgs().toUpperCase();
                    event.getJDA().getPresence().setStatus(OnlineStatus.valueOf(status));
                }
                catch(Exception e)
                {
                    event.replyError("Error when changing the status of shard "+(shard.getShardInfo().getShardId()+1)+
                            "! Check the Bot console for more information.");
                    e.printStackTrace();
                }
            });
            event.replySuccess("Changed the status to "+event.getJDA().getPresence().getStatus()+" across "
                    +jda.asBot().getShardManager().getShardsTotal()+" shards");
        }
    }

    private class PlayingCmd extends EndlessCommand
    {
        PlayingCmd()
        {
            this.name = "game";
            this.help = "Sets the Game (Game.playing) of the bot.";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArguments = false;
            this.parent = BotCPanelCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            JDA jda = event.getJDA();
            jda.asBot().getShardManager().getShards().forEach(shard -> {
                if(event.getArgs().isEmpty())
                {
                    try
                    {
                        shard.getPresence().setGame(null);
                    }
                    catch(Exception e)
                    {
                        event.replyError("Error when changing the game of shard "+(shard.getShardInfo().getShardId()+1)+
                                "! Check the Bot console for more information.");
                        e.printStackTrace();
                    }
                }
                else
                {
                    try
                    {
                        shard.getPresence().setGame(Game.playing(event.getArgs()));
                    }
                    catch(Exception e)
                    {
                        event.replyError("Error when changing the game of shard "+(shard.getShardInfo().getShardId()+1)+
                                "! Check the Bot console for more information.");
                        e.printStackTrace();
                    }
                }
            });
            event.replySuccess("Changed the game to "+event.getJDA().getPresence().getGame().getName()+" across "
                    +jda.asBot().getShardManager().getShardsTotal()+" shards");
        }
    }

    private class DefaultGameUpdateCmd extends EndlessCommand
    {
        DefaultGameUpdateCmd()
        {
            this.name = "updategame";
            this.help = "Updates the default game.";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArguments = false;
            this.parent = BotCPanelCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            JDA jda = event.getJDA();
            jda.asBot().getShardManager().getShards().forEach(shard -> {
                JDA.ShardInfo shardInfo = shard.getShardInfo();
                Presence presence = shard.getPresence();
                try
                {
                    presence.setGame(Game.playing("Type "+event.getClient().getPrefix()+"help | Version "
                            +Const.VERSION+" | On "+shard.getGuildCache().size()+" Guilds | "+shard.getUserCache().size()+
                            " Users | Shard "+(shardInfo.getShardId()+1)));
                }
                catch(Exception e)
                {
                    event.replyError("Error when updating the game of shard "+(shard.getShardInfo().getShardId()+1)+
                            "! Check the Bot console for more information.");
                    e.printStackTrace();
                }
            });
            event.replySuccess("Updated the game to "+event.getJDA().getPresence().getGame().getName()+" across "
                    +jda.asBot().getShardManager().getShardsTotal()+" shards");
        }
    }

    private class OptimizeCmd extends EndlessCommand
    {
        OptimizeCmd()
        {
            this.name = "optimize";
            this.help = "Optimizes the Bot's RAM usage. Use with caution.";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArguments = false;
            this.parent = BotCPanelCmd.this;
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


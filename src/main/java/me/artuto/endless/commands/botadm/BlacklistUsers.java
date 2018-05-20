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
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BlacklistUsers extends EndlessCommand
{
    private final Bot bot;

    public BlacklistUsers(Bot bot)
    {
        this.bot = bot;
        this.name = "blacklistuser";
        this.help = "Adds, removes or displays the list with blacklisted users.";
        this.category = Categories.BOTADM;
        this.children = new Command[]{new Add(), new Remove(), new Check(), new BlacklistList()};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        String prefix = event.getClient().getPrefix();

        if(event.getArgs().isEmpty())
            event.replyWarning("Please choose a subcommand:\n"+"- `"+prefix+"blacklistuser add`: Adds a user ID to the blacklisted users list.\n"+"- `"+prefix+"blacklistuser remove`: Removes a user ID from the blacklisted users list.\n"+"- `"+prefix+"blacklistuser list`: Displays blacklisted users.\n"+"- `"+prefix+"blacklistuser check`: Checks if a user ID is blacklisted.");
        else if(!(event.getArgs().contains("add")) || !(event.getArgs().contains("remove")) || !(event.getArgs().contains("list") || !(event.getArgs().contains("check"))))
            event.replyWarning("Please choose a subcommand:\n"+"- `"+prefix+"blacklistuser add`: Adds a user ID to the blacklisted users list.\n"+"- `"+prefix+"blacklistuser remove`: Removes a user ID from the blacklisted users list.\n"+"- `"+prefix+"blacklistuser list`: Displays blacklisted users.\n"+"- `"+prefix+"blacklistuser check`: Checks if a user ID is blacklisted.");
    }

    private class Add extends EndlessCommand
    {
        Add()
        {
            this.name = "add";
            this.help = "Adds a user ID to the blacklisted users list.";
            this.arguments = "<user ID>";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            User user;

            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Please specify a user ID!");
                return;
            }

            try
            {
                user = event.getJDA().retrieveUserById(event.getArgs()).complete();
            }
            catch(Exception e)
            {
                event.replyError("That ID isn't valid!");
                return;
            }

            if(bot.bdm.isBlacklisted(user.getIdLong()))
            {
                event.replyError("That user is already on the blacklist!");
                return;
            }

            try
            {
                bot.bdm.addBlacklist(user.getIdLong(), null, Const.BlacklistType.USER);
                event.replySuccess("Added **"+user.getName()+"#"+user.getDiscriminator()+"** to the blacklist.");
            }
            catch(Exception e)
            {
                event.replyError("Something went wrong when adding the user: \n```"+e+"```");
            }
        }
    }

    private class Remove extends EndlessCommand
    {
        Remove()
        {
            this.name = "remove";
            this.help = "Removes a user ID to the blacklisted users list.";
            this.arguments = "<user ID>";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            User user;

            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Please specify a user ID!");
                return;
            }

            try
            {
                user = event.getJDA().retrieveUserById(event.getArgs()).complete();
            }
            catch(Exception e)
            {
                event.replyError("That ID isn't valid!");
                return;
            }

            try
            {
                if(!(bot.bdm.isBlacklisted(user.getIdLong())))
                {
                    event.replyError("That ID isn't in the blacklist!");
                    return;
                }
            }
            catch(Exception e)
            {
                event.replyError("Something went wrong when getting the blacklisted users list: \n```"+e+"```");
                return;
            }

            try
            {
                bot.bdm.removeBlacklist(user.getIdLong());
                event.replySuccess("Removed **"+user.getName()+"#"+user.getDiscriminator()+"** from the blacklist.");
            }
            catch(Exception e)
            {
                event.replyError("Something went wrong when writing to the blacklisted users file: \n```"+e+"```");
            }
        }
    }

    private class BlacklistList extends EndlessCommand
    {
        BlacklistList()
        {
            this.name = "list";
            this.help = "Displays blacklisted users.";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            List<User> list;
            EmbedBuilder builder = new EmbedBuilder();
            Color color;

            if(event.isFromType(ChannelType.PRIVATE))
                color = Color.decode("#33ff00");
            else
                color = event.getGuild().getSelfMember().getColor();

            try
            {
                list = bot.bdm.getBlacklistedUsers(event.getJDA());

                if(list.isEmpty())
                    event.reply("The list is empty!");
                else
                {
                    builder.setDescription(list.stream().map(u -> u.getName()+"#"+u.getDiscriminator()+" (ID: "+u.getId()+")").collect(Collectors.joining("\n")));
                    builder.setFooter(event.getSelfUser().getName()+"'s Blacklisted Users", event.getSelfUser().getEffectiveAvatarUrl());
                    builder.setColor(color);
                    event.reply(builder.build());
                }
            }
            catch(Exception e)
            {
                event.replyError("Something went wrong when getting the blacklisted users list: \n```"+e+"```");
            }
        }
    }

    private class Check extends EndlessCommand
    {
        Check()
        {
            this.name = "check";
            this.help = "Checks if a user ID is blacklisted.";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            User user;

            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Please specify a user ID!");
                return;
            }

            try
            {
                user = event.getJDA().retrieveUserById(event.getArgs()).complete();
            }
            catch(Exception e)
            {
                event.replyError("That ID isn't valid!");
                return;
            }

            try
            {
                if(!(bot.bdm.isBlacklisted(user.getIdLong())))
                    event.replySuccess("**"+user.getName()+"#"+user.getDiscriminator()+"** isn't blacklisted!");
                else
                    event.replySuccess("**"+user.getName()+"#"+user.getDiscriminator()+"** is blacklisted!");
            }
            catch(Exception e)
            {
                event.replyError("Something went wrong when getting the blacklisted users list: \n```"+e+"```");
            }
        }
    }
}

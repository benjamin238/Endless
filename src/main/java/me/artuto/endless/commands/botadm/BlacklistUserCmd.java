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
import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.core.entities.Blacklist;
import me.artuto.endless.core.entities.BlacklistType;
import me.artuto.endless.utils.ArgsUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;

public class BlacklistUserCmd extends EndlessCommand
{
    private final Bot bot;

    public BlacklistUserCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "blacklistuser";
        this.help = "Adds, removes or displays the list with blacklisted users.";
        this.category = Categories.BOTADM;
        this.children = new Command[]{new AddCmd(), new RemoveCmd(), new CheckCmd(), new BlacklistListCmd()};
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        event.replyWarning("Please specify a subcommand!");
    }

    private class AddCmd extends EndlessCommand
    {
        AddCmd()
        {
            this.name = "add";
            this.help = "Adds a user ID to the blacklisted users list.";
            this.arguments = "<user ID> for [reason]";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Please specify a user ID and a reason!";
            this.parent = BlacklistUserCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            String[] args = ArgsUtils.splitWithReason(2, event.getArgs(), " for ");

            event.getJDA().retrieveUserById(args[0]).queue(user -> {
                if(!(bot.endless.getBlacklist(user.getIdLong())==null))
                {
                    event.replyError("That user is already on the blacklist!");
                    return;
                }

                bot.bdm.addBlacklist(BlacklistType.USER, user.getIdLong(), OffsetDateTime.now().toInstant().toEpochMilli(), args[1]);
                event.replySuccess("Added **"+user.getName()+"#"+user.getDiscriminator()+"** to the blacklist.");
            }, e -> event.replyError("That ID isn't valid!"));
        }
    }

    private class RemoveCmd extends EndlessCommand
    {
        RemoveCmd()
        {
            this.name = "remove";
            this.help = "Removes a user ID to the blacklisted users list.";
            this.arguments = "<user ID>";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Please specify a user ID!";
            this.parent = BlacklistUserCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            event.getJDA().retrieveUserById(event.getArgs()).queue(user -> {
                if(bot.endless.getBlacklist(user.getIdLong())==null)
                {
                    event.replyError("That ID isn't in the blacklist!");
                    return;
                }

                bot.bdm.removeBlacklist(user.getIdLong());
                event.replySuccess("Removed **"+user.getName()+"#"+user.getDiscriminator()+"** from the blacklist.");
            }, e -> event.replyError("That ID isn't valid!"));
        }
    }

    private class BlacklistListCmd extends EndlessCommand
    {
        BlacklistListCmd()
        {
            this.name = "list";
            this.help = "Displays blacklisted users.";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArguments = false;
            this.parent = BlacklistUserCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            List<Blacklist> list;
            EmbedBuilder builder = new EmbedBuilder();
            Color color;

            if(event.isFromType(ChannelType.PRIVATE))
                color = Color.decode("#33ff00");
            else
                color = event.getGuild().getSelfMember().getColor();

            list = bot.endless.getUserBlacklists();

            if(list.isEmpty())
                event.reply("The list is empty!");
            else
            {
                StringBuilder sb = new StringBuilder();
                list.forEach(b -> event.getJDA().retrieveUserById(b.getId()).queue(user ->
                    sb.append(user.getName()).append("#").append(user.getDiscriminator()).append(" (ID: ").append(user.getId()).append(")\n"),
                        e -> {}));
                builder.setDescription(sb.toString().isEmpty()?"None":sb);
                builder.setFooter(event.getSelfUser().getName()+"'s Blacklisted Users", event.getSelfUser().getEffectiveAvatarUrl());
                builder.setColor(color);
                event.reply(builder.build());
            }
        }
    }

    private class CheckCmd extends EndlessCommand
    {
        CheckCmd()
        {
            this.name = "check";
            this.help = "Checks if a user ID is blacklisted.";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Please specify a user ID!";
            this.parent = BlacklistUserCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Please specify a user ID!");
                return;
            }

            event.getJDA().retrieveUserById(event.getArgs()).queue(user -> {
                Blacklist blacklist = bot.endless.getBlacklist(user.getIdLong());
                if(blacklist==null)
                    event.replySuccess("**"+user.getName()+"#"+user.getDiscriminator()+"** isn't blacklisted!");
                else
                {
                    String reason = blacklist.getReason()==null?"[no reason provided]":blacklist.getReason();
                    event.replyWarning("**"+user.getName()+"#"+user.getDiscriminator()+"** is blacklisted!\n" +
                            "Reason: `"+reason+"`");
                }
            }, e -> event.replyError("That ID isn't valid!"));
        }
    }
}

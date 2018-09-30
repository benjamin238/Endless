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
import net.dv8tion.jda.core.entities.Guild;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;

public class BlacklistGuildCmd extends EndlessCommand
{
    private final Bot bot;

    public BlacklistGuildCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "blacklistguild";
        this.help = "Adds, removes or displays the list with blacklisted guilds.";
        this.category = Categories.BOTADM;
        this.children = new Command[]{new AddCmd(), new RemoveCmd(), new CheckCmd(), new BlacklistListCmd()};
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
            this.help = "Adds a guild ID to the blacklisted guilds list.";
            this.arguments = "<guild ID> for [reason]";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Please specify a Guild ID and a reason!";
            this.parent = BlacklistGuildCmd.this;
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
            Guild guild = event.getJDA().asBot().getShardManager().getGuildCache().getElementById(args[0]);

            if(guild==null)
            {
                event.replyError("That ID isn't valid!");
                return;
            }

            if(!(bot.endless.getBlacklist(guild.getIdLong())==null))
            {
                event.replyError("That guild is already on the blacklist!");
                return;
            }

            bot.bdm.addBlacklist(BlacklistType.GUILD, guild.getIdLong(), OffsetDateTime.now().toInstant().toEpochMilli(), args[1]);
            event.replySuccess("Added **"+guild.getName()+"** to the blacklist.");
            // guild.leave().queue();
        }
    }

    private class RemoveCmd extends EndlessCommand
    {
        RemoveCmd()
        {
            this.name = "remove";
            this.help = "Removes a guild ID to the blacklisted guilds list.";
            this.arguments = "<guild ID>";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Please specify a Guild ID!";
            this.parent = BlacklistGuildCmd.this;
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

            Guild guild = event.getJDA().asBot().getShardManager().getGuildCache().getElementById(event.getArgs());

            if(guild==null)
            {
                event.replyError("That ID isn't valid!");
                return;
            }

            if(bot.endless.getBlacklist(guild.getIdLong())==null)
            {
                event.replyError("That ID isn't in the blacklist!");
                return;
            }

            bot.bdm.removeBlacklist(guild.getIdLong());
            event.replySuccess("Removed **"+guild.getName()+"** from the blacklist.");
        }
    }

    private class BlacklistListCmd extends EndlessCommand
    {
        BlacklistListCmd()
        {
            this.name = "list";
            this.help = "Displays blacklisted guilds.";
            this.category = Categories.BOTADM;
            this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArguments = false;
            this.parent = BlacklistGuildCmd.this;
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

            list = bot.endless.getGuildBlacklists();

            if(list.isEmpty())
                event.reply("The list is empty!");
            else
            {
                StringBuilder sb = new StringBuilder();
                list.forEach(b -> {
                    Guild guild = event.getJDA().asBot().getShardManager().getGuildById(b.getId());
                    if(!(guild==null))
                        sb.append(guild.getName()).append(" (ID: ").append(guild.getId()).append(")\n");
                });
                builder.setDescription(sb.toString().isEmpty()?"None":sb);
                builder.setFooter(event.getSelfUser().getName()+"'s Blacklisted Guilds", event.getSelfUser().getEffectiveAvatarUrl());
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
            this.help = "Checks if a guild ID is blacklisted.";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Please specify a guild ID!";
            this.parent = BlacklistGuildCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            Blacklist blacklist = bot.endless.getBlacklist(Long.valueOf(event.getArgs()));
            if(blacklist==null)
            {
                Guild guild = event.getJDA().asBot().getShardManager().getGuildById(event.getArgs());

                if(guild==null)
                    event.replySuccess("The ID "+event.getArgs()+" isn't blacklisted!");
                else
                    event.replySuccess("**"+guild.getName()+"** isn't blacklisted!");
            }
            else
            {
                Guild guild = event.getJDA().asBot().getShardManager().getGuildById(event.getArgs());
                String reason = blacklist.getReason()==null?"[no reason provided]":blacklist.getReason();

                if(guild==null)
                    event.replyWarning("**"+event.getArgs()+"** is blacklisted!\n" +
                            "Reason: `"+reason+"`");
                else
                    event.replyWarning("**"+guild.getName()+"** is blacklisted!\n" +
                            "Reason: `"+reason+"`");
            }
        }
    }
}

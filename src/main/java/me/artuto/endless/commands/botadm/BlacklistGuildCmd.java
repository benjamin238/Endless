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
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.core.entities.Blacklist;
import me.artuto.endless.utils.ArgsUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Map;

public class BlacklistGuildCmd extends EndlessCommand
{
    private final Bot bot;

    public BlacklistGuildCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "blacklistguild";
        this.help = "Adds, removes or displays the list with blacklisted guilds.";
        this.category = Categories.BOTADM;
        this.children = new Command[]{new Add(), new Remove(), new Check(), new BlacklistList()};
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        event.replyWarning("Please specify a subcommand!");
    }

    private class Add extends EndlessCommand
    {
        Add()
        {
            this.name = "add";
            this.help = "Adds a guild ID to the blacklisted guilds list.";
            this.arguments = "<guild ID> for [reason]";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Please specify a Guild ID and a reason!";
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            String[] args = ArgsUtils.splitWithReason(2, event.getArgs(), " for ");
            Guild guild = event.getJDA().getGuildCache().getElementById(args[0]);

            if(guild==null)
            {
                event.replyError("That ID isn't valid!");
                return;
            }

            if(!(bot.bdm.getBlacklist(guild.getIdLong())==null))
            {
                event.replyError("That guild is already on the blacklist!");
                return;
            }

            bot.bdm.addBlacklist(Const.BlacklistType.GUILD, guild.getIdLong(), OffsetDateTime.now().toInstant().toEpochMilli(), args[1]);
            event.replySuccess("Added **"+guild.getName()+"** to the blacklist.");
            guild.leave().queue();
        }
    }

    private class Remove extends EndlessCommand
    {
        Remove()
        {
            this.name = "remove";
            this.help = "Removes a guild ID to the blacklisted guilds list.";
            this.arguments = "<guild ID>";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Please specify a Guild ID!";
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Please specify a user ID!");
                return;
            }

            Guild guild = event.getJDA().getGuildCache().getElementById(event.getArgs());

            if(guild==null)
            {
                event.replyError("That ID isn't valid!");
                return;
            }

            if(bot.bdm.getBlacklist(guild.getIdLong())==null)
            {
                event.replyError("That ID isn't in the blacklist!");
                return;
            }

            bot.bdm.removeBlacklist(guild.getIdLong());
            event.replySuccess("Removed **"+guild.getName()+"** from the blacklist.");
        }
    }

    private class BlacklistList extends EndlessCommand
    {
        BlacklistList()
        {
            this.name = "list";
            this.help = "Displays blacklisted guilds.";
            this.category = Categories.BOTADM;
            this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArguments = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            Map<Blacklist, Guild> map;
            EmbedBuilder builder = new EmbedBuilder();
            Color color;

            if(event.isFromType(ChannelType.PRIVATE))
                color = Color.decode("#33ff00");
            else
                color = event.getGuild().getSelfMember().getColor();

            map = bot.bdm.getBlacklistedGuilds(event.getJDA());

            if(map.isEmpty())
                event.reply("The list is empty!");
            else
            {
                StringBuilder sb = new StringBuilder();
                map.forEach((b, g) -> sb.append(g.getName()).append(" (ID: ").append(g.getId()).append(")"));
                builder.setDescription(sb);
                builder.setFooter(event.getSelfUser().getName()+"'s Blacklisted Guilds", event.getSelfUser().getEffectiveAvatarUrl());
                builder.setColor(color);
                event.reply(builder.build());
            }
        }
    }

    private class Check extends EndlessCommand
    {
        Check()
        {
            this.name = "check";
            this.help = "Checks if a guild ID is blacklisted.";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Please specify a guild ID!";
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            Blacklist blacklist = bot.bdm.getBlacklist(Long.valueOf(event.getArgs()));
            if(blacklist==null)
            {
                Guild guild = event.getJDA().getGuildCache().getElementById(event.getArgs());

                if(guild==null)
                    event.replySuccess("The ID "+event.getArgs()+" isn't blacklisted!");
                else
                    event.replySuccess("**"+guild.getName()+"** isn't blacklisted!");
            }
            else
            {
                Guild guild = event.getJDA().getGuildCache().getElementById(event.getArgs());
                String reason = blacklist.getReason()==null?"[no reason provided]":blacklist.getReason();

                if(guild==null)
                    event.replyWarning("The ID "+event.getArgs()+" is blacklisted!");
                else
                    event.replyWarning("**"+guild.getName()+"** is blacklisted!\n" +
                            "Reason: `"+reason+"`");
            }
        }
    }
}

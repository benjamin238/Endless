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

package me.artuto.endless.commands.tools;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;

import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Artuto
 */

public class PollCmd extends EndlessCommand
{
    private final Bot bot;

    public PollCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "poll";
        this.help = "Run a poll with options. Execute `e!"+
                "poll flags` to get a list of all the supported flags.";
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        if(!(bot.dataEnabled))
        {
            event.replyError("Endless is running on No-data mode.");
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();

        if(event.getArgs().equalsIgnoreCase("flags"))
        {
            builder.setTitle("Available flags:");
            builder.setDescription("`-t` - Sets how long the poll will be.\n" +
                    "`-e` - Emotes to apply to the poll.\n" +
                    "`-d` - Sets the description of the poll.\n" +
                    "`-c` - The color of the poll.\n");
            builder.setColor(Color.YELLOW);
            event.replyInDm(builder.build(), s -> event.reactSuccess(),
                    e -> event.replyWarning("Help cannot be sent because you are blocking Drect Messages."));
        }
        else
        {
            Color color;
            int time;
            List<Emote> emotes = event.getMessage().getEmotes().stream().filter(e -> !(e.isFake()))
                    .collect(Collectors.toList());
            String[] args = splitArgs(event.getArgs());
            if(args[0].isEmpty())
            {
                event.replyWarning("I could not determine a question for the poll!");
                return;
            }
            color = getColor(args[1]);
            time = ArgsUtils.parseTime(args[3]);
            int minutes = time/60;
            Instant endTime = Instant.now().plus(minutes, ChronoUnit.MINUTES);
            if(time<0)
            {
                event.replyError("The time cannot be negative!");
                return;
            }

            builder.setTitle(args[0]);
            builder.setColor(color==null?event.getMember().getColor():color);
            if(!(args[2].isEmpty()))
                builder.setDescription(args[2]);
            builder.setFooter("This poll will expire", event.getAuthor().getEffectiveAvatarUrl());
            builder.setTimestamp(endTime);
            event.getTextChannel().sendMessage(builder.build()).queue(msg -> {
                bot.pldm.createPoll(endTime.toEpochMilli(), event.getGuild().getIdLong(),
                        msg.getIdLong(), event.getTextChannel().getIdLong());

                if(emotes.isEmpty())
                {
                    msg.addReaction("\uD83D\uDC4D").queue();
                    msg.addReaction("\uD83D\uDC4E").queue();
                }
                else
                    emotes.forEach(e -> msg.addReaction(e).queue());
            });
        }
    }

    private Color getColor(String color)
    {
        try
        {
            if(!(color.startsWith("#")))
                color = "#"+color;
            return Color.decode(color);
        }
        catch(NumberFormatException e)
        {
            return null;
        }
    }

    private String[] splitArgs(String preArgs)
    {
        String[] args = preArgs.split(" \\| ");
        String color = "";
        String description = "";
        String emotes = "";
        String time = "10s";
        String question = "";

        for(String part : args)
        {
            if(!(part.startsWith("-")))
                question = part;
            else if(part.startsWith("-c"))
                color = part.replace("-c ", "");
            else if(part.startsWith("-d"))
                description = part.replace("-d ", "");
            else if(part.startsWith("-e"))
                emotes = part.replace("-e ", "");
            else if(part.startsWith("-t"))
                time = part.replaceAll("-t ", "");
        }

        return new String[]{question.trim(), color.trim(), description.trim(), time.trim(), emotes.trim()};
    }
}

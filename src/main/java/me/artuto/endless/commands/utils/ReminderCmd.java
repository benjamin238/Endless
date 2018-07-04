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

package me.artuto.endless.commands.utils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.core.entities.Reminder;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author Artuto
 */

public class ReminderCmd extends EndlessCommand
{
    private final Bot bot;

    public ReminderCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "reminder";
        this.aliases = new String[]{"remindme", "remind"};
        this.children = new Command[]{new Create(), new Delete()};
        this.help = "Shows the list of reminders.";
        this.category = Categories.UTILS;
        this.needsArguments = false;
        this.guildOnly = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        User author = event.getAuthor();
        List<Reminder> reminders = bot.rdm.getRemindersByUser(author.getIdLong());
        if(reminders.isEmpty())
        {
            event.replyWarning("You don't have any reminder currently active!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("**").append(reminders.size()).append("** reminders:");
        for(Reminder r : reminders)
        {
            TextChannel tc = event.getJDA().asBot().getShardManager().getTextChannelById(r.getChannelId());
            sb.append("\n`").append(reminders.indexOf(r)).append(".` ").append(tc==null?"Direct Message":tc.getAsMention());
            sb.append(" - \"").append(r.getMessage().length()>20?r.getMessage().substring(0, 20)+"...":r.getMessage()).append("\" in ")
                    .append(FormatUtil.formatTimeFromSeconds(OffsetDateTime.now().until(r.getExpiryTime(), ChronoUnit.SECONDS)));
        }
        event.replySuccess(sb.toString());
    }

    private class Create extends EndlessCommand
    {
        Create()
        {
            this.name = "create";
            this.aliases = new String[]{"add"};
            this.help = "Creates a reminder.";
            this.arguments = "<time> <message>";
            this.guildOnly = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            User author = event.getAuthor();
            List<Reminder> reminders = bot.rdm.getRemindersByUser(author.getIdLong());
            if(reminders.size()>100)
            {
                event.replyError("You can't have more than 100 active reminders!");
                return;
            }
            String[] args = splitArgs(event.getArgs());
            if(args[0].equals("0") || args[0].equals("-1"))
            {
                event.replyError("You didn't provide a valid time!");
                return;
            }
            if(args[1].isEmpty())
            {
                event.replyError("You didn't provide a message!");
                return;
            }
            int time = Integer.valueOf(args[0]);
            int minutes = time/60;
            Instant expiryTime = Instant.now().plus(minutes, ChronoUnit.MINUTES);
            if(time<0)
            {
                event.replyError("The time cannot be negative!");
                return;
            }
            String formattedTime = FormatUtil.formatTimeFromSeconds(time);

            bot.rdm.createReminder(event.getChannel().getIdLong(), expiryTime.toEpochMilli(), event.getAuthor().getIdLong(), args[1]);
            event.replySuccess("Set reminder to expire in "+formattedTime);
        }
    }

    private class Delete extends EndlessCommand
    {
        Delete()
        {
            this.name = "delete";
            this.aliases = new String[]{"remove"};
            this.arguments = "<reminder id>";
            this.help = "Deletes a reminder.";
            this.guildOnly = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            User author = event.getAuthor();
            List<Reminder> reminders = bot.rdm.getRemindersByUser(author.getIdLong());
            if(reminders.isEmpty())
            {
                event.replyWarning("You don't have any reminder currently active!");
                return;
            }

            long id;
            try
            {
                id = Long.parseLong(event.getArgs());
            }
            catch(NumberFormatException e)
            {
                event.replyError("The reminder ID should be a number between 0 and 100!");
                return;
            }

            if(reminders.size()>id+1 || reminders.size()<id+1)
            {
                event.replyError("A reminder with that ID couldn't be found!");
                return;
            }

            Reminder reminder = reminders.get((int)id);
            bot.rdm.deleteReminder(reminder.getId(), author.getIdLong());
            event.replySuccess("Successfully removed reminder with ID "+id);
        }
    }

    private String[] splitArgs(String preArgs)
    {
        try
        {
            String[] args = preArgs.split("\\s", 2);
            return new String[]{String.valueOf(ArgsUtils.parseTime(args[0])), args[1]};
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return new String[]{preArgs, ""};
        }
    }
}

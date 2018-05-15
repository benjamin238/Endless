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
import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.ProfileDataManager;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRulesException;
import java.util.List;

public class TimeFor extends EndlessCommand
{
    private final Bot bot;

    public TimeFor(Bot bot)
    {
        this.bot = bot;
        this.name = "timefor";
        this.aliases = new String[]{"tf"};
        this.children = new Command[]{new Change(), new TList()};
        this.help = "Shows the timezone for the specified user";
        this.arguments = "<user>";
        this.category = Categories.UTILS;
        this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildCommand = true;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        ProfileDataManager.Profile p;
        ZonedDateTime t;
        ZoneId zone;
        String time;
        String time24;
        String name;
        User user;

        if(event.getArgs().isEmpty())
        {
            user = event.getAuthor();
            p = bot.prdm.getProfile(user);
            name = "**"+user.getName()+"#"+user.getDiscriminator()+"**";

            if(!(bot.prdm.hasAProfile(user))) event.replyError("You don't have a timezone configured!");
            else
            {
                try
                {
                    zone = ZoneId.of(p.timezone);
                }
                catch(ZoneRulesException e)
                {
                    event.replyError("`"+p.timezone+"` isn't a valid timezone!");
                    return;
                }

                t = event.getMessage().getCreationTime().atZoneSameInstant(zone);
                time = t.format(DateTimeFormatter.ofPattern("h:mma"));
                time24 = t.format(DateTimeFormatter.ofPattern("HH:mm"));

                event.reply(":clock1: The time for "+name+" is `"+time+"` (`"+time24+"`)");
            }
        }
        else
        {
            List<Member> list = FinderUtil.findMembers(event.getArgs(), event.getGuild());

            if(list.isEmpty())
            {
                event.replyWarning("I was not able to found a user with the provided arguments: '"+event.getArgs()+"'");
                return;
            }
            else if(list.size()>1)
            {
                event.replyWarning(FormatUtil.listOfMembers(list, event.getArgs()));
                return;
            }
            else user = list.get(0).getUser();

            p = bot.prdm.getProfile(user);
            name = "**"+user.getName()+"#"+user.getDiscriminator()+"**";

            if(!(bot.prdm.hasAProfile(user))) event.replyError(name+" doesn't has a timezone configured!");
            else
            {
                try
                {
                    zone = ZoneId.of(p.timezone);
                }
                catch(ZoneRulesException e)
                {
                    event.replyError("`"+p.timezone+"` isn't a valid timezone!");
                    return;
                }

                t = event.getMessage().getCreationTime().atZoneSameInstant(zone);
                time = t.format(DateTimeFormatter.ofPattern("h:mma"));
                time24 = t.format(DateTimeFormatter.ofPattern("HH.mm"));

                event.reply(":clock1: The time for "+name+" is `"+time+"` (`"+time24+"`)");
            }
        }
    }

    private class Change extends EndlessCommand
    {
        Change()
        {
            this.name = "change";
            this.aliases = new String[]{"set"};
            this.help = "Changes your timezone";
            this.arguments = "<timezone>";
            this.category = Categories.FUN;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildCommand = true;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            String args = event.getArgs();

            if(args.isEmpty())
            {
                event.replyWarning("Please specify a timezone!");
                return;
            }

            try
            {
                ZoneId.of(args);
            }
            catch(ZoneRulesException e)
            {
                event.replyError("Please specify a valid timezone!");
                return;
            }

            bot.prdm.setTimezone(event.getAuthor(), args);
            event.replySuccess("Successfully updated timezone!");
        }
    }

    private class TList extends EndlessCommand
    {
        TList()
        {
            this.name = "list";
            this.aliases = new String[]{"timezones"};
            this.help = "Shows the list with valid timezones";
            this.category = Categories.FUN;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildCommand = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            event.replySuccess("Here is the list: ");
            try
            {
                event.getChannel().sendFile(new URL("https://endless.artuto.me/files/Timezones.txt").openStream(), "Timezones.txt", null).queue();
            }
            catch(IOException e)
            {
                event.replyError("Error when uploading the list, please visit **https://endless.artuto.me/files/Timezones.txt** to see the list.");
            }
        }
    }


}

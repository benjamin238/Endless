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

package me.artuto.endless.commands.serverconfig;

import com.jagrosh.jdautilities.command.Command;
import me.artuto.endless.commands.EndlessCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.GuildSettingsDataManager;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Starboard extends EndlessCommand
{
    private final GuildSettingsDataManager db;
    private final EventWaiter ew;

    public Starboard(GuildSettingsDataManager db, EventWaiter waiter)
    {
        this.db = db;
        this.ew = waiter;
        this.name = "starboard";
        this.children = new Command[]{new SetChannel(), new SetCount()};
        this.aliases = new String[]{"sb"};
        this.help = "If no valid arguments are given the setup to install the starboard is launched.";
        this.category = Categories.SERVER_CONFIG;
        this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        this.ownerCommand = false;
        this.guildCommand = true;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        event.replySuccess("Hi! Welcome to the Endless' Starboard Setup. This will automagically install an starboard on your server, I only need some "+"information to continue.");

        waitForChannel(event);
    }

    private void waitForChannel(CommandEvent event)
    {
        event.replySuccess("Alright! Lets start; First, Do you want to create a new channel or use a channel already created?\n"+"Type **\"create\"** to create a new channel and automatically setup permissions.\n"+"Type **\"created <channel name>\"** to use an already created channel.\n"+"Type **\"cancel\"** to cancel the setup.");

        ew.waitForEvent(GuildMessageReceivedEvent.class, e -> event.getAuthor().equals(e.getAuthor()) && event.getTextChannel().equals(e.getChannel()), e ->
        {
            switch(e.getMessage().getContentRaw().split(" ", 2)[0].toLowerCase())
            {
                case "cancel":
                    event.replyWarning("Alright, setup cancelled.");
                    break;
                case "create":
                    if(!(event.getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)) || !(event.getSelfMember().hasPermission(Permission.MANAGE_PERMISSIONS)))
                    {
                        event.replyError("An error happened when creating the channel and setting the permissions! Check I have the proper permissions.");
                        waitForChannel(event);
                        return;
                    }

                    event.replySuccess("Ok, this will create a channel named **\"starboard\"**, you can change the name later.", m -> m.editMessage("Starting setup...").queueAfter(3, TimeUnit.SECONDS, (s) ->
                    {
                        event.getGuild().getController().createTextChannel("starboard").queue(tc -> tc.createPermissionOverride(event.getGuild().getPublicRole()).setDeny(Permission.MESSAGE_WRITE).queue(self -> self.getChannel().createPermissionOverride(event.getSelfMember()).setAllow(Permission.MESSAGE_WRITE).queue(next ->
                        {
                            event.replySuccess("Channel created successfully!");
                            db.setStarboardChannel(event.getGuild(), (TextChannel) tc);
                            waitForStarCount(event);
                        })));
                    }));
                    break;
                case "created":
                    String tc;

                    try
                    {
                        String[] args = e.getMessage().getContentRaw().split(" ", 2);
                        tc = args[1].trim();
                    }
                    catch(ArrayIndexOutOfBoundsException ex)
                    {
                        event.replyWarning("You didn't provide me a channel!");
                        waitForChannel(event);
                        return;
                    }

                    List<TextChannel> list = FinderUtil.findTextChannels(tc, event.getGuild());
                    if(list.isEmpty()) event.replyWarning("No Text Channels found matching \""+tc+"\"");
                    else if(list.size()>1) event.replyWarning(FormatUtil.listOfTcChannels(list, tc));
                    else
                    {
                        db.setStarboardChannel(event.getGuild(), list.get(0));
                        event.replySuccess("Alright, the starboard channel will be "+list.get(0).getAsMention());
                        waitForStarCount(event);
                    }
                    break;
                default:
                    event.replyWarning("Thats isn't a valid option!");
                    waitForChannel(event);
            }
        }, 2, TimeUnit.MINUTES, () -> event.replyWarning("Oh uh.... You took more than 2 minutes to answer "+event.getAuthor().getAsMention()+"! Cancelling setup."));
    }

    private void waitForStarCount(CommandEvent event)
    {
        event.replySuccess("Now, what minimum amount of stars are needed to appear in the starboard? Minimum amount is 1 and maximum is 20.");

        ew.waitForEvent(GuildMessageReceivedEvent.class, e -> event.getAuthor().equals(e.getAuthor()) && event.getTextChannel().equals(e.getChannel()), e ->
        {
            Integer args;

            if(e.getMessage().getContentRaw().isEmpty())
            {
                event.replyError("Please provide me a number!");
                waitForStarCount(event);
                return;
            }

            try
            {
                args = Integer.valueOf(e.getMessage().getContentRaw());
            }
            catch(NumberFormatException ex)
            {
                event.replyError("You didn't provided a valid number!");
                waitForStarCount(event);
                return;
            }

            if(args<1 || args>20)
            {
                event.replyError("You provided a number that isn't between the limits!");
                waitForStarCount(event);
            }
            else
            {
                event.replySuccess("OK! The required star amount is `"+args+"`.");
                db.setStarboardCount(event.getGuild(), args);
                finished(event);
            }

        }, 2, TimeUnit.MINUTES, () -> event.replyWarning("Oh uh.... You took more than 2 minutes to answer "+event.getAuthor().getAsMention()+"! Cancelling setup."));
    }

    private void finished(CommandEvent event)
    {
        event.replySuccess("The starboard has been installed successfully! Thanks for using Endless' starboard!");
    }

    private class SetChannel extends EndlessCommand
    {
        SetChannel()
        {
            this.name = "setchannel";
            this.aliases = new String[]{"channel"};
            this.help = "Changes the channel of the starboard.";
            this.category = Categories.SERVER_CONFIG;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildCommand = true;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty()) event.replyError("Please include a text channel or NONE");
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                db.setStarboardChannel(event.getGuild(), null);
                event.replySuccess("Starboard disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty()) event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if(list.size()>1) event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    db.setStarboardChannel(event.getGuild(), list.get(0));
                    event.replySuccess("The starboard is now "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class SetCount extends EndlessCommand
    {
        SetCount()
        {
            this.name = "setcount";
            this.aliases = new String[]{"count", "amount", "setamount"};
            this.help = "Changes the amount of stars required to be in the starboard.";
            this.category = Categories.SERVER_CONFIG;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildCommand = true;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            Integer args;

            if(event.getArgs().isEmpty())
            {
                event.replyError("Please provide me a number!");
                return;
            }

            try
            {
                args = Integer.valueOf(event.getArgs());
            }
            catch(NumberFormatException ex)
            {
                event.replyError("You didn't provided a valid number!");
                return;
            }

            if(args<1 || args>20) event.replyError("You provided a number that isn't between the limits!");
            else
            {
                event.replySuccess("OK! The required star amount is `"+args+"`.");
                db.setStarboardCount(event.getGuild(), args);
            }
        }
    }
}

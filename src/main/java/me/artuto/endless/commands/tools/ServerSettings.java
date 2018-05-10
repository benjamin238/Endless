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

import com.jagrosh.jdautilities.command.Command;
import me.artuto.endless.commands.EndlessCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.GuildSettingsDataManager;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class ServerSettings extends EndlessCommand
{
    private final GuildSettingsDataManager db;

    public ServerSettings(GuildSettingsDataManager db)
    {
        this.db = db;
        this.name = "config";
        this.children = new Command[]{new ModLog(), new ServerLog(), new Welcome(), new Leave()};
        this.aliases = new String[]{"settings"};
        this.help = "Displays the settings of the server";
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        this.ownerCommand = false;
        this.guildCommand = true;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel modlog = db.getModlogChannel(guild);
        TextChannel serverlog = db.getServerlogChannel(guild);
        TextChannel welcome = db.getWelcomeChannel(guild);
        TextChannel leave = db.getLeaveChannel(guild);
        EmbedBuilder builder = new EmbedBuilder();
        String title = ":information_source: Settings of **"+event.getGuild().getName()+"**:";

        try
        {
            builder.addField("Modlog Channel: ", (modlog == null ? "None" : modlog.getAsMention()), true);
            builder.addField("Serverlog Channel: ", (serverlog == null ? "None" : serverlog.getAsMention()), true);
            builder.addField("Welcome Channel: ", (welcome == null ? "None" : welcome.getAsMention()), true);
            builder.addField("Leave Channel: ", (leave == null ? "None" : leave.getAsMention()), true);
            builder.setColor(event.getSelfMember().getColor());

            event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();
        }
        catch(Exception e)
        {
            event.replyError("Something went wrong when getting the settings: \n```"+e+"```");
        }

    }

    private class ModLog extends EndlessCommand
    {
        public ModLog()
        {
            this.name = "modlog";
            this.aliases = new String[]{"banlog", "kicklog", "banslog", "kickslog"};
            this.help = "Sets the modlog channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = new Command.Category("Settings");
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
                db.setModlogChannel(event.getGuild(), null);
                event.replySuccess("Modlogging disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty()) event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if(list.size()>1) event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    db.setModlogChannel(event.getGuild(), list.get(0));
                    event.replySuccess("Modlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class ServerLog extends EndlessCommand
    {
        public ServerLog()
        {
            this.name = "serverlog";
            this.help = "Sets the serverlog channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = new Command.Category("Settings");
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
                db.setServerlogChannel(event.getGuild(), null);
                event.replySuccess("Serverlogging disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty()) event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if(list.size()>1) event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    db.setServerlogChannel(event.getGuild(), list.get(0));
                    event.replySuccess("Serverlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class Welcome extends EndlessCommand
    {
        public Welcome()
        {
            this.name = "welcome";
            this.aliases = new String[]{"joinschannel", "joinslog", "joins"};
            this.help = "Sets the welcome channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = new Command.Category("Settings");
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
                db.setWelcomeChannel(event.getGuild(), null);
                event.replySuccess("Welcome channel disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty()) event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if(list.size()>1) event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    db.setWelcomeChannel(event.getGuild(), list.get(0));
                    event.replySuccess("The message configured will be sent in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class Leave extends EndlessCommand
    {
        public Leave()
        {
            this.name = "leave";
            this.aliases = new String[]{"leaveschannel", "leaveslog", "leaves"};
            this.help = "Sets the leave channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = new Command.Category("Settings");
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
                db.setLeaveChannel(event.getGuild(), null);
                event.replySuccess("Leave channel disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty()) event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if(list.size()>1) event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    db.setLeaveChannel(event.getGuild(), list.get(0));
                    event.replySuccess("The message configured will be sent in "+list.get(0).getAsMention());
                }
            }
        }
    }
}

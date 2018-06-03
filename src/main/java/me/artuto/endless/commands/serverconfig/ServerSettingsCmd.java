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
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Bot;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.entities.GuildSettings;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class ServerSettingsCmd extends EndlessCommand
{
    private final Bot bot;

    public ServerSettingsCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "config";
        this.children = new Command[]{new Modlog(), new Serverlog(), new Welcome(), new Leave(), new MutedRole(), new BanDeleteDays()};
        this.aliases = new String[]{"settings"};
        this.help = "Displays the settings of the server";
        this.category = Categories.SERVER_CONFIG;
        this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        this.guildOnly = true;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        Guild guild = event.getGuild();
        String title = ":information_source: Settings of **"+event.getGuild().getName()+"**:";

        GuildSettings settings = bot.db.getSettings(guild);
        int banDeleteDays = settings.getBanDeleteDays();
        int starboardCount = settings.getStarboardCount();
        Role mutedRole = GuildUtils.getMutedRole(guild);
        String welcomeMsg = settings.getWelcomeMsg();
        String leaveMsg = settings.getLeaveMsg();
        TextChannel modlog = guild.getTextChannelById(settings.getModlog());
        TextChannel serverlog = guild.getTextChannelById(settings.getServerlog());
        TextChannel welcome = guild.getTextChannelById(settings.getWelcomeChannel());
        TextChannel leave = guild.getTextChannelById(settings.getLeaveChannel());
        TextChannel starboard = guild.getTextChannelById(settings.getStarboard());

        builder.addField("Modlog Channel:", (modlog==null?"None":modlog.getAsMention()), true);
        builder.addField("Serverlog Channel:", (serverlog==null?"None":serverlog.getAsMention()), true);
        builder.addBlankField(true);
        builder.addField("Welcome Channel:", (welcome==null?"None":welcome.getAsMention()), true);
        builder.addField("Welcome Message:", (welcomeMsg==null?"None":"```"+welcomeMsg+"```"), true);
        builder.addBlankField(true);
        builder.addField("Leave Channel:", (leave==null?"None":leave.getAsMention()), true);
        builder.addField("Leave Message:", (leaveMsg==null?"None":"```"+leaveMsg+"```"), true);
        builder.addBlankField(true);
        builder.addField("Starboard Channel:", (starboard==null?"None":starboard.getAsMention()), true);
        builder.addField("Starboard Count:", (starboardCount==0?"Disabled":String.valueOf(starboardCount)), true);
        builder.addBlankField(true);
        builder.addField("Muted Role:", (mutedRole==null?"None":mutedRole.getAsMention()), true);
        builder.addField("Ban delete days:", String.valueOf(banDeleteDays), true);
        builder.addBlankField(true);
        builder.setColor(event.getSelfMember().getColor());

        event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());
    }

    private class Modlog extends EndlessCommand
    {
        Modlog()
        {
            this.name = "modlog";
            this.help = "Sets the modlog channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty()) event.replyError("Please include a text channel or NONE");
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setModlogChannel(event.getGuild(), null);
                event.replySuccess("Modlogging disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty()) event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if(list.size()>1) event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    bot.gsdm.setModlogChannel(event.getGuild(), list.get(0));
                    event.replySuccess("Modlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class Serverlog extends EndlessCommand
    {
        Serverlog()
        {
            this.name = "serverlog";
            this.help = "Sets the serverlog channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty()) event.replyError("Please include a text channel or NONE");
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setServerlogChannel(event.getGuild(), null);
                event.replySuccess("Serverlogging disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty()) event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if(list.size()>1) event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    bot.gsdm.setServerlogChannel(event.getGuild(), list.get(0));
                    event.replySuccess("Serverlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class Welcome extends EndlessCommand
    {
        Welcome()
        {
            this.name = "welcome";
            this.aliases = new String[]{"joinschannel", "joinslog", "joins"};
            this.help = "Sets the welcome channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty()) event.replyError("Please include a text channel or NONE");
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setWelcomeChannel(event.getGuild(), null);
                event.replySuccess("Welcome channel disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty()) event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if(list.size()>1) event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    bot.gsdm.setWelcomeChannel(event.getGuild(), list.get(0));
                    event.replySuccess("The message configured will be sent in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class Leave extends EndlessCommand
    {
        Leave()
        {
            this.name = "leave";
            this.aliases = new String[]{"leaveschannel", "leaveslog", "leaves"};
            this.help = "Sets the leave channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty()) event.replyError("Please include a text channel or NONE");
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setLeaveChannel(event.getGuild(), null);
                event.replySuccess("Leave channel disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty()) event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if(list.size()>1) event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    bot.gsdm.setLeaveChannel(event.getGuild(), list.get(0));
                    event.replySuccess("The message configured will be sent in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class MutedRole extends EndlessCommand
    {
        MutedRole()
        {
            this.name = "mutedrole";
            this.help = "Sets the muted role";
            this.arguments = "<@Role|Role ID|Role name>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty()) event.replyError("Please include a role or NONE");
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setMutedRole(event.getGuild(), null);
                event.replySuccess("Muted role disabled");
            }
            else
            {
                List<Role> list = FinderUtil.findRoles(event.getArgs(), event.getGuild());
                if(list.isEmpty()) event.replyWarning("No Roles found matching \""+event.getArgs()+"\"");
                else if(list.size()>1) event.replyWarning(FormatUtil.listOfRoles(list, event.getArgs()));
                else
                {
                    if(!(GuildUtils.getMutedRole(event.getGuild())==null))
                    {
                        event.replyError("You already have a Muted role!");
                        return;
                    }

                    bot.gsdm.setMutedRole(event.getGuild(), list.get(0));
                    event.replySuccess("The muted role is now "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class BanDeleteDays extends EndlessCommand
    {
        BanDeleteDays()
        {
            this.name = "bandeletedays";
            this.help = "Sets the amount of messages to delete when banning";
            this.arguments = "<number of day(s)>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty()) event.replyError("Please include a number or 0");
            else if(event.getArgs().equalsIgnoreCase("0"))
            {
                bot.gsdm.setBanDeleteDays(event.getGuild(), 0);
                event.replySuccess("Ban delete days set to 0 (No delete)");
            }
            else if(event.getArgs().equalsIgnoreCase("1"))
            {
                bot.gsdm.setBanDeleteDays(event.getGuild(), 1);
                event.replySuccess("Ban delete days set to 1");
            }
            else if(event.getArgs().equalsIgnoreCase("7"))
            {
                bot.gsdm.setBanDeleteDays(event.getGuild(), 7);
                event.replySuccess("Ban delete days set to 7");
            }
            else
                event.replyError("That isn't a valid option! Valid options are `0` (Don't delete), `1` and `7`");
        }
    }
}

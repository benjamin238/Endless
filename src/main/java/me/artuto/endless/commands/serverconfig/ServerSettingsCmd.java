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
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.core.entities.Room;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.List;

public class ServerSettingsCmd extends EndlessCommand
{
    private final Bot bot;

    public ServerSettingsCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "settings";
        this.children = new Command[]{new ModlogCmd(), new ServerlogCmd(), new WelcomeCmd(), new LeaveCmd(),
                new AdminRoleCmd(), new ModRoleCmd(), new MutedRoleCmd(), new BanDeleteDaysCmd(), new TimezoneCmd(), new SetFairQueueCmd()};
        this.help = "Displays the settings of the server";
        this.category = Categories.SERVER_CONFIG;
        this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        this.guildOnly = true;
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        Guild guild = event.getGuild();
        String title = ":information_source: Settings of **"+event.getGuild().getName()+"**:";
        GuildSettings settings = bot.endless.getGuildSettings(guild);

        Emote starboardEmote = getEmote(guild, settings.getStarboardEmote());
        int banDeleteDays = settings.getBanDeleteDays();
        int starboardCount = settings.getStarboardCount();
        Role adminRole = GuildUtils.getAdminRole(guild);
        Role djRole = event.getGuild().getRoleById(settings.getDJRole());
        Role modRole = GuildUtils.getModRole(guild);
        Role mutedRole = GuildUtils.getMutedRole(guild);
        Room.Mode roomMode = settings.getRoomMode();
        String fairQueue = "**"+(settings.isFairQueueEnabled()?"Yes":"No")+"**";
        String welcomeDm = settings.getWelcomeDM();
        String welcomeMsg = settings.getWelcomeMsg();
        String leaveMsg = settings.getLeaveMsg();
        TextChannel modlog = guild.getTextChannelById(settings.getModlog());
        TextChannel serverlog = guild.getTextChannelById(settings.getServerlog());
        TextChannel welcome = guild.getTextChannelById(settings.getWelcomeChannel());
        TextChannel leave = guild.getTextChannelById(settings.getLeaveChannel());
        TextChannel musicTc = guild.getTextChannelById(settings.getTextChannelMusic());
        TextChannel starboard = guild.getTextChannelById(settings.getStarboard());
        VoiceChannel musicVc = guild.getVoiceChannelById(settings.getVoiceChannelMusic());
        ZoneId tz = settings.getTimezone();

        StringBuilder logsString = new StringBuilder();
        StringBuilder messagesString = new StringBuilder();
        StringBuilder settingsString = new StringBuilder();
        StringBuilder starboardString = new StringBuilder();
        StringBuilder musicString = new StringBuilder();

        logsString.append("Modlog Channel: ").append((modlog==null?"None":"**"+modlog.getAsMention()+"**"))
                .append("\nServerlog Channel: ").append((serverlog==null?"None":"**"+serverlog.getAsMention()+"**"))
                .append("\nWelcome Channel: ").append((welcome==null?"None":"**"+welcome.getAsMention()+"**"))
                .append("\nLeave Channel: ").append((leave==null?"None":"**"+leave.getAsMention()+"**"));

        messagesString.append("Welcome DM: ").append((welcomeDm==null?"None":"`"+welcomeDm+"`"))
                .append("\nWelcome Message: ").append((welcomeMsg==null?"None":"`"+welcomeMsg+"`"))
                .append("\nLeave Message: ").append((leaveMsg==null?"None":"`"+leaveMsg+"`"));

        settingsString.append("Admin Role: ").append((adminRole==null?"None":"**"+adminRole.getAsMention()+"**"))
                .append("\nMod Role: ").append((modRole==null?"None":"**"+modRole.getAsMention()+"**"))
                .append("\nMuted Role: ").append((mutedRole==null?"None":"**"+mutedRole.getAsMention()+"**"))
                .append("\nBan delete days: ").append((banDeleteDays==0?"Don't delete":String.valueOf("**"+banDeleteDays+"**")))
                .append("\nRoom Mode: **").append(roomMode.getName()).append("**")
                .append("\nTimezone: **").append(tz.toString()).append("**");

        starboardString.append("Starboard Channel: ").append((starboard==null?"None":"**"+starboard.getAsMention()+"**"))
                .append("\nStar Count: ").append((starboardCount==0?"Disabled":String.valueOf("**"+starboardCount+"**")))
                .append("\nEmote: ").append(starboardEmote==null?settings.getStarboardEmote():starboardEmote.getAsMention());

        musicString.append("DJ Role: ").append((djRole==null?"None":"**"+djRole.getAsMention()+"**"))
                .append("\nText Channel: ").append((musicTc==null?"None":"**"+musicTc.getAsMention()+"**"))
                .append("\nVoice Channel: ").append((musicVc==null?"None":"**"+musicVc.getName()+"**"))
                .append("\nFair Queue: ").append(fairQueue);

        builder.addField(":mag: Logs", logsString.toString(), false);
        builder.addField(":speech_balloon: Messages", messagesString.toString(), false);
        builder.addField(":bar_chart: Server Settings", settingsString.toString(), false);
        builder.addField(":star: Starboard", starboardString.toString(), false);
        builder.addField(":notes: Music", musicString.toString(), false);

        builder.setColor(event.getSelfMember().getColor());
        event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());
    }

    private class ModlogCmd extends EndlessCommand
    {
        ModlogCmd()
        {
            this.name = "modlog";
            this.help = "Sets the modlog channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Please include a text channel or NONE";
            this.parent = ServerSettingsCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setModlogChannel(event.getGuild(), null);
                event.replySuccess("Modlogging disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty())
                    event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if(list.size()>1)
                    event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    bot.gsdm.setModlogChannel(event.getGuild(), list.get(0));
                    event.replySuccess("Modlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class ServerlogCmd extends EndlessCommand
    {
        ServerlogCmd()
        {
            this.name = "serverlog";
            this.help = "Sets the serverlog channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Please include a text channel or NONE";
            this.parent = ServerSettingsCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setServerlogChannel(event.getGuild(), null);
                event.replySuccess("Serverlogging disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty())
                    event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if(list.size()>1)
                    event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    bot.gsdm.setServerlogChannel(event.getGuild(), list.get(0));
                    event.replySuccess("Serverlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class WelcomeCmd extends EndlessCommand
    {
        WelcomeCmd()
        {
            this.name = "welcome";
            this.aliases = new String[]{"joinschannel", "joinslog", "joins"};
            this.help = "Sets the welcome channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Please include a text channel or NONE";
            this.parent = ServerSettingsCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setWelcomeChannel(event.getGuild(), null);
                event.replySuccess("Welcome channel disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty())
                    event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if(list.size()>1)
                    event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    bot.gsdm.setWelcomeChannel(event.getGuild(), list.get(0));
                    event.replySuccess("The message configured will be sent in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class LeaveCmd extends EndlessCommand
    {
        LeaveCmd()
        {
            this.name = "leave";
            this.aliases = new String[]{"leaveschannel", "leaveslog", "leaves"};
            this.help = "Sets the leave channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Please include a text channel or NONE";
            this.parent = ServerSettingsCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setLeaveChannel(event.getGuild(), null);
                event.replySuccess("Leave channel disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty())
                    event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if(list.size()>1)
                    event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    bot.gsdm.setLeaveChannel(event.getGuild(), list.get(0));
                    event.replySuccess("The message configured will be sent in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class AdminRoleCmd extends EndlessCommand
    {
        AdminRoleCmd()
        {
            this.name = "adminrole";
            this.help = "Sets the admin role";
            this.arguments = "<@Role|Role ID|Role name>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Please include a role or NONE";
            this.parent = ServerSettingsCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setAdminRole(event.getGuild(), null);
                event.replySuccess("Admin role disabled");
            }
            else
            {
                List<Role> list = FinderUtil.findRoles(event.getArgs(), event.getGuild());
                if(list.isEmpty())
                    event.replyWarning("No Roles found matching \""+event.getArgs()+"\"");
                else if(list.size()>1)
                    event.replyWarning(FormatUtil.listOfRoles(list, event.getArgs()));
                else
                {
                    if(!(GuildUtils.getAdminRole(event.getGuild())==null))
                    {
                        event.replyError("You already have an Admin role!");
                        return;
                    }

                    bot.gsdm.setAdminRole(event.getGuild(), list.get(0));
                    event.replySuccess("The admin role is now "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class ModRoleCmd extends EndlessCommand
    {
        ModRoleCmd()
        {
            this.name = "modrole";
            this.help = "Sets the mod role";
            this.arguments = "<@Role|Role ID|Role name>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Please include a role or NONE";
            this.parent = ServerSettingsCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setModRole(event.getGuild(), null);
                event.replySuccess("Mod role disabled");
            }
            else
            {
                List<Role> list = FinderUtil.findRoles(event.getArgs(), event.getGuild());
                if(list.isEmpty())
                    event.replyWarning("No Roles found matching \""+event.getArgs()+"\"");
                else if(list.size()>1)
                    event.replyWarning(FormatUtil.listOfRoles(list, event.getArgs()));
                else
                {
                    if(!(GuildUtils.getModRole(event.getGuild())==null))
                    {
                        event.replyError("You already have a Mod role!");
                        return;
                    }

                    bot.gsdm.setModRole(event.getGuild(), list.get(0));
                    event.replySuccess("The mod role is now "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class MutedRoleCmd extends EndlessCommand
    {
        MutedRoleCmd()
        {
            this.name = "mutedrole";
            this.help = "Sets the muted role";
            this.arguments = "<@Role|Role ID|Role name>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Please include a role or NONE";
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setMutedRole(event.getGuild(), null);
                event.replySuccess("Muted role disabled");
            }
            else
            {
                List<Role> list = FinderUtil.findRoles(event.getArgs(), event.getGuild());
                if(list.isEmpty())
                    event.replyWarning("No Roles found matching \""+event.getArgs()+"\"");
                else if(list.size()>1)
                    event.replyWarning(FormatUtil.listOfRoles(list, event.getArgs()));
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

    private class BanDeleteDaysCmd extends EndlessCommand
    {
        BanDeleteDaysCmd()
        {
            this.name = "bandeletedays";
            this.help = "Sets the amount of messages to delete when banning";
            this.arguments = "<number of day(s)>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Please include a number or 0";
            this.parent = ServerSettingsCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(event.getArgs().equalsIgnoreCase("0"))
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

    private class TimezoneCmd extends EndlessCommand
    {
        TimezoneCmd()
        {
            this.name = "timezone";
            this.help = "Sets the timezone for logs on the guild";
            this.arguments = "<timezone>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.parent = ServerSettingsCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            String args = event.getArgs();
            ZoneId tz;

            try
            {
                tz = ZoneId.of(args);
            }
            catch(ZoneRulesException e)
            {
                event.replyError("Please specify a valid timezone!");
                return;
            }

            bot.gsdm.setTimezone(event.getGuild(), tz);
            event.replySuccess("Successfully updated timezone!");
        }
    }

    private class SetFairQueueCmd extends EndlessCommand
    {
        SetFairQueueCmd()
        {
            this.name = "fairqueue";
            this.help = "Toggles on and off the fairqueue";
            this.arguments = "<status true or false>";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Please provide me `true` or `false`!";
            this.parent = ServerSettingsCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(!(GuildUtils.isPremiumGuild(event.getGuild())))
            {
                event.replyError("This feature is only available to Donators' Guilds! If you want to support Endless'" +
                        " development please consider donating by doing `"+event.getClient().getPrefix()+"donate`");
                return;
            }

            String args = event.getArgs();

            if(args.equalsIgnoreCase("true"))
            {
                bot.gsdm.setFairQueueStatus(event.getGuild(), true);
                event.replySuccess("Successfully enabled FairQueue");
            }
            else if(args.equalsIgnoreCase("false"))
            {
                bot.gsdm.setFairQueueStatus(event.getGuild(), false);
                event.replySuccess("Successfully disabled FairQueue");
            }
            else
                event.replyError("Invalid option! Must be `true` or `false`!");
        }
    }

    private Emote getEmote(Guild guild, String emote)
    {
        long id;
        try
        {
             id = Long.parseLong(emote);
        }
        catch(NumberFormatException e)
        {
            return null;
        }

        return guild.getEmoteById(id);
    }
}

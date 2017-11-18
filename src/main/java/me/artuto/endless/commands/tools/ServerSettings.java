package me.artuto.endless.commands.tools;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.JLDataManager;
import me.artuto.endless.data.LoggingDataManager;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class ServerSettings extends Command
{
    private final LoggingDataManager ldm;
    private final JLDataManager jldm;

    public ServerSettings(LoggingDataManager ldm, JLDataManager jldm)
    {
        this.ldm = ldm;
        this.jldm = jldm;
        this.name = "config";
        this.children = new Command[]{new ModLog(), new ServerLog(), new Welcome(), new Leave()};
        this.aliases = new String[]{"settings"};
        this.help = "Displays the settings of the server";
        this.category = Categories.TOOLS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.ownerCommand = false;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel modlog = ldm.getModlogChannel(guild);
        TextChannel serverlog = ldm.getServerlogChannel(guild);
        TextChannel welcome = jldm.getWelcomeChannel(guild);
        TextChannel leave = jldm.getLeaveChannel(guild);
        EmbedBuilder builder = new EmbedBuilder();
        String title = ":information_source: Settings of **"+event.getGuild().getName()+"**:";

        try
        {
            builder.addField("Modlog Channel: ", (modlog==null?"None":modlog.getAsMention()), true);
            builder.addField("Serverlog Channel: ", (serverlog==null?"None":serverlog.getAsMention()), true);
            builder.addField("Welcome Channel: ", (welcome==null?"None":welcome.getAsMention()), true);
            builder.addField("Leave Channel: ", (leave==null?"None":leave.getAsMention()), true);
            builder.setColor(event.getSelfMember().getColor());

            event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();
        }
        catch(Exception e)
        {
            event.replyError("Something went wrong when getting the settings: \n```"+e+"```");
        }

    }

    private class ModLog extends Command
    {
        public ModLog()
        {
            this.name = "modlog";
            this.aliases = new String[]{"banlog", "kicklog", "banslog", "kickslog"};
            this.help = "Sets the modlog channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = new Command.Category("Settings");
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
                event.replyError("Please include a text channel or NONE");
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                ldm.setModlogChannel(event.getGuild(), null);
                event.replySuccess("Modlogging disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty())
                    event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if (list.size()>1)
                    event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    ldm.setModlogChannel(event.getGuild(), list.get(0));
                    event.replySuccess("Modlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class ServerLog extends Command
    {
        public ServerLog()
        {
            this.name = "serverlog";
            this.help = "Sets the serverlog channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = new Command.Category("Settings");
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
                event.replyError("Please include a text channel or NONE");
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                ldm.setServerlogChannel(event.getGuild(), null);
                event.replySuccess("Serverlogging disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty())
                    event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if (list.size()>1)
                    event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    ldm.setServerlogChannel(event.getGuild(), list.get(0));
                    event.replySuccess("Serverlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class Welcome extends Command
    {
        public Welcome()
        {
            this.name = "welcome";
            this.aliases = new String[]{"joinschannel", "joinslog", "joins"};
            this.help = "Sets the welcome channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = new Command.Category("Settings");
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
                event.replyError("Please include a text channel or NONE");
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                jldm.setWelcomeChannel(event.getGuild(), null);
                event.replySuccess("Welcome channel disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty())
                    event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if (list.size()>1)
                    event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    jldm.setWelcomeChannel(event.getGuild(), list.get(0));
                    event.replySuccess("The message configured will be sent in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class Leave extends Command
    {
        public Leave()
        {
            this.name = "leave";
            this.aliases = new String[]{"leaveschannel", "leaveslog", "leaves"};
            this.help = "Sets the leave channel";
            this.arguments = "<#channel|Channel ID|Channel name>";
            this.category = new Command.Category("Settings");
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
                event.replyError("Please include a text channel or NONE");
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                jldm.setLeaveChannel(event.getGuild(), null);
                event.replySuccess("Leave channel disabled");
            }
            else
            {
                List<TextChannel> list = FinderUtil.findTextChannels(event.getArgs(), event.getGuild());
                if(list.isEmpty())
                    event.replyWarning("No Text Channels found matching \""+event.getArgs()+"\"");
                else if (list.size()>1)
                    event.replyWarning(FormatUtil.listOfTcChannels(list, event.getArgs()));
                else
                {
                    jldm.setLeaveChannel(event.getGuild(), list.get(0));
                    event.replySuccess("The message configured will be sent in "+list.get(0).getAsMention());
                }
            }
        }
    }
}

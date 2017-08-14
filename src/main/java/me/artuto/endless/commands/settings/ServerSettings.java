package me.artuto.endless.commands.settings;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import me.artuto.endless.data.Database;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.utils.ModLogging;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class ServerSettings extends Command
{
    private final Database database;
    private final ModLogging modlog;

    public ServerSettings(Database database, ModLogging modlog)
    {
        this.database = database;
        this.modlog = modlog;
        this.name = "config";
        this.children = new Command[]{new ModLog(database, modlog), new ServerLog(database, modlog)};
        this.aliases = new String[]{"settings"};
        this.help = "Displays the settings of the server";
        this.category = new Command.Category("Settings");
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        Database.GuildSettings s = database.getSettings(event.getGuild());
        TextChannel modlog = event.getGuild().getTextChannelById(s.modlog_Id);
        TextChannel serverlog = event.getGuild().getTextChannelById(s.serverlog_Id);
        EmbedBuilder builder = new EmbedBuilder();

        String title = ":information_source: Settings of **"+event.getGuild().getName()+"**";

        try
        {
            builder.addField("Modlog Channel: ", (modlog==null?"None":modlog.getAsMention()), false);
            builder.addField("Serverlog Channel: ", (serverlog==null?"None":serverlog.getAsMention()), false);
            builder.setColor(event.getSelfMember().getColor());

            event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();
        }
        catch(Exception e)
        {
            event.replyError("An error happened when getting the settings!");
        }

    }

    private class ModLog extends Command
    {
        private final Database database;
        private final ModLogging modlog;

        public ModLog(Database database, ModLogging modlog)
        {
            this.database = database;
            this.modlog = modlog;
            this.name = "modlog";
            this.aliases = new String[]{"banlog", "kicklog", "banslog", "kickslog"};
            this.help = "Sets the modlog channel";
            this.arguments = "#channel | Channel ID | Channel name";
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
            {
                event.replyError("Please include a text channel or NONE");
            }
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                database.setModlogChannel(event.getGuild(), null);
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
                    database.setModlogChannel(event.getGuild(), list.get(0));
                    event.replySuccess("Modlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class ServerLog extends Command
    {
        private final Database database;
        private final ModLogging modlog;

        public ServerLog(Database database, ModLogging modlog)
        {
            this.database = database;
            this.modlog = modlog;
            this.name = "serverlog";
            this.help = "Sets the serverlog channel";
            this.arguments = "#channel | Channel ID | Channel name";
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
            {
                event.replyError("Please include a text channel or NONE");
            }
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                database.setServerlogChannel(event.getGuild(), null);
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
                    database.setServerlogChannel(event.getGuild(), list.get(0));
                    event.replySuccess("Serverlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class ModLogSwitch extends Command
    {
        private final Database database;
        private final ModLogging modlog;

        public ModLogSwitch(Database database, ModLogging modlog)
        {
            this.database = database;
            this.modlog = modlog;
            this.name = "switch modlog";
            this.help = "Sets the modlog channel";
            this.arguments = "true | false";
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
            {
                event.replyWarning("No arguments found. Valid: `ON`, `OFF`");
            }
            else if(event.getArgs().equalsIgnoreCase("on"))
            {
                database.setModlogSwitch(event.getGuild(), true);
                event.replySuccess("Modlogging enabled!");
            }
            else if(event.getArgs().equalsIgnoreCase("off"))
            {
                database.setModlogSwitch(event.getGuild(), false);
                event.replySuccess("Modlogging disabled!");
            }
        }
    }

    private class ServerLogSwitch extends Command
    {
        private final Database database;
        private final ModLogging modlog;

        public ServerLogSwitch(Database database, ModLogging modlog)
        {
            this.database = database;
            this.modlog = modlog;
            this.name = "switch serverlog";
            this.help = "Sets the modlog channel";
            this.arguments = "on | off";
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
            {
                event.replyWarning("No arguments found. Valid: `ON`, `OFF`");
            }
            else if(event.getArgs().equalsIgnoreCase("on"))
            {
                database.setServerlogSwitch(event.getGuild(), true);
                event.replySuccess("Serverlogging enabled!");
            }
            else if(event.getArgs().equalsIgnoreCase("off"))
            {
                database.setServerlogSwitch(event.getGuild(), false);
                event.replySuccess("Serverlogging disabled!");
            }
        }
    }
}

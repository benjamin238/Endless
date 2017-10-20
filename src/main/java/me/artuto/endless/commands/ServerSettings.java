package me.artuto.endless.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.DatabaseManager;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.SQLException;
import java.util.List;
import me.artuto.endless.Bot;
import me.artuto.endless.data.Settings;

public class ServerSettings extends Command
{
    private final DatabaseManager db;

    public ServerSettings(DatabaseManager db)
    {
        this.db = db;
        this.name = "config";
        this.children = new Command[]{new ModLog(db), new ServerLog(db), new Welcome(db)};
        this.aliases = new String[]{"settings"};
        this.help = "Displays the settings of the server";
        this.category = Categories.TOOLS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel modlog = db.getModlogChannel(guild);
        TextChannel serverlog = db.getServerlogChannel(guild);
        TextChannel welcome = db.getWelcomeChannel(guild);
        EmbedBuilder builder = new EmbedBuilder();
        String title = ":information_source: Settings of **"+event.getGuild().getName()+"**";

        try
        {
            builder.addField("Modlog Channel: ", (modlog==null?"None":modlog.getAsMention()), false);
            builder.addField("Serverlog Channel: ", (serverlog==null?"None":serverlog.getAsMention()), false);
            builder.addField("Welcome Channel: ", (welcome==null?"None":welcome.getAsMention()), false);
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
        private final DatabaseManager db;

        public ModLog(DatabaseManager db)
        {
            this.db = db;
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
            {
                event.replyError("Please include a text channel or NONE");
            }
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                boolean is = db.clearModlogChannel(event.getGuild());

                if(is)
                    event.replySuccess("Modlogging disabled");
                else
                    event.replyError("You don't have any channel configured as Modlog!");
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
                    db.setModlogChannel(event.getGuild(), list.get(0));
                    event.replySuccess("Modlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class ServerLog extends Command
    {
        private final DatabaseManager db;

        public ServerLog(DatabaseManager db)
        {
            this.db = db;
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
            {
                event.replyError("Please include a text channel or NONE");
            }
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                boolean is = db.clearServerlogChannel(event.getGuild());

                if(is)
                    event.replySuccess("Serverlogging disabled");
                else
                    event.replyError("You don't have any channel configured as Serverlog!");
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
                    db.setServerlogChannel(event.getGuild(), list.get(0));
                    event.replySuccess("Serverlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class Welcome extends Command
    {
        private final DatabaseManager db;

        public Welcome(DatabaseManager db)
        {
            this.db = db;
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
            {
                event.replyError("Please include a text channel or NONE");
            }
            else if(event.getArgs().equalsIgnoreCase("none"))
            {
                boolean is = db.clearWelcomeChannel(event.getGuild());

                if(is)
                    event.replySuccess("Welcome channel disabled");
                else
                    event.replyError("You don't have any channel configured as a Welcome channel!");
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
                    db.setWelcomeChannel(event.getGuild(), list.get(0));
                    event.replySuccess("The message configured will be sent in "+list.get(0).getAsMention());
                }
            }
        }
    }
}

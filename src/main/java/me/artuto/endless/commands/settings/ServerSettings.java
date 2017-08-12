package me.artuto.endless.commands.settings;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import me.artuto.endless.Bot;
import me.artuto.endless.data.Settings;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class ServerSettings extends Command
{
    private final Bot bot;

    public ServerSettings(Bot bot)
    {
        this.bot = bot;
        this.name = "config";
        this.children = new Command[]{new ModLog(bot), new ServerLog(bot)};
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
        EmbedBuilder builder = new EmbedBuilder();
        Settings s = bot.getSettings(event.getGuild());
        TextChannel modlog = event.getGuild().getTextChannelById(s.getModLogId());
        TextChannel serverlog = event.getGuild().getTextChannelById(s.getServerLogId());

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
        private final Bot bot;

        public ModLog(Bot bot)
        {
            this.bot = bot;
            this.name = "modlog";
            this.aliases = new String[]{"banlog", "kicklog", "banslog", "kickslog"};
            this.help = "Sets the modlog channel";
            this.arguments = "#channel | Channel ID | Channel name";
            this.category = new Command.Category("Settings");
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
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
                bot.clearModLogChannel(event.getGuild());
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
                    bot.setModLogChannel(list.get(0));
                    event.replySuccess("Modlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }

    private class ServerLog extends Command
    {
        private final Bot bot;

        public ServerLog(Bot bot)
        {
            this.bot = bot;
            this.name = "serverlog";
            this.help = "Sets the serverlog channel";
            this.arguments = "#channel | Channel ID | Channel name";
            this.category = new Command.Category("Settings");
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
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
                bot.clearServerLogChannel(event.getGuild());
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
                    bot.setServerLogChannel(list.get(0));
                    event.replySuccess("Serverlogging actions will be logged in "+list.get(0).getAsMention());
                }
            }
        }
    }
}

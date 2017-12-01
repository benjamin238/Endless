package me.artuto.endless.events;

import com.jagrosh.jagtag.Parser;
import com.jagrosh.jagtag.ParserBuilder;
import com.jagrosh.jagtag.libraries.*;
import me.artuto.endless.Const;
import me.artuto.endless.data.GuildSettingsDataManager;
import me.artuto.endless.data.TagDataManager;
import me.artuto.endless.entities.ImportedTag;
import me.artuto.endless.loader.Config;
import me.artuto.endless.tempdata.AfkManager;
import me.artuto.endless.tools.Variables;
import me.artuto.endless.utils.FinderUtil;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GuildEvents extends ListenerAdapter
{
    private final Config config;
    private final TagDataManager tdm;
    private final GuildSettingsDataManager ldm;
    private final Parser parser;

    public GuildEvents(Config config, TagDataManager tdm, GuildSettingsDataManager ldm)
    {
        this.config = config;
        this.tdm = tdm;
        this.ldm = ldm;
        this.parser = new ParserBuilder()
                .addMethods(Variables.getMethods())
                .addMethods(Arguments.getMethods())
                .addMethods(Functional.getMethods())
                .addMethods(Miscellaneous.getMethods())
                .addMethods(Strings.getMethods())
                .addMethods(Time.getMethods())
                .addMethods(com.jagrosh.jagtag.libraries.Variables.getMethods())
                .setMaxOutput(2000)
                .setMaxIterations(1000)
                .build();
    }

    private String getReason(Guild guild)
    {
        String reason = GuildUtils.checkBadGuild(guild);

        switch (reason)
        {
            case "LEFT: BOTS":
                return "Too many bots!";
            case "LEFT: BOT LIST":
                return "Hey! You can't have this bot on a bot list!";
            default:
                return null;
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        Guild guild = event.getGuild();
        User owner = guild.getOwner().getUser();
        LoggerFactory.getLogger("Logging").info("[GUILD JOIN]: "+guild.getName()+" (ID: "+guild.getId()+")\n");
        long botCount = guild.getMembers().stream().map(m -> m.getUser()).filter(u -> u.isBot()).count();
        long userCount = guild.getMembers().stream().map(m -> m.getUser()).filter(u -> !(u.isBot())).count();
        long totalCount = guild.getMembers().size();
        GuildUtils.checkBadGuild(guild);
        TextChannel tc = event.getJDA().getTextChannelById(config.getBotlogChannelId());
        TextChannel defaultTc = FinderUtil.getDefaultChannel(guild);

        if(!(GuildUtils.isBadGuild(guild)) && config.isBotlogEnabled() && !(tc==null) && tc.canTalk())
        {
            tc.sendMessage(":inbox_tray: `[New Guild]:` "+guild.getName()+" (ID: "+guild.getId()+")\n" +
                    "`[Owner]:` **"+owner.getName()+"**#**"+owner.getDiscriminator()+"** (ID: "+owner.getId()+"\n" +
                    "`[Members]:` Humans: **"+userCount+"** Bots: **"+botCount+"** Total Count: **"+totalCount+"**\n").queue();

            if(!(defaultTc==null) && defaultTc.canTalk())
                defaultTc.sendMessage("Hey! Thanks for adding Endless to your guild! First of all, you need to know if you activate the ModLogging/ServerLogging " +
                        "you allow me to log all your messages, users, ids, avatars, channels, roles and other guild settings.\n" +
                        "If you don't agree to this you **must** remove Endless from your guild.\n" +
                        "\n" +
                        "To know what Endless can do check out `"+config.getPrefix()+"help` which shows all of my available commands.\n" +
                        "\n" +
                        "If you want to recieve a notification when a new update is released, report a bug or ask for an improvement please join my server: " +
                        "**<"+Const.INVITE+">** and post it on the correspondient channel.").queue();
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event)
    {
        Guild guild = event.getGuild();
        User owner = guild.getOwner().getUser();
        LoggerFactory.getLogger("Logging").info("[GUILD LEFT]: "+guild.getName()+" (ID: "+guild.getId()+")\n");
        long botCount = guild.getMembers().stream().map(m -> m.getUser()).filter(u -> u.isBot()).count();
        long userCount = guild.getMembers().stream().map(m -> m.getUser()).filter(u -> !(u.isBot())).count();
        long totalCount = guild.getMembers().size();
        TextChannel tc = event.getJDA().getTextChannelById(config.getBotlogChannelId());
        String reason = getReason(guild);

        if(config.isBotlogEnabled() && !(tc==null) && tc.canTalk())
        {
            StringBuilder builder = new StringBuilder().append(":outbox_tray: `[Left Guild]:` "+guild.getName()+" (ID: "+guild.getId()+")\n" +
                    "`[Owner]:` **"+owner.getName()+"**#**"+owner.getDiscriminator()+"** (ID: "+owner.getId()+"\n" +
                    "`[Members]:` Humans: **"+userCount+"** Bots: **"+botCount+"** Total Count: **"+totalCount+"**\n");

            if(!(reason==null))
                builder.append("`[Reason]:` "+reason);

            tc.sendMessage(builder.toString()).queue();
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        Logger LOG = LoggerFactory.getLogger("AFK Manager");
        User author = event.getAuthor();
        Message msg = event.getMessage();
        String message;
        List<ImportedTag> importedT = tdm.getImportedTagsForGuild(event.getGuild().getIdLong());
        TextChannel modlog = ldm.getModlogChannel(event.getGuild());

        if(!(msg.getAttachments().isEmpty()))
        {
            for(Message.Attachment at : msg.getAttachments())
            {
                if(at.getFileName().endsWith(".js"))
                {
                   at.download(new File(at.getFileName()));

                    List<String> lines;
                    try
                    {
                        lines = Files.readAllLines(Paths.get(at.getFileName()));
                        for(String str : lines)
                        {
                            if(str.contains("proxy.contentWindow.localStorage.token"))
                            {
                                if(!(modlog==null))
                                    modlog.sendMessage(":warning: **"+author.getName()+"#"+author.getDiscriminator()+"** ("+author.getId()+") has sent a suspicious file with code to steal tokens. It has been deleted. Message ID: "+msg.getId()).queue(s -> msg.delete().queue(), e -> msg.delete().queue());
                                break;
                            }
                            new File(at.getFileName()).delete();
                        }
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        if(!(importedT==null))
        {
            for(ImportedTag tag : importedT)
            {
                String name = tag.getName();
                String command = config.getPrefix().toLowerCase()+name;
                String tagargs;
                String[] args;

                if(msg.getContent().startsWith(command) && event.getChannel().canTalk())
                {
                    try
                    {
                        args = msg.getContent().split(command+" ", 2);
                        tagargs = args[1];
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        tagargs = "";
                    }

                    parser.clear().put("user", event.getAuthor()).put("guild", event.getGuild()).put("channel", event.getChannel()).put("args", tagargs);
                    event.getChannel().sendMessage(parser.parse(tag.getContent())).queue();
                }
            }
        }

        if(AfkManager.isAfk(author.getIdLong()))
        {
            author.openPrivateChannel().queue(pc -> pc.sendMessage(config.getDoneEmote()+" I've removed your AFK status.").queue(null,
                    (e) -> LOG.warn("I was not able to DM "+author.getName()+"#"+author.getDiscriminator()+" about removing its AFK status.")));
            AfkManager.unsetAfk(author.getIdLong());
        }

        for(Member afk : event.getGuild().getMembers())
        {
            User user = afk.getUser();
            message = ":bed: **"+user.getName()+"** is AFK!";

            if(AfkManager.isAfk(user.getIdLong()))
            {
                if(msg.getMentionedUsers().contains(user))
                {
                    if(!(user.isBot()))
                    {
                        EmbedBuilder builder = new EmbedBuilder();

                        builder.setAuthor(author.getName()+"#"+author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
                        builder.setDescription(msg.getContent());
                        builder.setFooter("#"+msg.getTextChannel().getName()+", "+event.getGuild().getName(), event.getGuild().getIconUrl());
                        builder.setTimestamp(msg.getCreationTime());
                        builder.setColor(event.getMember().getColor());

                        user.openPrivateChannel().queue(pc -> pc.sendMessage(new MessageBuilder().setEmbed(builder.build()).build()).queue(null, null));
                    }

                    if(AfkManager.getMessage(user.getIdLong())==null)
                        event.getChannel().sendMessage(message).queue();
                    else
                    {
                        EmbedBuilder builder = new EmbedBuilder();

                        builder.setDescription(AfkManager.getMessage(user.getIdLong()));
                        builder.setColor(event.getGuild().getMember(user).getColor());

                        event.getChannel().sendMessage(new MessageBuilder().append(message).setEmbed(builder.build()).build()).queue();
                    }
                }
            }
        }
    }
}

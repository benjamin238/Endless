package me.artuto.endless.logging;

import com.jagrosh.jagtag.Parser;
import com.jagrosh.jagtag.ParserBuilder;
import com.jagrosh.jagtag.libraries.*;
import me.artuto.endless.Messages;
import me.artuto.endless.data.GuildSettingsDataManager;
import me.artuto.endless.tempdata.MessagesLogging;
import me.artuto.endless.tools.Variables;
import me.artuto.endless.utils.FinderUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.user.UserAvatarUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ServerLogging extends ListenerAdapter
{
    private static GuildSettingsDataManager db;
    private final Parser parser;

    public ServerLogging(GuildSettingsDataManager db)
    {
        ServerLogging.db = db;
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

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel serverlog = db.getServerlogChannel(guild);
        TextChannel welcome = db.getWelcomeChannel(guild);
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        User newmember = event.getMember().getUser();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));
        String msg = db.getWelcomeMessage(guild);
        parser.clear().put("user", newmember).put("guild", guild).put("channel", welcome);

        if(!(serverlog==null))
        {
            if(!(serverlog.getGuild().getSelfMember().hasPermission(serverlog, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                    null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                serverlog.sendMessage("`["+hour+":"+min+":"+sec+"] [Member Join]:` :inbox_tray: :bust_in_silhouette: **"+newmember.getName()+"**#**"+newmember.getDiscriminator()+"** ("+newmember.getId()+") joined the guild! User count: **"+guild.getMembers().size()+"** members").queue();
        }

        if(!(welcome==null) && !(msg==null))
        {
            if(!(welcome.getGuild().getSelfMember().hasPermission(welcome, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.WELCOME_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.WELCOME_NOPERMISSIONS).queue()));
            else
                welcome.sendMessage(parser.parse(msg).trim()).queueAfter(2, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel serverlog = db.getServerlogChannel(guild);
        TextChannel leave = db.getLeaveChannel(guild);
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        User odMember = event.getMember().getUser();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));
        String msg = db.getLeaveMessage(guild);
        parser.clear().put("user", odMember).put("guild", guild).put("channel", leave);

        if(!(serverlog==null))
        {
            if(!(serverlog.getGuild().getSelfMember().hasPermission(serverlog, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                    null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                serverlog.sendMessage("`["+hour+":"+min+":"+sec+"] [Member Left]:` :outbox_tray: :bust_in_silhouette: **"+odMember.getName()+"**#**"+odMember.getDiscriminator()+"** ("+odMember.getId()+") left the guild! User count: **"+guild.getMembers().size()+"** members").queue();
        }

        if(!(leave==null) && !(msg==null))
        {
            if(!(leave.getGuild().getSelfMember().hasPermission(leave, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.LEAVE_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.LEAVE_NOPERMISSIONS).queue()));
            else
                leave.sendMessage(parser.parse(msg).trim()).queue();
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        TextChannel tc = db.getServerlogChannel(event.getGuild());

        if(!(tc==null) && !(event.getAuthor().isBot()))
            MessagesLogging.addMessage(event.getMessage().getIdLong(), event.getMessage());
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        Guild guild = event.getGuild();
        TextChannel tc = db.getServerlogChannel(guild);
        Message message = MessagesLogging.getMsg(event.getMessageIdLong());
        Message newmsg = event.getMessage();
        String title;
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());

        if(!(message.getContent().equals("No cached message")) && !(tc==null) && !(event.getAuthor().isBot()))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
            {
                title = "`[Message Edited]:` :pencil2: **"+message.getAuthor().getName()+"#"+message.getAuthor().getDiscriminator()+"**'s message was edited in "+message.getTextChannel().getAsMention()+":";

                builder.setAuthor(message.getAuthor().getName(), null, message.getAuthor().getEffectiveAvatarUrl());
                builder.addField("Old Content:", "```"+message.getContent()+"```", false);
                builder.addField("New Content:", "```"+newmsg.getContent()+"```", false);
                builder.setFooter("Message ID: "+message.getId(), null);
                builder.setColor(event.getGuild().getSelfMember().getColor());
                builder.setTimestamp(message.getCreationTime());

                tc.sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue((m) -> {
                    MessagesLogging.removeMessage(newmsg.getIdLong());
                    MessagesLogging.addMessage(newmsg.getIdLong(), newmsg);
                });
            }
        }
        else
        {
            if(!(tc==null) && !(event.getAuthor().isBot()))
            {
                if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                    guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                            null, (e) -> tc.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
                else
                {
                    title = "`[Message Edited]:` :pencil2: A message was edited:";

                    builder.addField("Old Content:", "```No cached message.```", false);
                    builder.addField("New Content:", "```"+newmsg.getContent()+"```", false);
                    builder.setFooter("Message ID: " + event.getMessageId(), null);
                    builder.setColor(event.getGuild().getSelfMember().getColor());

                    tc.sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue((m) -> {
                        MessagesLogging.removeMessage(newmsg.getIdLong());
                        MessagesLogging.addMessage(newmsg.getIdLong(), newmsg);
                    });
                }
            }
        }
    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        Guild guild = event.getGuild();
        TextChannel tc = db.getServerlogChannel(guild);
        Message message = MessagesLogging.getMsg(event.getMessageIdLong());
        String title;
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());

        if(!(message.getContent().equals("No cached message")) && !(tc==null) && !(message.getAuthor().isBot()))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
            {
                title = "`[Message Deleted]:` :wastebasket: **"+message.getAuthor().getName()+"#"+message.getAuthor().getDiscriminator()+"**'s message was deleted in "+message.getTextChannel().getAsMention()+":";

                builder.setAuthor(message.getAuthor().getName(), null, message.getAuthor().getEffectiveAvatarUrl());
                builder.setDescription("```\n"+message.getContent()+"```");
                builder.setFooter("Message ID: "+message.getId(), null);
                builder.setColor(event.getGuild().getSelfMember().getColor());
                builder.setTimestamp(message.getCreationTime());

                tc.sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue((m) -> MessagesLogging.removeMessage(message.getIdLong()));
            }
        }
        else
        {
            if(!(tc==null) && !(message.getAuthor()==null))
            {
                if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                    guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                            null, (e) -> tc.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
                else
                {
                    title = "`[Message Deleted]:` :wastebasket: A message was deleted:";

                    builder.setDescription("```No cached message.```");
                    builder.setFooter("Message ID: " + event.getMessageId(), null);
                    builder.setColor(event.getGuild().getSelfMember().getColor());

                    tc.sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue((m) -> MessagesLogging.removeMessage(message.getIdLong()));
                }
            }
        }
    }

    @Override
    public void onUserAvatarUpdate(UserAvatarUpdateEvent event)
    {
        List<Guild> guilds = event.getUser().getMutualGuilds();
        EmbedBuilder builder = new EmbedBuilder();
        User user = event.getUser();
        String title = "`[Avatar Update]:` :frame_photo: **"+user.getName()+"#"+user.getDiscriminator()+"** changed their avatar: ";

        if(!(guilds.isEmpty()) && !(user.isBot()))
        {
            for(Guild guild : guilds)
            {
                TextChannel tc = db.getServerlogChannel(guild);
                TextChannel channel = FinderUtil.getDefaultChannel(guild);

                if(!(tc==null))
                {
                    if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                        guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                                null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
                    else
                    {
                        builder.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
                        builder.setThumbnail(event.getPreviousAvatarUrl());
                        builder.setImage(user.getEffectiveAvatarUrl());
                        builder.setColor(guild.getSelfMember().getColor());

                        tc.sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();
                    }
                }
            }
        }
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel tc = db.getServerlogChannel(guild);
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        VoiceChannel vc = event.getChannelJoined();
        User user = event.getMember().getUser();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc==null) && !(user.isBot()))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Voice Join]:`  **"+user.getName()+"#"+user.getDiscriminator()+"** has joined a Voice Channel: **"+vc.getName()+"** (ID: "+vc.getId()+")").queue();
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel tc = db.getServerlogChannel(guild);
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        VoiceChannel vcold = event.getChannelLeft();
        VoiceChannel vcnew = event.getChannelJoined();
        User user = event.getMember().getUser();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc==null) && !(user.isBot()))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Voice Move]:` **"+user.getName()+"#"+user.getDiscriminator()+"** switched between Voice Channels: From: **"+vcold.getName()+"** To: **"+vcnew.getName()+"**").queue();
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel tc = db.getServerlogChannel(guild);
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        VoiceChannel vc = event.getChannelLeft();
        User user = event.getMember().getUser();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc==null) && !(user.isBot()))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Voice Left]:` **"+user.getName()+"#"+user.getDiscriminator()+"** left a Voice Channel: **"+vc.getName()+"**").queue();
        }
    }
}

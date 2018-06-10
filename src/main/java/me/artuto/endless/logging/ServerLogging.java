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

package me.artuto.endless.logging;

import com.jagrosh.jagtag.Parser;
import com.jagrosh.jagtag.ParserBuilder;
import com.jagrosh.jagtag.libraries.*;
import me.artuto.endless.Bot;
import me.artuto.endless.Messages;
import me.artuto.endless.data.managers.GuildSettingsDataManager;
import me.artuto.endless.entities.ParsedAuditLog;
import me.artuto.endless.tempdata.MessagesLogging;
import me.artuto.endless.tools.Variables;
import me.artuto.endless.utils.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audit.ActionType;
import net.dv8tion.jda.core.audit.AuditLogEntry;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ServerLogging
{
    private final GuildSettingsDataManager gsdm;
    private final Parser parser;

    public ServerLogging(Bot bot)
    {
        this.gsdm = bot.gsdm;
        this.parser = new ParserBuilder().addMethods(Variables.getMethods()).addMethods(Arguments.getMethods()).addMethods(Functional.getMethods()).addMethods(Miscellaneous.getMethods()).addMethods(Strings.getMethods()).addMethods(Time.getMethods()).addMethods(com.jagrosh.jagtag.libraries.Variables.getMethods()).setMaxOutput(2000).setMaxIterations(1000).build();
    }

    void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel serverlog = gsdm.getServerlogChannel(guild);
        TextChannel welcome = gsdm.getWelcomeChannel(guild);
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        User newMember = event.getMember().getUser();
        String msg = gsdm.getWelcomeMessage(guild);
        parser.clear().put("user", newMember).put("guild", guild).put("channel", welcome);

        if(!(serverlog == null))
        {
            if(!(Checks.hasPermission(guild.getSelfMember(), serverlog, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                serverlog.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Member Join]:` :inbox_tray: :bust_in_silhouette: **"+newMember.getName()+"**#**"+newMember.getDiscriminator()+"** ("+newMember.getId()+") joined the guild! User count: **"+guild.getMembers().size()+"** members").queue();
        }

        if(!(welcome == null) && !(msg == null))
        {
            if(!(Checks.hasPermission(guild.getSelfMember(), welcome, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.WELCOME_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.WELCOME_NOPERMISSIONS).queue()));
            else welcome.sendMessage(parser.parse(msg).trim()).queueAfter(1, TimeUnit.SECONDS);
        }
    }

    void onGuildMemberLeave(GuildMemberLeaveEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel serverlog = gsdm.getServerlogChannel(guild);
        TextChannel leave = gsdm.getLeaveChannel(guild);
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        User oldMember = event.getMember().getUser();
        String msg = gsdm.getLeaveMessage(guild);
        parser.clear().put("user", oldMember).put("guild", guild).put("channel", leave);

        if(!(serverlog == null))
        {
            if(Checks.hasPermission(guild.getSelfMember(), null, Permission.VIEW_AUDIT_LOGS))
            {
                guild.getAuditLogs().type(ActionType.BAN).limit(20).queue(preEntries -> {
                    List<AuditLogEntry> entries = preEntries.stream().filter(ale -> ale.getTargetIdLong()==oldMember.getIdLong()).collect(Collectors.toList());

                    if(entries.isEmpty())
                        return;

                    ParsedAuditLog parsedAuditLog = GuildUtils.getAuditLog(entries.get(0), null);
                    if(!(parsedAuditLog==null))
                        return;

                    if(!(Checks.hasPermission(guild.getSelfMember(), serverlog, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)))
                        guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
                    else
                        serverlog.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Member Left]:` :outbox_tray: :bust_in_silhouette: **"+oldMember.getName()+"**#**"+oldMember.getDiscriminator()+"** ("+oldMember.getId()+") left the guild! User count: **"+guild.getMembers().size()+"** members").queue();
                });
            }
            else
            {
                if(!(Checks.hasPermission(guild.getSelfMember(), serverlog, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)))
                    guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
                else
                    serverlog.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Member Left]:` :outbox_tray: :bust_in_silhouette: **"+oldMember.getName()+"**#**"+oldMember.getDiscriminator()+"** ("+oldMember.getId()+") left the guild! User count: **"+guild.getMembers().size()+"** members").queue();
            }
        }

        if(!(leave == null) && !(msg == null))
        {
            if(!(Checks.hasPermission(guild.getSelfMember(), leave, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.LEAVE_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.LEAVE_NOPERMISSIONS).queue()));
            else leave.sendMessage(parser.parse(msg).trim()).queue();
        }
    }

    void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        TextChannel tc = gsdm.getServerlogChannel(event.getGuild());

        if(!(tc == null) && !(event.getAuthor().isBot()))
        {
            if(IgnoreUtils.isIgnored(event.getAuthor().getId(), tc.getTopic()) || IgnoreUtils.isIgnored(event.getChannel().getId(), tc.getTopic()))
                return;

            MessagesLogging.addMessage(event.getMessage().getIdLong(), event.getMessage());
        }
    }

    void onGuildMessageUpdate(GuildMessageUpdateEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        Guild guild = event.getGuild();
        TextChannel tc = gsdm.getServerlogChannel(guild);
        Message message = MessagesLogging.getMsg(event.getMessageIdLong());
        Message newmsg = event.getMessage();
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        StringBuilder oldContent = new StringBuilder();
        StringBuilder newContent = new StringBuilder();

        if(!(event.getAuthor().isBot()) && !(message.getContentRaw().equals("No cached message")) && !(tc == null))
        {
            if(IgnoreUtils.isIgnored(event.getAuthor().getId(), tc.getTopic()) || IgnoreUtils.isIgnored(event.getChannel().getTopic(), tc.getTopic()))
                return;

            if(!(Checks.hasPermission(guild.getSelfMember(), tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
            {
                oldContent.append(message.getContentRaw()).append("\n");
                newContent.append(newmsg.getContentRaw()).append("\n");
                for(Message.Attachment att : message.getAttachments())
                    oldContent.append(att.getUrl()).append("\n");
                for(Message.Attachment att : newmsg.getAttachments())
                    newContent.append(att.getUrl()).append("\n");

                String title = "`"+TimeUtils.getTimeAndDate()+" [Message Edited]:` :pencil2: **"+message.getAuthor().getName()+"#"+message.getAuthor().getDiscriminator()+"**'s message was edited in "+message.getTextChannel().getAsMention()+":";

                builder.addField("From:", oldContent.toString(), false);
                builder.addField("To:", newContent.toString(), false);
                builder.setFooter("Message ID: "+message.getId(), null);
                builder.setColor(Color.YELLOW);

                tc.sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue((m) ->
                {
                    MessagesLogging.removeMessage(newmsg.getIdLong());
                    MessagesLogging.addMessage(newmsg.getIdLong(), newmsg);
                }, null);
            }
        }
    }

    void onGuildMessageDelete(GuildMessageDeleteEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        Guild guild = event.getGuild();
        TextChannel tc = gsdm.getServerlogChannel(guild);
        Message message = MessagesLogging.getMsg(event.getMessageIdLong());
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        StringBuilder sb = new StringBuilder();

        if(!(message.getContentRaw().equals("No cached message")) && !(tc == null) && !(message.getAuthor().isBot()))
        {
            if(IgnoreUtils.isIgnored(message.getAuthor().getId(), tc.getTopic()) || IgnoreUtils.isIgnored(event.getChannel().getTopic(), tc.getTopic()))
                return;

            if(!(Checks.hasPermission(guild.getSelfMember(), tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
            {
                sb.append(message.getContentRaw()).append("\n");
                for(Message.Attachment att : message.getAttachments())
                    sb.append(att.getUrl()).append("\n");

                String title = "`"+TimeUtils.getTimeAndDate()+" [Message Deleted]:` :wastebasket: **"+message.getAuthor().getName()+"#"+message.getAuthor().getDiscriminator()+"**'s message was deleted in "+message.getTextChannel().getAsMention()+":";

                builder.setDescription(sb.toString());
                builder.setFooter("Message ID: "+message.getId(), null);
                builder.setColor(Color.RED);

                tc.sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue((m) -> MessagesLogging.removeMessage(message.getIdLong()), null);
            }
        }
    }

    void onUserUpdateAvatar(UserUpdateAvatarEvent event)
    {
        List<Guild> guilds = event.getUser().getMutualGuilds();
        EmbedBuilder builder = new EmbedBuilder();
        User user = event.getUser();
        String title = "`"+TimeUtils.getTimeAndDate()+" [Avatar Update]:` :frame_photo: **"+user.getName()+"#"+user.getDiscriminator()+"** changed their avatar: ";

        if(!(guilds.isEmpty()) && !(user.isBot()))
        {
            for(Guild guild : guilds)
            {
                TextChannel tc = gsdm.getServerlogChannel(guild);
                TextChannel channel = FinderUtil.getDefaultChannel(guild);

                if(!(tc == null))
                {
                    if(IgnoreUtils.isIgnored(user.getId(), tc.getTopic()))
                        return;

                    if(!(Checks.hasPermission(guild.getSelfMember(), tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS)))
                        guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
                    else
                    {
                        builder.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
                        builder.setImage("attachment://avatarchange"+user.getId()+".png");
                        builder.setColor(guild.getSelfMember().getColor());

                        File f = LogUtils.getAvatarUpdateImage(event);
                        if(f==null)
                            return;

                        tc.sendFile(f, new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();
                    }
                }
            }
        }
    }

    void onGuildVoiceJoin(GuildVoiceJoinEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel tc = gsdm.getServerlogChannel(guild);
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        VoiceChannel vc = event.getChannelJoined();
        User user = event.getMember().getUser();

        if(!(tc == null) && !(user.isBot()))
        {
            if(IgnoreUtils.isIgnored(user.getId(), tc.getTopic()) || IgnoreUtils.isIgnored(vc.getId(), tc.getTopic()))
                return;

            if(!(Checks.hasPermission(guild.getSelfMember(), tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                tc.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Voice Join]:`  **"+user.getName()+"#"+user.getDiscriminator()+"** has joined a Voice Channel: **"+vc.getName()+"** (ID: "+vc.getId()+")").queue();
        }
    }

    void onGuildVoiceMove(GuildVoiceMoveEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel tc = gsdm.getServerlogChannel(guild);
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        VoiceChannel vcold = event.getChannelLeft();
        VoiceChannel vcnew = event.getChannelJoined();
        User user = event.getMember().getUser();

        if(!(tc == null) && !(user.isBot()))
        {
            if(IgnoreUtils.isIgnored(user.getId(), tc.getTopic()) || IgnoreUtils.isIgnored(vcnew.getId(), tc.getTopic()) || IgnoreUtils.isIgnored(vcold.getId(), tc.getTopic()))
                return;

            if(!(Checks.hasPermission(guild.getSelfMember(), tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                tc.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Voice Move]:` **"+user.getName()+"#"+user.getDiscriminator()+"** switched between Voice Channels: From: **"+vcold.getName()+"** To: **"+vcnew.getName()+"**").queue();
        }
    }

    void onGuildVoiceLeave(GuildVoiceLeaveEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel tc = gsdm.getServerlogChannel(guild);
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        VoiceChannel vc = event.getChannelLeft();
        User user = event.getMember().getUser();

        if(!(tc == null) && !(user.isBot()))
        {
            if(IgnoreUtils.isIgnored(user.getId(), tc.getTopic()) || IgnoreUtils.isIgnored(vc.getId(), tc.getTopic()))
                return;

            if(!(Checks.hasPermission(guild.getSelfMember(), tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                tc.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Voice Left]:` **"+user.getName()+"#"+user.getDiscriminator()+"** left a Voice Channel: **"+vc.getName()+"**").queue();
        }
    }
}

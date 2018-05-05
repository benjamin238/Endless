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
import me.artuto.endless.Messages;
import me.artuto.endless.data.GuildSettingsDataManager;
import me.artuto.endless.tempdata.MessagesLogging;
import me.artuto.endless.tools.Variables;
import me.artuto.endless.utils.FinderUtil;
import me.artuto.endless.utils.IgnoreUtils;
import me.artuto.endless.utils.TimeUtils;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ServerLogging extends ListenerAdapter
{
    private final GuildSettingsDataManager db;
    private final Parser parser;

    public ServerLogging(GuildSettingsDataManager db)
    {
        this.db = db;
        this.parser = new ParserBuilder().addMethods(Variables.getMethods()).addMethods(Arguments.getMethods()).addMethods(Functional.getMethods()).addMethods(Miscellaneous.getMethods()).addMethods(Strings.getMethods()).addMethods(Time.getMethods()).addMethods(com.jagrosh.jagtag.libraries.Variables.getMethods()).setMaxOutput(2000).setMaxIterations(1000).build();
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel serverlog = db.getServerlogChannel(guild);
        TextChannel welcome = db.getWelcomeChannel(guild);
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        User newMember = event.getMember().getUser();
        String msg = db.getWelcomeMessage(guild);
        parser.clear().put("user", newMember).put("guild", guild).put("channel", welcome);

        if(!(serverlog == null))
        {
            if(!(serverlog.getGuild().getSelfMember().hasPermission(serverlog, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                serverlog.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Member Join]:` :inbox_tray: :bust_in_silhouette: **"+newMember.getName()+"**#**"+newMember.getDiscriminator()+"** ("+newMember.getId()+") joined the guild! User count: **"+guild.getMembers().size()+"** members").queue();
        }

        if(!(welcome == null) && !(msg == null))
        {
            if(!(welcome.getGuild().getSelfMember().hasPermission(welcome, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.WELCOME_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.WELCOME_NOPERMISSIONS).queue()));
            else welcome.sendMessage(parser.parse(msg).trim()).queueAfter(2, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel serverlog = db.getServerlogChannel(guild);
        TextChannel leave = db.getLeaveChannel(guild);
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        User oldMember = event.getMember().getUser();
        String msg = db.getLeaveMessage(guild);
        parser.clear().put("user", oldMember).put("guild", guild).put("channel", leave);

        if(!(serverlog == null))
        {
            if(guild.getSelfMember().hasPermission(Permission.BAN_MEMBERS))
                if(wasBanned(event.getMember())) return;

            if(!(serverlog.getGuild().getSelfMember().hasPermission(serverlog, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                serverlog.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Member Left]:` :outbox_tray: :bust_in_silhouette: **"+oldMember.getName()+"**#**"+oldMember.getDiscriminator()+"** ("+oldMember.getId()+") left the guild! User count: **"+guild.getMembers().size()+"** members").queue();
        }

        if(!(leave == null) && !(msg == null))
        {
            if(!(leave.getGuild().getSelfMember().hasPermission(leave, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.LEAVE_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.LEAVE_NOPERMISSIONS).queue()));
            else leave.sendMessage(parser.parse(msg).trim()).queue();
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        TextChannel tc = db.getServerlogChannel(event.getGuild());

        if(!(tc == null) && !(event.getAuthor().isBot()))
        {
            if(IgnoreUtils.isIgnored(event.getAuthor().getId(), tc.getTopic()) || IgnoreUtils.isIgnored(event.getChannel().getId(), tc.getTopic()))
                return;

            MessagesLogging.addMessage(event.getMessage().getIdLong(), event.getMessage());
        }
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        Guild guild = event.getGuild();
        TextChannel tc = db.getServerlogChannel(guild);
        Message message = MessagesLogging.getMsg(event.getMessageIdLong());
        Message newmsg = event.getMessage();
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        StringBuilder oldContent = new StringBuilder();
        StringBuilder newContent = new StringBuilder();

        if(!(event.getAuthor().isBot()) && !(message.getContentRaw().equals("No cached message")) && !(tc == null))
        {
            if(IgnoreUtils.isIgnored(event.getAuthor().getId(), tc.getTopic()) || IgnoreUtils.isIgnored(event.getChannel().getTopic(), tc.getTopic()))
                return;

            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
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

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        Guild guild = event.getGuild();
        TextChannel tc = db.getServerlogChannel(guild);
        Message message = MessagesLogging.getMsg(event.getMessageIdLong());
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        StringBuilder sb = new StringBuilder();

        if(!(message.getContentRaw().equals("No cached message")) && !(tc == null) && !(message.getAuthor().isBot()))
        {
            if(IgnoreUtils.isIgnored(message.getAuthor().getId(), tc.getTopic()) || IgnoreUtils.isIgnored(event.getChannel().getTopic(), tc.getTopic()))
                return;

            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
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

    @Override
    public void onUserUpdateAvatar(UserUpdateAvatarEvent event)
    {
        List<Guild> guilds = event.getUser().getMutualGuilds();
        EmbedBuilder builder = new EmbedBuilder();
        User user = event.getUser();
        String title = "`"+TimeUtils.getTimeAndDate()+" [Avatar Update]:` :frame_photo: **"+user.getName()+"#"+user.getDiscriminator()+"** changed their avatar: ";

        if(!(guilds.isEmpty()) && !(user.isBot()))
        {
            for(Guild guild : guilds)
            {
                TextChannel tc = db.getServerlogChannel(guild);
                TextChannel channel = FinderUtil.getDefaultChannel(guild);

                if(!(tc == null))
                {
                    if(IgnoreUtils.isIgnored(user.getId(), tc.getTopic()))
                        return;

                    if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                        guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
                    else
                    {
                        builder.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
                        builder.setThumbnail(event.getOldAvatarUrl());
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

        if(!(tc == null) && !(user.isBot()))
        {
            if(IgnoreUtils.isIgnored(user.getId(), tc.getTopic()) || IgnoreUtils.isIgnored(vc.getId(), tc.getTopic()))
                return;

            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                tc.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Voice Join]:`  **"+user.getName()+"#"+user.getDiscriminator()+"** has joined a Voice Channel: **"+vc.getName()+"** (ID: "+vc.getId()+")").queue();
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

        if(!(tc == null) && !(user.isBot()))
        {
            if(IgnoreUtils.isIgnored(user.getId(), tc.getTopic()) || IgnoreUtils.isIgnored(vcnew.getId(), tc.getTopic()) || IgnoreUtils.isIgnored(vcold.getId(), tc.getTopic()))
                return;

            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                tc.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Voice Move]:` **"+user.getName()+"#"+user.getDiscriminator()+"** switched between Voice Channels: From: **"+vcold.getName()+"** To: **"+vcnew.getName()+"**").queue();
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

        if(!(tc == null) && !(user.isBot()))
        {
            if(IgnoreUtils.isIgnored(user.getId(), tc.getTopic()) || IgnoreUtils.isIgnored(vc.getId(), tc.getTopic()))
                return;

            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
                tc.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Voice Left]:` **"+user.getName()+"#"+user.getDiscriminator()+"** left a Voice Channel: **"+vc.getName()+"**").queue();
        }
    }

    @Override
    public void onGuildBan(GuildBanEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel tc = db.getServerlogChannel(guild);
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        User user = event.getUser();

        if(!(tc == null))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            else
            {
                if(guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS))
                    tc.sendMessage(":hammer: `"+TimeUtils.getTimeAndDate()+" [User Banned]:` **"+user.getName()+"#"+user.getDiscriminator()+"** has been banned!\n" +
                        "`[Reason]:` "+getBanReason(user, guild)).queue();

            }
        }
    }

    private boolean wasBanned(Member member)
    {
        for(Guild.Ban ban : member.getGuild().getBanList().complete())
            return ban.getUser().equals(member.getUser());
        return false;
    }

    private String getBanReason(User user, Guild guild)
    {
        String reason;
        reason = guild.getBanList().complete().stream().filter(ban -> ban.getUser().equals(user)).map(Guild.Ban::getReason).collect(Collectors.joining("\n"));

        if(reason==null || reason.isEmpty() || reason.equals("null"))
        {
            if(guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS))
                reason = guild.getAuditLogs().type(ActionType.BAN).complete().stream().filter(entry -> entry.getTargetIdLong()==user.getIdLong()).limit(1).map(AuditLogEntry::getReason).collect(Collectors.joining("\n"));
            else reason = "[no reason specified]";
        }

        return reason;
    }
}

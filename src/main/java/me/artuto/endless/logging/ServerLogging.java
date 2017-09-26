package me.artuto.endless.logging;

import me.artuto.endless.Bot;
import me.artuto.endless.Messages;
import me.artuto.endless.data.Settings;
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

public class ServerLogging extends ListenerAdapter
{
    private static Bot bot;
    private static Settings settings;
    public ServerLogging(Bot bot)
    {
        ServerLogging.bot = bot;
    }
    private EmbedBuilder builder = new EmbedBuilder();

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        Settings set = bot.getSettings(event.getGuild());
        TextChannel tc = event.getGuild().getTextChannelById(set.getServerLogId());
        Guild guild = event.getGuild();
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        User newmember = event.getMember().getUser();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc==null))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                    null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Member Join]:` :inbox_tray: :bust_in_silhouette: **"+newmember.getName()+"**#**"+newmember.getDiscriminator()+"** ("+newmember.getId()+") joined the guild! User count: **"+guild.getMembers().size()+"** members").queue();
            }
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event)
    {
        Settings set = bot.getSettings(event.getGuild());
        TextChannel tc = event.getGuild().getTextChannelById(set.getServerLogId());
        Guild guild = event.getGuild();
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        User oldmember = event.getMember().getUser();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc==null))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                    null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Member Left]:` :outbox_tray: :bust_in_silhouette: **"+oldmember.getName()+"**#**"+oldmember.getDiscriminator()+"** ("+oldmember.getId()+") left the guild! User count: **"+guild.getMembers().size()+"** members").queue();
            }
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        Settings set = bot.getSettings(event.getGuild());
        TextChannel tc = event.getGuild().getTextChannelById(set.getServerLogId());

        if(!(tc==null) && !(event.getAuthor().isBot()))
        {
            MessagesLogging.addMessage(event.getMessage().getIdLong(), event.getMessage());
        }
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event)
    {
        Settings set = bot.getSettings(event.getGuild());
        TextChannel tc = event.getGuild().getTextChannelById(set.getServerLogId());
        Message message = MessagesLogging.getMsg(event.getMessageIdLong());
        Message newmsg = event.getMessage();
        String title;
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        Guild guild = event.getGuild();

        if(!(message.getContent().equals("No cached message")) && !(tc==null))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                title = "`[Message Edited]:` :pencil2: **"+message.getAuthor().getName()+"#"+message.getAuthor().getDiscriminator()+"**'s message was edited in "+message.getTextChannel().getAsMention()+":";

                builder.setAuthor(message.getAuthor().getName(), null, message.getAuthor().getEffectiveAvatarUrl());
                builder.addField("Old Content:", "```"+message.getContent()+"```", false);
                builder.addField("New Content:", "```"+newmsg.getContent()+"```", false);
                builder.setFooter("Message ID: "+message.getId(), null);
                builder.setColor(event.getGuild().getSelfMember().getColor());
                builder.setTimestamp(message.getCreationTime());

                tc.sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();

                MessagesLogging.removeMessage(newmsg.getIdLong());
                MessagesLogging.addMessage(newmsg.getIdLong(), newmsg);
            }
        }
        else
        {
            if(!(tc==null))
            {
                if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                {
                    guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                            null, (e) -> tc.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
                }
                else
                {
                    title = "`[Message Edited]:` :pencil2: A message was edited:";

                    builder.addField("Old Content:", "```No cached message.```", false);
                    builder.addField("New Content:", "```"+newmsg.getContent()+"```", false);
                    builder.setFooter("Message ID: " + event.getMessageId(), null);
                    builder.setColor(event.getGuild().getSelfMember().getColor());

                    tc.sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();

                    MessagesLogging.removeMessage(newmsg.getIdLong());
                    MessagesLogging.addMessage(newmsg.getIdLong(), newmsg);
                }
            }
        }


    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event)
    {
        Settings set = bot.getSettings(event.getGuild());
        TextChannel tc = event.getGuild().getTextChannelById(set.getServerLogId());
        Message message = MessagesLogging.getMsg(event.getMessageIdLong());
        String title;
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        Guild guild = event.getGuild();

        if(!(message.getContent().equals("No cached message")) && !(tc==null))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                title = "`[Message Deleted]:` :wastebasket: **"+message.getAuthor().getName()+"#"+message.getAuthor().getDiscriminator()+"**'s message was deleted in "+message.getTextChannel().getAsMention()+":";

                builder.setAuthor(message.getAuthor().getName(), null, message.getAuthor().getEffectiveAvatarUrl());
                builder.setDescription("```\n"+message.getContent()+"```");
                builder.setFooter("Message ID: "+message.getId(), null);
                builder.setColor(event.getGuild().getSelfMember().getColor());
                builder.setTimestamp(message.getCreationTime());

                tc.sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();

                MessagesLogging.removeMessage(message.getIdLong());
            }
        }
        else
        {
            if(!(tc==null))
            {
                if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                {
                    guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                            null, (e) -> tc.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
                }
                else
                {
                    title = "`[Message Deleted]:` :wastebasket: A message was deleted:";

                    builder.setDescription("```No cached message.```");
                    builder.setFooter("Message ID: " + event.getMessageId(), null);
                    builder.setColor(event.getGuild().getSelfMember().getColor());

                    tc.sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();

                    MessagesLogging.removeMessage(message.getIdLong());
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

        if(!(guilds.isEmpty()))
        {
            for(Guild guild : guilds)
            {
                Settings set = bot.getSettings(guild);
                TextChannel tc = guild.getTextChannelById(set.getServerLogId());
                TextChannel channel = FinderUtil.getDefaultChannel(guild);

                if(!(tc==null))
                {
                    if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                    {
                        guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                                null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
                    }
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
        Settings set = bot.getSettings(event.getGuild());
        TextChannel tc = event.getGuild().getTextChannelById(set.getServerLogId());
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        Guild guild = event.getGuild();
        VoiceChannel vc = event.getChannelJoined();
        User user = event.getMember().getUser();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc==null))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Voice Join]:`  **"+user.getName()+"#"+user.getDiscriminator()+"** has joined a Voice Channel: **"+vc.getName()+"** (ID: "+vc.getId()+")").queue();
            }
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event)
    {
        Settings set = bot.getSettings(event.getGuild());
        TextChannel tc = event.getGuild().getTextChannelById(set.getServerLogId());
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        Guild guild = event.getGuild();
        VoiceChannel vcold = event.getChannelLeft();
        VoiceChannel vcnew = event.getChannelJoined();
        User user = event.getMember().getUser();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc==null))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Voice Move]:` **"+user.getName()+"#"+user.getDiscriminator()+"** switched between Voice Channels: From: **"+vcold.getName()+"** To: **"+vcnew.getName()+"**").queue();
            }
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event)
    {
        Settings set = bot.getSettings(event.getGuild());
        TextChannel tc = event.getGuild().getTextChannelById(set.getServerLogId());
        TextChannel channel = FinderUtil.getDefaultChannel(event.getGuild());
        Guild guild = event.getGuild();
        VoiceChannel vc = event.getChannelLeft();
        User user = event.getMember().getUser();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc==null))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Voice Left]:` **"+user.getName()+"#"+user.getDiscriminator()+"** left a Voice Channel: **"+vc.getName()+"**").queue();
            }
        }
    }
}

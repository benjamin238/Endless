/*package me.artuto.endless.utils;

import me.artuto.endless.Bot;
import me.artuto.endless.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ServerLogging extends ListenerAdapter
{
    private final Bot bot;

    public ServerLogging(Bot bot)
    {
        this.bot = bot;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        Settings set = bot.getSettings(event.getGuild());
        TextChannel tc = event.getGuild().getTextChannelById(set.getServerLogId());
        Guild guild = event.getGuild();
        TextChannel channel = event.getGuild().getPublicChannel();
        User newmember = event.getMember().getUser();
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        if(set.getServerLogId()==0)
        {
            return;
        }
        else if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
        {
            guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                    null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
        }
        else
        {
            tc.sendMessage("`["+hour+":"+min+":"+sec+"]` `Member Join:` :bust_in_silhouette: **"+newmember.getName()+"**#**"+newmember.getDiscriminator()+"** ("+newmember.getId()+") joined the guild! User count: **"+guild.getMembers().size()+"** members").queue();
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event)
    {
        Settings set = bot.getSettings(event.getGuild());
        TextChannel tc = event.getGuild().getTextChannelById(set.getServerLogId());
        Guild guild = event.getGuild();
        TextChannel channel = event.getGuild().getPublicChannel();
        User newmember = event.getMember().getUser();
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        if(set.getServerLogId()==0)
        {
            return;
        }
        else if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
        {
            guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                    null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
        }
        else
        {
            tc.sendMessage("`["+hour+":"+min+":"+sec+"]` `Member Left:` :bust_in_silhouette: **"+newmember.getName()+"**#**"+newmember.getDiscriminator()+"** ("+newmember.getId()+") left the guild! User count: **"+guild.getMembers().size()+"** members").queue();
        }
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        Settings set = bot.getSettings(event.getGuild());
        TextChannel tc = event.getGuild().getTextChannelById(set.getServerLogId());
        Guild guild = event.getGuild();
        TextChannel channel = event.getGuild().getPublicChannel();
        User editer = event.getMember().getUser();
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        String success = "`["+hour+":"+min+":"+sec+"]` `Message Edited:` :pencil2: **"+editer.getName()+"**#**"+editer.getDiscriminator()+"** ("+editer.getId()+") edited a message:";

        builder.addField("From: ", event.getMessage()., false);

        if(set.getServerLogId()==0)
        {
            return;
        }
        else if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
        {
            guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                    null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
        }
        else
        {
            tc.sendMessage().queue();
        }
    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        Settings set = bot.getSettings(event.getGuild());
        TextChannel tc = event.getGuild().getTextChannelById(set.getServerLogId());
        Guild guild = event.getGuild();
        TextChannel channel = event.getGuild().getPublicChannel();
        User editer = event.getMember().getUser();
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        String success = "`["+hour+":"+min+":"+sec+"]` `Message Edited:` :pencil2: **"+editer.getName()+"**#**"+editer.getDiscriminator()+"** ("+editer.getId()+") edited a message:";

        builder.addField("From: ", event.getJDA().event.getMessageId(), false);

        if(set.getServerLogId()==0)
        {
            return;
        }
        else if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
        {
            guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue(
                    null, (e) -> channel.sendMessage(Messages.SRVLOG_NOPERMISSIONS).queue()));
        }
        else
        {
            tc.sendMessage().queue();
        }
    }
}*/

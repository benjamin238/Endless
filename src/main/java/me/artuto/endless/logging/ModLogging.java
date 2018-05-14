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

import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.Messages;
import me.artuto.endless.data.GuildSettingsDataManager;
import me.artuto.endless.entities.GuildSettings;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.utils.TimeUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audit.AuditLogChange;
import net.dv8tion.jda.core.audit.AuditLogEntry;
import net.dv8tion.jda.core.audit.AuditLogKey;
import net.dv8tion.jda.core.entities.*;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

/**
 * @author Artuto
 * Class used for ModLog actions.
 */

public class ModLogging
{
    private Bot bot;

    public ModLogging(Bot bot)
    {
        this.bot = bot;
    }

    public void logBan(User author, Member target, String reason, Guild guild, TextChannel channel)
    {
        TextChannel tc = bot.gsdm.getModlogChannel(guild);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc == null))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Ban]:` :hammer: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") banned **"+target.getUser().getName()+"**#**"+target.getUser().getDiscriminator()+"** ("+target.getUser().getId()+")\n"+"`[Reason]:` "+reason).queue();
            }
        }
    }

    public void logHackban(User author, User target, String reason, Guild guild, TextChannel channel)
    {
        TextChannel tc = bot.gsdm.getModlogChannel(guild);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc == null))
        {
            if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Hackban]:` :hammer: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") hackbanned **"+target.getName()+"**#**"+target.getDiscriminator()+"** ("+target.getId()+")\n"+"`[Reason]:` "+reason).queue();
            }
        }
    }

    public void logKick(User author, Member target, String reason, Guild guild, TextChannel channel)
    {
        TextChannel tc = bot.gsdm.getModlogChannel(guild);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc == null))
        {
            if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Kick]:` :boot: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") kicked **"+target.getUser().getName()+"**#**"+target.getUser().getDiscriminator()+"** ("+target.getUser().getId()+")\n"+"`[Reason]:` "+reason).queue();
            }
        }
    }

    public void logSoftban(User author, Member target, String reason, Guild guild, TextChannel channel)
    {
        TextChannel tc = bot.gsdm.getModlogChannel(guild);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc == null))
        {
            if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Softban]:` :banana: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") softbanned **"+target.getUser().getName()+"**#**"+target.getUser().getDiscriminator()+"** ("+target.getUser().getId()+")\n"+"`[Reason]:` "+reason).queue();
            }
        }
    }

    public void logUnban(User author, User target, String reason, Guild guild, TextChannel channel)
    {
        TextChannel tc = bot.gsdm.getModlogChannel(guild);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc == null))
        {
            if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Unban]:` :wrench: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") unbanned **"+target.getName()+"**#**"+target.getDiscriminator()+"** ("+target.getId()+")\n"+"`[Reason]:` "+reason).queue();
            }
        }
    }

    public void logClear(User author, TextChannel channel, String reason, Guild guild, List<Message> deleted, String args)
    {
        TextChannel tc = bot.gsdm.getModlogChannel(guild);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));
        File file = new File("cleared.txt");

        if(!(tc == null))
        {
            if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                try
                {
                    Writer output = new BufferedWriter(new FileWriter(file, true));

                    for(Message msg : deleted)
                    {
                        User a = msg.getAuthor();

                        if(!(msg.getContentDisplay().isEmpty()))
                        {
                            String toWrite = a.getName()+"#"+a.getDiscriminator()+": "+msg.getContentDisplay()+"\n";
                            output.append(toWrite);
                        }
                    }

                    output.close();
                }
                catch(Exception e)
                {
                    LoggerFactory.getLogger("Clear Modlog").error("Error when creating the text file with the deleted messages: "+e);
                }

                String message = "`["+hour+":"+min+":"+sec+"] [Clear]:` :wastebasket: **"+author.getName()+"**#**"+author.getDiscriminator()+"** cleared **"+deleted.size()+"** messages in "+channel.getAsMention()+" ("+args+").\n"+"`[Reason]:` "+reason;

                if(!(file.exists())) tc.sendMessage(message).queue();
                else
                    tc.sendFile(file, "cleared.txt", new MessageBuilder().append(message).build()).queue((s) -> file.delete());
            }
        }
    }

    public void logMute(User author, Member target, String reason, Guild guild, TextChannel channel)
    {
        TextChannel tc = bot.gsdm.getModlogChannel(guild);

        if(!(tc == null))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            else
            {
                if(author==null)
                    tc.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Mute]:` :mute: **"+target.getUser().getName()+"**#**"+target.getUser().getDiscriminator()+"** " +
                            "("+target.getUser().getId()+") has been muted.\n"+"`[Reason]:` "+reason).queue();
                else
                    tc.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Mute]:` :mute: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") " +
                            "muted **"+target.getUser().getName()+"**#**"+target.getUser().getDiscriminator()+"** ("+target.getUser().getId()+")\n"+"`[Reason]:` "+reason).queue();
            }
        }
    }

    public void logTempMute(User author, Member target, String reason, Guild guild, TextChannel channel, int time)
    {
        TextChannel tc = bot.gsdm.getModlogChannel(guild);
        String formattedTime = FormatUtil.formatTimeFromSeconds(time);

        if(!(tc == null))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            else
                tc.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Mute]:` :mute: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") " +
                        "muted **"+target.getUser().getName()+"**#**"+target.getUser().getDiscriminator()+"** ("+target.getUser().getId()+") for "+formattedTime+"\n"+"`[Reason]:` "+reason).queue();
        }
    }

    public void logUnmute(User author, Member target, String reason, Guild guild, TextChannel channel)
    {
        TextChannel tc = bot.gsdm.getModlogChannel(guild);

        if(!(tc == null))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            else
            {
                if(author==null)
                    tc.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Unmute]:` :speaker: **"+target.getUser().getName()+"**#**"+target.getUser().getDiscriminator()+"** " +
                            "("+target.getUser().getId()+") has been unmuted.\n"+"`[Reason]:` "+reason).queue();
                else
                    tc.sendMessage("`"+TimeUtils.getTimeAndDate()+" [Unmute]:` :speaker: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") unmuted " +
                            "**"+target.getUser().getName()+"**#**"+target.getUser().getDiscriminator()+"** ("+target.getUser().getId()+")\n"+"`[Reason]:` "+reason).queue();
            }
        }
    }
}

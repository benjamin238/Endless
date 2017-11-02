/*
 * Copyright (C) 2017 Artu
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

import me.artuto.endless.Messages;
import me.artuto.endless.data.DatabaseManager;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import me.artuto.endless.Bot;
import me.artuto.endless.data.Settings;

/**
 *
 * @author Artu
 * Class used for ModLog actions.
 */

public class ModLogging 
{
    private static DatabaseManager db;

    public ModLogging(DatabaseManager db)
    {
        ModLogging.db = db;
    }

    public void logBan(User author, Member target, String reason, Guild guild, TextChannel channel)
    {
        TextChannel tc = db.getModlogChannel(guild);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc==null))
        {
            if(!(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Ban]:` :hammer: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") banned **"+target.getUser().getName()+"**#**"+target.getUser().getDiscriminator()+"** ("+target.getUser().getId()+")\n"
                        + "`[Reason]:` "+reason).queue();
            }
        }
    }

    public void logHackban(User author, User target, String reason, Guild guild, TextChannel channel)
    {
        TextChannel tc = db.getModlogChannel(guild);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc==null))
        {
            if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Hackban]:` :hammer: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") hackbanned **"+target.getName()+"**#**"+target.getDiscriminator()+"** ("+target.getId()+")\n"
                        + "`[Reason]:` "+reason).queue();
            }
        }
    }

    public void logKick(User author, Member target, String reason, Guild guild, TextChannel channel)
    {
        TextChannel tc = db.getModlogChannel(guild);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc==null))
        {
            if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Kick]:` :boot: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") kicked **"+target.getUser().getName()+"**#**"+target.getUser().getDiscriminator()+"** ("+target.getUser().getId()+")\n"
                        + "`[Reason]:` "+reason).queue();
            }
        }
    }

    public void logSoftban(User author, Member target, String reason, Guild guild, TextChannel channel)
    {
        TextChannel tc = db.getModlogChannel(guild);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc==null))
        {
            if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Softban]:` :banana: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") softbanned **"+target.getUser().getName()+"**#**"+target.getUser().getDiscriminator()+"** ("+target.getUser().getId()+")\n"
                        + "`[Reason]:` "+reason).queue();
            }
        }
    }

    public void logUnban(User author, User target, String reason, Guild guild, TextChannel channel)
    {
        TextChannel tc = db.getModlogChannel(guild);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        if(!(tc==null))
        {
            if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                tc.sendMessage("`["+hour+":"+min+":"+sec+"] [Unban]:` :wrench: **"+author.getName()+"**#**"+author.getDiscriminator()+"** ("+author.getId()+") unbanned **"+target.getName()+"**#**"+target.getDiscriminator()+"** ("+target.getId()+")\n"
                        + "`[Reason]:` "+reason).queue();
            }
        }
    }

    public void logClear(User author, TextChannel channel, String reason, Guild guild, List<Message> deleted, String args)
    {
        TextChannel tc = db.getModlogChannel(guild);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));
        File file = new File("cleared.txt");

        if(!(tc==null))
        {
            if(!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY))
            {
                guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue(
                        null, (e) -> channel.sendMessage(Messages.MODLOG_NOPERMISSIONS).queue()));
            }
            else
            {
                try
                {
                    Writer output = new BufferedWriter(new FileWriter(file, true));

                        for(Message msg : deleted)
                        {
                            User a = msg.getAuthor();

                            if(!(msg.getContent().isEmpty()))
                            {
                                String toWrite = a.getName() + "#" + a.getDiscriminator() + ": " + msg.getContent() + "\n";
                                output.append(toWrite);
                            }
                        }

                        output.close();
                }
                catch(Exception e)
                {
                    SimpleLog.getLog("Clear Modlog").fatal("Error when creating the text file with the deleted messages: "+e);
                }

                String message = "`["+hour+":"+min+":"+sec+"] [Clear]:` :wastebasket: **"+author.getName()+"**#**"+author.getDiscriminator()+"** cleared **"+deleted.size()+"** messages in "+channel.getAsMention()+" ("+args+").\n"
                        + "`[Reason]:` "+reason;

                if(!(file.exists()))
                    tc.sendMessage(message).queue();
                else
                    tc.sendFile(file, "cleared.txt", new MessageBuilder().append(message).build()).queue((s) -> file.delete());
            }
        }
    }
}

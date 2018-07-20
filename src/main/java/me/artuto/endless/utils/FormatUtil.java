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

package me.artuto.endless.utils;

import net.dv8tion.jda.core.entities.*;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.List;

/**
 * @author Artuto
 *
 */

public class FormatUtil
{
    public static String formatLogKick(String message)
    {
        return String.format(message);
    }

    public static String listOfMembers(List<Member> list, String query)
    {
        String out = " Multiple members found matching \""+query+"\":";
        for(int i = 0; i<6 && i<list.size(); i++)
            out += "\n - "+list.get(i).getUser().getName()+"#"+list.get(i).getUser().getDiscriminator()+" (ID:"+list.get(i).getUser().getId()+")";
        if(list.size()>6) out += "\n**And "+(list.size()-6)+" more...**";
        return out;
    }

    public static String listOfUsers(List<User> list, String query)
    {
        String out = " Multiple users found matching \""+query+"\":";
        for(int i = 0; i<6 && i<list.size(); i++)
            out += "\n - "+list.get(i).getName()+"#"+list.get(i).getDiscriminator()+" (ID:"+list.get(i).getId()+")";
        if(list.size()>6) out += "\n**And "+(list.size()-6)+" more...**";
        return out;
    }

    public static String listOfRoles(List<Role> list, String query)
    {
        String out = " Multiple roles found matching \""+query+"\":";
        for(int i = 0; i<6 && i<list.size(); i++)
            out += "\n - "+list.get(i).getName()+" (ID:"+list.get(i).getId()+")";
        if(list.size()>6) out += "\n**And "+(list.size()-6)+" more...**";
        return out;
    }

    public static String listOfTcChannels(List<TextChannel> list, String query)
    {
        String out = " Multiple text channels found matching \""+query+"\":";
        for(int i = 0; i<6 && i<list.size(); i++)
            out += "\n - "+list.get(i).getName()+" (ID:"+list.get(i).getId()+")";
        if(list.size()>6) out += "\n**And "+(list.size()-6)+" more...**";
        return out;
    }

    public static String listOfVcChannels(List<VoiceChannel> list, String query)
    {
        String out = " Multiple voice channels found matching \""+query+"\":";
        for(int i = 0; i<6 && i<list.size(); i++)
            out += "\n - "+list.get(i).getName()+" (ID:"+list.get(i).getId()+")";
        if(list.size()>6) out += "\n**And "+(list.size()-6)+" more...**";
        return out;
    }

    public static String formatTimeFromSeconds(long seconds)
    {
        StringBuilder builder = new StringBuilder();
        int years = (int)(seconds / (60*60*24*365));
        if(years>0)
        {
            builder.append("**").append(years).append("** years, ");
            seconds = seconds % (60*60*24*365);
        }
        int weeks = (int)(seconds / (60*60*24*365));
        if(weeks>0)
        {
            builder.append("**").append(weeks).append("** weeks, ");
            seconds = seconds % (60*60*24*7);
        }
        int days = (int)(seconds / (60*60*24));
        if(days>0)
        {
            builder.append("**").append(days).append("** days, ");
            seconds = seconds % (60*60*24);
        }
        int hours = (int)(seconds / (60*60));
        if(hours>0)
        {
            builder.append("**").append(hours).append("** hours, ");
            seconds = seconds % (60*60);
        }
        int minutes = (int)(seconds / (60));
        if(minutes>0)
        {
            builder.append("**").append(minutes).append("** minutes, ");
            seconds = seconds % (60);
        }
        if(seconds>0)
            builder.append("**").append(seconds).append("** seconds");
        String str = builder.toString();
        if(str.endsWith(", "))
            str = str.substring(0, str.length()-2);
        if(str.isEmpty())
            str = "**No time**";
        return str;
    }
}

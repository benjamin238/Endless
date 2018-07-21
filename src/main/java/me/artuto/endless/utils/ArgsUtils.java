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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Artuto
 */

public class ArgsUtils
{
    private static final Pattern ID = Pattern.compile("(?:^|\\s)(\\d{17,22})(?:$|\\s)");

    public static int parseTime(String timestr)
    {
        timestr = timestr.replaceAll("(?i)(\\s|,|and)","").replaceAll("(?is)(-?\\d+|[a-z]+)", "$1 ").trim();
        String[] vals = timestr.split("\\s+");
        int timeinseconds = 0;

        try
        {
            for(int j=0; j<vals.length; j+=2)
            {
                int num = Integer.parseInt(vals[j]);
                if(vals[j+1].toLowerCase().startsWith("m"))
                    num*=60;
                else if(vals[j+1].toLowerCase().startsWith("h"))
                    num*=60*60;
                else if(vals[j+1].toLowerCase().startsWith("d"))
                    num*=60*60*24;
                else if(vals[j+1].toLowerCase().startsWith("w"))
                    return -1;
                else if(vals[j+1].toLowerCase().startsWith("m"))
                    num*=60*60*24*30;
                else if(vals[j+1].toLowerCase().startsWith("y"))
                    num*=60*60*24*365;
                timeinseconds+=num;
            }
        }
        catch(Exception ex)
        {
            return 0;
        }

        return timeinseconds;
    }

    public static Member findMember(CommandEvent event, String query)
    {
        List<Member> list = FinderUtil.findMembers(query, event.getGuild());

        if(list.isEmpty())
        {
            event.replyWarning("I was not able to found a user with the provided arguments: '"+query+"'");
            return null;
        }
        else if(list.size()>1)
        {
            event.replyWarning(FormatUtil.listOfMembers(list, query));
            return null;
        }
        else
            return list.get(0);
    }

    public static User findBannedUser(CommandEvent event, String query)
    {
        List<User> list = FinderUtil.findBannedUsers(query, event.getGuild());

        if(list.isEmpty())
        {
            event.replyWarning("I was not able to found a banned user with the provided arguments: '"+query+"'");
            return null;
        }
        else if(list.size()>1)
        {
            event.replyWarning(FormatUtil.listOfUsers(list, query));
            return null;
        }
        else
            return list.get(0);
    }

    public static User findUser(boolean full, CommandEvent event, String query)
    {
        List<User> list = FinderUtil.findUsers(query, event.getJDA());

        if(list.isEmpty())
        {
            if(full)
            {
                Matcher m = ID.matcher(query);
                if(ID.matcher(query).matches())
                {
                    try
                    {
                        return event.getJDA().retrieveUserById(m.group(1)).complete();
                    }
                    catch(ErrorResponseException ignored) {}
                }
            }
            event.replyWarning("I was not able to found a user with the provided arguments: '"+query+"'");
            return null;
        }
        else if(list.size()>1)
        {
            event.replyWarning(FormatUtil.listOfUsers(list, query));
            return null;
        }
        else
            return list.get(0);
    }

    public static String[] splitWithReason(int limit, String args, String regex)
    {
        try
        {
            String[] argsArr = args.split(regex, limit);
            return new String[]{argsArr[0], argsArr[1]};
        }
        catch(IndexOutOfBoundsException e)
        {
            return new String[]{args, "[no reason provided]"};
        }
    }

    public static String[] splitWithReasonAndTime(int limit, String args, String regex)
    {
        int time;
        String reason = "[no reason specified]";
        String target;

        try
        {
            String[] argsArr = args.split(regex, limit);
            target = argsArr[0].trim();
            time = ArgsUtils.parseTime(argsArr[1].trim());

            try
            {
                if(time==0)
                    reason = argsArr[1].trim();
                else
                    reason = argsArr[1].trim().split(regex, 2)[1].trim();
            }
            catch(ArrayIndexOutOfBoundsException ignored) {}
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            target = args.trim();
            time = 0;
        }

        return new String[]{target, String.valueOf(time), reason};
    }
}

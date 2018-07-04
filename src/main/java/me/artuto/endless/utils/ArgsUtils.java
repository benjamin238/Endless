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

/**
 * @author Artuto
 */

public class ArgsUtils
{
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
}

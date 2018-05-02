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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Artuto
 */

public class TimeUtils
{
    public static String getTimeAndDate()
    {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        String month = String.format("%02d", calendar.get(Calendar.MONTH)+1);
        String year = String.format("%02d", calendar.get(Calendar.YEAR));
        String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        return "["+day+"/"+month+"/"+year+"] ["+hour+":"+min+":"+sec+"]";
    }

    public static String getTime()
    {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String sec = String.format("%02d", calendar.get(Calendar.SECOND));

        return "["+hour+":"+min+":"+sec+"]";
    }

    public static String getDate()
    {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        String month = String.format("%02d", calendar.get(Calendar.MONTH)+1);
        String year = String.format("%02d", calendar.get(Calendar.YEAR));

        return "["+day+"/"+month+"/"+year+"]";
    }
}

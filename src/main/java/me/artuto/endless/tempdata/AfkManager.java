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

package me.artuto.endless.tempdata;

import java.util.HashMap;

public class AfkManager
{
    private static HashMap<Long, String> afk = new HashMap<>();

    public static void setAfk(Long id, String message)
    {
        afk.put(id, message);
    }

    public static String getMessage(Long id)
    {
        return afk.get(id);
    }

    public static HashMap<Long, String> getMap()
    {
        return afk;
    }

    public static void unsetAfk(Long id)
    {
        afk.remove(id);
    }

    public static boolean isAfk(Long id)
    {
        return afk.containsKey(id);
    }
}

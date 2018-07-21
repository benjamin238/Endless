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

package me.artuto.endless;

/**
 * @author Artuto
 * <p>
 * <p>
 * Since sometimes we need to use the same messages a lot of times,
 * is better having everything equals.
 */

public class Messages
{
    // Warning
    public static String SRVLOG_NOPERMISSIONS = "You have a serverlog channel configured, but I don't have the required permissions, make sure I have: `Message Read` and `Message Write`!";
    public static String WELCOME_NOPERMISSIONS = "You have a welcome channel configured, but I don't have the required permissions, make sure I have: `Message Read` and `Message Write`!";
    public static String LEAVE_NOPERMISSIONS = "You have a leave channel configured, but I don't have the required permissions, make sure I have: `Message Read` and `Message Write`!";
}

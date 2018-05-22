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
    // Moderation Messages
    public static String BAN_SUCCESS = "Successfully banned user ";
    public static String BAN_ERROR = "An error happened when banning ";
    public static String KICK_SUCCESS = "Successfully kicked user ";
    public static String KICK_ERROR = "An error happened when kicking ";
    public static String UNBAN_SUCCESS = "Successfully unbanned user ";
    public static String UNBAN_ERROR = "An error happened when unbanning ";
    public static String SOFTBAN_SUCCESS = "Successfully softbanned user ";
    public static String SOFTBAN_ERROR = "An error happened when softbanning ";
    public static String HACKBAN_SUCCESS = "Successfully hackbanned user ";
    public static String HACKBAN_ERROR = "An error happened when hackbanning ";
    public static String CLEAR_SUCCESS = "Successfully cleared ";
    public static String CLEAR_ERROR = "An error happened when clearing ";
    public static String MUTE_SUCCESS = "Successfully muted ";
    public static String MUTE_ERROR = "An error happened when muting ";

    // Warning
    public static String MODLOG_NOPERMISSIONS = "You have a modlog channel configured, but I don't have the required permissions, make sure I have: `Message Read` and `Message Write`!";
    public static String CLEARMODLOG_NOPERMISSIONS = "You have a modlog channel configured, but I don't have the required permissions, make sure I have: `Upload Attachments`!";
    public static String SRVLOG_NOPERMISSIONS = "You have a serverlog channel configured, but I don't have the required permissions, make sure I have: `Message Read` and `Message Write`!";
    public static String WELCOME_NOPERMISSIONS = "You have a welcome channel configured, but I don't have the required permissions, make sure I have: `Message Read` and `Message Write`!";
    public static String LEAVE_NOPERMISSIONS = "You have a leave channel configured, but I don't have the required permissions, make sure I have: `Message Read` and `Message Write`!";
}

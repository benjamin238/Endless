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

import net.dv8tion.jda.core.JDAInfo;

/**
 * @author Artuto
 */

public class Const
{
    public static String VERSION = Const.class.getPackage().getImplementationVersion()==null?"DEV":Const.class.getPackage().getImplementationVersion();
    public static String INVITE = "https://discord.gg/CXKfYW3";
    public static String DEV = "`@Artuto#0424 | 264499432538505217`";
    public static String USER_AGENT = "Endless "+VERSION+" (Discord Bot/JDA "+JDAInfo.VERSION+")";

    // IDs
    public static long ARTUTO_ID = 264499432538505217L;
    public static long ARTUTO_ALT_ID = 302534881370439681L;
    public static long MAIN_GUILD = 312776731704426496L;
    public static long GUILD_TESTING = 323954859713888256L;

    // Emotes
    public static String BAN = ":hammer:";
    public static String BOTADM = "<:stafftools:334859814700187650>";
    public static String ENDLESS = "<:endless:447899790727053324>";
    public static String GIPHY = "<:giphy:373675520099090436>";
    public static String GITHUB = "<:github:326118305062584321>";
    public static String GOOGLE = "<:google:447911997783277569>";
    public static String INFO = "<:endlessInfo:444203939303522305>";
    public static String LINE_START = ":white_medium_small_square:";
    public static String LOADING = "<a:endlessLoading:444198122965434399>";
    public static String PEOPLE = ":bust_in_silhouette:";
    public static String SERVER_SETTINGS = ":wrench:";

    // Enums
    public enum BlacklistType
    {
        GUILD,
        USER
    }
    public enum PunishmentType
    {
        BAN,
        MUTE,
        TEMPBAN,
        TEMPMUTE
    }
}

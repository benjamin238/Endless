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

package me.artuto.endless.tools;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

public class InfoTools
{
    public static boolean hasUserRoles(Member member)
    {
        if(member.getRoles().isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static boolean hasGuildRoles(Guild guild)
    {
        if(guild.getRoles().toString().replace("@everyone", "").isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String mentionUserRoles(Member member)
    {
        StringBuilder rolesbldr = new StringBuilder();

        if(hasUserRoles(member))
        {
            member.getRoles().forEach(r -> rolesbldr.append(" ").append(r.getAsMention()));
            return rolesbldr.toString();
        }
        else
        {
            return "None";
        }
    }

    public static String mentionGuildRoles(Guild guild)
    {
        StringBuilder rolesbldr = new StringBuilder();

        if(hasGuildRoles(guild))
        {
            guild.getRoles().forEach(r -> rolesbldr.append(" ").append(r.getAsMention()));
            return rolesbldr.toString();
        }
        else
        {
            return "None";
        }
    }

    public static String onlineStatus(Member member)
    {
        String emote;

        if(member.getOnlineStatus().toString().equals("ONLINE"))
        {
            emote = "<:online:334859814410911745>";
        }
        else if(member.getOnlineStatus().toString().equals("IDLE"))
        {
            emote = "<:away:334859813869584384>";
        }
        else if(member.getOnlineStatus().toString().equals("DO_NOT_DISTURB"))
        {
            emote = "<:dnd:334859814029099008>";
        }
        else if(member.getOnlineStatus().toString().equals("INVISIBLE"))
        {
            emote = "<:invisible:334859814410649601>";
        }
        else if(member.getOnlineStatus().toString().equals("OFFLINE"))
        {
            emote = "<:offline:334859814423232514>";
        }
        else
        {
            emote = ":interrobang:";
        }

        return emote;
    }

    public static boolean nitroCheck(User user)
    {
        if(!(user.getAvatarId() == null))
        {
            if(user.getAvatarId().startsWith("a_"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }


}

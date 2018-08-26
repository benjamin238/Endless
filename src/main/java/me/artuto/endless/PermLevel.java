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

import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Artuto
 */

public enum PermLevel
{
    EVERYONE(0),
    MODERATOR(2),
    ADMINISTRATOR(3),
    OWNER(4);

    private final int level;

    PermLevel(int level)
    {
        this.level = level;
    }

    public boolean isAtLeast(PermLevel other)
    {
        return level>=other.getLevel();
    }

    public int getLevel()
    {
        return level;
    }

    public static PermLevel getLevel(GuildSettings settings, Member member)
    {
        User user = member.getUser();
        if(user.getIdLong()==Const.ARTUTO_ID || user.getIdLong()==Const.ARTUTO_ALT_ID)
            return OWNER;
        else if(ChecksUtil.hasPermission(member, null, Permission.MANAGE_SERVER))
            return ADMINISTRATOR;
        else
        {
            if(settings.isDefault())
                return EVERYONE;
            else if(member.getRoles().contains(GuildUtils.getAdminRole(member.getGuild())))
                return ADMINISTRATOR;
            else if(member.getRoles().contains(GuildUtils.getModRole(member.getGuild())))
                return MODERATOR;
            return EVERYONE;
        }
    }
}

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

import me.artuto.endless.Const;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;


/**
 * @author Artuto
 */

public class ChecksUtil
{
    public static boolean canMemberInteract(Member issuer, Member target)
    {
        User user = issuer.getUser();

        if(user.getIdLong()==Const.ARTUTO_ID || user.getIdLong()==Const.ARTUTO_ALT_ID)
            return true;

        return issuer.canInteract(target);
    }

    public static boolean canMemberInteract(Member issuer, Role target)
    {
        User user = issuer.getUser();

        if(user.getIdLong()==Const.ARTUTO_ID || user.getIdLong()==Const.ARTUTO_ALT_ID)
            return true;

        return issuer.canInteract(target);
    }

    public static boolean hasPermission(Member target, Channel channel, Permission... perms)
    {
        User user = target.getUser();

        if(user.getIdLong()==Const.ARTUTO_ID || user.getIdLong()==Const.ARTUTO_ALT_ID)
            return true;
        else if(!(channel==null))
            return target.hasPermission(channel, perms);
        else
            return target.hasPermission(perms);
    }
}

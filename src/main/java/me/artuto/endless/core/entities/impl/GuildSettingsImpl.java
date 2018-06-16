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

package me.artuto.endless.core.entities.impl;

import me.artuto.endless.core.entities.GuildSettings;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;

import java.util.Collection;
import java.util.List;

public class GuildSettingsImpl implements GuildSettings
{
    private final Collection<String> prefixes;
    private final Guild guild;
    private final int banDeleteDays, starboardCount;
    private final List<Role> roleMeRoles;
    private final long modlogId, serverlogId, welcomeId, leaveId, starboardId, mutedRoleId;
    private final String welcomeMsg, leaveMsg;

    public GuildSettingsImpl(Collection<String> prefixes, Guild guild, int banDeleteDays, int starboardCount, List<Role> roleMeRoles, long leaveId, long modlogId, long mutedRoleId,
                             long serverlogId, long starboardId, long welcomeId, String leaveMsg, String welcomeMsg)
    {
        this.prefixes = prefixes;
        this.guild = guild;
        this.banDeleteDays = banDeleteDays;
        this.starboardCount = starboardCount;
        this.roleMeRoles = roleMeRoles;
        this.leaveId = leaveId;
        this.modlogId = modlogId;
        this.mutedRoleId = mutedRoleId;
        this.serverlogId = serverlogId;
        this.starboardId = starboardId;
        this.welcomeId = welcomeId;
        this.leaveMsg = leaveMsg;
        this.welcomeMsg = welcomeMsg;
    }

    @Override
    public Collection<String> getPrefixes()
    {
        return prefixes;
    }

    @Override
    public Guild getGuild()
    {
        return guild;
    }

    @Override
    public int getBanDeleteDays()
    {
        return banDeleteDays;
    }

    @Override
    public int getStarboardCount()
    {
        return starboardCount;
    }

    @Override
    public List<Role> getRoleMeRoles()
    {
        return roleMeRoles;
    }

    @Override
    public long getLeaveChannel()
    {
        return leaveId;
    }

    @Override
    public long getModlog()
    {
        return modlogId;
    }

    @Override
    public long getMutedRole()
    {
        return mutedRoleId;
    }

    @Override
    public long getServerlog()
    {
        return serverlogId;
    }

    @Override
    public long getStarboard()
    {
        return starboardId;
    }

    @Override
    public long getWelcomeChannel()
    {
        return welcomeId;
    }

    @Override
    public String getLeaveMsg()
    {
        return leaveMsg;
    }

    @Override
    public String getWelcomeMsg()
    {
        return welcomeMsg;
    }

    @Override
    public String toString()
    {
        return String.format("GS:%s(%s)", guild.getName(), guild.getId());
    }
}

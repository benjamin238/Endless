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

package me.artuto.endless.entities.impl;

import me.artuto.endless.entities.GuildSettings;

import java.util.Collection;

public class GuildSettingsImpl implements GuildSettings
{
    private final Collection<String> prefixes;
    private final int starboardCount;
    private final long modlogId, serverlogId, welcomeId, leaveId, starboardId, mutedRoleId;
    private final String welcomeMsg, leaveMsg;

    public GuildSettingsImpl(long modlogId, long serverlogId, long welcomeId, String welcomeMsg,
                             long leaveId, String leaveMsg, long starboardId, int starboardCount, Collection<String> prefixes, long mutedRoleId)
    {
        this.modlogId = modlogId;
        this.serverlogId = serverlogId;
        this.welcomeId = welcomeId;
        this.welcomeMsg = welcomeMsg;
        this.leaveMsg = leaveMsg;
        this.leaveId = leaveId;
        this.starboardId = starboardId;
        this.starboardCount = starboardCount;
        this.prefixes = prefixes;
        this.mutedRoleId = mutedRoleId;
    }

    @Override
    public long getModlog()
    {
        return modlogId;
    }

    @Override
    public long getServerlog()
    {
        return serverlogId;
    }

    @Override
    public long getWelcomeChannel()
    {
        return welcomeId;
    }

    @Override
    public String getWelcomeMsg()
    {
        return welcomeMsg;
    }

    @Override
    public long getLeaveChannel()
    {
        return leaveId;
    }

    @Override
    public String getLeaveMsg()
    {
        return leaveMsg;
    }

    @Override
    public long getStarboard()
    {
        return starboardId;
    }

    @Override
    public int getStarboardCount()
    {
        return starboardCount;
    }

    @Override
    public Collection<String> getPrefixes()
    {
        return prefixes;
    }

    @Override
    public long getMutedRole()
    {
        return mutedRoleId;
    }
}

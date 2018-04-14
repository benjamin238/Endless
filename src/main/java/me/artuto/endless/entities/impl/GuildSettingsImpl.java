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
    private final Long modlogId;
    private final Long serverlogId;
    private final Long welcomeId;
    private final String welcomeMsg;
    private final Long leaveId;
    private final String leaveMsg;
    private final Long starboardId;
    private final int starboardCount;
    private final Collection<String> prefixes;

    public GuildSettingsImpl(Long modlogId, Long serverlogId, Long welcomeId, String welcomeMsg, Long leaveId, String leaveMsg, Long starboardId, int starboardCount, Collection<String> prefixes)
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
    }

    @Override
    public Long getModlog()
    {
        return modlogId;
    }

    @Override
    public Long getServerlog()
    {
        return serverlogId;
    }

    @Override
    public Long getWelcomeChannel()
    {
        return welcomeId;
    }

    @Override
    public String getWelcomeMsg()
    {
        return welcomeMsg;
    }

    @Override
    public Long getLeaveChannel()
    {
        return leaveId;
    }

    @Override
    public String getLeaveMsg()
    {
        return leaveMsg;
    }

    @Override
    public Long getStarboard()
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
}

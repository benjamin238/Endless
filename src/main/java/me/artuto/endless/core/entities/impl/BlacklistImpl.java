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

import me.artuto.endless.core.entities.Blacklist;
import me.artuto.endless.core.entities.BlacklistType;

import java.time.OffsetDateTime;

/**
 * @author Artuto
 */

public class BlacklistImpl implements Blacklist
{
    private BlacklistType type;
    private long id;
    private OffsetDateTime time;
    private String reason;

    public BlacklistImpl(BlacklistType type, long id, OffsetDateTime time, String reason)
    {
        this.type = type;
        this.id = id;
        this.time = time;
        this.reason = reason;
    }

    @Override
    public BlacklistType getType()
    {
        return type;
    }

    @Override
    public long getId()
    {
        return id;
    }

    @Override
    public OffsetDateTime getTime()
    {
        return time;
    }

    @Override
    public String getReason()
    {
        return reason;
    }
}

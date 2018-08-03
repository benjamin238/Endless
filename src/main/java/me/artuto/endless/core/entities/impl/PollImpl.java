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

import me.artuto.endless.core.entities.Poll;

import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Artuto
 */

public class PollImpl implements Poll
{
    private final long endTime, guildId, msgId, tcId;

    public PollImpl(long endTime, long guildId, long msgId, long tcId)
    {
        this.endTime = endTime;
        this.guildId = guildId;
        this.msgId = msgId;
        this.tcId = tcId;
    }

    @Override
    public long getGuildId()
    {
        return guildId;
    }

    @Override
    public long getMessageId()
    {
        return msgId;
    }

    @Override
    public long getTextChannelId()
    {
        return tcId;
    }

    @Override
    public OffsetDateTime getEndTime()
    {
        Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmt.setTimeInMillis(endTime);
        return OffsetDateTime.ofInstant(gmt.toInstant(), gmt.getTimeZone().toZoneId());
    }

    @Override
    public String toString()
    {
        return String.format("P:%d(%d)", msgId, tcId);
    }
}

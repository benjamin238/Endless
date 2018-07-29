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

import me.artuto.endless.core.entities.StarboardMessage;

public class StarboardMessageImpl implements StarboardMessage
{
    private final Long msgId;
    private final Long tcId;
    private final Long guildId;
    private final Integer amount;
    private final Long starboardMsgId;

    public StarboardMessageImpl(Long msgId, Long tcId, Long guildId, Integer amount, Long starboardMsgId)
    {
        this.msgId = msgId;
        this.tcId = tcId;
        this.guildId = guildId;
        this.amount = amount;
        this.starboardMsgId = starboardMsgId;
    }

    @Override
    public String getMessageId()
    {
        return msgId.toString();
    }

    @Override
    public Long getMessageIdLong()
    {
        return msgId;
    }

    @Override
    public String getTextChannelId()
    {
        return tcId.toString();
    }

    @Override
    public Long getTextChannelIdLong()
    {
        return tcId;
    }

    @Override
    public String getGuildId()
    {
        return guildId.toString();
    }

    @Override
    public Long getGuildIdLong()
    {
        return guildId;
    }

    @Override
    public Integer getAmount()
    {
        return amount;
    }

    @Override
    public String getStarboardMessageId()
    {
        return starboardMsgId.toString();
    }

    @Override
    public Long getStarboardMessageIdLong()
    {
        return starboardMsgId;
    }

    @Override
    public String toString()
    {
        return "SM:"+getMessageId()+" ("+getTextChannelId()+")";
    }
}

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

import me.artuto.endless.core.entities.LocalTag;

/**
 * @author Artuto
 */

public class LocalTagImpl implements LocalTag
{
    private boolean overriden;
    private long guildId, ownerId, tagId;
    private String content, name;

    public LocalTagImpl(boolean overriden, long guildId, long ownerId, long tagId, String content, String name)
    {
        this.overriden = overriden;
        this.guildId = guildId;
        this.ownerId = ownerId;
        this.tagId = tagId;
        this.content = content;
        this.name = name;
    }

    @Override
    public boolean isGlobal()
    {
        return false;
    }

    @Override
    public boolean isNSFW()
    {
        return content.toLowerCase().contains("{nsfw}");
    }

    @Override
    public boolean isOverriden()
    {
        return overriden;
    }

    @Override
    public long getGuildId()
    {
        return guildId;
    }

    @Override
    public long getId()
    {
        return tagId;
    }

    @Override
    public long getOwnerId()
    {
        return ownerId;
    }

    @Override
    public String getContent()
    {
        return content;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return String.format("LT:%s(%d:%d)", name, guildId, tagId);
    }

    public void setContent(String newContent)
    {
        this.content = newContent;
    }

    public void setGuildId(long guildId)
    {
        this.guildId = guildId;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setOwnerId(long ownerId)
    {
        this.ownerId = ownerId;
    }
}

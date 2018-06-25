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

import me.artuto.endless.core.entities.GlobalTag;

/**
 * @author Artuto
 */

public class GlobalTagImpl implements GlobalTag
{
    private long ownerId, tagId;
    private String content, name;

    public GlobalTagImpl(long ownerId, long tagId, String content, String name)
    {
        this.ownerId = ownerId;
        this.tagId = tagId;
        this.content = content;
        this.name = name;
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
        return String.format("GlobalTag: %s(%s)", name, tagId);
    }

    public void setContent(String newContent)
    {
        this.content = newContent;
    }

    public void setOwnerId(long ownerId)
    {
        this.ownerId = ownerId;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}

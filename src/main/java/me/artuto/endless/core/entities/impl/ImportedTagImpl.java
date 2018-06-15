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

import me.artuto.endless.core.entities.ImportedTag;

public class ImportedTagImpl implements ImportedTag
{
    private final Long tagId;
    private final String name;
    private final String content;
    private final Long owner;
    private final Long guild;

    public ImportedTagImpl(Long tagId, String name, String content, Long owner, Long guild)
    {
        this.tagId = tagId;
        this.name = name;
        this.content = content;
        this.owner = owner;
        this.guild = guild;
    }

    @Override
    public String getId()
    {
        return tagId.toString();
    }

    @Override
    public Long getIdLong()
    {
        return tagId;
    }

    @Override
    public String getName()
    {
        return name.toLowerCase().split(":", 2)[1];
    }

    @Override
    public String getInternalName()
    {
        return name.toLowerCase();
    }

    @Override
    public String getContent()
    {
        return content;
    }

    @Override
    public Long getOwnerId()
    {
        return owner;
    }

    @Override
    public Long getGuildId()
    {
        return guild;
    }

    @Override
    public String toString()
    {
        return "IT:"+getName()+"/"+guild+" ("+tagId+")";
    }
}

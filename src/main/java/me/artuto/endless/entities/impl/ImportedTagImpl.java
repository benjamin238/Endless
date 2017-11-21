package me.artuto.endless.entities.impl;

import me.artuto.endless.entities.ImportedTag;

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

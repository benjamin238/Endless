package me.artuto.endless.entities.impl;

import me.artuto.endless.entities.Tag;

public class TagImpl implements Tag
{
    private final Long tagId;
    private final String name;
    private final String content;
    private final Long owner;

    public TagImpl(Long tagId, String name, String content, Long owner)
    {
        this.tagId = tagId;
        this.name = name;
        this.content = content;
        this.owner = owner;
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
        return name;
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
    public String toString()
    {
        return "T:"+getName()+" ("+tagId+")";
    }
}

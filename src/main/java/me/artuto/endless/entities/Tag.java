package me.artuto.endless.entities;

public class Tag
{
    private final Long tagId;
    private final String name;
    private final String content;
    private final Long owner;

    public Tag(Long tagId, String name, String content, Long owner)
    {
        this.tagId = tagId;
        this.name = name;
        this.content = content;
        this.owner = owner;
    }

    public String getId()
    {
        return tagId.toString();
    }

    public Long getIdLong()
    {
        return tagId;
    }

    public String getName()
    {
        return name.toLowerCase();
    }

    public String getContent()
    {
        return content;
    }

    public Long getOwnerId()
    {
        return owner;
    }
}

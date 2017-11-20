package me.artuto.endless.entities;

public class ImportedTag
{
    private final Long tagId;
    private final String name;
    private final String content;
    private final Long owner;
    private final Long guild;

    public ImportedTag(Long tagId, String name, String content, Long owner, Long guild)
    {
        this.tagId = tagId;
        this.name = name;
        this.content = content;
        this.owner = owner;
        this.guild = guild;
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

    public Long getGuildId()
    {
        return guild;
    }
}

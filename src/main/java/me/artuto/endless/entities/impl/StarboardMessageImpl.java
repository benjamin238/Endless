package me.artuto.endless.entities.impl;

import me.artuto.endless.entities.StarboardMessage;

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

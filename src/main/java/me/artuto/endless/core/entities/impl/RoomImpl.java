package me.artuto.endless.core.entities.impl;

import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.core.entities.Room;
import me.artuto.endless.utils.GuildUtils;

public class RoomImpl implements Room
{
    private boolean restricted;
    private long guildId, tcId, ownerId, vcId;

    public RoomImpl(boolean restricted, long guildId, long tcId, long ownerId, long vcId)
    {
        this.restricted = restricted;
        this.guildId = guildId;
        this.tcId = tcId;
        this.ownerId = ownerId;
        this.vcId = vcId;
    }

    @Override
    public boolean canAccess(EndlessCommandEvent event)
    {
        if(event.getAuthor().getIdLong()==ownerId || event.isOwner())
            return true;
        if(Bot.getInstance().dataEnabled)
            return event.getMember().getRoles().contains(GuildUtils.getAdminRole(event.getGuild()));
        else
            return !(restricted);
    }

    @Override
    public boolean isCombo()
    {
        return !(tcId==0L) && !(vcId==0L);
    }

    @Override
    public boolean isRestricted()
    {
        return restricted;
    }

    @Override
    public boolean isText()
    {
        return !(tcId==0) && vcId==0L;
    }

    @Override
    public boolean isVoice()
    {
        return !(vcId==0) && tcId==0L;
    }

    @Override
    public long getGuildId()
    {
        return guildId;
    }

    @Override
    public long getTextChannelId()
    {
        return tcId;
    }

    @Override
    public long getOwnerId()
    {
        return ownerId;
    }

    @Override
    public long getVoiceChannelId()
    {
        return vcId;
    }

    @Override
    public String toString()
    {
        if(isCombo())
            return "R:Combo("+tcId+"-"+vcId+")";
        else if(isText())
            return "R:Text("+tcId+")";
        else
            return "R:Voice("+vcId+")";
    }
}

package me.artuto.endless.entities.impl;

import me.artuto.endless.entities.GuildSettings;

import java.util.Collection;

public class GuildSettingsImpl implements GuildSettings
{
    private final Long modlogId;
    private final Long serverlogId;
    private final Long welcomeId;
    private final String welcomeMsg;
    private final Long leaveId;
    private final String leaveMsg;
    private final Long starboardId;
    private final int starboardCount;
    private final Collection<String> prefixes;

    public GuildSettingsImpl(Long modlogId, Long serverlogId, Long welcomeId, String welcomeMsg, Long leaveId, String leaveMsg, Long starboardId, int starboardCount, Collection<String> prefixes)
    {
        this.modlogId = modlogId;
        this.serverlogId = serverlogId;
        this.welcomeId = welcomeId;
        this.welcomeMsg = welcomeMsg;
        this.leaveMsg = leaveMsg;
        this.leaveId = leaveId;
        this.starboardId = starboardId;
        this.starboardCount = starboardCount;
        this.prefixes = prefixes;
    }

    @Override
    public Long getModlog()
    {
        return modlogId;
    }

    @Override
    public Long getServerlog()
    {
        return serverlogId;
    }

    @Override
    public Long getWelcomeChannel()
    {
        return welcomeId;
    }

    @Override
    public String getWelcomeMsg()
    {
        return welcomeMsg;
    }

    @Override
    public Long getLeaveChannel()
    {
        return leaveId;
    }

    @Override
    public String getLeaveMsg()
    {
        return leaveMsg;
    }

    @Override
    public Long getStarboard()
    {
        return starboardId;
    }
    @Override
    public int getStarboardCount()
    {
        return starboardCount;
    }

    @Override
    public Collection<String> getPrefixes()
    {
        return prefixes;
    }
}

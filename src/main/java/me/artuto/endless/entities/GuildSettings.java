package me.artuto.endless.entities;

public class GuildSettings
{
    private final Long modlogId;
    private final Long serverlogId;
    private final Long welcomeId;
    private final String welcomeMsg;
    private final Long leaveId;
    private final String leaveMsg;

    public GuildSettings(Long modlogId, Long serverlogId, Long welcomeId, String welcomeMsg, Long leaveId, String leaveMsg)
    {
        this.modlogId = modlogId;
        this.serverlogId = serverlogId;
        this.welcomeId = welcomeId;
        this.welcomeMsg = welcomeMsg;
        this.leaveMsg = leaveMsg;
        this.leaveId = leaveId;
    }

    public Long getModlog()
    {
        return modlogId;
    }

    public Long getServerlog()
    {
        return serverlogId;
    }

    public Long getWelcomeChannel()
    {
        return welcomeId;
    }

    public String getWelcomeMsg()
    {
        return welcomeMsg;
    }

    public Long getLeaveChannel()
    {
        return leaveId;
    }

    public String getLeaveMsg()
    {
        return leaveMsg;
    }
}

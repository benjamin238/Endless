package me.artuto.endless.entities;

import java.util.Collection;

public interface GuildSettings
{
    Long getModlog();

    Long getServerlog();

    Long getWelcomeChannel();

    String getWelcomeMsg();

    Long getLeaveChannel();

    String getLeaveMsg();

    Long getStarboard();

    int getStarboardCount();

    Collection<String> getPrefixes();
}

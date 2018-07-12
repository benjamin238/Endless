package me.artuto.endless.core.entities;

import com.jagrosh.jdautilities.command.CommandEvent;

import java.time.OffsetDateTime;

public interface Room
{
    boolean canAccess(CommandEvent event);

    boolean isCombo();

    boolean isRestricted();

    boolean isTemporal();

    boolean isText();

    boolean isVoice();

    long getGuildId();

    long getTextChannelId();

    long getOwnerId();

    long getVoiceChannelId();

    OffsetDateTime getExpiryTime();
}

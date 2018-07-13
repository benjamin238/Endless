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

    enum Mode
    {
        COMBO_ONLY("Combo Only"),
        NO_CREATION("No Creation"),
        TEXT_ONLY("Text Only"),
        VOICE_ONLY("Voice Only");

        private String name;

        Mode(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}

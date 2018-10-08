package me.artuto.endless.core.entities;

import me.artuto.endless.commands.EndlessCommandEvent;

public interface Room
{
    boolean canAccess(EndlessCommandEvent event);

    boolean isCombo();

    boolean isRestricted();

    boolean isText();

    boolean isVoice();

    long getGuildId();

    long getTextChannelId();

    long getOwnerId();

    long getVoiceChannelId();

    enum Mode
    {
        ALL("All"),
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

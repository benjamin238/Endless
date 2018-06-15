package me.artuto.endless.core.events.guildsettings;

import me.artuto.endless.core.EndlessCore;
import me.artuto.endless.core.entities.GuildSettings;
import net.dv8tion.jda.core.entities.Guild;

public class GuildSettingsUpdateEvent extends GenericGuildSettingsEvent
{
    private final GuildSettings settings;

    public GuildSettingsUpdateEvent(EndlessCore endless, Guild guild, GuildSettings settings)
    {
        super(endless, guild);
        this.settings = settings;
    }

    public GuildSettings getSettings()
    {
        return settings;
    }
}

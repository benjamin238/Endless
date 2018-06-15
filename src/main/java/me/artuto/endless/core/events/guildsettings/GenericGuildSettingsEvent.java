package me.artuto.endless.core.events.guildsettings;

import me.artuto.endless.core.EndlessCore;
import me.artuto.endless.core.events.EndlessEvent;
import net.dv8tion.jda.core.entities.Guild;

public class GenericGuildSettingsEvent extends EndlessEvent
{
    private final Guild guild;

    public GenericGuildSettingsEvent(EndlessCore endless, Guild guild)
    {
        super(endless);
        this.guild = guild;
    }

    public Guild getGuild()
    {
        return guild;
    }
}

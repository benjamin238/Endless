package me.artuto.endless.events;

import me.artuto.endless.loader.Config;
import me.artuto.endless.tempdata.AfkManager;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.user.UserTypingEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserEvents extends ListenerAdapter
{
    private final Config config;

    public UserEvents(Config config)
    {
        this.config = config;
    }

    @Override
    public void onUserTyping(UserTypingEvent event)
    {
        Logger LOG = LoggerFactory.getLogger("AFK Manager");
        User user = event.getUser();

        if(AfkManager.isAfk(user.getIdLong()))
        {
            user.openPrivateChannel().queue(pc -> pc.sendMessage(config.getDoneEmote()+" I've removed your AFK status.").queue(null,
                    (e) -> LOG.warn("I was not able to DM "+user.getName()+"#"+user.getDiscriminator()+" about removing its AFK status.")));
            AfkManager.unsetAfk(user.getIdLong());
        }
    }
}

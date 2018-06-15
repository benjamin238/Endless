package me.artuto.endless.commands;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.core.EndlessCore;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class EndlessCommandEvent extends CommandEvent
{
    private EndlessCore endless = Bot.getInstance().endless;

    public EndlessCommandEvent(MessageReceivedEvent event, String args, CommandClient client)
    {
        super(event, args, client);
    }

    public EndlessCore getEndless()
    {
        return endless;
    }
}

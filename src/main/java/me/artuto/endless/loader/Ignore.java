package me.artuto.endless.loader;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import me.artuto.endless.data.UserIgnore;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Ignore implements CommandListener
{

    @Override
    public void onCommand(CommandEvent event, Command command)
    {
        UserIgnore ignore = new UserIgnore();

        if(ignore.list.contains(event.getAuthor()))
        {
            event.reply("Not allowed");
            return;
        }
    }

    @Override
    public void onCompletedCommand(CommandEvent commandEvent, Command command)
    {

    }

    @Override
    public void onTerminatedCommand(CommandEvent commandEvent, Command command)
    {

    }

    @Override
    public void onNonCommandMessage(MessageReceivedEvent messageReceivedEvent)
    {

    }
}

package me.artuto.endless.loader;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import me.artuto.endless.data.UserIgnore;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Ignore /*implements CommandListener*/ extends ListenerAdapter
{
    public static void getIgnoredGuilds() throws IOException
    {

    }



    /*@Override
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

    }*/
}

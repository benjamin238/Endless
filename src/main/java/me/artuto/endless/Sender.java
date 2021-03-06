/*
 * Copyright (C) 2017-2018 Artuto
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.artuto.endless;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * @author Artuto
 * Message sanitization taken from JDA-Utilities
 */

public class Sender
{
    public static void sendFile(MessageChannel chan, File file, Message message)
    {
        chan.sendFile(file, message).queue();
    }
    public static void sendFile(MessageChannel chan, File file, Message message, Consumer<Message> success)
    {
        chan.sendFile(file, message).queue(success);
    }

    public static void sendMessage(MessageChannel chan, Message message, Consumer<Message> success)
    {
        chan.sendMessage(message).queue(success);
    }

    public static void sendMessage(MessageChannel chan, Message message)
    {
        chan.sendMessage(message).queue();
    }

    public static void sendMessage(MessageChannel chan, String message, Consumer<Message> success)
    {
        ArrayList<String> messages = splitMessage(message);
        for(int i=0; i<CommandEvent.MAX_MESSAGES && i<messages.size(); i++)
        {
            if(i+1==CommandEvent.MAX_MESSAGES || i+1==messages.size())
                chan.sendMessage(messages.get(i)).queue(success);
            else
                chan.sendMessage(messages.get(i)).queue();
        }
    }

    public static void sendMessage(MessageChannel chan, String message)
    {
        ArrayList<String> messages = splitMessage(message);
        for(int i = 0; i<CommandEvent.MAX_MESSAGES && i<messages.size(); i++)
            chan.sendMessage(messages.get(i)).queue();
    }

    private static ArrayList<String> splitMessage(String stringtoSend)
    {
        ArrayList<String> msgs =  new ArrayList<>();
        if(stringtoSend!=null)
        {
            stringtoSend = stringtoSend.replace("@everyone", "@\u0435veryone").replace("@here", "@h\u0435re").trim();
            while(stringtoSend.length()>2000)
            {
                int leeway = 2000 - (stringtoSend.length()%2000);
                int index = stringtoSend.lastIndexOf("\n", 2000);
                if(index<leeway)
                    index = stringtoSend.lastIndexOf(" ", 2000);
                if(index<leeway)
                    index=2000;
                String temp = stringtoSend.substring(0,index).trim();
                if(!temp.equals(""))
                    msgs.add(temp);
                stringtoSend = stringtoSend.substring(index).trim();
            }
            if(!stringtoSend.equals(""))
                msgs.add(stringtoSend);
        }
        return msgs;
    }
}

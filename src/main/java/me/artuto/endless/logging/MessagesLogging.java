package me.artuto.endless.logging;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import java.util.HashMap;

class MessagesLogging
{
    private static HashMap<Long,Message> messages = new HashMap<>();

    static void addMessage(Long id, Message msg)
    {
        messages.put(id, msg);
    }

    static Message getMsg(Long id)
    {
        if(messages.containsKey(id))
        {
            return messages.get(id);
        }
        else
        {
            return new MessageBuilder().append("No cached message").build();
        }
    }

    static HashMap<Long, Message> getMap()
    {
        return messages;
    }

    static void removeMessage(Long id)
    {
        if(messages.containsKey(id))
            messages.remove(id);


    }
}

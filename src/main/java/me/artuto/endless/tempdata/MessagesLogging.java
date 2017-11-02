package me.artuto.endless.tempdata;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import java.util.HashMap;

public class MessagesLogging
{
    private static HashMap<Long,Message> messages = new HashMap<>();

    public static void addMessage(Long id, Message msg)
    {
        messages.put(id, msg);
    }

    public static Message getMsg(Long id)
    {
        if(messages.containsKey(id))
            return messages.get(id);
        else
            return new MessageBuilder().append("No cached message").build();
    }

    public static HashMap<Long, Message> getMap()
    {
        return messages;
    }

    public static void removeMessage(Long id)
    {
        if(messages.containsKey(id))
            messages.remove(id);
    }
}

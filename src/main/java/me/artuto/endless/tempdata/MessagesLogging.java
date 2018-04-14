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

package me.artuto.endless.tempdata;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.HashMap;

public class MessagesLogging
{
    private static HashMap<Long, Message> messages = new HashMap<>();

    public static void addMessage(Long id, Message msg)
    {
        messages.put(id, msg);
    }

    public static Message getMsg(Long id)
    {
        if(messages.containsKey(id)) return messages.get(id);
        else return new MessageBuilder().append("No cached message").build();
    }

    public static HashMap<Long, Message> getMap()
    {
        return messages;
    }

    public static void removeMessage(Long id)
    {
        if(messages.containsKey(id)) messages.remove(id);
    }
}

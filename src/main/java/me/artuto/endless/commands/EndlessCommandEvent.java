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

package me.artuto.endless.commands;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.core.entities.GuildSettings;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Artuto
 */

public class EndlessCommandEvent extends CommandEvent
{
    private Bot bot;
    private CommandEvent event;

    private EndlessCommandEvent(MessageReceivedEvent event, String args, CommandClient client)
    {
        super(event, args, client);
    }

    public EndlessCommandEvent(CommandEvent event)
    {
        this(event.getEvent(), event.getArgs(), event.getClient());
        this.event = event;
        this.bot = Bot.getInstance();
    }

    // Reply methods. To localize strings.
    @Override
    public void reply(String message)
    {
        if(event.isFromType(ChannelType.TEXT))
        {
            GuildSettings gs = getClient().getSettingsFor(getGuild());

        }
        else
            event.reply(message);
    }
}

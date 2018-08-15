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

package me.artuto.endless.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.music.AudioPlayerSendHandler;

/**
 * @author Artuto
 */

public class SkipCmd extends EndlessCommand
{
    public SkipCmd()
    {
        this.name = "stop";
        this.help = "Stops the current song and clears the queue.";
        this.category = MusicCommand.DJ;
    }

    @Override
    public void executeCommand(CommandEvent event)
    {
        AudioPlayerSendHandler handler = (AudioPlayerSendHandler)event.getGuild().getAudioManager().getSendingHandler();
        if(!(handler==null))
            handler.stopAndClear();
        event.getGuild().getAudioManager().closeAudioConnection();
        event.replySuccess("Successfully stopped player and cleared queue.");
    }
}

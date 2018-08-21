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
import me.artuto.endless.Bot;
import me.artuto.endless.music.AudioPlayerSendHandler;
import me.artuto.endless.music.QueuedTrack;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Artuto
 */

public class RemoveCmd extends MusicCommand
{
    public RemoveCmd(Bot bot)
    {
        super(bot);
        this.name = "remove";
        this.help = "Removes a song from the queue";
        this.arguments = "<position|ALL>";
        this.listening = true;
        this.playing = true;
    }

    @Override
    public void executeMusicCommand(CommandEvent event)
    {
        AudioPlayerSendHandler handler = (AudioPlayerSendHandler)event.getGuild().getAudioManager().getSendingHandler();

        if(handler.getQueue().isEmpty())
        {
            event.replyWarning("There is nothing on the queue!");
            return;
        }
        if(event.getArgs().equalsIgnoreCase("all"))
        {
            int count;
            List<QueuedTrack> queue = handler.getQueue();
            if(isDJ(event))
            {
                event.replySuccess("Successfully removed **"+queue.size()+"** songs from the queue!", (s) -> queue.clear());
                return;
            }

            List<QueuedTrack> toRemove = queue.stream().filter(qt -> qt.getOwner()==event.getAuthor().getIdLong()).collect(Collectors.toList());
            count = toRemove.size();
            if(count==0)
                event.replyError("You don't have any songs in queue to remove!");
            else
            {
                queue.removeAll(toRemove);
                event.replySuccess("Successfully removed **"+count+"** songs from the queue!");
            }
            return;
        }

        event.replyWarning("If you have come to this step, congrats! Because i haven't finished this yet:tm: :D");
    }
}

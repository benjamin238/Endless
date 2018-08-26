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
import net.dv8tion.jda.core.entities.User;

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
        List<QueuedTrack> queue = handler.getQueue();

        if(queue.isEmpty())
        {
            event.replyWarning("There is nothing on the queue!");
            return;
        }
        if(event.getArgs().equalsIgnoreCase("all"))
        {
            int count;
            if(isDJ(event))
            {
                event.replySuccess("Successfully removed **"+queue.size()+"** songs from the queue!");
                queue.clear();
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

        int position;
        try{position = Integer.parseInt(event.getArgs());}
        catch(NumberFormatException ignored){position = 0;}
        if(position<1 || position>queue.size())
        {
            event.replyError("The position must be a valid integer between 1 and "+queue.size()+"!");
            return;
        }

        QueuedTrack qt = queue.get(position-1);
        if(qt.getOwner()==event.getAuthor().getIdLong())
        {
            queue.remove(qt);
            event.replySuccess("Successfully removed **"+qt.getTrack().getInfo().title+"** from the queue!");
        }
        else if(isDJ(event))
        {
            queue.remove(qt);
            User requester = event.getJDA().asBot().getShardManager().getUserById(qt.getOwner());
            event.replySuccess("Successfully removed **"+qt.getTrack().getInfo().title+"** from the queue!"+(requester==null?"":" (Requested by **"
                    +requester.getName()+"**#**"+requester.getDiscriminator()+"**)"));
        }
        else
            event.replyError("You can't remove **"+qt.getTrack().getInfo().title+"** because you didn't added it!");
    }
}

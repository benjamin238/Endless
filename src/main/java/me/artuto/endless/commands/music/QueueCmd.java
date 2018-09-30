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

import com.jagrosh.jdautilities.menu.Paginator;
import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.music.AudioPlayerSendHandler;
import me.artuto.endless.music.QueuedTrack;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Artuto
 */

public class QueueCmd extends MusicCommand
{
    private final Paginator.Builder pB;

    public QueueCmd(Bot bot)
    {
        super(bot);
        this.name = "queue";
        this.help = "Shows the current playing queue";
        this.arguments = "[page number]";
        this.botPerms = new Permission[]{Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS};
        this.needsArguments = false;
        this.playing = true;
        this.pB = new Paginator.Builder()
                .setColumns(1)
                .setItemsPerPage(10)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .showPageNumbers(true)
                .setEventWaiter(bot.waiter)
                .setTimeout(2, TimeUnit.MINUTES)
                .setFinalAction(msg -> {
                    try{msg.clearReactions().queue();}
                    catch(PermissionException ignored){}
                });
    }

    @Override
    public void executeMusicCommand(EndlessCommandEvent event)
    {
        int page = 1;
        try{page = Integer.parseInt(event.getArgs());}
        catch(NumberFormatException ignored){}

        AudioPlayerSendHandler handler = (AudioPlayerSendHandler)event.getGuild().getAudioManager().getSendingHandler();
        List<QueuedTrack> queue = handler.getQueue();

        if(queue.isEmpty())
        {
            String message = event.localize("command.queue.noQueue")+(!(handler.isMusicPlaying())?"":" "+event.localize("core.music.np")+"\n\n**"+
                handler.getPlayer().getPlayingTrack().getInfo().title+"**\n"+FormatUtil.embedFormat(event, handler));
            event.replyWarning(false, message);
            return;
        }

        int size = queue.size();
        long duration = 0;
        String[] tracks = new String[size];
        for(int i=0; i<size; i++)
        {
            duration += queue.get(i).getTrack().getDuration();
            tracks[i] = queue.get(i).toString();
        }
        long durationf = duration;
        pB.setText((i1, i2) -> event.getClient().getSuccess()+" "+getQueueTitle(handler, event, tracks.length, durationf,
                bot.endless.getGuildSettings(event.getGuild()).isRepeatModeEnabled()))
                .setItems(tracks).setUsers(event.getAuthor()).setColor(event.getSelfMember().getColor());
        pB.build().paginate(event.getTextChannel(), page);
    }

    // I'm just lazy tbh
    private String getQueueTitle(AudioPlayerSendHandler handler, EndlessCommandEvent event, int songslength, long total, boolean repeatmode)
    {
        StringBuilder sb = new StringBuilder();
        if(!(handler.getPlayer().getPlayingTrack()==null))
            sb.append("**").append(handler.getPlayer().getPlayingTrack().getInfo().title).append("**\n")
                    .append(FormatUtil.embedFormat(event, handler)).append("\n\n");
        return FormatUtil.sanitize(sb.append(event.getClient().getSuccess()).append(" ").append(event.localize("command.queue.current", songslength,
                FormatUtil.formatTime(total))).append(" ").append(repeatmode ? "| \uD83D\uDD01" : "").toString());
    }
}

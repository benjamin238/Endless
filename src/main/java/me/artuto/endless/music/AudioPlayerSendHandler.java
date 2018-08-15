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

package me.artuto.endless.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import me.artuto.endless.Bot;
import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.music.queue.FairQueue;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Artuto
 */

public class AudioPlayerSendHandler extends AudioEventAdapter implements AudioSendHandler
{
    private AudioFrame lastFrame;
    private final AudioPlayer player;
    private final Bot bot;
    private final Guild guild;

    // Queues
    private final FairQueue<QueuedTrack> queue;
    private final List<AudioTrack> defQueue;
    private long requester;

    private final Set<String> votes;

    AudioPlayerSendHandler(AudioPlayer player, Bot bot, Guild guild)
    {
        this.player = player;
        this.bot = bot;
        this.guild = guild;

        this.queue = new FairQueue<>();
        this.defQueue = new LinkedList<>();

        this.votes = new HashSet<>();
    }

    @Override
    public boolean canProvide()
    {
        lastFrame = player.provide();
        return !(lastFrame==null);
    }

    @Override
    public boolean isOpus()
    {
        return true;
    }

    @Override
    public byte[] provide20MsAudio()
    {
        return lastFrame.getData();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason)
    {
        GuildSettings gs = bot.endless.getGuildSettings(guild);
        if(reason==AudioTrackEndReason.FINISHED && gs.isRepeatModeEnabled())
            queue.add(new QueuedTrack(track.makeClone(), requester));
        requester = 0;
        if(queue.isEmpty())
            bot.endlessPool.submit(() -> guild.getAudioManager().closeAudioConnection());
        else
        {
            QueuedTrack qt = queue.pull();
            requester = qt.getIdentifier();
            player.playTrack(qt.getTrack());
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track)
    {
        votes.clear();
    }

    public AudioPlayer getPlayer()
    {
        return player;
    }

    public boolean isMusicPlaying()
    {
        return guild.getSelfMember().getVoiceState().inVoiceChannel() && !(player.getPlayingTrack()==null);
    }

    public FairQueue<QueuedTrack> getQueue()
    {
        return queue;
    }

    int fairQueueTrack(AudioTrack track, User author)
    {
        if(player.getPlayingTrack()==null)
        {
            requester = author.getIdLong();
            player.playTrack(track);
            return -1;
        }
        else
            return queue.add(new QueuedTrack(track, author.getIdLong()));
    }

    int queueTrack(AudioTrack track, User author)
    {
        if(player.getPlayingTrack()==null)
        {
            requester = author.getIdLong();
            player.playTrack(track);
            return -1;
        }
        else
        {
            defQueue.add(track);
            return defQueue.indexOf(track)+1;
        }
    }

    public long getRequester()
    {
        return requester;
    }

    public Set<String> getVotes()
    {
        return votes;
    }

    public void stopAndClear()
    {
        queue.clear();
        defQueue.clear();
        player.stopTrack();
    }
}

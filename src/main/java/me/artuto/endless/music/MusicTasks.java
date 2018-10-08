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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.artuto.endless.Bot;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Artuto
 */

public class MusicTasks
{
    private AudioPlayerManager manager;
    private Bot bot;

    private final Map<Long, ScheduledFuture> futures = new HashMap<>();

    public void setupSystem(Bot bot)
    {
        this.bot = bot;
        bot.audioManager = new DefaultAudioPlayerManager();
        this.manager = bot.audioManager;
        AudioSourceManagers.registerLocalSource(manager);
        AudioSourceManagers.registerRemoteSources(manager);
        manager.source(YoutubeAudioSourceManager.class).setPlaylistPageCount(10);
    }

    public AudioPlayerSendHandler setupHandler(CommandEvent event)
    {
        return setupHandler(event.getGuild());
    }

    public AudioPlayerSendHandler setupHandler(Guild guild)
    {
        AudioPlayerSendHandler handler;
        AudioPlayer player;
        if(guild.getAudioManager().getSendingHandler()==null)
        {
            player = manager.createPlayer();
            player.setVolume(bot.endless.getGuildSettings(guild).getVolume());
            handler = new AudioPlayerSendHandler(player, bot, guild);
            player.addListener(handler);
            guild.getAudioManager().setSendingHandler(handler);
        }
        else
            handler = (AudioPlayerSendHandler)guild.getAudioManager().getSendingHandler();

        return handler;
    }

    public void scheduleLeave(VoiceChannel vc, Guild guild)
    {
        AudioPlayerSendHandler handler = setupHandler(guild);
        ScheduledFuture future = bot.voiceLeaverScheduler.schedule(handler::stopAndClear, 5, TimeUnit.MINUTES);
        futures.put(vc.getIdLong(), future);
    }

    public void cancelLeave(VoiceChannel vc)
    {
        ScheduledFuture future = futures.remove(vc.getIdLong());
        if(future==null)
            return;

        future.cancel(true);
    }

    int putInQueue(AudioTrack track, CommandEvent event)
    {
        if(setupHandler(event).isFairQueue())
            return fairQueueTrack(track, event);
        else
            return queueTrack(track, event);
    }

    private int fairQueueTrack(AudioTrack track, CommandEvent event)
    {
        return setupHandler(event).fairQueueTrack(track, event.getAuthor());
    }

    private int queueTrack(AudioTrack track, CommandEvent event)
    {
        return setupHandler(event).queueTrack(track, event.getAuthor());
    }
}

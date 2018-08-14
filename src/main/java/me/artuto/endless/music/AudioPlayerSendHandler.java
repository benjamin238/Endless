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
import me.artuto.endless.Bot;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.Guild;

/**
 * @author Artuto
 */

public class AudioPlayerSendHandler extends AudioEventAdapter implements AudioSendHandler
{
    private final AudioPlayer player;
    private final Bot bot;
    private final Guild guild;

    public AudioPlayerSendHandler(AudioPlayer player, Bot bot, Guild guild)
    {
        this.player = player;
        this.bot = bot;
        this.guild = guild;
    }

    @Override
    public boolean canProvide()
    {
        return false;
    }

    @Override
    public boolean isOpus()
    {
        return true;
    }

    @Override
    public byte[] provide20MsAudio()
    {
        return new byte[0];
    }
}

/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.artuto.endless.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.artuto.endless.music.queue.Queueable;
import me.artuto.endless.utils.FormatUtil;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */

public class QueuedTrack implements Queueable
{
    private final AudioTrack track;
    private final long owner;

    public QueuedTrack(AudioTrack track, long owner)
    {
        this.track = track;
        this.owner = owner;
    }

    public long getOwner()
    {
        return owner;
    }

    public AudioTrack getTrack()
    {
        return track;
    }

    @Override
    public String toString()
    {
        return "`["+FormatUtil.formatTime(track.getDuration())+"]` **"+track.getInfo().title+"** - <@"+owner+">";
    }
}

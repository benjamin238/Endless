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

import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.music.AudioPlayerSendHandler;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Artuto
 */

public class ForceSkipCmd extends MusicCommand
{
    public ForceSkipCmd(Bot bot)
    {
        super(bot);
        this.name = "forceskip";
        this.help = "Skips the current playing song without needing other users to vote";
        this.aliases = new String[]{"fskip", "modskip"};
        this.category = MusicCommand.DJ;
        this.needsArguments = false;
        this.playing = true;
    }

    @Override
    public void executeMusicCommand(EndlessCommandEvent event)
    {
        AudioPlayerSendHandler handler = (AudioPlayerSendHandler)event.getGuild().getAudioManager().getSendingHandler();
        User requester = bot.shardManager.getUserById(handler.getRequester());

        String message = event.localize("command.skip.success", handler.getPlayer().getPlayingTrack().getInfo().title)+" "+
                (requester==null?"":event.localize("core.music.requestedBy", requester.getName()+"#"+requester.getDiscriminator()));

        event.replySuccess(false, message);
        handler.getPlayer().stopTrack();
    }
}

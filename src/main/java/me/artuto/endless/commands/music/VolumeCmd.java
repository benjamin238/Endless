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
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.music.AudioPlayerSendHandler;
import me.artuto.endless.utils.FormatUtil;

/**
 * @author Artuto
 */

public class VolumeCmd extends EndlessCommand
{
    private final Bot bot;

    public VolumeCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "volume";
        this.aliases = new String[]{"vol"};
        this.help = "Sets the volume for the current guild.";
        this.arguments = "[0-150]";
        this.category = MusicCommand.DJ;
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        AudioPlayerSendHandler handler = (AudioPlayerSendHandler)event.getGuild().getAudioManager().getSendingHandler();
        GuildSettings gs = event.getClient().getSettingsFor(event.getGuild());
        int volume = (handler==null && gs.getVolume()==0)?100:(handler==null?gs.getVolume():handler.getPlayer().getVolume());

        if(event.getArgs().isEmpty())
            event.reply(FormatUtil.volumeIcon(volume)+" The current volume is **"+volume+"**");
        else
        {
            int newVolume;
            try{newVolume = Integer.parseInt(event.getArgs());}
            catch(NumberFormatException ignored){newVolume = -1;}

            if(newVolume<0 || newVolume>150)
            {
                event.replyError("The volume must be a valid number between 0 and 150!");
                return;
            }

            bot.musicTasks.setupHandler(event).getPlayer().setVolume(newVolume);
            bot.gsdm.setVolume(event.getGuild(), newVolume);
            event.replySuccess("Successfully changed volume from `"+volume+"` to `"+newVolume+"`.");
        }
    }
}

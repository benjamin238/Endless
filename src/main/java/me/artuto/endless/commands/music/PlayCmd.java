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
import me.artuto.endless.Const;
import me.artuto.endless.music.ResultHandler;

/**
 * @author Artuto
 */

public class PlayCmd extends MusicCommand
{
    public PlayCmd(Bot bot)
    {
        super(bot);
        this.name = "play";
        this.arguments = "<URL|title|subcommand>";
        this.help = "Plays the specified song";
        this.listening = true;
        this.playing = false;
        this.needsArguments = false;
    }

    @Override
    public void executeMusicCommand(CommandEvent event)
    {
        if(event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty())
        {
            event.replyError("Please specify an URL, a title or upload an attachment!");
            return;
        }

        String args = (event.getArgs().startsWith("<") && event.getArgs().endsWith(">"))?
                event.getArgs().substring(1, event.getArgs().length()-1):(event.getArgs().isEmpty()?
                event.getMessage().getAttachments().get(0).getUrl():event.getArgs());
        event.reply(Const.LOADING+" Loading... `["+args+"]`", msg -> bot.audioManager.loadItemOrdered(event.getGuild(), args,
                new ResultHandler(bot, false, event, msg)));
    }
}

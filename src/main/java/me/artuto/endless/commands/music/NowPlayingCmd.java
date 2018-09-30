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

import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;

/**
 * @author Artuto
 */

public class NowPlayingCmd extends EndlessCommand
{
    public NowPlayingCmd()
    {
        this.name = "nowplaying";
        this.aliases = new String[]{"np", "current", "currentsong"};
        this.help = "Shows the currently playing song";
        this.category = Categories.MUSIC;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.needsArguments = false;
    }

    @Override
    public void executeCommand(EndlessCommandEvent event)
    {
        event.reply(FormatUtil.nowPlayingMessage(event, event.getGuild(), "\uD83C\uDFB6"));
    }
}

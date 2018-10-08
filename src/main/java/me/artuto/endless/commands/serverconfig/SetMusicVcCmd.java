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

package me.artuto.endless.commands.serverconfig;

import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * @author Artuto
 */

public class SetMusicVcCmd extends EndlessCommand
{
    private final Bot bot;

    public SetMusicVcCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "setmusicvc";
        this.help = "Sets the Music Voice Channel";
        this.aliases = new String[]{"setvc"};
        this.arguments = "<Channel ID|Channel name>";
        this.category = Categories.SERVER_CONFIG;
        this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        this.needsArgumentsMessage = "Please include a voice channel or NONE";
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        if(event.getArgs().equalsIgnoreCase("none"))
        {
            bot.gsdm.setMusicVc(event.getGuild(), null);
            event.replySuccess("command.setmusicvc.unset");
        }
        else
        {
            VoiceChannel vc = ArgsUtils.findVoiceChannel(event, event.getArgs());
            if(vc==null)
                return;

            bot.gsdm.setMusicVc(event.getGuild(), vc);
            event.replySuccess("command.setmusicvc.set", vc.getName());
        }
    }
}

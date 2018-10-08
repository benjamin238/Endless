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
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author Artuto
 */

public class SetMusicTcCmd extends EndlessCommand
{
    private final Bot bot;

    public SetMusicTcCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "setmusictc";
        this.help = "Sets the Music Text Channel";
        this.aliases = new String[]{"settc"};
        this.arguments = "<#channel|Channel ID|Channel name>";
        this.category = Categories.SERVER_CONFIG;
        this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        this.needsArgumentsMessage = "Please include a text channel or NONE";
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        if(event.getArgs().equalsIgnoreCase("none"))
        {
            bot.gsdm.setMusicTc(event.getGuild(), null);
            event.replySuccess("command.setmusictc.unset");
        }
        else
        {
            TextChannel tc = ArgsUtils.findTextChannel(event, event.getArgs());
            if(tc==null)
                return;

            bot.gsdm.setMusicTc(event.getGuild(), tc);
            event.replySuccess("command.setmusictc.set", tc.getAsMention());
        }
    }
}

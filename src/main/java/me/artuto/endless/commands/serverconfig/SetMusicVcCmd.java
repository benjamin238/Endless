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

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.List;

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
            event.replySuccess("Music can now be played on every channel now.");
        }
        else
        {
            List<VoiceChannel> list = FinderUtil.findVoiceChannels(event.getArgs(), event.getGuild());
            if(list.isEmpty())
                event.replyWarning("No Voice Channels found matching \""+event.getArgs()+"\"");
            else if(list.size()>1)
                event.replyWarning(FormatUtil.listOfVcChannels(list, event.getArgs()));
            else
            {
                bot.gsdm.setMusicVc(event.getGuild(), list.get(0));
                event.replySuccess("Music can now be played only on **"+list.get(0).getName()+"** now");
            }
        }
    }
}

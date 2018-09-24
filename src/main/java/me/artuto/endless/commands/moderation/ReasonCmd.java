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

package me.artuto.endless.commands.moderation;

import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.core.entities.GuildSettings;
import net.dv8tion.jda.core.Permission;

/**
 * @author Artuto
 */

public class ReasonCmd extends EndlessCommand
{
    private final Bot bot;

    public ReasonCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "reason";
        this.help = "Updates the reason for the specified case, if not specified uses the latest case.";
        this.arguments = "[case] <reason>";
        this.category = Categories.MODERATION;
        this.userPerms = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        int caseNum;
        String[] parts = event.getArgs().split("\\s+", 2);
        String str;
        try
        {
            caseNum = Integer.parseInt(parts[0]);
            str = parts.length==1 ? null : parts[1];
        }
        catch(NumberFormatException ex)
        {
            caseNum = -1;
            str = event.getArgs();
        }

        if(caseNum<-1 || caseNum==0)
        {
            event.replyError("Case number must be a positive integer! The case number can be omitted to use the latest un-reasoned case.");
            return;
        }
        if(str==null || str.isEmpty())
        {
            event.replyError("Please provide a reason!");
            return;
        }

        String fstr = str;
        int fcaseNum = caseNum;
        event.async(() ->
        {
            int result = bot.modlog.updateCase(event.getGuild(), fcaseNum, fstr);
            switch(result)
            {
                case -1:
                    event.replyError("No modlog is set on this server!");
                    break;
                case -2:
                    event.replyError("I am unable to Read, Write or retrieve History in the modlog!");
                    break;
                case -3:
                    event.replyError("Case `"+fcaseNum+"` could not be found among the recent cases in the modlog!");
                    break;
                case -4:
                    event.replyError("A recent case with no reason could not be found in the modlog!");
                    break;
                default:
                    event.replySuccess("Updated case **"+result+"** in <#"+((GuildSettings)event.getClient().getSettingsFor(event.getGuild())).getModlog()+">");
                    break;
            }
        });
    }
}

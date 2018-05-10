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

package me.artuto.endless.commands.severconfig;

import com.jagrosh.jdautilities.command.Command;
import me.artuto.endless.commands.EndlessCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.GuildSettingsDataManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

public class Leave extends EndlessCommand
{
    private final GuildSettingsDataManager db;

    public Leave(GuildSettingsDataManager db)
    {
        this.db = db;
        this.name = "leave";
        this.children = new Command[]{new Change()};
        this.aliases = new String[]{"leavemessage", "leavemsg"};
        this.help = "Changes or shows the welcome message";
        this.category = Categories.SERVER_CONFIG;
        this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildCommand = true;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        Guild guild = event.getGuild();
        String msg = db.getLeaveMessage(guild);

        if(!(msg == null)) event.replySuccess("Leave message at **"+guild.getName()+"**: `"+msg+"`");
        else event.replyError("No message configured!");
    }

    private class Change extends EndlessCommand
    {
        public Change()
        {
            this.name = "change";
            this.help = "Changes the welcome message";
            this.category = Categories.SERVER_CONFIG;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildCommand = true;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a new leave message!");
                return;
            }

            db.setLeaveMessage(event.getGuild(), event.getArgs());
            event.replySuccess("leave message configured.");
        }
    }
}
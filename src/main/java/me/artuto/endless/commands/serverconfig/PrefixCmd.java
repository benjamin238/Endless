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

import com.jagrosh.jdautilities.command.Command;
import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.core.entities.GuildSettings;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

import java.util.Collection;

public class PrefixCmd extends EndlessCommand
{
    private final Bot bot;

    public PrefixCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "prefix";
        this.children = new Command[]{new AddCmd(), new RemoveCmd()};
        this.help = "Displays or adds a prefix";
        this.category = Categories.SERVER_CONFIG;
        this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        StringBuilder sb = new StringBuilder();
        Guild guild = event.getGuild();
        String defP = event.getClient().getPrefix();
        GuildSettings gs = event.getClient().getSettingsFor(guild);
        Collection<String> prefixes = gs.getPrefixes();

        if(prefixes.isEmpty())
            event.reply("command.prefix.default");
        else
        {
            sb.append("`").append(defP).append("`");
            prefixes.forEach(p -> sb.append(", `").append(p).append("`"));

            event.reply("command.prefix.list", sb.toString());
        }
    }

    private class AddCmd extends EndlessCommand
    {
        AddCmd()
        {
            this.name = "add";
            this.help = "Adds a custom prefix";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "You didn't provided me a prefix!";
            this.parent = PrefixCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            String args = event.getArgs().toLowerCase().trim();
            Guild guild = event.getGuild();
            GuildSettings settings = event.getClient().getSettingsFor(guild);

            if(settings.getPrefixes().contains(args))
                event.replyError("command.prefix.add.alreadyAdded");
            else
            {
                bot.gsdm.addPrefix(guild, args);
                event.replySuccess("command.prefix.add.added");
            }
        }
    }

    private class RemoveCmd extends EndlessCommand
    {
        RemoveCmd()
        {
            this.name = "remove";
            this.help = "Removes a custom prefix";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "You didn't provided me a prefix!";
            this.parent = PrefixCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            String args = event.getArgs().toLowerCase().trim();
            Guild guild = event.getGuild();
            GuildSettings settings = event.getClient().getSettingsFor(guild);

            if(settings.getPrefixes().contains(args))
            {
                bot.gsdm.removePrefix(guild, args);
                event.replySuccess("command.prefix.remove.removed");
            }
            else
                event.replyError("command.prefix.remove.notAdded");
        }
    }
}

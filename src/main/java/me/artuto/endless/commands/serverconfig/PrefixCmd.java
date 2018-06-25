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
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.data.managers.ClientGSDMProvider;
import me.artuto.endless.utils.GuildUtils;
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
        this.children = new Command[]{new Add(), new Remove()};
        this.help = "Displays or adds a prefix";
        this.category = Categories.SERVER_CONFIG;
        this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        StringBuilder sb = new StringBuilder();
        Guild guild = event.getGuild();
        String defP = event.getClient().getPrefix();

        Collection<String> prefixes = GuildUtils.getPrefixes(guild);

        if(prefixes.isEmpty())
            event.reply("The prefix for this guild is `"+defP+"`");
        else
        {
            sb.append("`").append(defP).append("`");
            prefixes.forEach(p -> sb.append(", `").append(p).append("`"));

            event.reply("The prefixes on this guild are: "+sb.toString());
        }
    }

    private class Add extends EndlessCommand
    {
        Add()
        {
            this.name = "add";
            this.help = "Adds a custom prefix";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "You didn't provided me a prefix!";
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            String args = event.getArgs().toLowerCase().trim();
            Guild guild = event.getGuild();
            ClientGSDMProvider settings = event.getClient().getSettingsFor(guild);

            if(!(settings.getPrefixes()==null) && settings.getPrefixes().contains(args))
                event.replyWarning("That prefix is already added!");
            else
            {
                bot.gsdm.addPrefix(guild, args);
                event.replySuccess("Successfully added prefix!");
            }
        }
    }

    private class Remove extends EndlessCommand
    {
        Remove()
        {
            this.name = "remove";
            this.help = "Removes a custom prefix";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "You didn't provided me a prefix!";
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            String args = event.getArgs().toLowerCase().trim();
            Guild guild = event.getGuild();
            ClientGSDMProvider settings = event.getClient().getSettingsFor(guild);

            if(!(settings.getPrefixes()==null) && settings.getPrefixes().contains(args))
            {
                bot.gsdm.removePrefix(guild, args);
                event.replySuccess("Successfully removed a prefix!");
            }
            else
                event.replyWarning("That prefix doesn't exists!");
        }
    }
}

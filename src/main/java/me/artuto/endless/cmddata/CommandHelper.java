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

package me.artuto.endless.cmddata;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.ChannelType;

import java.util.Arrays;

/**
 * @author Artuto
 */

public class CommandHelper
{
    public static void getHelpBiConsumer(CommandEvent event, Command command)
    {
        StringBuilder sb = new StringBuilder("**Help for `");
        String[] aliases = command.getAliases();
        Command[] children = command.getChildren();
        sb.append(command.getName()).append("` (");

        if(event.isFromType(ChannelType.TEXT))
            sb.append(event.getTextChannel().getAsMention()).append("):**\n");
        else
            sb.append("Direct Message):**\n");

        sb.append("**Usage:** `e!").append(command.getName()).append(" ").append(command.getArguments()==null?"":command.getArguments()).append("`\n");

        if(!(aliases.length==0))
        {
            StringBuilder aliasesBuilder = new StringBuilder("**Aliases:** ");
            Arrays.stream(aliases).forEach(a -> aliasesBuilder.append("`").append(a).append("` "));
            sb.append(aliasesBuilder).append("\n");
        }

        sb.append("*").append(command.getHelp()).append("*\n\n");

        if(!(children.length==0))
        {
            StringBuilder childrenBuilder = new StringBuilder("**Subcommands:**");
            Arrays.stream(children).filter(c -> {
                if(event.isFromType(ChannelType.TEXT))
                {
                    if(event.getMember().hasPermission(c.getUserPermissions()) || event.getMember().hasPermission(event.getTextChannel(), c.getUserPermissions()))
                        return true;
                    else if(event.isOwner())
                        return true;
                    else
                        return false;
                }
                else if(c.isGuildOnly())
                    return false;

                return true;
            }).forEach(c -> childrenBuilder.append("\n`e!").append(command.getName()).append(" ")
                    .append(c.getName()).append(" ").append(c.getArguments()==null?"":c.getArguments()).append("` - *").append(c.getHelp()).append("*"));
            if(!(childrenBuilder.toString().replace("**Subcommands:**", "").length()==0))
                sb.append(childrenBuilder).append("\n");
        }

        event.replyInDm(sb.toString(), s -> event.reactSuccess(),
                e -> event.replyWarning("Help cannot be sent because you are blocking Drect Messages."));
    }
}

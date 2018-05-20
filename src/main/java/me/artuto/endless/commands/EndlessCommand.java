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

package me.artuto.endless.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.cmddata.CommandHelper;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.function.BiConsumer;

/**
 * @author Artuto
 */

public abstract class EndlessCommand extends Command
{
    private BiConsumer<CommandEvent, Command> extendedHelp = CommandHelper::getHelpBiConsumer;
    protected boolean ownerCommand = false;

    @Override
    public void execute(CommandEvent event)
    {
        helpBiConsumer = extendedHelp;
        Member member = event.getMember();
        Member selfMember = event.getSelfMember();
        TextChannel tc = event.getTextChannel();

        if(ownerCommand && !(event.isOwner()))
        {
            event.replyError("This command is only for Bot Owners!");
            return;
        }

        if(event.isFromType(ChannelType.TEXT))
        {
            for(Permission p : botPermissions)
            {
                if(!(selfMember.hasPermission(p) || selfMember.hasPermission(tc, p)))
                {
                    event.replyError(String.format("I need the %s permission in this Guild to execute this command!", p.getName()));
                    break;
                }
            }

            if(event.isOwner())
            {
                executeCommand(event);
                return;
            }

            for(Permission p : userPermissions)
            {
                if(!(member.hasPermission(p) || member.hasPermission(tc, p)))
                {
                    event.replyError(String.format("You need the %s permission in this Guild to execute this command!", p.getName()));
                    break;
                }
            }
        }

        executeCommand(event);
    }

    protected abstract void executeCommand(CommandEvent event);
}

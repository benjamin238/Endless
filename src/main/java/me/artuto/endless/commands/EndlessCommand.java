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
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.cmddata.CommandHelper;
import me.artuto.endless.utils.Checks;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author Artuto
 */

public abstract class EndlessCommand extends Command
{
    protected boolean needsArguments = true;
    protected boolean ownerCommand = false;
    protected Permission[] botPerms = new Permission[0];
    protected Permission[] userPerms = new Permission[0];
    protected String needsArgumentsMessage = null;

    public EndlessCommand()
    {
        this.guildOnly = true;
        this.helpBiConsumer = CommandHelper::getHelpBiConsumer;
    }

    @Override
    public void execute(CommandEvent preEvent)
    {
        EndlessCommandEvent event = (EndlessCommandEvent)preEvent;
        CommandClient client = event.getClient();
        Member member = event.getMember();
        Member selfMember = event.getSelfMember();
        TextChannel tc = event.getTextChannel();

        if(ownerCommand && !(event.isOwner()))
        {
            event.replyError("This command is only for Bot Owners!");
            return;
        }

        if(needsArguments && event.getArgs().isEmpty())
        {
            if(needsArgumentsMessage==null)
                event.replyError("**Too few arguments provided!**\n" +
                        "Try running `"+client.getPrefix()+this.name+" help` to get help.");
            else
                event.replyWarning(needsArgumentsMessage);
            return;
        }

        if(event.isFromType(ChannelType.TEXT))
        {
            for(Permission p : botPerms)
            {
                if(!(Checks.hasPermission(selfMember, tc, p)))
                {
                    event.replyError(String.format("I need the %s permission in this Guild to execute this command!", p.getName()));
                    return;
                }
                break;
            }

            if(event.isOwner())
            {
                executeCommand(event);
                return;
            }

            for(Permission p : userPerms)
            {
                if(!(Checks.hasPermission(member, tc, p)))
                {
                    event.replyError(String.format("You need the %s permission in this Guild to execute this command!", p.getName()));
                    return;
                }
                break;
            }
        }

        executeCommand(event);
    }

    protected abstract void executeCommand(EndlessCommandEvent event);

    public boolean isOwnerCommand()
    {
        return ownerCommand;
    }

    public Permission[] getBotPerms()
    {
        return botPerms;
    }

    public Permission[] getUserPerms()
    {
        return userPerms;
    }
}

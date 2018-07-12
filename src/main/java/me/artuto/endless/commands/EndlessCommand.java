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
import me.artuto.endless.Bot;
import me.artuto.endless.Endless;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.commands.cmddata.CommandHelper;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

/**
 * @author Artuto
 */

public abstract class EndlessCommand extends Command
{
    protected boolean needsArguments = true;
    protected boolean ownerCommand = false;
    protected EndlessCommand parent = null;
    protected Permission[] botPerms = new Permission[0];
    protected Permission[] userPerms = new Permission[0];
    protected String needsArgumentsMessage = null;

    public EndlessCommand()
    {
        this.guildOnly = true;
        this.helpBiConsumer = CommandHelper::getHelpBiConsumer;
    }

    @Override
    public void execute(CommandEvent event)
    {
        boolean hasPerms = false;
        CommandClient client = event.getClient();
        Guild guild = event.getGuild();
        Member member = event.getMember();
        Member selfMember = event.getSelfMember();
        TextChannel tc = event.getTextChannel();
        User author = event.getAuthor();

        if(ownerCommand && !(event.isOwner()))
        {
            Endless.LOG.warn(author.getName()+"#"+author.getDiscriminator()+" ("+author.getId()+") tried to run a owner-only command!");
            return;
        }

        if(needsArguments && event.getArgs().isEmpty())
        {
            if(needsArgumentsMessage==null)
                event.replyError("**Too few arguments provided!**\n" +
                        "Try running `"+client.getPrefix()+(parent==null?"":parent.getName()+" ")+this.name+" help` to get help.");
            else
                event.replyWarning(needsArgumentsMessage);
            return;
        }

        if(event.isFromType(ChannelType.TEXT))
        {
            for(Permission p : botPerms)
            {
                if(!(ChecksUtil.hasPermission(selfMember, tc, p)))
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

            if(Bot.getInstance().dataEnabled)
            {
                if(this.category==Categories.MODERATION)
                {
                    if(member.getRoles().contains(GuildUtils.getAdminRole(guild)) || member.getRoles().contains(GuildUtils.getModRole(guild)))
                        hasPerms = true;
                }
                else if(member.getRoles().contains(GuildUtils.getAdminRole(guild)))
                    hasPerms = true;
            }

            if(!(hasPerms))
            {
                for(Permission p : userPerms)
                {
                    if(!(ChecksUtil.hasPermission(member, tc, p)))
                    {
                        event.replyError(String.format("You need the %s permission in this Guild to execute this command!", p.getName()));
                        return;
                    }
                    break;
                }
            }
        }

        executeCommand(event);
    }

    protected abstract void executeCommand(CommandEvent event);

    public boolean isOwnerCommand()
    {
        return ownerCommand;
    }

    public EndlessCommand getParent()
    {
        return parent;
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

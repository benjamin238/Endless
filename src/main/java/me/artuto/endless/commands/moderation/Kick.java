/*
 * Copyright (C) 2017 Artu
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

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

/**
 *
 * @author Artu
 */

public class Kick extends Command
{
    public Kick()
    {
        this.name = "kick";
        this.help = "Kicks the specified user";
        this.arguments = "@user or ID";
        this.category = new Command.Category("Moderation");
        this.botPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.ownerCommand = false;
        this.guildOnly = true;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
        Member member;
    	if(event.getMessage().getMentionedUsers().isEmpty())
    	{
    		try
    		{
    			member = event.getGuild().getMemberById(event.getArgs());
    		} catch(Exception e) 
    		    		    		
    		{
    			member = null;
    		}
    	}
    	else
    		member = event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0));
    	if(member==null)
    	{
    		event.reply(event.getClient().getError()+" I wasn't able to find the user "+event.getArgs());
    		return;
    	}
    }
}

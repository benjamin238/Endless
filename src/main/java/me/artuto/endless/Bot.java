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

package me.artuto.endless;

import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;

/**
 *
 * @author Artu
 */

public class Bot extends ListenerAdapter
{
    private final SimpleLog LOG = SimpleLog.getLog("DISCORD-BANS");
    
    
    
    @Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        Guild guild;
        guild = event.getGuild();
        
        User owner;
        owner = event.getJDA().getUserById(Config.getOwnerId());
        
        String leavemsg;
        leavemsg = "Hi! Sorry, but you can't have a copy of Endless on Discord Bots, this is for my own security.\n"
                    + "If you think this is an error, please contact the Developer.";
        
        String warnmsg;
        warnmsg = "<@264499432538505217>, **"+owner.getName()+"#"+owner.getDiscriminator()+"** has a copy of Endless here!";
        
        if(event.getGuild().getId().equals("110373943822540800"))
        {
            guild.getPublicChannel().sendMessage(warnmsg);
            owner.openPrivateChannel().queue(s -> s.sendMessage(leavemsg).queue(null, (e) -> LOG.fatal(leavemsg)));
            guild.leave().complete();
        }
    }
}

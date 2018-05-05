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

package me.artuto.endless.handlers;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.data.BlacklistDataManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Artuto
 */

public class BlacklistHandler
{
    private final Logger LOG = LoggerFactory.getLogger("Blacklisted Users");
    private BlacklistDataManager db;

    public BlacklistHandler(BlacklistDataManager db)
    {
        this.db = db;
    }

    public boolean handleBlacklist(CommandEvent event)
    {
        Guild guild = event.getGuild();
        User user = event.getAuthor();

        if(event.isOwner()) return true;
        else if(db.isBlacklisted(user.getIdLong()))
        {
            LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
            event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
            return false;
        }
        else if(db.isBlacklisted(guild.getIdLong()))
        {
            LOG.info("Command executed in blacklisted guild: "+guild.getName()+" (ID: "+guild.getId()+")");
            event.replyError("I'm sorry, but the owner of this bot has blocked this guild from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
            guild.leave().queue();
            return false;
        }

        return true;
    }
}

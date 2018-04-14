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

import com.jagrosh.jdautilities.command.Command.Category;
import me.artuto.endless.data.BlacklistDataManager;
import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Categories
{
    private static BlacklistDataManager db;
    private static Config config;
    private static final Logger LOG = LoggerFactory.getLogger("Blacklisted Users");

    public Categories(BlacklistDataManager db)
    {
        this.db = db;
        this.config = config;
    }

    public static final Category BOT = new Category("Bot", event ->
    {
        User user = event.getAuthor();

        if(event.isOwner()) return true;
        else
        {
            if(db.isUserBlacklisted(user))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else return true;
        }
    });

    public static final Category BOTADM = new Category("Bot Administration", event ->
    {
        if(event.isOwner())

            return true;
        else
        {
            event.replyError("Sorry, but you don't have access to this command! Only Bot owners!");
            return false;
        }
    });

    public static final Category MODERATION = new Category("Moderation", event ->
    {
        User user = event.getAuthor();

        if(event.isOwner()) return true;
        else
        {
            if(db.isUserBlacklisted(user))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else return true;
        }
    });

    public static final Category TOOLS = new Category("Tools", event ->
    {
        User user = event.getAuthor();

        if(event.isOwner()) return true;
        else
        {
            if(db.isUserBlacklisted(user))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else return true;
        }
    });

    public static final Category UTILS = new Category("Utilities", event ->
    {
        User user = event.getAuthor();

        if(event.isOwner()) return true;
        else
        {
            if(db.isUserBlacklisted(user))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else return true;
        }
    });

    public static final Category FUN = new Category("Fun", event ->
    {
        User user = event.getAuthor();

        if(event.isOwner()) return true;
        else
        {
            if(db.isUserBlacklisted(user))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else return true;
        }
    });

    public static final Category OTHERS = new Category("Others", event ->
    {
        User user = event.getAuthor();

        if(event.isOwner()) return true;
        else
        {
            if(db.isUserBlacklisted(user))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else return true;
        }
    });
}

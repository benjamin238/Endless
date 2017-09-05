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

package me.artuto.endless.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 *
 * @author Artu
 */

public class FinderUtil
{
    public final static String USER_MENTION = "<@!?(\\d+)";

    public static TextChannel getDefaultChannel(Guild guild)
    {

        if(guild.getTextChannelById(guild.getId())==null)
        {
            return guild.getTextChannels().stream().filter(TextChannel::canTalk).findFirst().orElse(null);
        }

        return guild.getTextChannelById(guild.getId());
    }
    
    public static List<User> findBannedUsers(String query, Guild guild)
    {
        List<User> bans;
        try
        {
            bans = guild.getBans().complete();
        }
        catch(Exception e)
        {
            return null;    
        }
        
        String id;
        String discrim = null;
        if(query.matches(USER_MENTION))
        {
            id = query.replaceAll(USER_MENTION, "$1");
            User u = guild.getJDA().getUserById(id);
            if(bans.contains(u))
            {
                return Collections.singletonList(u);
            }
            for(User user : bans)
            {
                if(user.getId().equals(id))
                {
                    return Collections.singletonList(user);
                }
            }
        }
        else if(query.matches("^.*#\\d{4}$"))
        {
            discrim = query.substring(query.length()-4);
            query = query.substring(0, query.length()-5).trim();
        }
        ArrayList<User> exact = new ArrayList<>();
        ArrayList<User> wrongcase = new ArrayList<>();
        ArrayList<User> startswith = new ArrayList<>();
        ArrayList<User> contains = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for(User u: bans)
        {
            if(discrim!=null && !(u.getDiscriminator().equals(discrim)))
            {
                continue;
            }
            if(u.getName().equals(query))
            {
                exact.add(u);
            }
            else if(exact.isEmpty() && u.getName().toLowerCase().startsWith(lowerQuery))
            {
                startswith.add(u);
            }
            else if(wrongcase.isEmpty() && u.getName().toLowerCase().startsWith(lowerQuery))
            {
                contains.add(u);
            }
        }
        
        if(!exact.isEmpty())
        {
            return exact;
        }
        if(!wrongcase.isEmpty())
        {
            return wrongcase;
        }
        if(!startswith.isEmpty())
        {
            return startswith;
        }
        return contains;
    }
}

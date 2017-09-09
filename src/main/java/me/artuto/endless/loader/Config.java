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

package me.artuto.endless.loader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.utils.SimpleLog;

/**
 *
 * @author Artu
 */

public class Config 
{
    private static String token;
    private static String prefix;
    private static String ownerid;
    private static String coownerid;
    private static OnlineStatus status;
    private static String dbanstoken;
    private static String dbotstoken;
    private static String dbotslisttoken;
    private static String done_e;
    private static String warn_e;
    private static String fail_e;

    public Config() throws Exception
    {
        List<String> lines = Files.readAllLines(Paths.get("config.yml"));
        SimpleLog LOG = SimpleLog.getLog("Config");
        for(String str : lines)
        {
            String[] parts = str.split("=",2);
            String key = parts[0].trim().toLowerCase();
            String value = parts.length>1 ? parts[1].trim() : null;
            switch(key) 
            {
                case "token":
                    token = value;
                    break;
                case "prefix":
                    if(value==null)
                    {
                        prefix = "";
                        LOG.warn("The prefix was defined as empty!");
                    }
                    else
                        prefix = value;
                    break;
                case "ownerid":
                    ownerid = value;
                    break;
                case "coownerid":
                    coownerid = value;
                    break;
                case "status":
                    status = OnlineStatus.fromKey(value);
                    break;
                case "dbanstoken":
                    dbanstoken = value;
                    break;
                case "dbotstoken":
                    dbotstoken = value;
                    break;
                case "dbotslisttoken":
                    dbotslisttoken = value;
                    break;
                case "done_e":
                    done_e = value;
                    break;
                case "warn_e":
                    warn_e=value;
                    break;
                case "fail_e":
                    fail_e=value;
                    break;

            }
        }
        if(token==null)
            throw new Exception("No token provided in the config file!");
        if(prefix==null)
            throw new Exception("No prefix provided in the config file!");
        if(ownerid==null)
            throw new Exception("No Owner ID provided in the config file!");
        if(coownerid==null)
            LOG.warn("No Co-Owner provided in the config file! Disabling feature...");
        if(status==OnlineStatus.UNKNOWN)
            LOG.warn("Invalid OnlineStatus! Using ONLINE.");
        if(dbanstoken==null)
            LOG.warn("No Discord Bans token provided in the config file! Disabling feature...");
        if(dbotstoken==null)
            LOG.warn("No Discord Bots token provided in the config file! Disabling feature...");
        if(dbotslisttoken==null)
            LOG.warn("No Discord Bots List token provided in the config file! Disabling feature...");
        if(done_e==null)
            LOG.warn("No Done Emote provided in the config file! Using the default emote...");
        if(warn_e==null)
            LOG.warn("No Warn Emote provided in the config file! Using the default emote...");
        if(fail_e==null)
            LOG.warn("No Error Emote provided in the config file! Using the default emote...");
    }
    
    public static String getToken()
    {
        return token;
    }
    
    public static String getPrefix()
    {
        return prefix;
    }
    
    public static String getOwnerId()
    {
        return ownerid;
    }

    public static String getCoOwnerId()
    {
        return coownerid;
    }

    public static OnlineStatus getStatus()
    {
        return status==OnlineStatus.UNKNOWN?OnlineStatus.ONLINE:status;
    }

    public static String getDBansToken()
    {
        return dbanstoken;
    }

    public static String getDBotsToken()
    {
        return dbotstoken;
    }

    public static String getDBotsListToken()
    {
        return dbotslisttoken;
    }

    public static String getDoneEmote()
    {
        return done_e==null?"✅":done_e;
    }

    public static String getWarnEmote()
    {
        return warn_e==null?"⚠":warn_e;
    }

    public static String getErrorEmote()
    {
        return fail_e==null?"❌":fail_e;
    }
}

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
import net.dv8tion.jda.core.utils.SimpleLog;

/**
 *
 * @author Artu
 */

public class Config 
{
    private final SimpleLog LOG = SimpleLog.getLog("Config");
    private String userToken;
    private static String prefix;
    private static String owner;
    private static String ownertag;
    private static String coownerid;
    private static String coownertag;
    private static String dbanstoken;
    
    public Config() throws Exception
    {
        List<String> lines = Files.readAllLines(Paths.get("config.yml"));
        for(String str : lines)
        {
            String[] parts = str.split("=",2);
            String key = parts[0].trim().toLowerCase();
            String value = parts.length>1 ? parts[1].trim() : null;
            switch(key) 
            {
                case "token":
                    userToken = value;
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
                case "owner":
                    owner = value;
                    break;
                case "ownertag":
                    ownertag = value;
                    break;
                case "coownerid":
                    coownerid = value;
                    break;
                case "coownertag":
                    coownertag = value;
                    break;
                case "dbanstoken":
                    dbanstoken = value;
                    break;    
            }
        }
        if(userToken==null)
            throw new Exception("No token provided in the config file!");
        if(prefix==null)
            throw new Exception("No prefix provided in the config file!");
        if(owner==null)
            throw new Exception("No Owner ID provided in the config file!");
        if(ownertag==null)
            throw new Exception("No Owner Tag provided in the config file!");
        if(coownerid==null)
            LOG.warn("No Co-Owner provided in the config file! Disabling feature...");
        if(coownertag==null)
            LOG.warn("No Co-Owner provided in the config file! Disabling feature...");
        if(dbanstoken==null)
            LOG.warn("No Discord Bans token provided in the config file! Disabling feature...");
    }
    
    public String getToken()
    {
        return userToken;
    }
    
    public static String getPrefix()
    {
        return prefix;
    }
    
    public static String getOwnerId()
    {
        return owner;
    }
    
    public static String getOwnerTag()
    {
        return ownertag;
    }
    
    public static String getCoOwnerId()
    {
        return coownerid;
    }
    
    public static String getCoOwnerTag()
    {
        return coownertag;
    }
    
    public static String getDBansToken()
    {
        return dbanstoken;
    }
}

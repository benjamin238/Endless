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

package me.artuto.endless.data;

import me.artuto.endless.Bot; /**
 *
 * @author Artu
 */

public class Settings 
{
    public final static Settings DEFAULT_SETTINGS = new Settings(0, 0);
    private long modlogId;
    private long srvlogId;
    
    public Settings(String modlogId, String srvlogId)
    {
        try
        {
            this.modlogId = Long.parseLong(modlogId);
        }
        catch(NumberFormatException e)
        {
            this.modlogId = 0;
        }
        try
        {
            this.srvlogId = Long.parseLong(srvlogId);
        }
        catch(NumberFormatException e)
        {
            this.srvlogId = 0;
        }
    }
    
    public Settings(long modlogId, long srvlogId)
    {
        this.modlogId = modlogId;
        this.srvlogId = srvlogId;
    }

    public long getModLogId()
    {
        return modlogId;
    }
    
    public long getServerLogId()
    {
        return srvlogId;
    }
    
    public void setModLogId(long id)
    {
        this.modlogId = id;
    }
    
    public void setServerLogId(long id)
    {
        this.srvlogId = id;
    }  
}

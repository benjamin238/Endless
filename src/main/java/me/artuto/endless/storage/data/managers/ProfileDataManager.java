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

package me.artuto.endless.storage.data.managers;

import me.artuto.endless.Bot;
import me.artuto.endless.core.entities.impl.EndlessCoreImpl;
import me.artuto.endless.storage.data.Database;
import me.artuto.endless.core.entities.Profile;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProfileDataManager
{
    private final Bot bot;
    private final Connection connection;

    public ProfileDataManager(Bot bot)
    {
        this.bot = bot;
        this.connection = bot.db.getConnection();
    }

    public Profile getProfile(User user)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            Profile p;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM PROFILES WHERE USER_ID = %s", user.getIdLong())))
            {
                if(results.next())
                {
                    p = bot.endlessBuilder.entityBuilder.createProfile(results, user);
                }
                else
                    p = bot.db.createDefaultProfile(user);
            }
            return p;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the profile of the specified user. ID: "+user.getId(), e);
            return bot.db.createDefaultProfile(user);
        }
    }

    public boolean hasProfile(User user)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM PROFILES WHERE USER_ID = %s", user.getId())))
            {
                return results.next();
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while checking if the specified user has a profile. ID: "+user.getId(), e);
            return false;
        }
    }

    public void setValue(User user, String field, String value)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM PROFILES WHERE user_id = %s", user.getId())))
            {
                if(results.next())
                {
                    results.updateString(field, value);
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("user_id", user.getIdLong());
                    results.updateString(field, value);
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the "+field+" of the specified user. ID: "+user.getId(), e);
        }
    }

    public void setTimezone(User user, String zone)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT user_id, timezone FROM PROFILES WHERE user_id = %s", user.getId())))
            {
                if(results.next())
                {
                    results.updateString("timezone", zone);
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("user_id", user.getIdLong());
                    results.updateString("timezone", zone);
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the timezone of the specified user. ID: "+user.getId(), e);
        }
    }
}

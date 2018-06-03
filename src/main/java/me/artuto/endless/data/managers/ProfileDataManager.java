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

package me.artuto.endless.data.managers;

import me.artuto.endless.data.Database;
import me.artuto.endless.entities.Profile;
import me.artuto.endless.entities.impl.ProfileImpl;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProfileDataManager
{
    private final Connection connection;
    private final Profile DEFAULT = new ProfileImpl(0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");

    public ProfileDataManager(Database db)
    {
        this.connection = db.getConnection();
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
                    p = new ProfileImpl(results.getInt("donation"), results.getString("timezone"), results.getString("twitter"),
                            results.getString("steam"), results.getString("wii"), results.getString("nnid"),
                            results.getString("xboxlive"), results.getString("psn"), results.getString("3ds"),
                            results.getString("skype"), results.getString("youtube"), results.getString("about"),
                            results.getString("twitch"), results.getString("minecraft"), results.getString("email"),
                            results.getString("lol"), results.getString("wow"), results.getString("battle"),
                            results.getString("splatoon"), results.getString("mkwii"), results.getString("reddit"));
                }
                else
                    p = DEFAULT;
            }
            return p;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the profile of the specified user. ID: "+user.getId(), e);
            return DEFAULT;
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

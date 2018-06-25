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

import me.artuto.endless.storage.data.Database;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class DonatorsDataManager
{
    private static Connection connection;

    public DonatorsDataManager(Database db)
    {
        connection = db.getConnection();
    }

    public boolean hasDonated(User user)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT user_id, donation FROM PROFILES WHERE user_id = %s", user.getId())))
            {
                if(results.next())
                    return !(results.getString("donation")==null);
                else
                    return false;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while checking if the specified user has donated. ID: "+user.getId(), e);
            return false;
        }
    }

    public void setDonation(long user, String donation)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT user_id, donation FROM PROFILES WHERE user_id = %s", user)))
            {
                if(results.next())
                {
                    results.updateString("donation", donation);
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("user_id", user);
                    results.updateString("donation", donation);
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting a donation for the specified user. ID: "+user, e);
        }
    }

    public String getDonation(User user)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT user_id, donation FROM PROFILES WHERE user_id = %s", user.getId())))
            {
                if(results.next())
                    return results.getString("donation");
                else
                    return null;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the donation of the specified user. ID: "+user.getId(), e);
            return null;
        }
    }

    public List<User> getUsersThatDonated(JDA jda)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            List<User> users;

            try(ResultSet results = statement.executeQuery("SELECT user_id, donation FROM PROFILES"))
            {
                users = new LinkedList<>();
                while(results.next())
                {
                    long id = results.getLong("user_id");
                    jda.retrieveUserById(id).queue(u -> {
                        if(hasDonated(u))
                            users.add(u);
                    }, e -> setDonation(id, null));
                }
            }
            return users;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the users that donated list.", e);
            return null;
        }
    }
}

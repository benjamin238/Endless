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

import ch.qos.logback.classic.Logger;
import me.artuto.endless.Endless;
import me.artuto.endless.storage.data.Database;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DonatorsDataManager
{
    private final Connection connection;
    private final Logger LOG = Endless.getLog(DonatorsDataManager.class);

    public DonatorsDataManager(Database db)
    {
        this.connection = db.getConnection();
    }

    public boolean hasDonated(User user)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT user_id, donation FROM PROFILES WHERE user_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, user.getIdLong());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    return !(results.getString("donation")==null);
                else
                    return false;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while checking if the specified user has donated. ID: {}", user.getId(), e);
            return false;
        }
    }

    public void setDonation(long user, String donation)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT user_id, donation FROM PROFILES WHERE user_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, user);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
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
            LOG.error("Error while setting a donation for the specified user. ID: {}", user, e);
        }
    }

    public String getDonation(User user)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT user_id, donation FROM PROFILES WHERE user_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, user.getIdLong());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    return results.getString("donation");
                else
                    return null;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting the donation of the specified user. ID: {}", user.getId(), e);
            return null;
        }
    }

    public List<User> getUsersThatDonated(JDA jda)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT user_id, donation FROM PROFILES",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            List<User> users;

            try(ResultSet results = statement.executeQuery())
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
            LOG.error("Error while getting the list of donators.", e);
            return Collections.emptyList();
        }
    }
}

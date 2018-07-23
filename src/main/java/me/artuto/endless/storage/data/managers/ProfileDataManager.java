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
import me.artuto.endless.Bot;
import me.artuto.endless.Endless;
import me.artuto.endless.core.entities.Profile;
import net.dv8tion.jda.core.entities.User;

import java.sql.*;

public class ProfileDataManager
{
    private final Bot bot;
    private final Connection connection;
    private final Logger LOG = Endless.getLog(ProfileDataManager.class);

    public ProfileDataManager(Bot bot)
    {
        this.bot = bot;
        this.connection = bot.db.getConnection();
    }

    public Profile getProfile(User user)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PROFILES where user_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, user.getIdLong());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    return bot.endlessBuilder.entityBuilder.createProfile(results, user);
                else
                    return bot.db.createDefaultProfile(user);
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting the profile of the specified user. ID: {}", user.getId(), e);
            return bot.db.createDefaultProfile(user);
        }
    }

    public boolean hasProfile(User user)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PROFILES where user_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, user.getIdLong());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                return results.next();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while checking if the specified user has a profile. ID: {}", user.getId(), e);
            return false;
        }
    }

    public void setValue(User user, String field, String value)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PROFILES where user_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, user.getIdLong());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
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
            LOG.error("Error while setting the {} of the specified user. ID: {}", field, user.getId(), e);
        }
    }

    public void setTimezone(User user, String zone)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT user_id, timezone FROM PROFILES where user_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, user.getIdLong());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
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
            LOG.error("Error while setting the timezone of the specified user. ID: {}", user.getId(), e);
        }
    }
}

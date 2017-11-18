package me.artuto.endless.data;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class BlacklistDataManager
{
    private static Connection connection;
    private final Logger LOG = LoggerFactory.getLogger("MySQL Database");

    public BlacklistDataManager(DatabaseManager db)
    {
        connection = db.getConnection();
    }

    public Connection getConnection()
    {
        return connection;
    }

    public boolean isUserBlacklisted(User user)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM BLACKLISTED_USERS WHERE user_id = %s", user.getId())))
            {
                return results.next();
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return false;
        }
    }

    public void addBlacklistUser(User user)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT user_id FROM BLACKLISTED_USERS WHERE user_id = %s", user.getId())))
            {
                if(results.next())
                {
                    results.updateLong("user_id", user==null ? 0l : user.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("user_id", user==null ? 0l : user.getIdLong());
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }

    public boolean removeBlacklistedUser(User user)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT user_id FROM BLACKLISTED_USERS WHERE user_id = %s", user.getId())))
            {
                if(results.next())
                {
                    results.deleteRow();
                    return true;
                }
                else
                    return false;
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return false;
        }
    }

    public Set<User> getBlacklistedUsersList(JDA jda)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            Set<User> users;

            try(ResultSet results = statement.executeQuery("SELECT * FROM BLACKLISTED_USERS"))
            {
                users = new HashSet<>();
                while(results.next())
                {
                    User u = jda.retrieveUserById(results.getLong("user_id")).complete();
                    if(!(u==null))
                        users.add(u);
                }
            }
            return users;
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return null;
        }
    }
}

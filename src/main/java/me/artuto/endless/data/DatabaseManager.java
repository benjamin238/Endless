package me.artuto.endless.data;

import me.artuto.endless.entities.GuildSettings;
import me.artuto.endless.entities.impl.GuildSettingsImpl;
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DatabaseManager
{
    private final Connection connection;
    private final Logger LOG = LoggerFactory.getLogger("MySQL Database");
    private final GuildSettings DEFAULT = new GuildSettingsImpl(0L, 0L, 0L, "", 0L,"", 0L, 0, null);

    public DatabaseManager(String host, String user, String pass) throws SQLException
    {
        connection = DriverManager.getConnection(host, user, pass);
    }

    //Connection getter

    Connection getConnection()
    {
        return connection;
    }

    //Guild settings

    public GuildSettings getSettings(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            GuildSettings gs;
            Set<String> prefixes = new HashSet<String>();
            String array;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getIdLong())))
            {
                if(results.next())
                {
                    array = results.getString("prefixes");

                    if(!(array==null))
                        for(Object prefix : new JSONArray(results.getString("prefixes")))
                            prefixes.add(prefix.toString());

                    gs = new GuildSettingsImpl(results.getLong("modlog_id"),
                            results.getLong("serverlog_id"),
                            results.getLong("welcome_id"),
                            results.getString("welcome_msg"),
                            results.getLong("leave_id"),
                            results.getString("leave_msg"),
                            results.getLong("starboard_id"),
                            results.getInt("starboard_count"),
                            prefixes);
                }
                else
                    gs = DEFAULT;
            }
            return gs;
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return DEFAULT;
        }
    }

    public GuildSettings getSettings(Long guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            GuildSettings gs;
            Set<String> prefixes = new HashSet<String>();
            String array;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild)))
            {
                if(results.next())
                {
                    array = results.getString("prefixes");

                    if(!(array==null))
                        for(Object prefix : new JSONArray(results.getString("prefixes")))
                            prefixes.add(prefix.toString());

                    gs = new GuildSettingsImpl(results.getLong("modlog_id"),
                            results.getLong("serverlog_id"),
                            results.getLong("welcome_id"),
                            results.getString("welcome_msg"),
                            results.getLong("leave_id"),
                            results.getString("leave_msg"),
                            results.getLong("starboard_id"),
                            results.getInt("starboard_count"),
                            prefixes);
                }
                else
                    gs = DEFAULT;
            }
            return gs;
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return DEFAULT;
        }
    }

    public boolean hasSettings(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
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

    public boolean hasSettings(Long guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild)))
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

    public void shutdown()
    {
        try
        {
            connection.close();
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }
}

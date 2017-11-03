package me.artuto.endless.data;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.sql.*;

public class DatabaseManager
{
    private final Connection connection;
    private final SimpleLog LOG = SimpleLog.getLog("MySQL Database");
    private final GuildSettings DEFAULT = new GuildSettings("0", "0", "0", "");

    public DatabaseManager(String host, String user, String pass) throws SQLException
    {
        connection = DriverManager.getConnection(host, user, pass);
    }

    //Connection getter

    public Connection getConnection()
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

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getIdLong())))
            {
                if(results.next())
                {
                    gs = new GuildSettings(results.getString("modlog_id"),
                            results.getString("serverlog_id"),
                            results.getString("welcome_id"),
                            results.getString("welcomemsg"));
                }
                else
                    gs = DEFAULT;
            }
            return gs;
        }
        catch(SQLException e)
        {
            LOG.warn(e);
            return DEFAULT;
        }
    }

    public boolean hasSettings(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            GuildSettings gs;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                return results.next();
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
            return false;
        }
    }

    public class GuildSettings
    {
        final String modlogId;
        final String serverlogId;
        final String welcomeId;
        final String welcomeMsg;

        private GuildSettings(String modlogId, String serverlogId, String welcomeId, String welcomeMsg)
        {
            this.modlogId = modlogId;
            this.serverlogId = serverlogId;
            this.welcomeId = welcomeId;
            this.welcomeMsg = welcomeMsg;
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
            LOG.warn(e);
        }
    }
}

package me.artuto.endless.data;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.sql.*;

public class DatabaseManager
{
    private final Connection connection;
    private final SimpleLog LOG = SimpleLog.getLog("MySQL Database");
    private final GuildSettings DEFAULT = new GuildSettings(0L, 0L, 0L, "", 0L,"");

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

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getIdLong())))
            {
                if(results.next())
                {
                    gs = new GuildSettings(results.getLong("modlog_id"),
                            results.getLong("serverlog_id"),
                            results.getLong("welcome_id"),
                            results.getString("welcome_msg"),
                            results.getLong("leave_id"),
                            results.getString("leave_msg"));
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
        public final Long modlogId;
        public final Long serverlogId;
        public final Long welcomeId;
        public final String welcomeMsg;
        public final Long leaveId;
        public final String leaveMsg;

        private GuildSettings(Long modlogId, Long serverlogId, Long welcomeId, String welcomeMsg, Long leaveId, String leaveMsg)
        {
            this.modlogId = modlogId;
            this.serverlogId = serverlogId;
            this.welcomeId = welcomeId;
            this.welcomeMsg = welcomeMsg;
            this.leaveMsg = leaveMsg;
            this.leaveId = leaveId;
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

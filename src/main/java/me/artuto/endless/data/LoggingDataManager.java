package me.artuto.endless.data;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class LoggingDataManager
{
    private final Connection connection;
    private final SimpleLog LOG = SimpleLog.getLog("MySQL Database");

    public LoggingDataManager(DatabaseManager db)
    {
        connection = db.getConnection();
    }

    public Connection getConnection()
    {
        return connection;
    }

    public TextChannel getModlogChannel(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            TextChannel tc;
            try (ResultSet results = statement.executeQuery(String.format("SELECT modlog_id FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                    tc = guild.getTextChannelById(Long.toString(results.getLong("modlog_id")));
                else tc=null;
            }
            return tc;
        }
        catch(SQLException e)
        {
            LOG.warn(e);
            return null;
        }
    }

    public TextChannel getServerlogChannel(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            TextChannel tc;
            try (ResultSet results = statement.executeQuery(String.format("SELECT serverlog_id FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                    tc = guild.getTextChannelById(Long.toString(results.getLong("serverlog_id")));
                else tc=null;
            }
            return tc;
        }
        catch(SQLException e)
        {
            LOG.warn(e);
            return null;
        }
    }

    public void setModlogChannel(Guild guild, TextChannel tc)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, modlog_id FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    results.updateLong("modlog_id", tc==null ? 0l : tc.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("modlog_id", tc==null ? 0l : tc.getIdLong());
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
        }
    }

    public void setServerlogChannel(Guild guild, TextChannel tc)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, serverlog_id FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    results.updateLong("serverlog_id", tc==null ? 0l : tc.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("serverlog_id", tc==null ? 0l : tc.getIdLong());
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
        }
    }
}

package me.artuto.endless.data;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class GuildSettingsDataManager
{
    private final Connection connection;
    private final Logger LOG = LoggerFactory.getLogger("MySQL Database");

    public GuildSettingsDataManager(DatabaseManager db)
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
            LOG.warn(e.toString());
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
            LOG.warn(e.toString());
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
                    results.updateLong("modlog_id", tc==null ? null : tc.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("modlog_id", tc==null ? null : tc.getIdLong());
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
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
            LOG.warn(e.toString());
        }
    }

    public TextChannel getWelcomeChannel(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            TextChannel tc;
            try (ResultSet results = statement.executeQuery(String.format("SELECT welcome_id FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                    tc = guild.getTextChannelById(Long.toString(results.getLong("welcome_id")));
                else tc=null;
            }
            return tc;
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return null;
        }
    }

    public void setWelcomeChannel(Guild guild, TextChannel tc)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, welcome_id FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    results.updateLong("welcome_id", tc==null ? null : tc.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("welcome_id", tc==null ? null : tc.getIdLong());
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }

    public TextChannel getLeaveChannel(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            TextChannel tc;
            try (ResultSet results = statement.executeQuery(String.format("SELECT leave_id FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                    tc = guild.getTextChannelById(Long.toString(results.getLong("leave_id")));
                else tc=null;
            }
            return tc;
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return null;
        }
    }

    public void setLeaveChannel(Guild guild, TextChannel tc)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, leave_id FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    results.updateLong("leave_id", tc==null ? null : tc.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("leave_id", tc==null ? null : tc.getIdLong());
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }

    public String getWelcomeMessage(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            String message;
            try (ResultSet results = statement.executeQuery(String.format("SELECT welcome_msg FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                    message = results.getString("welcome_msg");
                else message="";
            }
            return message;
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return null;
        }
    }

    public void setWelcomeMessage(Guild guild, String message)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, welcome_msg FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    results.updateString("welcome_msg", message.isEmpty() ? "" : message);
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateString("welcome_msg", message.isEmpty() ? "" : message);
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }

    public String getLeaveMessage(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            String message;
            try (ResultSet results = statement.executeQuery(String.format("SELECT leave_msg FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                    message = results.getString("leave_msg");
                else message="";
            }
            return message;
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return null;
        }
    }

    public void setLeaveMessage(Guild guild, String message)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, leave_msg FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    results.updateString("leave_msg", message.isEmpty() ? null : message);
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateString("leave_msg", message.isEmpty() ? null : message);
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }

    public TextChannel getStarboardChannel(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            TextChannel tc;
            try (ResultSet results = statement.executeQuery(String.format("SELECT starboard_id FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                    tc = guild.getTextChannelById(Long.toString(results.getLong("starboard_id")));
                else tc=null;
            }
            return tc;
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return null;
        }
    }

    public void setStarboardChannel(Guild guild, TextChannel tc)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, starboard_id FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    results.updateLong("starboard_id", tc==null ? null : tc.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("starboard_id", tc==null ? null : tc.getIdLong());
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }

    public Integer getStarboardCount(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            Integer count;
            try (ResultSet results = statement.executeQuery(String.format("SELECT starboard_count FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                    count = results.getInt("starboard_count");
                else count=null;
            }
            return count;
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return null;
        }
    }

    public void setStarboardCount(Guild guild, Integer count)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, starboard_count FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    results.updateInt("starboard_count", count);
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateInt("starboard_count", count);
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }
}

package me.artuto.endless.data;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.sql.*;

public class Database
{
    private final Connection connection;
    private final SimpleLog LOG = SimpleLog.getLog("MySQL");
    private final GuildSettings DEFAULT = new GuildSettings("0", "0", false, false);

    public Database(String host, String user, String pass) throws SQLException
    {
            try
            {
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            }
            catch(InstantiationException e)
            {
                e.printStackTrace();
            }
            catch(IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch(ClassNotFoundException e)
            {
                e.printStackTrace();
            }

        connection = DriverManager.getConnection(host, user, pass);
    }

    public void startupCheck()
    {
        try
        {
            if(!connection.getMetaData().getColumns(null, null, "GUILD_SETTINGS", "MODLOG_SWITCH").next())
            {
                LOG.info("Creating column 'modlog_switch'");
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.closeOnCompletion();
                statement.execute( "ALTER TABLE GUILD_SETTINGS\n" +
                        "ADD COLUMN modlog_switch BOOLEAN NOT NULL DEFAULT FALSE");
            }
            if(!connection.getMetaData().getColumns(null, null, "GUILD_SETTINGS", "SRVLOG_SWITCH").next())
            {
                LOG.info("Creating column 'serverlog_switch'");
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.closeOnCompletion();
                statement.execute( "ALTER TABLE GUILD_SETTINGS\n" +
                        "ADD COLUMN serverlog_switch BOOLEAN NOT NULL DEFAULT FALSE");
            }
            if(!connection.getMetaData().getColumns(null, null, "GUILD_SETTINGS", "MODLOG_CHANNEL_ID").next())
            {
                LOG.info("Creating column 'modlog_channel_id'");
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.closeOnCompletion();
                statement.execute( "ALTER TABLE GUILD_SETTINGS\n" +
                        "ADD COLUMN modlog_channel_id INT NOT NULL");
            }
            if(!connection.getMetaData().getColumns(null, null, "GUILD_SETTINGS", "SERVERLOG_CHANNEL_ID").next())
            {
                LOG.info("Creating column 'serverlog_channel_id'");
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.closeOnCompletion();
                statement.execute( "ALTER TABLE GUILD_SETTINGS\n" +
                        "ADD COLUMN serverlog_channel_id INT NOT NULL");
            }
            if(!connection.getMetaData().getColumns(null, null, "GUILD_SETTINGS", "GUILD_ID").next())
            {
                LOG.info("Creating column 'guild_id'");
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.closeOnCompletion();
                statement.execute( "ALTER TABLE GUILD_SETTINGS\n" +
                        "ADD COLUMN guild_id INT NOT NULL");
            }
        }
        catch(SQLException e)
        {
            LOG.fatal(e);
        }
    }

    public GuildSettings getSettings(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            GuildSettings settings;
            try (ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                {
                    settings = new GuildSettings(Long.toString(results.getShort("modlog_channel_id")),
                            Long.toString(results.getShort("serverlog_channel_id")),
                            results.getBoolean("modlog_switch"),
                            results.getBoolean("serverlog_switch"));
                }
                else
                {
                    settings = DEFAULT;
                }
            }
            return settings;
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
            GuildSettings settings;
            try (ResultSet results = statement.executeQuery(String.format("SELECT * FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
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

    public TextChannel getModlogChannel(Guild guild)
    {
        try {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            TextChannel tc;
            try (ResultSet results = statement.executeQuery(String.format("SELECT modlog_channel_id FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                {
                    tc = guild.getTextChannelById(Long.toString(results.getLong("modlog_channel_id")));
                }
                else
                {
                    tc=null;
                }
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
        try {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            TextChannel tc;
            try (ResultSet results = statement.executeQuery(String.format("SELECT serverlog_channel_id FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                {
                    tc = guild.getTextChannelById(Long.toString(results.getLong("serverlog_channel_id")));
                }
                else
                {
                    tc=null;
                }
            }
            return tc;
        }
        catch( SQLException e)
        {
            LOG.warn(e);
            return null;
        }
    }

    public boolean hasModlogEnabled(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            GuildSettings settings;
            try (ResultSet results = statement.executeQuery(String.format("SELECT modlog_switch FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
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

    public boolean hasServerlogEnabled(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            GuildSettings settings;
            try (ResultSet results = statement.executeQuery(String.format("SELECT serverlog_switch FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
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

    public void setModlogChannel(Guild guild, TextChannel tc)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, modlog_channel_id FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    results.updateLong("modlog_channel_id", tc==null ? 0l : tc.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("modlog_channel_id", tc==null ? 0l : tc.getIdLong());
                    results.insertRow();
                }
            }
            statement.closeOnCompletion();
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
            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, serverlog_channel_id FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    results.updateLong("serverlog_channel_id", tc==null ? 0l : tc.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("serverlog_channel_id", tc==null ? 0l : tc.getIdLong());
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
        }
    }

    public void setModlogSwitch(Guild guild, boolean status)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, modlog_switch FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    results.updateBoolean("modlog_switch", status);
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateBoolean("modlog_switch", status);
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
        }
    }

    public void setServerlogSwitch(Guild guild, boolean status)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, serverlog_switch FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    results.updateBoolean("serverlog_switch", status);
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateBoolean("serverlog_switch", status);
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
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

    public class GuildSettings
    {
        public final String modlog_Id;
        public final String serverlog_Id;
        public final boolean modlog_switch;
        public final boolean serverlog_switch;

        private GuildSettings(String modlogId, String serverlogId, boolean modlogSwitch, boolean serverlogSwitch)
        {
            this.modlog_Id = modlogId;
            this.serverlog_Id = serverlogId;
            this.modlog_switch = modlogSwitch;
            this.serverlog_switch = serverlogSwitch;
        }
    }
}

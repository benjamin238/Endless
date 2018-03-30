package me.artuto.endless.data;

import me.artuto.endless.entities.StarboardMessage;
import me.artuto.endless.entities.impl.StarboardMessageImpl;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StarboardDataManager
{
    private final Connection connection;
    private final Logger LOG = LoggerFactory.getLogger("MySQL Database");

    public StarboardDataManager(DatabaseManager db)
    {
        connection = db.getConnection();
    }

    public Connection getConnection()
    {
        return connection;
    }

    public boolean addMessage(Message msg, Integer amount)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM STARBOARD WHERE msg_id = %s", msg.getId())))
            {
                results.moveToInsertRow();
                results.updateLong("msg_id", msg.getIdLong());
                results.updateLong("tc_id", msg.getTextChannel().getIdLong());
                results.updateLong("guild_id", msg.getGuild().getIdLong());
                results.updateInt("star_amount", amount);
                results.insertRow();
                return true;
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return false;
        }
    }

    public boolean setStarboardMessageId(Message msg, Long starboardMsg)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT msg_id, starboard_msg_id FROM STARBOARD WHERE msg_id = %s", msg.getId())))
            {
                if(results.next())
                {
                    results.updateLong("starboard_msg_id", starboardMsg);
                    results.updateRow();
                    return true;
                }
                else return false;
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return false;
        }
    }

    public boolean updateCount(Long msg, Integer amount)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM STARBOARD WHERE msg_id = %s", msg)))
            {
                if(results.next())
                {
                    results.updateInt("star_amount", amount);
                    results.updateRow();
                    return true;
                }
                else return false;
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return false;
        }
    }

    public StarboardMessage getStarboardMessage(Long message)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT msg_id, tc_id, guild_id, star_amount, starboard_msg_id FROM STARBOARD WHERE msg_id = %s", message)))
            {
                if(results.next())
                    return new StarboardMessageImpl(results.getLong("msg_id"), results.getLong("tc_id"),
                            results.getLong("guild_id"), results.getInt("star_amount"), results.getLong("starboard_msg_id"));
                else return null;
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return null;
        }
    }

    public void deleteMessage(Long msg, Long starboardMsg)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM STARBOARD WHERE msg_id = \"%s\"", msg)))
            {
                if(results.next())
                {
                    results.updateLong("msg_id", 0);
                    results.updateRow();
                }
            }
            statement.executeUpdate(String.format("DELETE FROM STARBOARD WHERE starboard_msg_id = \"%s\"", starboardMsg));
            statement.closeOnCompletion();
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }
}

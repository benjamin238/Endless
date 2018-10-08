package me.artuto.endless.storage.data.managers;

import ch.qos.logback.classic.Logger;
import me.artuto.endless.Bot;
import me.artuto.endless.Endless;
import me.artuto.endless.core.entities.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RoomsDataManager
{
    private Bot bot;
    private Connection connection;
    private Logger LOG = Endless.getLog(RoomsDataManager.class);

    public RoomsDataManager(Bot bot)
    {
        this.bot = bot;
        this.connection = bot.db.getConnection();
    }

    public void createComboRoom(boolean restricted, long guildId, long tcId, long ownerId, long vcId)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ROOMS",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                results.moveToInsertRow();
                results.updateBoolean("restricted", restricted);
                results.updateLong("guild_id", guildId);
                results.updateLong("tc_id", tcId);
                results.updateLong("owner_id", ownerId);
                results.updateLong("vc_id", vcId);
                results.insertRow();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while creating a combo room.", e);
        }
    }

    public void createTextRoom(boolean restricted, long guildId, long tcId, long ownerId)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ROOMS",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                results.moveToInsertRow();
                results.updateBoolean("restricted", restricted);
                results.updateLong("guild_id", guildId);
                results.updateLong("tc_id", tcId);
                results.updateLong("owner_id", ownerId);
                results.insertRow();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while creating a text room.", e);
        }
    }

    public void createVoiceRoom(boolean restricted, long guildId, long ownerId, long vcId)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ROOMS",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery("SELECT * FROM ROOMS"))
            {
                results.moveToInsertRow();
                results.updateBoolean("restricted", restricted);
                results.updateLong("guild_id", guildId);
                results.updateLong("vc_id", vcId);
                results.updateLong("owner_id", ownerId);
                results.insertRow();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while creating a voice room.", e);
        }
    }

    public void deleteComboRoom(long tcId, long vcId)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ROOMS WHERE tc_id = ? AND vc_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, tcId);
            statement.setLong(2, vcId);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    results.deleteRow();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while deleting a combo room.", e);
        }
    }

    public void deleteTextRoom(long tcId)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ROOMS WHERE tc_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, tcId);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    results.deleteRow();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while deleting a text room.", e);
        }
    }

    public void deleteVoiceRoom(long vcId)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ROOMS WHERE vc_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, vcId);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    results.deleteRow();
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while deleting a voice room.", e);
        }
    }

    public void lockRoom(boolean status, long roomId)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ROOMS WHERE tc_id = ? OR vc_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, roomId);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                {
                    results.updateBoolean("restricted", status);
                    results.updateRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while locking a room.", e);
        }
    }

    public void transferProperty(long newOwnerId, long roomId)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ROOMS WHERE tc_id = ? OR vc_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, roomId);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                {
                    results.updateLong("owner_id", newOwnerId);
                    results.updateRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while transferring the property of a room.", e);
        }
    }

    public List<Room> getRoomsForGuild(long guildId)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ROOMS WHERE guild_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, guildId);
            statement.closeOnCompletion();
            List<Room> list;

            try(ResultSet results = statement.executeQuery())
            {
                list = new LinkedList<>();
                while(results.next())
                    list.add(bot.endlessBuilder.entityBuilder.createRoom(results));
                return list;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting a list of rooms for guild ID: {}", guildId, e);
            return Collections.emptyList();
        }
    }

    public Room getRoom(long id)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ROOMS WHERE tc_id = ? OR vc_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    return bot.endlessBuilder.entityBuilder.createRoom(results);
                else
                    return null;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while locking a room.", e);
            return null;
        }
    }
}

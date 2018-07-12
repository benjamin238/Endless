package me.artuto.endless.storage.data.managers;

import ch.qos.logback.classic.Logger;
import me.artuto.endless.Endless;
import me.artuto.endless.core.entities.Room;
import me.artuto.endless.core.entities.impl.RoomImpl;
import me.artuto.endless.storage.data.Database;
import me.artuto.endless.utils.ChecksUtil;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.*;

public class RoomsDataManager
{
    private Connection connection;
    private Logger LOG = Endless.getLog(RoomsDataManager.class);

    public RoomsDataManager(Database db)
    {
        connection = db.getConnection();
    }

    public void createComboRoom(boolean restricted, long expiryTime, long guildId, long tcId, long ownerId, long vcId)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery("SELECT * FROM ROOMS"))
            {
                results.moveToInsertRow();
                results.updateBoolean("restricted", restricted);
                results.updateLong("guild_id", guildId);
                results.updateLong("tc_id", tcId);
                results.updateLong("owner_id", ownerId);
                results.updateLong("vc_id", vcId);
                if(!(expiryTime==0L))
                    results.updateLong("expiry_time", expiryTime);
                results.insertRow();
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while creating a combo room.", e);
        }
    }

    public void createTextRoom(boolean restricted, long expiryTime, long guildId, long tcId, long ownerId)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery("SELECT * FROM ROOMS"))
            {
                results.moveToInsertRow();
                results.updateBoolean("restricted", restricted);
                results.updateLong("guild_id", guildId);
                results.updateLong("tc_id", tcId);
                results.updateLong("owner_id", ownerId);
                if(!(expiryTime==0L))
                    results.updateLong("expiry_time", expiryTime);
                results.insertRow();
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while creating a text room.", e);
        }
    }

    public void createVoiceRoom(boolean restricted, long expiryTime, long guildId, long ownerId, long vcId)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery("SELECT * FROM ROOMS"))
            {
                results.moveToInsertRow();
                results.updateBoolean("restricted", restricted);
                results.updateLong("guild_id", guildId);
                results.updateLong("vc_id", vcId);
                results.updateLong("owner_id", ownerId);
                if(!(expiryTime==0L))
                    results.updateLong("expiry_time", expiryTime);
                results.insertRow();
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while creating a voice room.", e);
        }
    }

    public void deleteComboRoom(long tcId, long vcId)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM ROOMS WHERE tc_id = %s AND vc_id = %s", tcId, vcId)))
            {
                if(results.next())
                    results.deleteRow();
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while deleting a combo room.", e);
        }
    }

    public void deleteTextRoom(long tcId)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM ROOMS WHERE tc_id = %s", tcId)))
            {
                if(results.next())
                    results.deleteRow();
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while deleting a text room.", e);
        }
    }

    public void deleteVoiceRoom(long vcId)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM ROOMS WHERE vc_id = %s", vcId)))
            {
                if(results.next())
                    results.deleteRow();
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while deleting a voice room.", e);
        }
    }

    public List<Room> getRoomsForGuild(long guildId)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            List<Room> list;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM ROOMS WHERE guild_id = %s", guildId)))
            {
                list = new LinkedList<>();
                while(results.next())
                {
                    if(!(results.getLong("expiry_time")==0L))
                    {
                        Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                        gmt.setTimeInMillis(results.getLong("expiry_time"));
                        list.add(new RoomImpl(results.getBoolean("restricted"), guildId, results.getLong("tc_id"),
                                results.getLong("vc_id"), results.getLong("owner_id"), OffsetDateTime.ofInstant(gmt.toInstant(), gmt.getTimeZone().toZoneId())));
                    }
                    else
                        list.add(new RoomImpl(results.getBoolean("restricted"), guildId, results.getLong("tc_id"),
                                results.getLong("vc_id"), results.getLong("owner_id"), null));
                }
                return list;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting a list of rooms for guild ID: "+guildId, e);
            return Collections.emptyList();
        }
    }

    public List<Room> getTempRooms()
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            List<Room> list;

            try(ResultSet results = statement.executeQuery("SELECT * FROM ROOMS WHERE expiry_time != null"))
            {
                list = new LinkedList<>();
                while(results.next())
                {
                    Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                    gmt.setTimeInMillis(results.getLong("expiry_time"));
                    list.add(new RoomImpl(results.getBoolean("restricted"), results.getLong("guild_id"), results.getLong("tc_id"),
                            results.getLong("vc_id"), results.getLong("owner_id"),
                            OffsetDateTime.ofInstant(gmt.toInstant(), gmt.getTimeZone().toZoneId())));
                }
                return list;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting a list of temporal rooms.", e);
            return Collections.emptyList();
        }
    }

    public void updateRooms(ShardManager shardManager)
    {
        for(Room room : getTempRooms())
        {
            if(OffsetDateTime.now().isAfter(room.getExpiryTime()))
            {
                Guild guild = shardManager.getGuildById(room.getGuildId());
                if(guild==null)
                    return;
                TextChannel tc;
                VoiceChannel vc;

                if(room.isCombo())
                {
                    tc = guild.getTextChannelById(room.getTextChannelId());
                    vc = guild.getVoiceChannelById(room.getVoiceChannelId());
                    deleteComboRoom(room.getTextChannelId(), room.getVoiceChannelId());
                    if(!(tc==null))
                    {
                        if(ChecksUtil.hasPermission(guild.getSelfMember(), tc, Permission.MANAGE_CHANNEL))
                            tc.delete().reason("[Text Room Expired]").queue(null,
                                    e -> LOG.error("Could not delete Text (Combo) Room with ID {}", tc.getId()));
                    }
                    if(!(vc==null))
                    {
                        if(ChecksUtil.hasPermission(guild.getSelfMember(), vc, Permission.MANAGE_CHANNEL))
                            vc.delete().reason("[Voice Room Expired]").queue(null,
                                    e -> LOG.error("Could not delete Voice (Combo) Room with ID {}", vc.getId()));
                    }
                }
                else if(room.isText())
                {
                    tc = guild.getTextChannelById(room.getTextChannelId());
                    deleteTextRoom(room.getTextChannelId());
                    if(tc==null)
                        return;
                    if(ChecksUtil.hasPermission(guild.getSelfMember(), tc, Permission.MANAGE_CHANNEL))
                        tc.delete().reason("[Text Room Expired]").queue(null,
                                e -> LOG.error("Could not delete Text Room with ID {}", tc.getId()));
                }
                else if(room.isVoice())
                {
                    vc = guild.getVoiceChannelById(room.getVoiceChannelId());
                    deleteVoiceRoom(room.getVoiceChannelId());
                    if(vc==null)
                        return;
                    if(ChecksUtil.hasPermission(guild.getSelfMember(), vc, Permission.MANAGE_CHANNEL))
                        vc.delete().reason("[Voice Room Expired]").queue(null,
                                e -> LOG.error("Could not delete Voice Room with ID {}", vc.getId()));
                }
            }
        }
    }
}

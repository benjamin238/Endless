/*
 * Copyright (C) 2017-2018 Artuto
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.artuto.endless.storage.data.managers;

import me.artuto.endless.core.entities.Reminder;
import me.artuto.endless.core.entities.impl.ReminderImpl;
import me.artuto.endless.storage.data.Database;
import me.artuto.endless.utils.ChecksUtil;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * @author Artuto
 */

public class RemindersDataManager
{
    private final Connection connection;

    public RemindersDataManager(Database db)
    {
        this.connection = db.getConnection();
    }

    public void createReminder(long channelId, long expiryTime, long userId, String msg)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery("SELECT * FROM REMINDERS"))
            {
                long reminderUserId = getReminderUserId(getRemindersByUser(userId));
                results.moveToInsertRow();
                results.updateLong("user_id", userId);
                results.updateLong("channel_id", channelId);
                results.updateLong("expiry_time", expiryTime);
                results.updateLong("reminder_user_id", reminderUserId);
                results.updateString("msg", msg);
                results.insertRow();
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while creating a reminder. User ID: "+userId, e);
        }
    }

    public void deleteReminder(long reminderUserId, long userId)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM REMINDERS WHERE user_id = %s AND reminder_user_id = %s",
                    userId, reminderUserId)))
            {
                if(results.next())
                    results.deleteRow();
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while deleting a reminder. User ID: "+userId, e);
        }
    }

    public List<Reminder> getReminders()
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            List<Reminder> list;

            try(ResultSet results = statement.executeQuery("SELECT * FROM REMINDERS"))
            {
                list = new LinkedList<>();
                while(results.next())
                {
                    Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                    gmt.setTimeInMillis(results.getLong("time"));
                    list.add(new ReminderImpl(results.getLong("channel_id"), results.getLong("reminder_user_id"),
                            results.getLong("user_id"), OffsetDateTime.ofInstant(gmt.toInstant(), gmt.getTimeZone().toZoneId()),
                            results.getString("msg")));
                }
                return list;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while adding a list of reminders", e);
            return Collections.emptyList();
        }
    }

    public List<Reminder> getRemindersByUser(long userId)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            List<Reminder> list;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM REMINDERS WHERE user_id = %s", userId)))
            {
                list = new LinkedList<>();
                while(results.next())
                {
                    Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                    gmt.setTimeInMillis(results.getLong("time"));
                    list.add(new ReminderImpl(results.getLong("channel_id"), results.getLong("reminder_user_id"),
                            results.getLong("user_id"), OffsetDateTime.ofInstant(gmt.toInstant(), gmt.getTimeZone().toZoneId()),
                            results.getString("msg")));
                }
                return list;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while adding a list of reminders of a specified user. User ID: "+userId, e);
            return Collections.emptyList();
        }
    }

    public long getReminderUserId(List<Reminder> reminders)
    {
        List<Long> ids = new LinkedList<>();
        reminders.forEach(r -> ids.add(r.getReminderUserId()));
        return Collections.max(ids);
    }

    public Reminder getReminder(long reminderUserId, long userId)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM REMINDERS WHERE user_id = %s AND reminder_user_id = %s",
                    userId, reminderUserId)))
            {
                if(results.next())
                {
                    Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                    gmt.setTimeInMillis(results.getLong("time"));
                    return new ReminderImpl(results.getLong("channel_id"), results.getLong("reminder_user_id"),
                            results.getLong("user_id"), OffsetDateTime.ofInstant(gmt.toInstant(), gmt.getTimeZone().toZoneId()),
                            results.getString("msg"));
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while adding a list of reminders of a specified user. User ID: "+userId, e);
            return null;
        }
        return null;
    }

    public void updateReminders(ShardManager shardManager)
    {
        for(Reminder reminder : getReminders())
        {
            if(OffsetDateTime.now().isAfter(reminder.getExpiryTime()))
            {
                deleteReminder(reminder.getReminderUserId(), reminder.getUserId());
                TextChannel tc = shardManager.getTextChannelById(reminder.getChannelId());
                if(tc==null)
                    return;
                User user = shardManager.getUserById(reminder.getUserId());
                if(user==null)
                    return;
                EmbedBuilder builder = new EmbedBuilder();
                MessageBuilder mb = new MessageBuilder();
                builder.setDescription(reminder.getMessage());
                if(!(ChecksUtil.hasPermission(tc.getGuild().getSelfMember(), tc, Permission.MESSAGE_EMBED_LINKS)))
                {
                    mb.setContent(":alarm_clock: You asked me to remind you about this.").setEmbed(builder.build());
                    user.openPrivateChannel().queue(c -> c.sendMessage(mb.build()).queue(null, e -> {}));
                }
                else if(tc.canTalk())
                {
                    mb.setContent(user.getAsMention()+" :alarm_clock: You asked me to remind you about this.").setEmbed(builder.build());
                    tc.sendMessage(mb.build()).queue();
                }
            }
        }
    }
}

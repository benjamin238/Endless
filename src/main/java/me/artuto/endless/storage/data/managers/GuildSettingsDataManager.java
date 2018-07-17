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

import me.artuto.endless.Bot;
import me.artuto.endless.core.entities.Ignore;
import me.artuto.endless.core.entities.Room;
import me.artuto.endless.core.entities.impl.EndlessCoreImpl;
import me.artuto.endless.core.entities.impl.GuildSettingsImpl;
import me.artuto.endless.core.entities.impl.IgnoreImpl;
import me.artuto.endless.storage.data.Database;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;

import java.sql.*;

public class GuildSettingsDataManager
{
    private final Bot bot;
    private final Connection connection;

    public GuildSettingsDataManager(Bot bot)
    {
        this.connection = bot.db.getConnection();
        this.bot = bot;
    }

    public void setModlogChannel(Guild guild, TextChannel tc)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, modlog_id FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(tc==null)
                {
                    if(results.next())
                    {
                        results.updateNull("modlog_id");
                        results.updateRow();
                    }
                }
                else
                {
                    if(results.next())
                    {
                        results.updateLong("modlog_id", tc.getIdLong());
                        results.updateRow();
                    }
                    else
                    {
                        results.moveToInsertRow();
                        results.updateLong("guild_id", guild.getIdLong());
                        results.updateLong("modlog_id", tc.getIdLong());
                        results.insertRow();
                    }
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.setModlogId(tc==null?0L:tc.getIdLong());
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the modlog channel for the guild "+guild.getId(), e);
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
                if(tc == null)
                {
                    if(results.next())
                    {
                        results.updateNull("serverlog_id");
                        results.updateRow();
                    }
                }
                else
                {
                    if(results.next())
                    {
                        results.updateLong("serverlog_id", tc.getIdLong());
                        results.updateRow();
                    }
                    else
                    {
                        results.moveToInsertRow();
                        results.updateLong("guild_id", guild.getIdLong());
                        results.updateLong("serverlog_id", tc.getIdLong());
                        results.insertRow();
                    }
                }
                GuildSettingsImpl settings = (GuildSettingsImpl) bot.endless.getGuildSettings(guild);
                settings.setServerlogId(tc == null ? 0L : tc.getIdLong());
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the serverlog channel for the guild "+guild.getId(), e);
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
                if(tc==null)
                {
                    if(results.next())
                    {
                        results.updateNull("welcome_id");
                        results.updateRow();
                    }
                }
                else
                {
                    if(results.next())
                    {
                        results.updateLong("welcome_id", tc.getIdLong());
                        results.updateRow();
                    }
                    else
                    {
                        results.moveToInsertRow();
                        results.updateLong("guild_id", guild.getIdLong());
                        results.updateLong("welcome_id", tc.getIdLong());
                        results.insertRow();
                    }
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.setWelcomeId(tc==null?0L:tc.getIdLong());
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the welcome channel for the guild "+guild.getId(), e);
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
                if(tc==null)
                {
                    if(results.next())
                    {
                        results.updateNull("leave_id");
                        results.updateRow();
                    }
                }
                else
                {
                    if(results.next())
                    {
                        results.updateLong("leave_id", tc.getIdLong());
                        results.updateRow();
                    }
                    else
                    {
                        results.moveToInsertRow();
                        results.updateLong("guild_id", guild.getIdLong());
                        results.updateLong("leave_id", tc.getIdLong());
                        results.insertRow();
                    }
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.setLeaveId(tc==null?0L:tc.getIdLong());
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the leave channel for the guild "+guild.getId(), e);
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
                if(message==null)
                {
                    if(results.next())
                    {
                        results.updateNull("welcome_msg");
                        results.updateRow();
                    }
                }
                else
                {
                    if(results.next())
                    {
                        results.updateString("welcome_msg", message);
                        results.updateRow();
                    }
                    else
                    {
                        results.moveToInsertRow();
                        results.updateLong("guild_id", guild.getIdLong());
                        results.updateString("welcome_msg", message);
                        results.insertRow();
                    }
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.setWelcomeMsg(message);
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the welcome message for the guild "+guild.getId(), e);
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
                if(message==null)
                {
                    if(results.next())
                    {
                        results.updateNull("leave_msg");
                        results.updateRow();
                    }
                }
                else
                {
                    if(results.next())
                    {
                        results.updateString("leave_msg", message);
                        results.updateRow();
                    }
                    else
                    {
                        results.moveToInsertRow();
                        results.updateLong("guild_id", guild.getIdLong());
                        results.updateString("leave_msg", message);
                        results.insertRow();
                    }
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.setLeaveMsg(message);
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the leave message for the guild "+guild.getId(), e);
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
                if(tc==null)
                {
                    if(results.next())
                    {
                        results.updateNull("starboard_id");
                        results.updateRow();
                    }
                }
                else
                {
                    if(results.next())
                    {
                        results.updateLong("starboard_id", tc.getIdLong());
                        results.updateRow();
                    }
                    else
                    {
                        results.moveToInsertRow();
                        results.updateLong("guild_id", guild.getIdLong());
                        results.updateLong("starboard_id", tc.getIdLong());
                        results.insertRow();
                    }
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.setStarboardId(tc==null?0L:tc.getIdLong());
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the starboard channel for the guild "+guild.getId(), e);
        }
    }

    public void setAdminRole(Guild guild, Role role)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, admin_role_id FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(role==null)
                {
                    if(results.next())
                    {
                        results.updateNull("admin_role_id");
                        results.updateRow();
                    }
                }
                else
                {
                    if(results.next())
                    {
                        results.updateLong("admin_role_id", role.getIdLong());
                        results.updateRow();
                    }
                    else
                    {
                        results.moveToInsertRow();
                        results.updateLong("guild_id", guild.getIdLong());
                        results.updateLong("admin_role_id", role.getIdLong());
                        results.insertRow();
                    }
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.setAdminRoleId(role==null?0L:role.getIdLong());
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the muted role for the guild "+guild.getId(), e);
        }
    }

    public void setModRole(Guild guild, Role role)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, mod_role_id FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(role==null)
                {
                    if(results.next())
                    {
                        results.updateNull("mod_role_id");
                        results.updateRow();
                    }
                }
                else
                {
                    if(results.next())
                    {
                        results.updateLong("mod_role_id", role.getIdLong());
                        results.updateRow();
                    }
                    else
                    {
                        results.moveToInsertRow();
                        results.updateLong("guild_id", guild.getIdLong());
                        results.updateLong("mod_role_id", role.getIdLong());
                        results.insertRow();
                    }
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.setModRoleId(role==null?0L:role.getIdLong());
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the muted role for the guild "+guild.getId(), e);
        }
    }

    public void setStarboardCount(Guild guild, int count)
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
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.setStarboardCount(count);
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the starboard count for the guild "+guild.getId(), e);
        }
    }

    public void addPrefix(Guild guild, String prefix)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            String prefixes;
            JSONArray array;

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, prefixes FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    prefixes = results.getString("prefixes");

                    if(prefixes == null) array = new JSONArray().put(prefix);
                    else array = new JSONArray(prefixes).put(prefix);

                    results.updateString("prefixes", array.toString());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateString("prefixes", new JSONArray().put(prefix).toString());
                    results.insertRow();
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.addPrefix(prefix);
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while adding a prefix for the guild "+guild.getId(), e);
        }
    }

    public void removePrefix(Guild guild, String prefix)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            String prefixes;
            JSONArray array;

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, prefixes FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    prefixes = results.getString("prefixes");

                    if(!(prefixes == null))
                    {
                        array = new JSONArray(prefixes);

                        for(int i = 0; i<array.length(); i++)
                        {
                            if(array.get(i).toString().equals(prefix))
                            {
                                array.remove(i);
                                if(array.length()<0)
                                {
                                    results.updateNull("prefixes");
                                    results.updateRow();
                                }
                                else
                                {
                                    results.updateString("prefixes", array.toString());
                                    results.updateRow();
                                }
                            }
                        }
                        GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                        settings.removePrefix(prefix);
                    }
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while removing a prefix for the guild "+guild.getId(), e);
        }
    }

    public void addRolemeRole(Guild guild, Role role)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            String roles;
            JSONArray array;

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, roleme_roles FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    roles = results.getString("roleme_roles");
                    if(roles==null)
                        array = new JSONArray().put(role.getId());
                    else
                        array = new JSONArray(roles).put(role.getId());

                    results.updateString("roleme_roles", array.toString());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateString("roleme_roles", new JSONArray().put(role.getId()).toString());
                    results.insertRow();
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.addRoleMeRole(role);
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while adding a roleme role for the guild "+guild.getId(), e);
        }
    }

    public void removeRolemeRole(Guild guild, Role role)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            String roles;
            JSONArray array;

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, roleme_roles FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    roles = results.getString("roleme_roles");

                    if(!(roles == null))
                    {
                        array = new JSONArray(roles);

                        for(int i = 0; i<array.length(); i++)
                        {
                            if(array.get(i).toString().equals(role.getId()))
                            {
                                array.remove(i);
                                if(array.length()<0)
                                {
                                    results.updateNull("roleme_roles");
                                    results.updateRow();
                                }
                                else
                                {
                                    results.updateString("roleme_roles", array.toString());
                                    results.updateRow();
                                }
                            }
                        }
                        GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                        settings.removeRoleMeRole(role);
                    }
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while removing a roleme role from the guild "+guild.getId(), e);
        }
    }

    public void setMutedRole(Guild guild, Role role)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, muted_role_id FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(role==null)
                {
                    if(results.next())
                    {
                        results.updateNull("muted_role_id");
                        results.updateRow();
                    }
                }
                else
                {
                    if(results.next())
                    {
                        results.updateLong("muted_role_id", role.getIdLong());
                        results.updateRow();
                    }
                    else
                    {
                        results.moveToInsertRow();
                        results.updateLong("guild_id", guild.getIdLong());
                        results.updateLong("muted_role_id", role.getIdLong());
                        results.insertRow();
                    }
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.setMutedRoleId(role==null?0L:role.getIdLong());
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the muted role for the guild "+guild.getId(), e);
        }
    }

    public void setBanDeleteDays(Guild guild, int days)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, ban_delete_days FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(days==0)
                {
                    if(results.next())
                    {
                        results.updateNull("ban_delete_days");
                        results.updateRow();
                    }
                }
                else
                {
                    if(results.next())
                    {
                        results.updateInt("ban_delete_days", days);
                        results.updateRow();
                    }
                    else
                    {
                        results.moveToInsertRow();
                        results.updateLong("guild_id", guild.getIdLong());
                        results.updateInt("ban_delete_days", days);
                        results.insertRow();
                    }
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.setBanDeleteDays(days);
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while settings the ban delete days number for the guild "+guild.getId(), e);
        }
    }

    public void addIgnore(Guild guild, long entity)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM IGNORES WHERE guild_id = %s", guild.getId())))
            {
                results.moveToInsertRow();
                results.updateLong("guild_id", guild.getIdLong());
                results.updateLong("entity_id", entity);
                results.insertRow();
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.addIgnoredEntity(new IgnoreImpl(entity, guild.getIdLong()));
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while adding a ignore for the guild "+guild.getId(), e);
        }
    }

    public void removeIgnore(Guild guild, long entity)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM IGNORES WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                    results.deleteRow();
                Ignore ignore = bot.endless.getIgnore(guild, entity);
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.removeIgnoredEntity(ignore);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while removing a ignore for the guild "+guild.getId(), e);
        }
    }

    public void setRoomMode(Guild guild, Room.Mode mode)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, room_mode FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(mode==null)
                {
                    if(results.next())
                    {
                        results.updateNull("room_mode");
                        results.updateRow();
                    }
                }
                else
                {
                    if(results.next())
                    {
                        results.updateString("room_mode", mode.getName());
                        results.updateRow();
                    }
                    else
                    {
                        results.moveToInsertRow();
                        results.updateLong("guild_id", guild.getIdLong());
                        results.updateString("mode_room", mode.getName());
                        results.insertRow();
                    }
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettings(guild);
                settings.setRoomMode(mode);
                if(bot.endless.getGuildSettingsById(guild.getIdLong()).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(guild, settings);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the room mode for the guild "+guild.getId(), e);
        }
    }
}

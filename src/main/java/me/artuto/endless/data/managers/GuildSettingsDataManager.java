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

package me.artuto.endless.data.managers;

import me.artuto.endless.data.Database;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class GuildSettingsDataManager
{
    private final Connection connection;

    public GuildSettingsDataManager(Database db)
    {
        connection = db.getConnection();
    }

    public TextChannel getModlogChannel(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            TextChannel tc;
            try(ResultSet results = statement.executeQuery(String.format("SELECT modlog_id FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                    tc = guild.getTextChannelById(Long.toString(results.getLong("modlog_id")));
                else
                    tc = null;
            }
            return tc;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the modlog for the guild "+guild.getId(), e);
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
            try(ResultSet results = statement.executeQuery(String.format("SELECT serverlog_id FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                    tc = guild.getTextChannelById(Long.toString(results.getLong("serverlog_id")));
                else
                    tc = null;
            }
            return tc;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the serverlog for the guild "+guild.getId(), e);
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
                    results.updateLong("modlog_id", tc == null ? null : tc.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("modlog_id", tc == null ? null : tc.getIdLong());
                    results.insertRow();
                }
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
                if(results.next())
                {
                    results.updateLong("serverlog_id", tc == null ? 0l : tc.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("serverlog_id", tc == null ? 0l : tc.getIdLong());
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the serverlog channel for the guild "+guild.getId(), e);
        }
    }

    public TextChannel getWelcomeChannel(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            TextChannel tc;
            try(ResultSet results = statement.executeQuery(String.format("SELECT welcome_id FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next()) tc = guild.getTextChannelById(Long.toString(results.getLong("welcome_id")));
                else tc = null;
            }
            return tc;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the welcome channel for the guild "+guild.getId(), e);
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
                    results.updateLong("welcome_id", tc == null ? null : tc.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("welcome_id", tc == null ? null : tc.getIdLong());
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the welcome channel for the guild "+guild.getId(), e);
        }
    }

    public TextChannel getLeaveChannel(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            TextChannel tc;
            try(ResultSet results = statement.executeQuery(String.format("SELECT leave_id FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next()) tc = guild.getTextChannelById(Long.toString(results.getLong("leave_id")));
                else tc = null;
            }
            return tc;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the leave channel for the guild "+guild.getId(), e);
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
                    results.updateLong("leave_id", tc == null ? null : tc.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("leave_id", tc == null ? null : tc.getIdLong());
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the leave channel for the guild "+guild.getId(), e);
        }
    }

    public String getWelcomeMessage(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            String message;
            try(ResultSet results = statement.executeQuery(String.format("SELECT welcome_msg FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next()) message = results.getString("welcome_msg");
                else message = "";
            }
            return message;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the welcome message for the guild "+guild.getId(), e);
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
            Database.LOG.error("Error while setting the welcome message for the guild "+guild.getId(), e);
        }
    }

    public String getLeaveMessage(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            String message;
            try(ResultSet results = statement.executeQuery(String.format("SELECT leave_msg FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next()) message = results.getString("leave_msg");
                else message = "";
            }
            return message;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the leave message for the guild "+guild.getId(), e);
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
            Database.LOG.error("Error while setting the leave message for the guild "+guild.getId(), e);
        }
    }

    public TextChannel getStarboardChannel(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            TextChannel tc;
            try(ResultSet results = statement.executeQuery(String.format("SELECT starboard_id FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next()) tc = guild.getTextChannelById(Long.toString(results.getLong("starboard_id")));
                else tc = null;
            }
            return tc;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the starboard channel for the guild "+guild.getId(), e);
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
                    results.updateLong("starboard_id", tc==null?null:tc.getIdLong());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("starboard_id", tc==null?null:tc.getIdLong());
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the starboard channel for the guild "+guild.getId(), e);
        }
    }

    public Integer getStarboardCount(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            Integer count;
            try(ResultSet results = statement.executeQuery(String.format("SELECT starboard_count FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next()) count = results.getInt("starboard_count");
                else count = null;
            }
            return count;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the starboard count for the guild "+guild.getId(), e);
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
            Database.LOG.error("Error while setting the starboard count for the guild "+guild.getId(), e);
        }
    }

    public boolean addPrefix(Guild guild, String prefix)
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
                    return true;
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateString("prefixes", new JSONArray().put(prefix).toString());
                    results.insertRow();
                    return true;
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while adding a prefix for the guild "+guild.getId(), e);
            return false;
        }
    }

    public boolean prefixExists(Guild guild, String prefix)
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

                    if(prefixes == null) return false;
                    else
                    {
                        array = new JSONArray(prefixes);

                        for(Object p : array)
                            return p.toString().equals(prefix);
                    }
                }
                else return false;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while checking if a prefix exists for the guild "+guild.getId(), e);
            return false;
        }
        return false;
    }

    public boolean removePrefix(Guild guild, String prefix)
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
                                results.updateString("prefixes", array.length()<0 ? null : array.toString());
                                results.updateRow();
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while removing a prefix for the guild "+guild.getId(), e);
            return false;
        }
    }

    public List<Role> getRolemeRoles(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            String rolemeRoles;
            JSONArray array;
            List<Role> roles = new LinkedList<>();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, roleme_roles FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    rolemeRoles = results.getString("roleme_roles");

                    if(rolemeRoles == null) return roles;
                    else
                    {
                        array = new JSONArray(rolemeRoles);
                        Role role;

                        for(Object r : array)
                        {
                            role = guild.getRoleById(r.toString());

                            if(!(role==null))
                                roles.add(role);
                        }

                        return roles;
                    }

                }
                else return roles;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the roleme roles for the guild "+guild.getId(), e);
            return null;
        }
    }

    public boolean addRolemeRole(Guild guild, Role role)
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

                    if(roles==null) array = new JSONArray().put(role.getId());
                    else array = new JSONArray(roles).put(role.getId());

                    results.updateString("roleme_roles", array.toString());
                    results.updateRow();
                    return true;
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateString("roleme_roles", new JSONArray().put(role.getId()).toString());
                    results.insertRow();
                    return true;
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while adding a roleme role for the guild "+guild.getId(), e);
            return false;
        }
    }

    public boolean removeRolemeRole(Guild guild, Role role)
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
                                results.updateString("roleme_roles", array.length()<0 ? null : array.toString());
                                results.updateRow();
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while removing a roleme role from the guild "+guild.getId(), e);
            return false;
        }
    }

    public Role getMutedRole(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            Role role;
            try(ResultSet results = statement.executeQuery(String.format("SELECT muted_role FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next()) role = guild.getRoleById(results.getLong("muted_role"));
                else role = null;
            }
            return role;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the muted role for the guild "+guild.getId(), e);
            return null;
        }
    }

    public boolean setMutedRole(Guild guild, Role role)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, muted_role_id FROM GUILD_SETTINGS WHERE guild_id = %s", guild.getId())))
            {
                if(results.next())
                {
                    results.updateLong("muted_role_id", role==null?null:role.getIdLong());
                    results.updateRow();
                    return true;
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild.getIdLong());
                    results.updateLong("muted_role_id", role==null?null:role.getIdLong());
                    results.insertRow();
                    return true;
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the muted role for the guild "+guild.getId(), e);
            return false;
        }
    }

    public int getBanDeleteDays(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            int days;
            try(ResultSet results = statement.executeQuery(String.format("SELECT muted_role FROM GUILD_SETTINGS WHERE GUILD_ID = %s", guild.getId())))
            {
                if(results.next())
                    days = results.getInt("ban_delete_days");
                else
                    days = 0;
            }
            return days;
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the ban delete days number for the guild "+guild.getId(), e);
            return 0;
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
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while settings the ban delete days number for the guild "+guild.getId(), e);
        }
    }
}

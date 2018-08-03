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

import ch.qos.logback.classic.Logger;
import me.artuto.endless.Bot;
import me.artuto.endless.Endless;
import me.artuto.endless.core.entities.LocalTag;
import me.artuto.endless.core.entities.Tag;
import me.artuto.endless.core.entities.impl.EndlessCoreImpl;
import me.artuto.endless.core.entities.impl.GlobalTagImpl;
import me.artuto.endless.core.entities.impl.GuildSettingsImpl;
import me.artuto.endless.core.entities.impl.LocalTagImpl;
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TagDataManager
{
    private final Bot bot;
    private final Connection connection;
    private final Logger LOG = Endless.getLog(TagDataManager.class);

    public TagDataManager(Bot bot)
    {
        this.bot = bot;
        this.connection = bot.db.getConnection();
    }

    private Tag getGlobalTag(String name)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAGS WHERE name = ? AND guild IS NULL",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, name);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    return bot.endlessBuilder.entityBuilder.createTag(results);
                else
                    return null;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting a global tag.", e);
            return null;
        }
    }

    private LocalTag getLocalTag(long guild, String name)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAGS WHERE name = ? AND guild = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, name);
            statement.setLong(2, guild);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    return bot.endlessBuilder.entityBuilder.createLocalTag(results);
                else
                    return null;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting a local tag.", e);
            return null;
        }
    }

    public Tag getGlobalTagById(long id)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAGS WHERE id = ? AND guild IS NULL",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, id);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    return bot.endlessBuilder.entityBuilder.createTag(results);
                else
                    return null;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting a global tag.", e);
            return null;
        }
    }

    public LocalTag getLocalTagById(long guild, long id)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAGS WHERE id = ? AND guild = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, id);
            statement.setLong(2, guild);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    return bot.endlessBuilder.entityBuilder.createLocalTag(results);
                else
                    return null;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting a local tag.", e);
            return null;
        }
    }

    public boolean isImported(long guild, String id)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT guild_id, imported_tags FROM GUILD_SETTINGS WHERE guild_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, guild);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                {
                    String array = results.getString("imported_tags");
                    if(array==null)
                        return false;
                    else
                        return new JSONArray(array).toList().contains(id);
                }
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while checking if a tag is imported. Guild ID: {}", guild, e);
            return false;
        }
        return false;
    }

    public List<Tag> getGlobalTags()
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAGS WHERE guild IS NULL",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                List<Tag> list = new LinkedList<>();
                while(results.next())
                    list.add(bot.endlessBuilder.entityBuilder.createTag(results));
                return list;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting the list of global tags.", e);
            return Collections.emptyList();
        }
    }

    public List<LocalTag> getLocalTagsForGuild(Guild guild)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAGS WHERE guild = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, guild.getIdLong());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                List<LocalTag> list = new LinkedList<>();
                while(results.next())
                    list.add(bot.endlessBuilder.entityBuilder.createLocalTag(results));
                return list;
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while getting the list of local tags for guild {}.", guild.getId(), e);
            return Collections.emptyList();
        }
    }

    public void createGlobalTag(long owner, String content, String name)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAGS",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                results.moveToInsertRow();
                results.updateString("name", name);
                results.updateString("content", content);
                results.updateLong("owner", owner);
                results.insertRow();
                Tag tag = getGlobalTag(name);
                ((EndlessCoreImpl)bot.endless).addGlobalTag(tag);
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while adding a global tag.", e);
        }
    }

    public void createLocalTag(long guild, long owner, String content, String name)
    {
        createLocalTag(false, guild, owner, content, name);
    }

    public void createLocalTag(boolean overriden, long guild, long owner, String content, String name)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAGS",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                results.moveToInsertRow();
                results.updateString("name", name);
                results.updateString("content", content);
                results.updateLong("guild", guild);
                results.updateLong("owner", owner);
                results.updateBoolean("overriden", overriden);
                results.insertRow();
                Tag tag = getLocalTag(guild, name);
                ((EndlessCoreImpl)bot.endless).addLocalTag((LocalTag)tag);
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while adding a local tag for guild {}.", guild, e);
        }
    }

    public void deleteGlobalTag(String name)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAGS WHERE name = ? AND guild IS NULL",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, name);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    results.deleteRow();
            }
            ((EndlessCoreImpl)bot.endless).removeGlobalTag(name);
        }
        catch(SQLException e)
        {
            LOG.error("Error while deleting a global tag.", e);
        }
    }

    public void deleteLocalTag(long guild, String name)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAGS WHERE name = ? AND guild = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, name);
            statement.setLong(2, guild);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                    results.deleteRow();
            }
            ((EndlessCoreImpl)bot.endless).removeLocalTag(guild, name);
        }
        catch(SQLException e)
        {
            LOG.error("Error while deleting a local tag for guild {}.", guild, e);
        }
    }

    public void importTag(long guild, Tag tag)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM GUILD_SETTINGS WHERE guild_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, guild);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                {
                    String importedTags = results.getString("imported_tags");
                    JSONArray array;

                    if(importedTags==null)
                        array = new JSONArray().put(String.valueOf(tag.getId()));
                    else
                        array = new JSONArray(importedTags).put(String.valueOf(tag.getId()));

                    results.updateString("imported_tags", array.toString());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("guild_id", guild);
                    results.updateString("imported_tags", new JSONArray().put(String.valueOf(tag.getId())).toString());
                    results.insertRow();
                }
                GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettingsById(guild);
                settings.addImportedTag(tag);
                if(bot.endless.getGuildSettingsById(guild).isDefault())
                    ((EndlessCoreImpl)bot.endless).addSettings(settings.getGuild(), settings);
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while importing a tag. Guild ID: {}", guild, e);
        }
    }

    public void unImportTag(long guild, Tag tag)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM GUILD_SETTINGS WHERE guild_id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setLong(1, guild);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                {
                    String importedTags = results.getString("imported_tags");
                    JSONArray array;

                    if(!(importedTags==null))
                    {
                        array = new JSONArray(importedTags);
                        for(int i = 0; i<array.length(); i++)
                        {
                            if((array.get(i)).equals(String.valueOf(tag.getId())))
                            {
                                array.remove(i);
                                if(array.length()<0)
                                {
                                    results.updateNull("imported_tags");
                                    results.updateRow();
                                }
                                else
                                {
                                    results.updateString("imported_tags", array.toString());
                                    results.updateRow();
                                }
                            }
                        }
                        GuildSettingsImpl settings = (GuildSettingsImpl)bot.endless.getGuildSettingsById(guild);
                        settings.removeImportedTag(tag);
                    }
                }
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while removing a imported tag for the guild {}", guild, e);
        }
    }

    public void updateGlobalTagContent(String name, String newContent)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAGS WHERE name = ? AND guild IS NULL",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, name);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                {
                    results.updateString("content", newContent);
                    results.updateRow();
                    ((GlobalTagImpl)bot.endless.getGlobalTag(name)).setContent(newContent);
                }
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while editing a global tag.", e);
        }
    }

    public void updateLocalTagContent(long guild, String name, String newContent)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAGS WHERE name = ? AND guild = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, name);
            statement.setLong(2, guild);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                {
                    results.updateString("content", newContent);
                    results.updateRow();
                    ((LocalTagImpl)bot.endless.getLocalTag(guild, name)).setContent(newContent);
                }
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while editing a local tag.", e);
        }
    }
}

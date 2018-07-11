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
import me.artuto.endless.core.entities.GlobalTag;
import me.artuto.endless.core.entities.LocalTag;
import me.artuto.endless.core.entities.Tag;
import me.artuto.endless.core.entities.impl.*;
import me.artuto.endless.storage.data.Database;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONArray;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TagDataManager
{
    private final Bot bot;
    private final Connection connection;

    public TagDataManager(Bot bot)
    {
        this.bot = bot;
        this.connection = bot.db.getConnection();
    }

    private GlobalTag getGlobalTag(String name)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GLOBAL_TAGS WHERE name = \"%s\"", name)))
            {
                if(results.next())
                    return new GlobalTagImpl(results.getLong("owner"), results.getInt("id"),
                            results.getString("content"), results.getString("name"));
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting a global tag.", e);
            return null;
        }
        return null;
    }

    private LocalTag getLocalTag(long guild, String name)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM LOCAL_TAGS WHERE name = \"%s\" AND guild = %s", name, guild)))
            {
                if(results.next())
                    return new LocalTagImpl(results.getBoolean("overriden"), results.getLong("guild"),
                            results.getLong("owner"), results.getInt("id"),
                            results.getString("content"), results.getString("name"));
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting a global tag.", e);
            return null;
        }
        return null;
    }

    public boolean isImported(long guild, String id)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, imported_tags FROM GUILD_SETTINGS WHERE guild_id = \"%s\"", guild)))
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
            Database.LOG.error("Error while importing a tag. Guild ID: "+guild, e);
            return false;
        }
        return false;
    }

    public List<GlobalTag> getGlobalTags()
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery("SELECT * FROM GLOBAL_TAGS"))
            {
                List<GlobalTag> list = new LinkedList<>();
                while(results.next())
                {
                    list.add(new GlobalTagImpl(results.getLong("owner"), results.getInt("id"),
                        results.getString("content"), results.getString("name")));
                }
                return list;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the list of global tags.", e);
            return Collections.emptyList();
        }
    }

    public List<LocalTag> getLocalTagsForGuild(Guild guild)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM LOCAL_TAGS WHERE guild = %s", guild.getIdLong())))
            {
                List<LocalTag> list = new LinkedList<>();
                while(results.next())
                {
                    list.add(new LocalTagImpl(results.getBoolean("overriden"), results.getLong("guild"),
                            results.getLong("owner"), results.getInt("id"),
                            results.getString("content"), results.getString("name")));
                }
                return list;
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while getting the list of global tags.", e);
            return Collections.emptyList();
        }
    }

    public void createGlobalTag(long guild, long owner, String content, String name)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GLOBAL_TAGS WHERE name = \"%s\"", name)))
            {
                results.moveToInsertRow();
                results.updateString("name", name);
                results.updateString("content", content);
                results.updateLong("owner", owner);
                results.insertRow();
                Tag tag = getGlobalTag(name);
                ((EndlessCoreImpl)bot.endless.getShard(bot.shardManager.getGuildById(guild).getJDA())).addGlobalTag((GlobalTag)tag);
                ((EndlessShardedImpl)bot.endless).addGlobalTag((GlobalTag)tag);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while adding a global tag.", e);
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
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM LOCAL_TAGS WHERE name = \"%s\"", name)))
            {
                results.moveToInsertRow();
                results.updateString("name", name);
                results.updateString("content", content);
                results.updateLong("guild", guild);
                results.updateLong("owner", owner);
                results.updateBoolean("overriden", overriden);
                results.insertRow();
                Tag tag = getLocalTag(guild, name);
                ((EndlessCoreImpl)bot.endless.getShard(bot.shardManager.getGuildById(guild).getJDA())).addLocalTag((LocalTag)tag);
                ((EndlessShardedImpl)bot.endless).addLocalTag((LocalTag)tag);
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while adding a global tag.", e);
        }
    }

    public void deleteGlobalTag(JDA jda, String name)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            statement.executeUpdate(String.format("DELETE FROM GLOBAL_TAGS WHERE name = \"%s\"", name));
            ((EndlessCoreImpl)bot.endless.getShard(jda)).removeGlobalTag(name);
            ((EndlessShardedImpl)bot.endless).removeGlobalTag(name);
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while deleting a global tag.", e);
        }
    }

    public void deleteLocalTag(long guild, String name)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            statement.executeUpdate(String.format("DELETE FROM LOCAL_TAGS WHERE name = \"%s\" AND guild = %s", name, guild));
            ((EndlessCoreImpl)bot.endless.getShard(bot.shardManager.getGuildById(guild).getJDA())).removeLocalTag(guild, name);
            ((EndlessShardedImpl)bot.endless).removeLocalTag(guild, name);
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while deleting a local tag.", e);
        }
    }

    public void importTag(long guild, Tag tag)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, imported_tags FROM GUILD_SETTINGS WHERE guild_id = \"%s\"", guild)))
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
                {
                    ((EndlessCoreImpl)bot.endless.getShard(settings.getGuild().getJDA())).addSettings(settings.getGuild(), settings);
                    ((EndlessShardedImpl)bot.endless).addSettings(settings.getGuild(), settings);
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while importing a tag. Guild ID: "+guild, e);
        }
    }

    public void unimportTag(long guild, Tag tag)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT guild_id, imported_tags FROM GUILD_SETTINGS WHERE guild_id = %s", guild)))
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
                        if(bot.endless.getGuildSettingsById(guild).isDefault())
                        {
                            ((EndlessCoreImpl)bot.endless.getShard(settings.getGuild().getJDA())).addSettings(settings.getGuild(), settings);
                            ((EndlessShardedImpl)bot.endless).addSettings(settings.getGuild(), settings);
                        }
                    }
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while removing a prefix for the guild "+guild, e);
        }
    }

    public void updateGlobalTagContent(JDA jda, String name, String newContent)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM GLOBAL_TAGS WHERE name = \"%s\"", name)))
            {
                if(results.next())
                {
                    results.updateString("content", newContent);
                    results.updateRow();
                    ((GlobalTagImpl)bot.endless.getShard(jda).getGlobalTag(name)).setContent(newContent);
                    ((GlobalTagImpl)bot.endless.getGlobalTag(name)).setContent(newContent);
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while editing a global tag.", e);
        }
    }

    public void updateLocalTagContent(JDA jda, long guild, String name, String newContent)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM LOCAL_TAGS WHERE name = \"%s\" AND guild = %s", name, guild)))
            {
                if(results.next())
                {
                    results.updateString("content", newContent);
                    results.updateRow();
                    ((LocalTagImpl)bot.endless.getShard(jda).getLocalTag(guild, name)).setContent(newContent);
                    ((LocalTagImpl)bot.endless.getLocalTag(guild, name)).setContent(newContent);
                }
            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while editing a local tag.", e);
        }
    }
}

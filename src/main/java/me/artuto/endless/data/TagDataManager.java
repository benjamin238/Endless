package me.artuto.endless.data;

import net.dv8tion.jda.core.utils.SimpleLog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class TagDataManager
{
    private final Connection connection;
    private final SimpleLog LOG = SimpleLog.getLog("MySQL Database");

    public TagDataManager(DatabaseManager db)
    {
        this.connection = db.getConnection();
    }

    public void addTag(String name, String content, Long owner)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT tag_id, tag_name, tag_content, tag_owner FROM TAGS WHERE tag_name = \"%s\"", name)))
            {
                if(results.next())
                {
                    results.updateString("tag_content", content);
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateString("tag_name", name);
                    results.updateString("tag_content", content);
                    results.updateLong("tag_owner", owner);
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
        }
    }

    public void editTag(String name, String content)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT tag_id, tag_name, tag_content FROM TAGS WHERE tag_name = \"%s\"", name)))
            {
                if(results.next())
                {
                    results.updateString("tag_content", content);
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateString("tag_content", content);
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
        }
    }

    public String getTagContent(String name)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            String content;
            try (ResultSet results = statement.executeQuery(String.format("SELECT tag_id, tag_name, tag_content FROM TAGS WHERE TAG_NAME = \"%s\"", name.trim())))
            {
                if(results.next())
                    content = results.getString("tag_content");
                else content=null;
            }
            return content;
        }
        catch(SQLException e)
        {
            LOG.warn(e);
            return null;
        }
    }

    public void removeTag(String name)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM TAGS WHERE TAG_NAME = \"%s\"", name.trim())))
            {
                if(results.next())
                {
                    results.updateInt("tag_id", 0);
                    results.updateRow();
                }
            }
            statement.executeUpdate(String.format("DELETE FROM TAGS WHERE tag_name = \"%s\"", name));
            statement.closeOnCompletion();
        }
        catch(SQLException e)
        {
            LOG.warn(e);
        }
    }

    public Long getTagOwner(String name)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT tag_id, tag_name, tag_owner FROM TAGS WHERE tag_name = \"%s\"", name)))
            {
                if(results.next())
                    return results.getLong("tag_owner");
                else
                    return null;
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
            return null;
        }
    }

    public void importTag(String name)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT tag_id, tag_name, imported FROM TAGS WHERE tag_name = \"%s\"", name)))
            {
                if(results.next())
                {
                    results.updateBoolean("imported", true);
                    results.updateRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
        }
    }

    public void unImportTag(String name)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT tag_id, tag_name, imported FROM TAGS WHERE tag_name = \"%s\"", name)))
            {
                if(results.next())
                {
                    results.updateBoolean("imported", false);
                    results.updateRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
        }
    }

    public List<String> getImportedTags()
    {
        List<String> names = new LinkedList<String>();

        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery("SELECT tag_id, tag_name, imported FROM TAGS WHERE imported = true"))
            {
                while (results.next())
                    names.add(results.getString("tag_name"));

                return names;
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
            return null;
        }
    }
}
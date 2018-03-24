package me.artuto.endless.data;

import me.artuto.endless.entities.ImportedTag;
import me.artuto.endless.entities.impl.ImportedTagImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class TagDataManager
{
    private final Connection connection;
    private final Logger LOG = LoggerFactory.getLogger("MySQL Database");

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
                results.moveToInsertRow();
                results.updateString("tag_name", name);
                results.updateString("tag_content", content);
                results.updateLong("tag_owner", owner);
                results.insertRow();
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }

    public void editTag(String name, String content)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM TAGS WHERE tag_name = \"%s\"", name)))
            {
                if(results.next())
                {
                     results.updateString("tag_content", content);
                results.updateRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }

    public String getTagContent(String name)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            String content;
            try (ResultSet results = statement.executeQuery(String.format("SELECT tag_id, tag_name, tag_content FROM TAGS WHERE TAG_NAME = \"%s\"", name)))
            {
                if(results.next())
                    content = results.getString("tag_content");
                else content=null;
            }
            return content;
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return null;
        }
    }

    public void removeTag(String name)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM TAGS WHERE TAG_NAME = \"%s\"", name)))
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
            LOG.warn(e.toString());
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
            LOG.warn(e.toString());
            return null;
        }
    }

    public boolean isTagImported(String name, Long guild)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT tag_id, tag_name FROM TAGS WHERE tag_name = \"imported-%s:%s\"", guild, name)))
            {
                return results.next();
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return false;
        }
    }

    public void importTag(String name, String content, Long owner, Long guild)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery(String.format("SELECT tag_id, tag_name, tag_content, tag_owner FROM TAGS WHERE tag_name = \"%s\"", name)))
            {
                if(results.next())
                {
                    results.moveToInsertRow();
                    results.updateString("tag_name", "imported-"+guild+":"+name);
                    results.updateString("tag_content", content);
                    results.updateLong("tag_owner", owner);
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }

    public void unImportTag(String name, Long guild)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            try(ResultSet results = statement.executeQuery(String.format("SELECT tag_id, tag_name, imported FROM TAGS WHERE tag_name = \"imported-%s:%s\"", guild, name)))
            {
                if(results.next())
                {
                    results.updateInt("tag_id", 0);
                    results.updateRow();
                }
            }
            statement.executeUpdate(String.format("DELETE FROM TAGS WHERE tag_name = \"imported-%s:%s\"", guild, name));
            statement.closeOnCompletion();
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
        }
    }

    public List<ImportedTag> getImportedTags()
    {
        List<ImportedTag> tags = new LinkedList<>();

        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery("SELECT tag_id, tag_name, tag_content, tag_owner FROM TAGS WHERE tag_name LIKE \'imported-%\'"))
            {
                while(results.next())
                    tags.add(new ImportedTagImpl(results.getLong("tag_id"),
                            results.getString("tag_name"),
                            results.getString("tag_content"),
                            results.getLong("tag_owner"),
                            Long.valueOf(results.getString("tag_name").split(":")[0].split("-")[1])));

                return tags.isEmpty()?null:tags;
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return null;
        }
    }

    public List<ImportedTag> getImportedTagsForGuild(Long guild)
    {
        List<ImportedTag> tags = new LinkedList<>();

        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            try(ResultSet results = statement.executeQuery("SELECT tag_id, tag_name, tag_content, tag_owner FROM TAGS WHERE tag_name LIKE \'imported-"+guild+":%\'"))
            {
                while(results.next())
                    tags.add(new ImportedTagImpl(results.getLong("tag_id"),
                            results.getString("tag_name"),
                            results.getString("tag_content"),
                            results.getLong("tag_owner"),
                            Long.valueOf(results.getString("tag_name").split(":")[0].split("-")[1])));

                return tags.isEmpty()?null:tags;
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e.toString());
            return null;
        }
    }
}

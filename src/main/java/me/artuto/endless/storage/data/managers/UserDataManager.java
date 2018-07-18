package me.artuto.endless.storage.data.managers;

import me.artuto.endless.Bot;
import me.artuto.endless.core.entities.impl.EndlessCoreImpl;
import me.artuto.endless.core.entities.impl.GuildSettingsImpl;
import me.artuto.endless.storage.data.Database;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONArray;

import java.sql.*;

public class UserDataManager
{
    private final Bot bot;
    private final Connection connection;

    public UserDataManager(Bot bot)
    {
        this.bot = bot;
        this.connection = bot.db.getConnection();
    }

    public void addHighlightWord(Guild guild, String word, User user)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM HIGHLIGHTS WHERE user_id = ? AND guild_id = ?");
            statement.setLong(1, user.getIdLong());
            statement.setLong(2, guild.getIdLong());
            statement.closeOnCompletion();
            String words;
            JSONArray array;

            try(ResultSet results = statement.executeQuery())
            {
                if(results.next())
                {
                    words = results.getString("highlight_words");
                    if(words==null)
                        array = new JSONArray().put(word);
                    else
                        array = new JSONArray(words).put(word);

                    results.updateString("highlight_words", array.toString());
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("user_id", user.getIdLong());
                    results.updateString("highlight_words", new JSONArray().put(word).toString());
                    results.insertRow();
                }

            }
        }
        catch(SQLException e)
        {
            Database.LOG.error("Error while setting the modlog channel for the guild "+guild.getId(), e);
        }
    }
}

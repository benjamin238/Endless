package me.artuto.endless.storage.data.managers;

import ch.qos.logback.classic.Logger;
import me.artuto.endless.Bot;
import me.artuto.endless.Endless;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONArray;

import java.sql.*;

public class UserDataManager
{
    private final Bot bot;
    private final Connection connection;
    private final Logger LOG = Endless.getLog(UserDataManager.class);

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
            LOG.error("Error while setting the modlog channel for the guild {}", guild.getId(), e);
        }
    }
}

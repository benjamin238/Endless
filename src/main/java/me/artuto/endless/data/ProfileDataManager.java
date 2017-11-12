package me.artuto.endless.data;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProfileDataManager
{
    private final Connection connection;
    private final SimpleLog LOG = SimpleLog.getLog("MySQL Database");
    private final Profile DEFAULT = new Profile("", 0,"", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "");

    public ProfileDataManager(DatabaseManager db)
    {
        this.connection = db.getConnection();
    }

    public Profile getProfile(User user)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
            Profile p;

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM PROFILES WHERE USER_ID = %s", user.getIdLong())))
            {
                if(results.next())
                {
                    p = new Profile(results.getString("timezone"),
                            results.getInt("donated_amount"),
                            results.getString("twitter"),
                            results.getString("steam"),
                            results.getString("wii"),
                            results.getString("nnid"),
                            results.getString("xboxlive"),
                            results.getString("psn"),
                            results.getString("3ds"),
                            results.getString("skype"),
                            results.getString("youtube"),
                            results.getString("about"),
                            results.getString("twitch"),
                            results.getString("minecraft"),
                            results.getString("email"),
                            results.getString("lol"),
                            results.getString("wow"),
                            results.getString("battle"),
                            results.getString("splatoon"),
                            results.getString("mkwii"),
                            results.getString("reddit"));
                }
                else
                    p = DEFAULT;
            }
            return p;
        }
        catch(SQLException e)
        {
            LOG.warn(e);
            return DEFAULT;
        }
    }

    public boolean hasAProfile(User user)
    {
        try
        {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT * FROM PROFILES WHERE USER_ID = %s", user.getId())))
            {
                return results.next();
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
            return false;
        }
    }

    public void setTimezone(User user, String zone)
    {
        try
        {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery(String.format("SELECT user_id, timezone FROM PROFILES WHERE timezone = %s", user.getId())))
            {
                if(results.next())
                {
                    results.updateString("timezone", zone);
                    results.updateRow();
                }
                else
                {
                    results.moveToInsertRow();
                    results.updateLong("user_id", user.getIdLong());
                    results.updateString("timezone", zone);
                    results.insertRow();
                }
            }
        }
        catch(SQLException e)
        {
            LOG.warn(e);
        }
    }

    public class Profile
    {
        public final String timezone;
        final int donatedAmount;
        final String twitter;
        final String steam;
        final String wii;
        final String nnid;
        final String xboxLive;
        final String psn;
        final String threeds;
        final String skype;
        final String youtube;
        final String about;
        final String twitch;
        final String minecraft;
        final String email;
        final String lol;
        final String wow;
        final String battle;
        final String splatoon;
        final String mkwii;
        final String reddit;

        private Profile(String timezone, int donatedAmount, String twitter, String steam, String wii, String nnid, String xboxLive, String psn, String threeds, String skype,
                        String youtube, String about, String twitch, String minecraft, String email, String lol, String wow, String battle, String splatoon, String mkwii, String reddit)
        {
            this.timezone = timezone;
            this.donatedAmount = donatedAmount;
            this.twitter = twitter;
            this.steam = steam;
            this.wii = wii;
            this.nnid = nnid;
            this.xboxLive = xboxLive;
            this.psn = psn;
            this.threeds = threeds;
            this.skype = skype;
            this.youtube = youtube;
            this.about = about;
            this.twitch = twitch;
            this.minecraft = minecraft;
            this.email = email;
            this.lol = lol;
            this.wow = wow;
            this.battle = battle;
            this.splatoon = splatoon;
            this.mkwii = mkwii;
            this.reddit = reddit;
        }
    }
}

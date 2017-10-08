package me.artuto.endless.utils;

import me.artuto.endless.data.DatabaseManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class GuildUtils
{
    private static DatabaseManager db;

    public GuildUtils(DatabaseManager db)
    {
        this.db = db;
    }

    public static void leaveBadGuilds(JDA jda)
    {
        jda.getGuilds().stream().filter(g -> {
            if(db.hasSettings(g))
                return false;

            long botCount = g.getMembers().stream().map(m -> m.getUser()).filter(u -> u.isBot()).count();
            if(botCount>20 && ((double)botCount/g.getMembers().size())>.50)
                return true;
            return false;
        }).forEach(g -> g.leave().queue());
    }

    public static String checkBadGuild(Guild guild)
    {
        long botCount = guild.getMembers().stream().map(m -> m.getUser()).filter(u -> u.isBot()).count();

        if(db.hasSettings(guild))
            return "STAY";


        if(botCount>20 && ((double)botCount/guild.getMembers().size())>.65)
        {
            guild.leave().queue();
            return "LEFT: BOTS";
        }
        else
        {
            return "STAY";
        }
    }
}

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
        if(isBadGuild(guild))
        {
            String msg = "Hey! I'm leaving this guild (**"+guild.getName()+"**) because I won't allow Bot Guilds, this means you have a lot of bots compared to the real user count.";
            TextChannel tc = FinderUtil.getDefaultChannel(guild);
            tc.sendMessage(msg).queue(null, (e) -> guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(msg).queue()));
            guild.leave().queue();
            return "LEFT: BOTS";
        }
        else
        {
            return "STAY";
        }
    }

    public static boolean isBadGuild(Guild guild)
    {
        long botCount = guild.getMembers().stream().map(m -> m.getUser()).filter(u -> u.isBot()).count();

        if(db.hasSettings(guild))
            return false;
        else if(botCount>20 && ((double)botCount/guild.getMembers().size())>.65)
            return true;
        else
            return false;
    }
}

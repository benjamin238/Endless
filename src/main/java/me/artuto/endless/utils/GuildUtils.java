package me.artuto.endless.utils;

import me.artuto.endless.data.DatabaseManager;
import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class GuildUtils
{
    private static Config config;
    private static DatabaseManager db;

    public GuildUtils(Config config, DatabaseManager db)
    {
        this.db = db;
        this.config = config;
    }

    public static void leaveBadGuilds(JDA jda)
    {
        User owner = jda.getUserById(config.getOwnerId());
        jda.getGuilds().stream().filter(g -> {
            if(db.hasSettings(g))
                return false;

            long botCount = g.getMembers().stream().map(m -> m.getUser()).filter(u -> u.isBot()).count();
            if(botCount>20 && ((double)botCount/g.getMembers().size())>.50)
                return true;

            if(isABotListGuild(g))
            {
                jda.getUserById("264499432538505217").openPrivateChannel().queue(s -> s.sendMessage("**"+owner.getName()+"#"+owner.getDiscriminator()+"** has a copy of Endless at "+g.getName()));
                return true;
            }

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
            return "STAY";
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

    public static boolean isABotListGuild(Guild guild)
    {
        if(guild.getId().equals("110373943822540800") || guild.getId().equals("264445053596991498") && !(config.getOwnerId()==264499432538505217L))
            return true;
        else
            return false;
    }
}

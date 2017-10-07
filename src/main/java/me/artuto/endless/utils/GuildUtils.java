package me.artuto.endless.utils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class GuildUtils
{
    public static void leaveBadGuilds(JDA jda)
    {
        jda.getGuilds().stream().filter(g -> {
            long botCount = g.getMembers().stream().map(m -> m.getUser()).filter(u -> u.isBot()).count();
            if(botCount>20 && ((double)botCount/g.getMembers().size())>.65)
                return true;
            return false;
        }).forEach(g -> g.leave().queue());
    }

    public static boolean checkBadGuild(Guild guild)
    {
        long botCount = guild.getMembers().stream().map(m -> m.getUser()).filter(u -> u.isBot()).count();

        for(TextChannel tcs : guild.getTextChannels())
        {
            if(tcs.isNSFW())
            {
                guild.leave().queue();
                return true;
            }
        }

        if(botCount>20 && ((double)botCount/guild.getMembers().size())>.65)
        {
            guild.leave().queue();
            return true;
        }
        else
        {
            return false;
        }
    }
}

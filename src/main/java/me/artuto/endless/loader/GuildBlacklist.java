package me.artuto.endless.loader;

import me.artuto.endless.utils.FinderUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GuildBlacklist extends ListenerAdapter
{
    private final SimpleLog LOG = SimpleLog.getLog("Blacklisted Guilds");

    @Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        String guildId = event.getGuild().getId();
        List<String> lines = null;
        Guild guild = event.getGuild();
        TextChannel tc = FinderUtil.getDefaultChannel(guild);

        try
        {
            lines = Files.readAllLines(Paths.get("data/blacklisted_guilds.txt"));
        }
        catch(IOException e)
        {
            LOG.warn("Failed to load blacklisted guilds: "+e);
        }

        if(!(lines==null) || !(lines.isEmpty()))
        {
            if(lines.contains(guildId))
            {
                LOG.info("Joined Blacklisted Guild: "+guild.getName()+" (ID: "+guild.getId()+")");
                tc.sendMessage("I'm sorry, but the owner of this bot has blocked your guild from joining, if you want to know the reason or get un-blacklisted contact the owner.").complete();
                event.getGuild().leave().complete();
            }
        }
    }

}

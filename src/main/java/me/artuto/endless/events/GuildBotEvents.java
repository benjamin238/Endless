package me.artuto.endless.events;

import me.artuto.endless.loader.Config;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;

public class GuildBotEvents extends ListenerAdapter
{
    private final Config config;

    public GuildBotEvents(Config config)
    {
        this.config = config;
    }

    private String getReason(Guild guild)
    {
        String reason = GuildUtils.checkBadGuild(guild);

        switch (reason)
        {

            case "LEFT: BOTS":
                return "Too many bots!";
            default:
                return null;
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        Guild guild = event.getGuild();
        User owner = guild.getOwner().getUser();
        SimpleLog.getLog("Logger").info("[GUILD JOIN]: "+guild.getName()+" (ID: "+guild.getId()+")\n");
        long botCount = guild.getMembers().stream().map(m -> m.getUser()).filter(u -> u.isBot()).count();
        long userCount = guild.getMembers().stream().map(m -> m.getUser()).filter(u -> !(u.isBot())).count();
        long totalCount = guild.getMembers().size();
        GuildUtils.checkBadGuild(guild);
        TextChannel tc = event.getJDA().getTextChannelById(config.getBotlogChannelId());

        if(config.isBotlogEnabled() && !(tc==null) && tc.canTalk())
        {
            tc.sendMessage(":inbox_tray: `[New Guild]:` "+guild.getName()+" (ID: "+guild.getId()+")\n" +
                    "`[Owner]:` **"+owner.getName()+"**#**"+owner.getDiscriminator()+"** (ID: "+owner.getId()+"\n" +
                    "`[Members]:` **"+userCount+"** Bots: **"+botCount+"** Total Count: **"+totalCount+"**\n").queue();
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event)
    {
        Guild guild = event.getGuild();
        User owner = guild.getOwner().getUser();
        SimpleLog.getLog("Logger").info("[GUILD LEFT]: "+guild.getName()+" (ID: "+guild.getId()+")\n");
        long botCount = guild.getMembers().stream().map(m -> m.getUser()).filter(u -> u.isBot()).count();
        long userCount = guild.getMembers().stream().map(m -> m.getUser()).filter(u -> !(u.isBot())).count();
        long totalCount = guild.getMembers().size();
        TextChannel tc = event.getJDA().getTextChannelById(config.getBotlogChannelId());
        String reason = getReason(guild);

        if(config.isBotlogEnabled() && !(tc==null) && tc.canTalk())
        {
            StringBuilder builder = new StringBuilder().append(":outbox_tray: `[Left Guild]:` "+guild.getName()+" (ID: "+guild.getId()+")\n" +
                    "`[Owner]:` **"+owner.getName()+"**#**"+owner.getDiscriminator()+"** (ID: "+owner.getId()+"\n" +
                    "`[Members]:` **"+userCount+"** Bots: **"+botCount+"** Total Count: **"+totalCount+"**\n");

            if(!(reason.isEmpty()))
                builder.append("`[Reason]:` "+reason);

            tc.sendMessage(builder.toString()).queue();
        }
    }

}

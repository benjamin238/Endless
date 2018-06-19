/*
 * Copyright (C) 2017-2018 Artuto
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.artuto.endless.logging;

import me.artuto.endless.Bot;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import org.slf4j.LoggerFactory;

/**
 * @author Artuto
 */

public class BotLogging
{
    private final Bot bot;

    public BotLogging(Bot bot)
    {
        this.bot = bot;
    }

    public void logJoin(GuildJoinEvent event)
    {
        Guild guild = event.getGuild();
        User owner = guild.getOwner().getUser();
        long botCount = guild.getMembers().stream().map(Member::getUser).filter(User::isBot).count();
        long userCount = guild.getMembers().stream().map(Member::getUser).filter(u -> !(u.isBot())).count();
        long totalCount = guild.getMemberCache().size();
        TextChannel tc = event.getJDA().getTextChannelCache().getElementById(bot.config.getBotlogChannelId());

        if(!(bot.bdm.getBlacklist(guild.getIdLong())==null || bot.bdm.getBlacklist(owner.getIdLong())==null))
        {
            LoggerFactory.getLogger("Logging").info("[BLACKLISTED GUILD/OWNER JOIN]: "+guild.getName()+" (ID: "+guild.getId()+")\n" +
                    "Owner: "+owner.getName()+"#"+owner.getDiscriminator()+" (ID: "+owner.getId()+")");
            guild.leave().queue();
            return;
        }

        if(bot.config.isBotlogEnabled() && !(tc == null) && tc.canTalk())
        {
            tc.sendMessage(":inbox_tray: `[New Guild]:` "+guild.getName()+" (ID: "+guild.getId()+")\n"+"`[Owner]:` **"+owner.getName()+"**#**"+owner.getDiscriminator()+"** (ID: "+owner.getId()+"\n"+
                    "`[Members]:` Humans: **"+userCount+"** Bots: **"+botCount+"** Total Count: **"+totalCount+"**\n").queue();
            LoggerFactory.getLogger("Logging").info("[GUILD JOIN]: "+guild.getName()+" (ID: "+guild.getId()+")\n");
        }
    }

    public void logLeave(GuildLeaveEvent event)
    {
        Guild guild = event.getGuild();
        LoggerFactory.getLogger("Logging").info("[GUILD LEFT]: "+guild.getName()+" (ID: "+guild.getId()+")\n");
        TextChannel tc = event.getJDA().getTextChannelById(bot.config.getBotlogChannelId());

        if(bot.config.isBotlogEnabled() && !(tc == null) && tc.canTalk())
        {
            long botCount = guild.getMembers().stream().map(Member::getUser).filter(User::isBot).count();
            long userCount = guild.getMembers().stream().map(Member::getUser).filter(u -> !(u.isBot())).count();
            long totalCount = guild.getMembers().size();
            User owner = guild.getOwner().getUser();

            String msg = (":outbox_tray: `[Left Guild]:` ```"+guild.getName()+"``` (ID: "+guild.getId()+")\n"+
                    "`[Owner]:` **"+owner.getName()+"**#**"+owner.getDiscriminator()+"** (ID: "+owner.getId()+"\n"+
                    "`[Members]:` Humans: **"+userCount+"** Bots: **"+botCount+"** Total Count: **"+totalCount+"**");
            tc.sendMessage(msg).queue();
        }
    }
}

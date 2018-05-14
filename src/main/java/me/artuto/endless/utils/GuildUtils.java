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

package me.artuto.endless.utils;

import me.artuto.endless.Bot;
import me.artuto.endless.data.DatabaseManager;
import me.artuto.endless.entities.ParsedAuditLog;
import me.artuto.endless.entities.impl.ParsedAuditLogImpl;
import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.audit.AuditLogChange;
import net.dv8tion.jda.core.audit.AuditLogEntry;
import net.dv8tion.jda.core.audit.AuditLogKey;
import net.dv8tion.jda.core.entities.*;

public class GuildUtils
{
    private static Bot bot;

    public GuildUtils(Bot bot)
    {
        GuildUtils.bot = bot;
    }

    public static void leaveBadGuilds(JDA jda)
    {
        User owner = jda.getUserById(bot.config.getOwnerId());
        jda.getGuilds().stream().filter(g ->
        {
            if(bot.db.hasSettings(g)) return false;

            long botCount = g.getMembers().stream().map(Member::getUser).filter(User::isBot).count();
            if(botCount>20 && ((double) botCount/g.getMembers().size())>.50) return true;

            /**if(isABotListGuild(g))
             {
             jda.getUserById("264499432538505217").openPrivateChannel().queue(s -> s.sendMessage("**"+owner.getName()+"#"+owner.getDiscriminator()+"** has a copy of Endless at "+g.getName()));
             return true;
             }*/

            return false;
        }).forEach(g -> g.leave().queue());
    }

    public static String checkBadGuild(Guild guild)
    {
        if(isBadGuild(guild))
        {
            String msg = "Hey! I'm leaving this guild (**"+guild.getName()+"**) because I won't allow Bot Guilds, this means you have a lot of bots compared to the real user count.";
            TextChannel tc = FinderUtil.getDefaultChannel(guild);
            if(!(tc==null))
                tc.sendMessage(msg).queue(null, (e) -> guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage(msg).queue()));

            guild.leave().queue();
            return "LEFT: BOTS";
        }
        else return "STAY";
    }

    public static boolean isBadGuild(Guild guild)
    {
        long botCount = guild.getMembers().stream().map(Member::getUser).filter(User::isBot).count();

        if(bot.db.hasSettings(guild)) return false;
        else if(botCount>20 && ((double) botCount/guild.getMembers().size())>.65) return true;
        else return false;
    }

    public static Role getMutedRole(Guild guild)
    {
        return guild.getRolesByName("Muted", true).stream().findFirst().orElse(guild.getRoleById(bot.db.getSettings(guild).getMutedRole()));
    }

    public static ParsedAuditLog getAuditLog(AuditLogEntry entry, AuditLogKey key)
    {
        JDA jda = entry.getJDA();
        User author = entry.getUser();
        User target = jda.getUserById(entry.getTargetIdLong());
        AuditLogChange change = entry.getChangeByKey(key);

        return new ParsedAuditLogImpl(key, change.getNewValue(), change.getOldValue(), entry.getReason(), author, target);
    }

    /**public static boolean isABotListGuild(Guild guild)
     {
     if(guild.getId().equals("110373943822540800") || guild.getId().equals("264445053596991498") && !(config.getOwnerId()==264499432538505217L))
     return true;
     else
     return false;
     }*/
}

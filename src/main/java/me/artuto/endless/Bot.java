/*
 * Copyright (C) 2017 Artu
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

package me.artuto.endless;

import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Artu
 */

public class Bot extends ListenerAdapter
{
    private final Config config;
    private final JDA jda;

    public Bot(Config config, JDA jda)
    {
        this.config = config;
        this.jda = jda;
    }

    public List<Guild> getManagedGuildsForUser(Long id)
    {
        List<Guild> guilds = new LinkedList<>();
        User user = jda.getUserById(id);

        if(!(user==null))
            user.getMutualGuilds().stream().filter(g -> g.getMember(user).hasPermission(Permission.MANAGE_SERVER)).forEach(g -> guilds.add(g));

        return guilds;
    }
    
    /**@Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        Guild guild = event.getGuild();
        User owner = event.getJDA().getUserById(config.getOwnerId());
        String leavemsg = "Hi! Sorry, but you can't have a copy of Endless on Discord Bots, this is for my own security.\n"
                    + "Please remove this Account from the Discord Bots list or I'll take further actions.\n"
                    + "If you think this is an error, please contact the Developer. ~Artuto";
        String warnmsg = "<@264499432538505217>, **"+owner.getName()+"#"+owner.getDiscriminator()+"** has a copy of Endless here!";
        Long ownerId = config.getOwnerId();
        
        if(event.getGuild().getId().equals("110373943822540800") || event.getGuild().getId().equals("264445053596991498") && !(ownerId==264499432538505217L))
        {
            event.getJDA().getTextChannelById("119222314964353025").sendMessage(warnmsg).complete();
            owner.openPrivateChannel().queue(s -> s.sendMessage(leavemsg).queue(null, (e) -> SimpleLog.getLog("DISCORD BOTS").fatal(leavemsg)));
            guild.leave().complete();
        }
    }*/
}

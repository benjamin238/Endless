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

package me.artuto.endless.libraries;

import com.jagrosh.jagtag.Method;
import com.jagrosh.jagtag.ParseException;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Bot;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Artuto
 */

public class DiscordJagTag
{
    public static Collection<Method> getMethods()
    {
        return Arrays.asList(
                new Method("user", (env) -> {
                    User u = env.get("user");
                    return u.getName();
                }, (env, in) -> {
                    if(in[0].isEmpty())
                        return "";
                    Guild guild = env.get("guild");
                    if(!(guild==null))
                        return findMember(guild, in[0]).getUser().getName();
                    User user = env.get("user");
                    JDA jda = user.getJDA();
                    return findUser(jda, in[0]).getName();
                }),
                new Method("nick", (env) -> {
                    User user = env.get("user");
                    TextChannel tc = env.get("channel");
                    Guild guild = tc.getGuild();
                    if(guild==null)
                        return user.getName();
                    Member member = guild.getMember(user);
                    if(member==null)
                        return user.getName();
                    return member.getEffectiveName();
                }, (env, in) -> {
                    if(in[0].isEmpty())
                        return "";
                    Guild guild = env.get("guild");
                    if(!(guild==null))
                        return findMember(guild, in[0]).getEffectiveName();
                    User user = env.get("user");
                    JDA jda = user.getJDA();
                    return findUser(jda, in[0]).getName();
                }),
                new Method("discrim", (env) -> {
                    User user = env.get("user");
                    return user.getDiscriminator();
                }, (env, in) -> {
                    if(in[0].isEmpty())
                        return "";
                    Guild guild = env.get("guild");
                    if(!(guild==null))
                        return findMember(guild, in[0]).getUser().getDiscriminator();
                    User user = env.get("user");
                    JDA jda = user.getJDA();
                    return findUser(jda, in[0]).getDiscriminator();
                }),
                new Method("userid", (env) -> {
                    User user = env.get("user");
                    return user.getId();
                }),
                new Method("atuser", (env) -> {
                    User user = env.get("user");
                    return user.getAsMention();
                }, (env, in) -> {
                    if(in[0].isEmpty())
                        return "";
                    Guild guild = env.get("guild");
                    if(!(guild==null))
                        return findMember(guild, in[0]).getUser().getAsMention();
                    User user = env.get("user");
                    JDA jda = user.getJDA();
                    return findUser(jda, in[0]).getAsMention();
                }),
                new Method("avatar", (env) -> {
                    User user = env.get("user");
                    return user.getEffectiveAvatarUrl();
                }, (env, in) -> {
                    if(in[0].isEmpty())
                        return "";
                    Guild guild = env.get("guild");
                    if(!(guild==null))
                        return findMember(guild, in[0]).getUser().getEffectiveAvatarUrl();
                    User user = env.get("user");
                    JDA jda = user.getJDA();
                    return findUser(jda, in[0]).getEffectiveAvatarUrl();
                }),
                new Method("server", (env) -> {
                    Guild guild = env.get("guild");
                    return guild==null?"Direct Message":guild.getName();
                }),
                new Method("serverid", (env) -> {
                    Guild guild = env.get("guild");
                    return guild==null?"0":guild.getId();
                }),
                new Method("servercount", (env) -> {
                    Guild guild = env.get("guild");
                    return guild==null?"2":String.valueOf(guild.getMemberCache().size());
                }),
                new Method("channel", (env) -> {
                    MessageChannel channel = env.get("channel");
                    return channel.getName();
                }),
                new Method("channelid", (env) -> {
                    MessageChannel channel = env.get("channel");
                    return channel.getId();
                }),
                new Method("randuser", (env) -> {
                    Guild guild = env.get("guild");
                    User user = env.get("user");
                    if(guild==null)
                        return user.getJDA().getUsers().get((int)(user.getJDA().getUserCache().size()*Math.random())).getName();
                    return guild.getMembers().get((int)(guild.getMemberCache().size()*Math.random())).getUser().getName();
                }),
                new Method("randonline", (env) -> {
                    Guild guild = env.get("guild");
                    User user = env.get("user");
                    if(guild==null)
                        return user.getName();
                    List<Member> online = guild.getMembers().stream().filter(m -> m.getOnlineStatus()==OnlineStatus.ONLINE).collect(Collectors.toList());
                    if(online.isEmpty())
                        return user.getName();
                    return online.get((int)(online.size()*Math.random())).getUser().getName();
                }),
                new Method("", (env) -> {
                    Guild guild = env.get("guild");
                    MessageChannel channel = env.get("channel");
                    if(guild==null)
                        return channel.getName();
                    return guild.getTextChannels().get((int)(guild.getTextChannelCache().size()*Math.random())).getName();
                }),
                new Method("nsfw", (env) -> ""));
    }

    private static Member findMember(Guild guild, String query) throws ParseException
    {
        CommandClient client = Bot.getInstance().client;
        List<Member> list = FinderUtil.findMembers(query, guild);

        if(list.isEmpty())
            throw new ParseException(String.format("%s I was not able to found a member with the provided arguments: '%s'", client.getWarning(), query));
        else if(list.size()>1)
            throw new ParseException(String.format("%s %s", client.getWarning(), FormatUtil.listOfMembers(list, query)));
        else
            return list.get(0);
    }

    private static User findUser(JDA jda, String query) throws ParseException
    {
        CommandClient client = Bot.getInstance().client;
        List<User> list = FinderUtil.findUsers(query, jda);

        if(list.isEmpty())
            throw new ParseException(String.format("%s I was not able to found a user with the provided arguments: '%s'", client.getWarning(), query));
        else if(list.size()>1)
            throw new ParseException(String.format("%s %s", client.getWarning(), FormatUtil.listOfUsers(list, query)));
        else
            return list.get(0);
    }
}

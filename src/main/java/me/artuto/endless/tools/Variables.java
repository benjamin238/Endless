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

package me.artuto.endless.tools;

import com.jagrosh.jagtag.Method;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.Collection;

public class Variables
{
    public static Collection<Method> getMethods()
    {
        return Arrays.asList(

                /*
                 *Guild Methods
                 *
                 *This gets the member count
                 */

                new Method("memberscount", (env) ->
                {
                    Guild g = env.get("guild");
                    return g == null ? "2" : String.valueOf(g.getMembers().size());
                }),

                new Method("userscount", (env) ->
                {
                    Guild g = env.get("guild");
                    return g == null ? "2" : String.valueOf(g.getMembers().size());
                }),

                new Method("guildcount", (env) ->
                {
                    Guild g = env.get("guild");
                    return g == null ? "2" : String.valueOf(g.getMembers().size());
                }),

                new Method("servercount", (env) ->
                {
                    Guild g = env.get("guild");
                    return g == null ? "2" : String.valueOf(g.getMembers().size());
                }),

                //Name of the guild

                new Method("guildname", (env) ->
                {
                    Guild g = env.get("guild");
                    return g == null ? "Direct Message" : g.getName();
                }),

                new Method("servername", (env) ->
                {
                    Guild g = env.get("guild");
                    return g == null ? "Direct Message" : g.getName();
                }),

                //Owner of the guild

                new Method("guildowner", (env) ->
                {
                    Guild g = env.get("guild");
                    return g == null ? "Direct Message" : g.getOwner().getUser().getName()+"#"+g.getOwner().getUser().getDiscriminator();
                }),

                new Method("serverowner", (env) ->
                {
                    Guild g = env.get("guild");
                    return g == null ? "Direct Message" : g.getOwner().getUser().getName()+"#"+g.getOwner().getUser().getDiscriminator();
                }),

                //ID of the guild

                new Method("guildid", (env) ->
                {
                    Guild g = env.get("guild");
                    return g == null ? "Direct Message" : g.getId();
                }),

                new Method("serverid", (env) ->
                {
                    Guild g = env.get("guild");
                    return g == null ? "Direct Message" : g.getId();
                }),

                //Name of the user

                new Method("username", (env) ->
                {
                    User u = env.get("user");
                    return u.getName()+"#"+u.getDiscriminator();
                }),

                //ID of the user

                new Method("userid", (env) ->
                {
                    User u = env.get("user");
                    return u.getId();
                }),

                //Avatar of the user

                new Method("useravatar", (env) ->
                {
                    User u = env.get("avatar");
                    return u.getEffectiveAvatarUrl();
                }),

                new Method("avatar", (env) ->
                {
                    User u = env.get("user");
                    return u.getEffectiveAvatarUrl();
                }),

                //Mentions user

                new Method("usermention", (env) ->
                {
                    User u = env.get("user");
                    return u.getAsMention();
                }),

                //Name of the current channel

                new Method("channelname", (env) ->
                {
                    TextChannel tc = env.get("channel");
                    return tc == null ? "Direct Message" : tc.getName();
                }),

                //ID of the current channel

                new Method("channelid", (env) ->
                {
                    TextChannel tc = env.get("channel");
                    return tc == null ? "Direct Message" : tc.getId();
                }));
    }
}

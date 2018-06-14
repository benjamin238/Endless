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

package me.artuto.endless.commands.tools;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.tools.InfoTools;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Artuto
 */

public class UserInfoCmd extends EndlessCommand
{
    public UserInfoCmd()
    {
        this.name = "user";
        this.aliases = new String[]{"member", "userinfo", "i", "info", "memberinfo", "whois"};
        this.help = "Shows info about the specified user";
        this.arguments = "<user>";
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        Member member;
        String ranks;
        String roles;
        String emote;
        User user;

        if(event.getArgs().isEmpty())
            user = event.getAuthor();
        else
        {
            List<User> list = FinderUtil.findUsers(event.getArgs(), event.getJDA());

            if(list.isEmpty())
            {
                event.replyWarning("I was not able to found a user with the provided arguments: '"+event.getArgs()+"'");
                return;
            }
            else if(list.size()>1)
            {
                event.replyWarning(FormatUtil.listOfUsers(list, event.getArgs()));
                return;
            }
            else
                user = list.get(0);
        }

        if(InfoTools.nitroCheck(user))
            ranks = "<:nitro:334859814566101004>";
        else
            ranks = "";

        String title = (user.isBot() ? ":information_source: Information about the bot **"+
                user.getName()+"**"+"#"+"**"+user.getDiscriminator()+"** <:bot:334859813915983872>":
                ":information_source: Information about the user **"+
                        user.getName()+"**"+"#"+"**"+user.getDiscriminator()+"** "+ranks);

        if(event.isFromType(ChannelType.PRIVATE))
        {
            member = user.getMutualGuilds().get(0).getMember(user);
            emote = InfoTools.onlineStatus(member);
            builder.addField(":1234: ID: ", user.getId(), true);
            builder.addField(emote+" Status: ", member.getOnlineStatus()+
                    (member.getGame() == null ? "" : " ("+(member.getGame().getType() == Game.GameType.STREAMING ?
                            "On Live at [*"+member.getGame().getName()+"*]":"Playing "+member.getGame().getName())+")"+""), false);
            builder.setThumbnail(member.getUser().getEffectiveAvatarUrl());

        }
        else
        {
             member = event.getGuild().getMember(user);
             if(member==null)
             {
                 member = user.getMutualGuilds().get(0).getMember(user);
                 emote = InfoTools.onlineStatus(member);
                 builder.addField(":1234: ID: ", user.getId(), true);
                 builder.addField(emote+" Status: ", member.getOnlineStatus()+
                         (member.getGame() == null ? "" : " ("+(member.getGame().getType() == Game.GameType.STREAMING ?
                                 "On Live at [*"+member.getGame().getName()+"*]":"Playing "+member.getGame().getName())+")"+""), false);
                 builder.setThumbnail(member.getUser().getEffectiveAvatarUrl());
             }
             else
             {
                 String strjoins;
                 List<Member> joins = new ArrayList<>(event.getGuild().getMembers());
                 Collections.sort(joins, (Member a, Member b) -> a.getJoinDate().compareTo(b.getJoinDate()));
                 int index = joins.indexOf(member);
                 int joinnumber = index;
                 index -= 3;
                 if(index<0) index = 0;

                 if(joins.get(index).equals(member))
                     strjoins = "**"+joins.get(index).getUser().getName()+"**";
                 else
                     strjoins = joins.get(index).getUser().getName();

                 for(int i = index+1; i<index+7; i++)
                 {
                     if(i>=joins.size()) break;

                     Member m = joins.get(i);
                     String name = m.getUser().getName();

                     if(m.equals(member)) name = "**"+name+"**";

                     strjoins += " > "+name;
                 }

                 roles = InfoTools.mentionUserRoles(member);
                 emote = InfoTools.onlineStatus(member);

                 builder.addField(":1234: ID: ", user.getId(), true);
                 if(!(member.getNickname()==null))
                     builder.addField(":bust_in_silhouette: Nickname: ", member.getNickname(), true);
                 builder.addField(emote+" Status: ", member.getOnlineStatus()+
                         (member.getGame() == null ? "" : " ("+(member.getGame().getType() == Game.GameType.STREAMING ?
                                 "On Live at [*"+member.getGame().getName()+"*]":"Playing "+member.getGame().getName())+")"+""), false);
                 if(!(roles.isEmpty()))
                     builder.addField(":performing_arts: Roles: ", roles, false);
                 builder.addField(":calendar_spiral: Guild Join Date: ", member.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
                 builder.addField(":calendar_spiral: Account Creation Date: ", member.getUser().getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
                 builder.addField("Join Order: `(#"+(joinnumber+1)+")`", strjoins, false);
                 builder.setColor(member.getColor());
             }
        }

        event.reply(new MessageBuilder(title).setEmbed(builder.build()).build());
    }
}

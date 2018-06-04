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
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;

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
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        String ranks;
        String roles;
        String emote;
        EmbedBuilder builder = new EmbedBuilder();
        Member member;

        if(event.getArgs().isEmpty()) member = event.getMessage().getMember();
        else
        {
            List<Member> list = FinderUtil.findMembers(event.getArgs(), event.getGuild());

            if(list.isEmpty())
            {
                event.replyWarning("I was not able to found a user with the provided arguments: '"+event.getArgs()+"'");
                return;
            }
            else if(list.size()>1)
            {
                event.replyWarning(FormatUtil.listOfMembers(list, event.getArgs()));
                return;
            }
            else member = list.get(0);
        }

        String strjoins;
        List<Member> joins = new ArrayList<>(event.getGuild().getMembers());
        Collections.sort(joins, (Member a, Member b) -> a.getJoinDate().compareTo(b.getJoinDate()));
        int index = joins.indexOf(member);
        int joinnumber = index;
        index -= 3;
        if(index<0) index = 0;

        if(joins.get(index).equals(member)) strjoins = "**"+joins.get(index).getUser().getName()+"**";
        else strjoins = joins.get(index).getUser().getName();

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

        if(InfoTools.nitroCheck(member.getUser())) ranks = "<:nitro:334859814566101004>";
        else ranks = "";

        String title = (member.getUser().isBot() ? ":information_source: Information about the bot **"+member.getUser().getName()+"**"+"#"+"**"+member.getUser().getDiscriminator()+"** <:bot:334859813915983872>" : ":information_source: Information about the user **"+member.getUser().getName()+"**"+"#"+"**"+member.getUser().getDiscriminator()+"** "+ranks);

        try
        {
            builder.addField(":1234: ID: ", member.getUser().getId(), true);
            builder.addField(":bust_in_silhouette: Nickname: ", (member.getNickname() == null ? "None" : member.getNickname()), true);
            builder.addField(":hammer: Roles: ", roles, false);
            builder.addField(emote+" Status: ", member.getOnlineStatus()+(member.getGame() == null ? "" : " ("+(member.getGame().getType() == Game.GameType.STREAMING ? "On Live at [*"+member.getGame().getName()+"*]" : "Playing "+member.getGame().getName())+")"+""), false);
            builder.addField(":calendar_spiral: Account Creation Date: ", member.getUser().getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
            builder.addField(":calendar_spiral: Guild Join Date: ", member.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
            builder.addField("Join Order: `(#"+(joinnumber+1)+")`", strjoins, false);
            builder.setThumbnail(member.getUser().getEffectiveAvatarUrl());
            builder.setColor(member.getColor());
            event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();
        }
        catch(Exception e)
        {
            event.replyError("Something went wrong when getting the user info: \n```"+e+"```");
        }
    }
}
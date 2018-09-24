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
import me.artuto.endless.Const;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.utils.MiscUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    protected void executeCommand(EndlessCommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        MessageBuilder mb = new MessageBuilder();
        StringBuilder sb = new StringBuilder();

        Member member;
        User user;

        if(event.getArgs().isEmpty())
            user = event.getAuthor();
        else
        {
            user = searchUser(event);
            if(user==null)
                return;
        }

        Game game = user.getMutualGuilds().get(0).getMember(user).getGame();
        String status = getStatus(user.getMutualGuilds().get(0).getMember(user).getOnlineStatus());
        String statusEmote = getStatusEmote(user.getMutualGuilds().get(0).getMember(user));

        sb.append(Const.LINE_START).append(" ID: **").append(user.getId()).append("**\n");

        if(event.isFromType(ChannelType.TEXT))
        {
            member = event.getGuild().getMember(user);
            if(member==null)
                member = user.getMutualGuilds().get(0).getMember(user);
            game = member.getGame();
            status = getStatus(member.getOnlineStatus());
            statusEmote = getStatusEmote(member);
            sb.append(Const.LINE_START).append(" Status: ").append(statusEmote).append(" **").append(status).append("**");
            if(!(game==null))
                sb.append(" (").append(getGame(game.getType().getKey())).append(" *").append(game.getName()).append("*)");
            sb.append("\n");

            if(!(event.getGuild().getMember(user)==null))
            {
                if(!(member.getNickname()==null))
                    sb.append(Const.LINE_START).append(" Nickname: **").append(member.getNickname()).append("**\n");
                String roles = member.getRoles().isEmpty()?"":member.getRoles().stream().map(IMentionable::getAsMention)
                        .collect(Collectors.joining(", "));
                if(!(roles.isEmpty()))
                    sb.append(Const.LINE_START).append(" Roles: ").append(roles).append("\n");
                sb.append(Const.LINE_START).append(" Guild Join Date: **").append(member.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME))
                        .append("**\n");
                sb.append(Const.LINE_START).append(" Account Creation Date: **").append(user.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME))
                        .append("**\n");

                StringBuilder strjoins;
                List<Member> joins = new ArrayList<>(event.getGuild().getMembers());
                joins.sort(Comparator.comparing(Member::getJoinDate));
                int index = joins.indexOf(member);
                int joinnumber = index;
                index -= 3;
                if(index<0) index = 0;

                if(joins.get(index).equals(member))
                    strjoins = new StringBuilder("**"+joins.get(index).getUser().getName()+"**");
                else
                    strjoins = new StringBuilder(joins.get(index).getUser().getName());

                for(int i = index+1; i<index+7; i++)
                {
                    if(i>=joins.size()) break;

                    Member m = joins.get(i);
                    String name = m.getUser().getName();

                    if(m.equals(member)) name = "**"+name+"**";

                    strjoins.append(" > ").append(name);
                }
                sb.append(Const.LINE_START).append(" Join Order: ").append("`(#").append(joinnumber+1).append(")` ").append(strjoins).append("\n");
                builder.setColor(member.getColor());
            }
            else
                sb.append(Const.LINE_START).append(" Account Creation Date: **").append(user.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME))
                        .append("**\n");
        }
        else
        {
            sb.append(Const.LINE_START).append(" Status: ").append(statusEmote).append(" **").append(status).append("**");
            if(!(game==null))
                sb.append(" (").append(getGame(game.getType().getKey())).append(" *").append(game.getName()).append("*)");
            sb.append("\n");
            sb.append(Const.LINE_START).append(" Account Creation Date: **").append(user.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME))
                    .append("**\n");
        }

        builder.setDescription(sb).setThumbnail(MiscUtils.getImageUrl("png", null, user.getEffectiveAvatarUrl()));
        boolean nitro = !(user.getAvatarId()==null) && user.getAvatarId().startsWith("a_");
        String title = (user.isBot()?Const.BOT:Const.PEOPLE)+" Information about **"+user.getName()+"**#**"+user.getDiscriminator()+"** "
                +(nitro?Const.NITRO:"");
        event.reply(mb.setContent(title).setEmbed(builder.build()).build());
    }

    private String getGame(int type)
    {
        switch(type)
        {
            case 0:
                return "Playing";
            case 1:
                return "Streaming";
            case 2:
                return "Listening";
            case 3:
                return "Watching";
            default:
                return "Playing";
        }
    }

    private String getStatusEmote(Member member)
    {
        if(!(member.getGame()==null) && member.getGame().getType()==Game.GameType.STREAMING)
            return Const.STREAMING;

        switch(member.getOnlineStatus())
        {
            case ONLINE:
                return Const.ONLINE;
            case IDLE:
                return Const.IDLE;
            case DO_NOT_DISTURB:
                return Const.DND;
            case OFFLINE:
                return Const.OFFLINE;
            default:
                return Const.INVISIBLE;
        }
    }

    private String getStatus(OnlineStatus status)
    {
        switch(status)
        {
            case ONLINE:
                return "Online";
            case IDLE:
                return "Idle";
            case DO_NOT_DISTURB:
                return "Do Not Disturb";
            case OFFLINE:
                return "Offline";
            default:
                return "Invisible";
        }
    }

    private User searchUser(CommandEvent event)
    {
        if(event.isFromType(ChannelType.TEXT))
        {
            List<Member> members = FinderUtil.findMembers(event.getArgs(), event.getGuild());

            if(members.isEmpty())
            {
                List<User> users = FinderUtil.findUsers(event.getArgs(), event.getJDA());

                if(users.isEmpty())
                {
                    event.replyWarning("I was not able to found a user with the provided arguments: '"+event.getArgs()+"'");
                    return null;
                }
                else if(users.size()>1)
                {
                    event.replyWarning(FormatUtil.listOfUsers(users, event.getArgs()));
                    return null;
                }
                else
                    return users.get(0);
            }
            else if(members.size()>1)
            {
                event.replyWarning(FormatUtil.listOfMembers(members, event.getArgs()));
                return null;
            }
            else
                return members.get(0).getUser();
        }
        else
        {
            List<User> users = FinderUtil.findUsers(event.getArgs(), event.getJDA());

            if(users.isEmpty())
            {
                event.replyWarning("I was not able to found a user with the provided arguments: '"+event.getArgs()+"'");
                return null;
            }
            else if(users.size()>1)
            {
                event.replyWarning(FormatUtil.listOfUsers(users, event.getArgs()));
                return null;
            }
            else
                return users.get(0);
        }
    }
}

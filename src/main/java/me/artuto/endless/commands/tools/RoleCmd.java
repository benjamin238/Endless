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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Const;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RoleCmd extends EndlessCommand
{
    public RoleCmd()
    {
        this.name = "role";
        this.help = "Displays info about the specified role";
        this.arguments = "<role>";
        this.children = new Command[]{new GiveRole(), new Ping(), new TakeRole()};
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        MessageBuilder mb = new MessageBuilder();
        StringBuilder sb = new StringBuilder();

        List<Member> preMembers;
        Role role;
        String color;
        String members;
        String perms;

        List<Role> list = FinderUtil.findRoles(event.getArgs(), event.getGuild());

        if(list.isEmpty())
        {
            event.replyWarning("I was not able to found a role with the provided arguments: '"+event.getArgs()+"'");
            return;
        }
        else if(list.size()>1)
        {
            event.replyWarning(FormatUtil.listOfRoles(list, event.getArgs()));
            return;
        }
        else
            role = list.get(0);

        preMembers = role.isPublicRole()?event.getGuild().getMembers():event.getGuild().getMembersWithRoles(role);
        color = "#"+(role.getColor()==null?"000000":Integer.toHexString(role.getColor().getRGB()).toUpperCase().substring(2));
        members = preMembers.isEmpty()?"None":preMembers.size()>20?"**"+preMembers.size()+"**":"**"+preMembers.size()+"**\n"
                +preMembers.stream().map(m -> m.getUser().getAsMention()).collect(Collectors.joining(", "));
        perms = role.getPermissions().isEmpty()?"None":role.getPermissions().stream().map(p -> "`"+p.getName()+"`")
                .collect(Collectors.joining(", "));

        sb.append(Const.LINE_START).append(" ID: **").append(role.getId()).append("**\n");
        sb.append(Const.LINE_START).append(" Creation: **").append(role.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**\n");
        sb.append(Const.LINE_START).append(" Position: **").append(event.getGuild().getRoles().indexOf(role)+1).append("**\n");
        sb.append(Const.LINE_START).append(" Color: **").append(color).append("**\n");
        sb.append(Const.LINE_START).append(" Mentionable: **").append(role.isMentionable()?"Yes":"No").append("**\n");
        sb.append(Const.LINE_START).append(" Hoisted: **").append(role.isHoisted()?"Yes":"No").append("**\n");
        sb.append(Const.LINE_START).append(" Managed: **").append(role.isManaged()?"Yes":"No").append("**\n");
        sb.append(Const.LINE_START).append(" Permissions: ").append(perms).append("\n");
        sb.append(Const.LINE_START).append(" Members: ").append(members);

        builder.setColor(role.getColor()).setDescription(sb);
        String title = ":performing_arts: Information about **"+role.getName()+"**";
        event.reply(mb.setContent(title).setEmbed(builder.build()).build());
    }

    private class GiveRole extends EndlessCommand
    {
        GiveRole()
        {
            this.name = "give";
            this.help = "Gives the specified role to the specified member";
            this.arguments = "<role> to <user>";
            this.category = Categories.TOOLS;
            this.botPerms = new Permission[]{Permission.MANAGE_ROLES};
            this.userPerms = new Permission[]{Permission.MANAGE_ROLES};
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            Role role;
            Member m;
            String roleToAdd;
            String member;
            Member author = event.getMember();

            try
            {
                String[] args = event.getArgs().split(" to ", 2);

                roleToAdd = args[0].trim();
                member = args[1].trim();
            }
            catch(IndexOutOfBoundsException e)
            {
                event.replyWarning("Invalid syntax: `"+event.getClient().getPrefix()+name+" "+arguments+"`");
                return;
            }

            List<Role> rlist = FinderUtil.findRoles(roleToAdd, event.getGuild());

            if(rlist.isEmpty())
            {
                event.replyWarning("I was not able to found a role with the provided arguments: '"+event.getArgs()+"'");
                return;
            }
            else if(rlist.size()>1)
            {
                event.replyWarning(FormatUtil.listOfRoles(rlist, event.getArgs()));
                return;
            }
            else role = rlist.get(0);

            List<Member> mlist = FinderUtil.findMembers(member, event.getGuild());

            if(mlist.isEmpty())
            {
                event.replyWarning("I was not able to found a user with the provided arguments: '"+event.getArgs()+"'");
                return;
            }
            else if(mlist.size()>1)
            {
                event.replyWarning(FormatUtil.listOfMembers(mlist, event.getArgs()));
                return;
            }
            else m = mlist.get(0);

            if(!(ChecksUtil.canMemberInteract(author, role)))
            {
                event.replyError("I can't interact with that role!");
                return;
            }
            if(!(ChecksUtil.canMemberInteract(author, role)))
            {
                event.replyError("You can't interact with that role!");
                return;
            }

            event.getGuild().getController().addSingleRoleToMember(m, role).reason("["+author.getUser().getName()+"#"+author.getUser().getDiscriminator()+"]").queue(s ->
                    event.replySuccess("Successfully given the role **"+role.getName()+"** to **"+m.getUser().getName()+"#"+m.getUser().getDiscriminator()+"**"),
                    e -> event.replyError("An error happened when giving the role **"+role.getName()+"** to **"+m.getUser().getName()+"#"+m.getUser().getDiscriminator()+"**"));
        }
    }

    private class Ping extends EndlessCommand
    {
        Ping()
        {
            this.name = "ping";
            this.aliases = new String[]{"notify"};
            this.help = "Pings the specified role";
            this.arguments = "<role>";
            this.category = Categories.TOOLS;
            this.botPerms = new Permission[]{Permission.MANAGE_ROLES};
            this.userPerms = new Permission[]{Permission.MANAGE_ROLES};
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            Role role;
            List<Role> list = FinderUtil.findRoles(event.getArgs(), event.getGuild());

            if(list.isEmpty())
            {
                event.replyWarning("I was not able to found a role with the provided arguments: '"+event.getArgs()+"'");
                return;
            }
            else if(list.size()>1)
            {
                event.replyWarning(FormatUtil.listOfRoles(list, event.getArgs()));
                return;
            }
            else
                role = list.get(0);

            if(!(ChecksUtil.canMemberInteract(event.getSelfMember(), role)))
            {
                event.replyError("I can't interact with that role!");
                return;
            }
            if(!(ChecksUtil.canMemberInteract(event.getMember(), role)))
            {
                event.replyError("You can't interact with that role!");
                return;
            }

            if(role.isMentionable())
                event.reply(role.getAsMention());
            else
            {
                role.getManager().setMentionable(true).queue(s -> event.reply(role.getAsMention(), s2 -> role.getManager().setMentionable(false).queue(s3 ->
                                event.reactSuccess(),
                        e -> event.replyError("Error while setting the role back to no mentionable!")),
                        e -> event.replyError("Error while setting the role to mentionable!")));
            }
        }
    }

    private class TakeRole extends EndlessCommand
    {
        TakeRole()
        {
            this.name = "take";
            this.help = "Takes the specified role from the specified member";
            this.arguments = "<role> from <user>";
            this.category = Categories.TOOLS;
            this.botPerms = new Permission[]{Permission.MANAGE_ROLES};
            this.userPerms = new Permission[]{Permission.MANAGE_ROLES};
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Invalid syntax: `"+event.getClient().getPrefix()+name+" "+arguments+"`");
                return;
            }

            Role role;
            Member m;
            String roleToAdd;
            String member;
            Member author = event.getMember();

            try
            {
                String[] args = event.getArgs().split(" from ", 2);

                roleToAdd = args[0].trim();
                member = args[1].trim();
            }
            catch(IndexOutOfBoundsException e)
            {
                event.replyWarning("Invalid syntax: `"+event.getClient().getPrefix()+name+" "+arguments+"`");
                return;
            }

            List<Role> rlist = FinderUtil.findRoles(roleToAdd, event.getGuild());

            if(rlist.isEmpty())
            {
                event.replyWarning("I was not able to found a role with the provided arguments: '"+event.getArgs()+"'");
                return;
            }
            else if(rlist.size()>1)
            {
                event.replyWarning(FormatUtil.listOfRoles(rlist, event.getArgs()));
                return;
            }
            else role = rlist.get(0);

            List<Member> mlist = FinderUtil.findMembers(member, event.getGuild());

            if(mlist.isEmpty())
            {
                event.replyWarning("I was not able to found a user with the provided arguments: '"+event.getArgs()+"'");
                return;
            }
            else if(mlist.size()>1)
            {
                event.replyWarning(FormatUtil.listOfMembers(mlist, event.getArgs()));
                return;
            }
            else m = mlist.get(0);

            if(!(ChecksUtil.canMemberInteract(event.getSelfMember(), role)))
            {
                event.replyError("I can't interact with that role!");
                return;
            }
            if(!(ChecksUtil.canMemberInteract(author, role)))
            {
                event.replyError("You can't interact with that role!");
                return;
            }

            event.getGuild().getController().removeSingleRoleFromMember(m, role).reason("["+author.getUser().getName()+"#"+author.getUser().getDiscriminator()+"]").queue(s ->
                    event.replySuccess("Successfully removed the role **"+role.getName()+"** from **"+m.getUser().getName()+"#"+m.getUser().getDiscriminator()+"**"),
                    e -> event.replyError("An error happened when removing the role **"+role.getName()+"** from **"+m.getUser().getName()+"#"+m.getUser().getDiscriminator()+"**"));
        }
    }
}

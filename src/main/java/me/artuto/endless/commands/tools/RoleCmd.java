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
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.IMentionable;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.awt.*;
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
        this.children = new Command[]{new GiveRole(), new TakeRole()};
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        Role rol;
        Color color;
        List<Member> members;
        List<Permission> perm;
        EmbedBuilder builder = new EmbedBuilder();
        String permissions;
        String membersInRole;

        List<net.dv8tion.jda.core.entities.Role> list = FinderUtil.findRoles(event.getArgs(), event.getGuild());

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
        else rol = list.get(0);

        color = rol.getColor();
        members = event.getGuild().getMembersWithRoles(rol);

        if(members.size()>20) membersInRole = String.valueOf(members.size());
        else if(members.isEmpty()) membersInRole = "Nobody";
        else membersInRole = members.stream().map(IMentionable::getAsMention).collect(Collectors.joining(", "));

        perm = rol.getPermissions();

        if(perm.isEmpty()) permissions = "None";
        else permissions = perm.stream().map(p -> "`"+p.getName()+"`").collect(Collectors.joining(", "));

        String title = ":performing_arts: Information about the role **"+rol.getName()+"**";

        try
        {
            builder.addField(":1234: ID: ", "**"+rol.getId()+"**", false);
            builder.addField(":calendar: Creation Date: ", "**"+rol.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", false);
            builder.addField(":paintbrush: Color: ", color == null ? "**#000000**" : "**#"+Integer.toHexString(color.getRGB()).substring(2).toUpperCase()+"**", true);
            builder.addField(":small_red_triangle: Position: ", String.valueOf("**"+(event.getGuild().getRoles().indexOf(rol)+1)+"**"), true);
            builder.addField(":bell: Mentionable: ", (rol.isMentionable() ? "**Yes**" : "**No**"), true);
            builder.addField(":wrench: Managed: ", (rol.isManaged() ? "**Yes**" : "**No**"), true);
            builder.addField(":link: Hoisted: ", (rol.isHoisted() ? "**Yes**" : "**No**"), true);
            builder.addField(":passport_control: Public Role: ", (rol.isPublicRole() ? "**Yes**" : "**No**"), true);
            builder.addField(":key: Permissions: ", permissions, false);
            builder.addField(":busts_in_silhouette: Members: ", membersInRole, false);
            builder.setColor(color);
            event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());
        }
        catch(Exception e)
        {
            event.replyError("Something went wrong when getting the role info: \n```"+e+"```");
        }
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

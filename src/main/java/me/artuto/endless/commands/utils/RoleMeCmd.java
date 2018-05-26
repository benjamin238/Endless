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

package me.artuto.endless.commands.utils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.utils.Checks;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.List;

/**
 * @author Artuto
 */

public class RoleMeCmd extends EndlessCommand
{
    private final Bot bot;

    public RoleMeCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "roleme";
        this.help = "Self-assignable roles.";
        this.arguments = "[roleme role]";
        this.category = Categories.UTILS;
        this.children = new Command[]{new Add(), new Remove()};
        this.botPerms = new Permission[]{Permission.MANAGE_ROLES};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        String args = event.getArgs();
        Guild guild = event.getGuild();
        List<Role> rolemeRoles = bot.gsdm.getRolemeRoles(guild);
        Member member = event.getMember();
        Role role;

        if(args.isEmpty())
        {
            if(rolemeRoles==null)
                event.replyError("Something has gone wrong while getting the settings, please contact the bot owner.");
            else if(rolemeRoles.isEmpty())
                event.replyWarning("This guild doesn't has any RoleMe roles.");
            else
            {
                StringBuilder sb = new StringBuilder();
                sb.append(":performing_arts: RoleMe roles available on **").append(guild.getName()).append("**:").append("\n");

                for(Role r : rolemeRoles)
                    sb.append(Const.LINE_START).append(" ").append(r.getName()).append("\n");

                event.reply(sb.toString());
            }
        }
        else
        {
            List<Role> list = FinderUtil.findRoles(args, event.getGuild());

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
            else role = list.get(0);

            if(rolemeRoles.contains(role))
            {
                if(!(Checks.canMemberInteract(event.getSelfMember(), role)))
                    event.replyError("I can't interact with that role!");
                else
                {
                    if(member.getRoles().contains(role))
                        guild.getController().removeSingleRoleFromMember(member, role).queue(s -> event.replySuccess("The role *"+role.getName()+"* has been removed."),
                                e -> event.replyError("Something has gone wrong while removing the role from you, please contact the bot owner."));
                    else
                        guild.getController().addSingleRoleToMember(member, role).queue(s -> event.replySuccess("You have been given the role *"+role.getName()+"*"),
                                e -> event.replyError("Something has gone wrong while giving you the role, please contact the bot owner."));
                }
            }
            else
                event.replyWarning("That role is not enabled for RoleMe!");
        }
    }

    private class Add extends EndlessCommand
    {
        Add()
        {
            this.name = "add";
            this.help = "Adds a role to the list of available RoleMe roles.";
            this.arguments = "[role]";
            this.category = Categories.UTILS;
            this.botPerms = new Permission[]{Permission.MANAGE_ROLES};
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        }

        protected void executeCommand(CommandEvent event)
        {
            Guild guild = event.getGuild();
            List<Role> rolemeRoles = bot.gsdm.getRolemeRoles(guild);
            String args = event.getArgs();
            Role role;

            if(args.isEmpty())
                event.replyWarning("Please specify a role to add!");
            else
            {
                List<Role> list = FinderUtil.findRoles(args, event.getGuild());

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
                else role = list.get(0);

                if(rolemeRoles==null)
                    event.replyError("Something has gone wrong while getting the RoleMe roles list, please contact the bot owner.");
                else if(rolemeRoles.contains(role))
                {
                    event.replyError("That role is already on the RoleMe roles list!");
                    return;
                }

                if(!(Checks.canMemberInteract(event.getSelfMember(), role)))
                    event.replyError("I can't interact with that role!");
                else if(bot.gsdm.addRolemeRole(guild, role))
                    event.replySuccess("Successfully added the role *"+role.getName()+"* to the RoleMe roles list.");
                else
                    event.replyError("Something has gone wrong while adding the role to the RoleMe roles list, please contact the bot owner.");
            }
        }
    }

    private class Remove extends EndlessCommand
    {
        Remove()
        {
            this.name = "remove";
            this.help = "Removes a role from the list of available RoleMe roles.";
            this.arguments = "[role]";
            this.category = Categories.UTILS;
            this.botPerms = new Permission[]{Permission.MANAGE_ROLES};
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        }

        protected void executeCommand(CommandEvent event)
        {
            Guild guild = event.getGuild();
            List<Role> rolemeRoles = bot.gsdm.getRolemeRoles(guild);
            String args = event.getArgs();
            Role role;

            if(args.isEmpty())
                event.replyWarning("Please specify a role to remove!");
            else
            {
                List<Role> list = FinderUtil.findRoles(args, event.getGuild());

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
                else role = list.get(0);

                if(rolemeRoles==null)
                    event.replyError("Something has gone wrong while getting the RoleMe roles list, please contact the bot owner.");
                else if(!(rolemeRoles.contains(role)))
                {
                    event.replyError("That role isn't on the RoleMe roles list!");
                    return;
                }

                if(bot.gsdm.removeRolemeRole(guild, role))
                    event.replySuccess("Successfully removed the role *"+role.getName()+"* from the RoleMe roles list.");
                else
                    event.replyError("Something has gone wrong while removing the role from the RoleMe roles list, please contact the bot owner.");
            }

        }
    }
}

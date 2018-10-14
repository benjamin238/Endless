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
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Const;
import me.artuto.endless.Endless;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

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
        this.children = new Command[]{new GiveRoleCmd(), new PingCmd(), new TakeRoleCmd()};
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        MessageBuilder mb = new MessageBuilder();
        StringBuilder sb = new StringBuilder();

        List<Member> preMembers;
        Role role = ArgsUtils.findRole(event, event.getArgs());
        String color;
        String members;
        String perms;
        if(role==null)
            return;

        preMembers = role.isPublicRole()?event.getGuild().getMembers():event.getGuild().getMembersWithRoles(role);
        color = "#"+(role.getColor()==null?"000000":Integer.toHexString(role.getColor().getRGB()).toUpperCase().substring(2));
        members = preMembers.isEmpty()?"None":preMembers.size()>20?"**"+preMembers.size()+"**":"**"+preMembers.size()+"**\n"
                +preMembers.stream().map(m -> m.getUser().getAsMention()).collect(Collectors.joining(", "));
        perms = role.getPermissions().isEmpty()?"None":role.getPermissions().stream().map(p -> "`"+p.getName()+"`")
                .collect(Collectors.joining(", "));

        sb.append(Const.LINE_START).append(" ID: **").append(role.getId()).append("**\n");
        sb.append(Const.LINE_START).append(" **").append(event.localize("command.role.creation")).append(": **").append(role.getCreationTime()
                .format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**\n");
        sb.append(Const.LINE_START).append(" **").append(event.localize("command.role.position")).append(": **")
                .append(event.getGuild().getRoles().indexOf(role)+1).append("**\n");
        sb.append(Const.LINE_START).append(" **").append(event.localize("command.role.color")).append(": **").append(color).append("**\n");
        sb.append(Const.LINE_START).append(" **").append(event.localize("command.role.mentionable")).append(": **")
                .append(role.isMentionable()?"Yes":"No").append("**\n");
        sb.append(Const.LINE_START).append(" **").append(event.localize("command.role.hoisted")).append(": **")
                .append(role.isHoisted()?"Yes":"No").append("**\n");
        sb.append(Const.LINE_START).append(" **").append(event.localize("command.role.managed")).append(": **")
                .append(role.isManaged()?"Yes":"No").append("**\n");
        sb.append(Const.LINE_START).append(" **").append(event.localize("command.role.permissions")).append(": **").append(perms).append("\n");
        sb.append(Const.LINE_START).append(" **").append(event.localize("command.role.members")).append(": **").append(members);

        builder.setColor(role.getColor()).setDescription(sb);
        String title = ":performing_arts: "+event.localize("command.role.title", role.getName());
        event.reply(mb.setContent(FormatUtil.sanitize(title)).setEmbed(builder.build()).build());
    }

    private class GiveRoleCmd extends EndlessCommand
    {
        GiveRoleCmd()
        {
            this.name = "give";
            this.help = "Gives the specified role to the specified member";
            this.arguments = "<role> to <user>";
            this.category = Categories.TOOLS;
            this.botPerms = new Permission[]{Permission.MANAGE_ROLES};
            this.userPerms = new Permission[]{Permission.MANAGE_ROLES};
            this.parent = RoleCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            Member author = event.getMember();
            String[] args = ArgsUtils.splitWithSeparator(2, event.getArgs(), "to");
            if(args[1].isEmpty())
            {
                event.replyWarning("command.role.give.invalidSyntax", event.getClient().getPrefix(), this.arguments);
                return;
            }
            Role role = ArgsUtils.findRole(event, args[0]);
            Member m = ArgsUtils.findMember(event, args[1]);
            if(role==null || m==null)
                return;

            if(!(ChecksUtil.canMemberInteract(event.getSelfMember(), role)))
            {
                event.replyError("core.error.cantInteract.role.bot");
                return;
            }
            if(!(ChecksUtil.canMemberInteract(author, role)))
            {
                event.replyError("core.error.cantInteract.role.executor");
                return;
            }

            User user = m.getUser();
            event.getGuild().getController().addSingleRoleToMember(m, role).reason("["+author.getUser().getName()+"#"+author.getUser().getDiscriminator()+"]")
                    .queue(s -> event.replySuccess("command.role.give.success", role.getName(), user.getName()+"#"+user.getDiscriminator()), e -> {
                        event.replyError("command.role.give.error", role.getName(), user.getName()+"#"+user.getDiscriminator());
                        Endless.LOG.error("Error while giving role {} to member {} in guild {}", role.getId(), user.getId(), event.getGuild().getId(), e);
                    });
        }
    }

    private class PingCmd extends EndlessCommand
    {
        PingCmd()
        {
            this.name = "ping";
            this.aliases = new String[]{"notify"};
            this.help = "Pings the specified role";
            this.arguments = "<role>";
            this.category = Categories.TOOLS;
            this.botPerms = new Permission[]{Permission.MANAGE_ROLES};
            this.userPerms = new Permission[]{Permission.MANAGE_ROLES};
            this.parent = RoleCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            Role role = ArgsUtils.findRole(event, event.getArgs());
            if(role==null)
                return;

            if(!(ChecksUtil.canMemberInteract(event.getSelfMember(), role)))
            {
                event.replyError("core.error.cantInteract.role.bot");
                return;
            }
            if(!(ChecksUtil.canMemberInteract(event.getMember(), role)))
            {
                event.replyError("core.error.cantInteract.role.executor");
                return;
            }

            if(role.isMentionable())
                event.reply(false, role.getAsMention());
            else
            {
                role.getManager().setMentionable(true).queue(s -> event.reply(role.getAsMention(), s2 -> role.getManager().setMentionable(false)
                                .queue(s3 -> event.reactSuccess(), e -> event.replyError("command.announcement.error.noMentionable")),
                        e -> event.replyError("command.announcement.error.mentionable")));
            }
        }
    }

    private class TakeRoleCmd extends EndlessCommand
    {
        TakeRoleCmd()
        {
            this.name = "take";
            this.help = "Takes the specified role from the specified member";
            this.arguments = "<role> from <user>";
            this.category = Categories.TOOLS;
            this.botPerms = new Permission[]{Permission.MANAGE_ROLES};
            this.userPerms = new Permission[]{Permission.MANAGE_ROLES};
            this.parent = RoleCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            Member author = event.getMember();
            String[] args = ArgsUtils.splitWithSeparator(2, event.getArgs(), "from");
            if(args[1].isEmpty())
            {
                event.replyWarning("command.role.take.invalidSyntax", event.getClient().getPrefix(), this.arguments);
                return;
            }
            Role role = ArgsUtils.findRole(event, args[0]);
            Member m = ArgsUtils.findMember(event, args[1]);
            if(role==null || m==null)
                return;

            if(!(ChecksUtil.canMemberInteract(event.getSelfMember(), role)))
            {
                event.replyError("core.error.cantInteract.role.bot");
                return;
            }
            if(!(ChecksUtil.canMemberInteract(author, role)))
            {
                event.replyError("core.error.cantInteract.role.executor");
                return;
            }

            User user = m.getUser();
            event.getGuild().getController().removeSingleRoleFromMember(m, role).reason("["+author.getUser().getName()+"#"+author.getUser().getDiscriminator()+"]")
                    .queue(s -> event.replySuccess("command.role.take.success", role.getName(), user.getName()+"#"+user.getDiscriminator()), e -> {
                        event.replyError("command.role.take.error", role.getName(), user.getName()+"#"+user.getDiscriminator());
                        Endless.LOG.error("Error while taking role {} from member {} in guild {}", role.getId(), user.getId(), event.getGuild().getId(), e);
                    });
        }
    }
}

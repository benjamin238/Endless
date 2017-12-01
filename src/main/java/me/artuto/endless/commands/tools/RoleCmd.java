package me.artuto.endless.commands.tools;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RoleCmd extends Command
{
    public RoleCmd()
    {
        this.name = "role";
        this.help = "Displays info about the specified role";
        this.arguments = "<role>";
        this.children = new Command[]{new GiveRole(), new TakeRole()};
        this.category = Categories.TOOLS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        Role rol;
        Color color;
        List<Member> members;
        List<Permission> perm;
        EmbedBuilder builder = new EmbedBuilder();
        String permissions;
        String membersInRole;

        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Please specify a role!");
            return;
        }

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
        else
            rol = list.get(0);

        color = rol.getColor();
        members = event.getGuild().getMembersWithRoles(rol);

        if(members.size()>20)
            membersInRole = String.valueOf(members.size());
        else if(members.isEmpty())
            membersInRole = "Nobody";
        else
            membersInRole = members.stream().map(m -> m.getAsMention()).collect(Collectors.joining(", "));

        perm = rol.getPermissions();

        if(perm.isEmpty())
            permissions = "None";
        else
            permissions = perm.stream().map(p -> "`"+p.getName()+"`").collect(Collectors.joining(", "));

        String title = ":performing_arts: Information about the role **"+rol.getName()+"**";

        try
        {
            builder.addField(":1234: ID: ", "**"+rol.getId()+"**", false);
            builder.addField(":calendar: Creation Date: ", "**"+rol.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", false);
            builder.addField(":paintbrush: Color: ", color==null?"**#000000**":"**#"+Integer.toHexString(color.getRGB()).substring(2).toUpperCase()+"**",true);
            builder.addField(":small_red_triangle: Position: ", String.valueOf("**"+rol.getPosition()+"**"), true);
            builder.addField(":bell: Mentionable: ", (rol.isMentionable()?"**Yes**":"**No**"), true);
            builder.addField(":wrench: Managed: ", (rol.isManaged()?"**Yes**":"**No**"), true);
            builder.addField(":link: Hoisted: ", (rol.isHoisted()?"**Yes**":"**No**"), true);
            builder.addField(":passport_control: Public Role: ", (rol.isPublicRole()?"**Yes**":"**No**"), true);
            builder.addField(":key: Permissions: ", permissions, false);
            builder.addField(":busts_in_silhouette: Members: ", membersInRole, false);
            builder.setColor(color);
            event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();
        }
        catch(Exception e)
        {
            event.replyError("Something went wrong when getting the role info: \n```"+e+"```");
        }
    }

    private class GiveRole extends Command
    {
        public GiveRole()
        {
            this.name = "give";
            this.help = "Gives the specified role to the specified member";
            this.arguments = "<role> to <user>";
            this.category = Categories.TOOLS;
            this.botPermissions = new Permission[]{Permission.MANAGE_ROLES};
            this.userPermissions = new Permission[]{Permission.MANAGE_ROLES};
            this.ownerCommand = false;
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Invalid syntax: `"+event.getClient().getPrefix()+name+" "+arguments+"`");
                return;
            }

            net.dv8tion.jda.core.entities.Role rol;
            Member m;
            String role;
            String member;
            Member author = event.getMember();

            try
            {
                String[] args = event.getArgs().split(" to ", 2);

                role = args[0];
                member = args[1];
            }
            catch(IndexOutOfBoundsException e)
            {
                event.replyWarning("Invalid syntax: `"+event.getClient().getPrefix()+name+" "+arguments+"`");
                return;
            }

            List<net.dv8tion.jda.core.entities.Role> rlist = FinderUtil.findRoles(role, event.getGuild());

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
            else
                rol = rlist.get(0);

            List<Member> mlist = FinderUtil.findMembers(member, event.getGuild());

            if(mlist.isEmpty())
            {
                event.replyWarning("I was not able to found a user with the provided arguments: '" + event.getArgs() + "'");
                return;
            }
            else if(mlist.size()>1)
            {
                event.replyWarning(FormatUtil.listOfMembers(mlist, event.getArgs()));
                return;
            }
            else
                m = mlist.get(0);

            if(!(author.canInteract(rol)))
            {
                event.replyError("You can't interact with that role!");
                return;
            }
            if(!(event.getSelfMember().canInteract(rol)))
            {
                event.replyError("I can't interact with that role!");
                return;
            }

            event.getGuild().getController().addSingleRoleToMember(m, rol).reason("["+author.getUser().getName()+"#"+author.getUser().getDiscriminator()+"]").queue(s ->
                            event.replySuccess("Successfully given the role **"+rol.getName()+"** to **"+m.getUser().getName()+"#"+m.getUser().getDiscriminator()+"**"), e ->
                    event.replyError("An error happened when giving the role **"+rol.getName()+"** to **"+m.getUser().getName()+"#"+m.getUser().getDiscriminator()+"**"));
        }
    }

    private class TakeRole extends Command
    {
        public TakeRole()
        {
            this.name = "take";
            this.help = "Takes the specified role from the specified member";
            this.arguments = "<role> from <user>";
            this.category = Categories.TOOLS;
            this.botPermissions = new Permission[]{Permission.MANAGE_ROLES};
            this.userPermissions = new Permission[]{Permission.MANAGE_ROLES};
            this.ownerCommand = false;
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Invalid syntax: `"+event.getClient().getPrefix()+name+" "+arguments+"`");
                return;
            }

            net.dv8tion.jda.core.entities.Role rol;
            Member m;
            String role;
            String member;
            Member author = event.getMember();

            try
            {
                String[] args = event.getArgs().split(" from ", 2);

                role = args[0];
                member = args[1];
            }
            catch(IndexOutOfBoundsException e)
            {
                event.replyWarning("Invalid syntax: `"+event.getClient().getPrefix()+name+" "+arguments+"`");
                return;
            }

            List<net.dv8tion.jda.core.entities.Role> rlist = FinderUtil.findRoles(role, event.getGuild());

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
            else
                rol = rlist.get(0);

            List<Member> mlist = FinderUtil.findMembers(member, event.getGuild());

            if(mlist.isEmpty())
            {
                event.replyWarning("I was not able to found a user with the provided arguments: '" + event.getArgs() + "'");
                return;
            }
            else if(mlist.size()>1)
            {
                event.replyWarning(FormatUtil.listOfMembers(mlist, event.getArgs()));
                return;
            }
            else
                m = mlist.get(0);

            if(!(author.canInteract(rol)))
            {
                event.replyError("You can't interact with that role!");
                return;
            }
            if(!(event.getSelfMember().canInteract(rol)))
            {
                event.replyError("I can't interact with that role!");
                return;
            }

            event.getGuild().getController().removeSingleRoleFromMember(m, rol).reason("["+author.getUser().getName()+"#"+author.getUser().getDiscriminator()+"]").queue(s ->
                    event.replySuccess("Successfully removed the role **"+rol.getName()+"** from **"+m.getUser().getName()+"#"+m.getUser().getDiscriminator()+"**"), e ->
                    event.replyError("An error happened when removing the role **"+rol.getName()+"** from **"+m.getUser().getName()+"#"+m.getUser().getDiscriminator()+"**"));
        }
    }
}
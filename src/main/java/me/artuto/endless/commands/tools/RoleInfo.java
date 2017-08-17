package me.artuto.endless.commands.tools;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RoleInfo extends Command
{
    public RoleInfo()
    {
        this.name = "roleinfo";
        this.help = "Displays info about the specified role";
        this.category = new Command.Category("Tools");
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        Role rol = null;
        Color color;
        List<Member> members;
        List<Permission> perm;
        EmbedBuilder builder = new EmbedBuilder();
        String permissions;
        String membersInRole;

        if(event.getArgs().isEmpty())
        {
            event.replyWarning("");
        }
        else
        {
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
            {
                rol = list.get(0);
            }
        }

        color = rol.getColor();
        members = event.getGuild().getMembersWithRoles(rol);

        StringBuilder membersbldr = new StringBuilder();
        members.forEach(me -> membersbldr.append(" ").append(me.getAsMention()));

        if(members.size()>20)
        {
            membersInRole = String.valueOf(members.size());
        }
        else if(members.isEmpty())
        {
            membersInRole = "Nobody";
        }
        else
        {
            membersInRole = membersbldr.toString();
        }

        perm = rol.getPermissions();

        StringBuilder permsbldr = new StringBuilder();
        perm.forEach(pe -> permsbldr.append(" ").append(pe.getName()));

        if(perm.isEmpty())
        {
            permissions = "None";
        }
        else
        {
            permissions = permsbldr.toString();
        }

        String title = ":performing_arts: Information about the role **"+rol.getName()+"**";

        

        try
        {
            builder.addField(":1234: ID: ", rol.getId(), true);
            builder.addField(":calendar: Creation Date: ", rol.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
            builder.addField(":paintbrush: Color: ", "#"+color.getRed()+color.getGreen()+color.getBlue(),true);
            builder.addField(":small_red_triangle: Position: ", String.valueOf(rol.getPosition()), true);
            builder.addField(":bell: Mentionable: ", (rol.isMentionable()?"Yes":"No"), true);
            builder.addField(":wrench: Managed: ", (rol.isManaged()?"Yes":"No"), true);
            builder.addField(":link: Hoisted: ", (rol.isHoisted()?"Yes":"No"), true);
            builder.addField(":passport_control: Public Role: ", (rol.isPublicRole()?"Yes":"No"), true);
            builder.addField(":key: Permissions: ", "`"+permissions+"`", false);
            builder.addField(":busts_in_silhouette: Members: ", membersInRole, false);
            builder.setColor(rol.getColor());
            event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();
        }
        catch(Exception e)
        {
            event.replyError("Something went wrong when getting the role info: \n```"+e+"```");

        }
    }
}

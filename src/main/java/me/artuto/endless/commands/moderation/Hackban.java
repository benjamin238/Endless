package me.artuto.endless.commands.moderation;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import me.artuto.endless.Messages;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.utils.ModLogging;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class Hackban extends Command
{
    public Hackban()
    {
        this.name = "hackban";
        this.help = "Bans the specified user";
        this.arguments = "User ID";
        this.category = new Command.Category("Moderation");
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.ownerCommand = false;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        User author;
        User user;
        author = event.getAuthor();
        String target;
        String reason;

        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Invalid Syntax: "+event.getClient().getPrefix()+"hackban User ID for *reason*");
            return;
        }

        try
        {
            String[] args = event.getArgs().split(" for ");
            target = args[0];
            reason = args[1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            event.replyWarning("Invalid Syntax: "+event.getClient().getPrefix()+"hackban User ID for *reason*");
            return;
        }

        try
        {
            user = event.getJDA().retrieveUserById(target).complete();
        }
        catch(Exception e)
        {
            event.replyError("That ID isn't valid!");
            return;
        }

        String success = "**"+user.getName()+"**#**"+user.getDiscriminator()+"**";

        if(event.getGuild().getMembers().contains(event.getGuild().getMemberById(target)))
        {
            event.replyWarning("This user is on this Guild! Please use `"+event.getClient().getPrefix()+"ban` instead.");
        }
        else
        {
            try
            {

                event.getGuild().getController().ban(user, 0).reason("["+author.getName()+"#"+author.getDiscriminator()+"]: "+reason).queue();

                ModLogging.logHackban(event.getAuthor(), user, reason, event.getGuild(), event.getTextChannel(), event.getMessage());

                event.replySuccess(Messages.HACKBAN_SUCCESS+success);
            }
            catch(Exception e)
            {
                event.replyError(Messages.HACKBAN_ERROR+user.getName()+"#"+user.getDiscriminator()+"**");
                SimpleLog.getLog("Hackban").fatal(e);
                e.printStackTrace();
            }
        }
    }
}

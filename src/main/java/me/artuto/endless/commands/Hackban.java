package me.artuto.endless.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.artuto.endless.Messages;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.loader.Config;
import me.artuto.endless.logging.ModLogging;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;

public class Hackban extends Command
{
    private final ModLogging modlog;
    private final Config config;

    public Hackban(ModLogging modlog, Config config)
    {
        this.modlog = modlog;
        this.config = config;
        this.name = "hackban";
        this.help = "Hackbans the specified user";
        this.arguments = "<ID> for [reason]";
        this.category = Categories.MODERATION;
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.ownerCommand = false;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        User author = event.getAuthor();
        User user;
        String target;
        String reason;

        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Invalid Syntax: "+event.getClient().getPrefix()+"hackban <ID> for [reason]");
            return;
        }

        try
        {
            String[] args = event.getArgs().split(" for", 2);
            target = args[0];
            reason = args[1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            target = event.getArgs();
            reason = "[no reason specified]";
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

        String username = "**"+user.getName()+"**#**"+user.getDiscriminator()+"**";

        if(event.getGuild().getMembers().contains(event.getGuild().getMemberById(target)))
            event.replyWarning("This user is on this Guild! Please use `"+event.getClient().getPrefix()+"ban` instead.");
        else
        {
            try
            {
                event.getGuild().getController().ban(user, 1).reason("["+author.getName()+"#"+author.getDiscriminator()+"]: "+reason).complete();
                event.replySuccess(Messages.HACKBAN_SUCCESS+username);
                modlog.logHackban(event.getAuthor(), user, reason, event.getGuild(), event.getTextChannel());
            }
            catch(Exception e)
            {
                event.replyError(Messages.HACKBAN_ERROR+username);
                SimpleLog.getLog("Hackban").fatal(e);
                if(config.isDebugEnabled())
                    e.printStackTrace();
            }
        }
    }
}

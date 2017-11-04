package me.artuto.endless.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.JLDataManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

public class Leave extends Command
{
    private final JLDataManager db;

    public Leave(JLDataManager db)
    {
        this.db = db;
        this.name = "leave";
        this.children = new Command[]{new Change()};
        this.aliases = new String[]{"leavemessage", "leavemsg"};
        this.help = "Changes or shows the welcome message";
        this.category = Categories.TOOLS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        Guild guild = event.getGuild();
        String msg = db.getLeaveMessage(guild);

        if(!(msg==null))
            event.replySuccess("Leave message at **"+guild.getName()+"**: `"+msg+"`");
        else
            event.replyError("No message configured!");
    }

    private class Change extends Command
    {
        public Change()
        {
            this.name = "change";
            this.help = "Changes the welcome message";
            this.category = Categories.TOOLS;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a new leave message!");
                return;
            }

            db.setLeaveMessage(event.getGuild(), event.getArgs());
            event.replySuccess("leave message configured.");
        }
    }
}

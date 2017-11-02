package me.artuto.endless.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.tempdata.AfkManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

public class Afk extends Command
{
    public Afk()
    {
        this.name = "afk";
        this.help = "Mark yourself ask afk with a message";
        this.arguments = "[message]";
        this.category = Categories.TOOLS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }

    protected void execute(CommandEvent event)
    {
        User user = event.getAuthor();

        AfkManager.setAfk(user.getIdLong(), event.getArgs().isEmpty()?null:event.getArgs());
        event.replySuccess("**"+user.getName()+"** is now AFK!");
    }
}

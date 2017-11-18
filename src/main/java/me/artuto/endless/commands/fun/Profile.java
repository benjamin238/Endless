package me.artuto.endless.commands.fun;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.ProfileDataManager;
import net.dv8tion.jda.core.Permission;

public class Profile extends Command
{
    private final ProfileDataManager db;

    public Profile(ProfileDataManager db)
    {
        this.db = db;
        this.name = "profile";
        this.aliases = new String[]{"p"};
        this.help = "Displays or edits the profile of the specified user";
        this.arguments = "<user>";
        this.category = Categories.FUN;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {

    }
}

package me.artuto.endless.commands.tools;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.DatabaseManager;
import me.artuto.endless.data.GuildSettingsDataManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

import java.util.Collection;

public class Prefix extends Command
{
    private final DatabaseManager db;
    private final GuildSettingsDataManager gsdm;

    public Prefix(DatabaseManager db, GuildSettingsDataManager gsdm)
    {
        this.db = db;
        this.gsdm = gsdm;
        this.name = "prefix";
        this.children = new Command[]{new Add(), new Remove()};
        this.help = "Displays or adds a prefix";
        this.category = Categories.TOOLS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.ownerCommand = false;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        StringBuilder sb = new StringBuilder();
        Guild guild = event.getGuild();
        String defP = event.getClient().getPrefix();

        Collection<String> prefixes = db.getSettings(guild).getPrefixes();

        if(prefixes.isEmpty())
            event.reply("The prefix for this guild is `"+defP+"`");
        else
        {
            sb.append("`").append(defP).append("`");
            prefixes.forEach(p -> sb.append(", `").append(p).append("`"));

            event.reply("The prefixes on this guild are: "+sb.toString());
        }
    }

    private class Add extends Command
    {
        Add()
        {
            this.name = "add";
            this.help = "Adds a custom prefix";
            this.category = Categories.TOOLS;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            String args = event.getArgs();
            Guild guild = event.getGuild();

            if(args.isEmpty())
                event.replyWarning("You didn't provided me a prefix!");
            else
            {
                if(gsdm.addPrefix(guild, args.toLowerCase().trim()))
                    event.replySuccess("Successfully added prefix!");
                else
                    event.replyError("There was an error when adding the prefix. Contact the owner.");
            }
        }
    }

    private class Remove extends Command
    {
        Remove()
        {
            this.name = "remove";
            this.help = "Removes a custom prefix";
            this.category = Categories.TOOLS;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            String args = event.getArgs();
            Guild guild = event.getGuild();

            if(args.isEmpty())
                event.replyWarning("You didn't provided me a prefix!");
            else
            {
                if(gsdm.prefixExists(guild, args.toLowerCase().trim()))
                {
                    if(gsdm.removePrefix(guild, args.toLowerCase().trim()))
                        event.replySuccess("Successfully removed a prefix!");
                    else
                        event.replyError("There was an error when removing the prefix. Contact the owner.");
                }
                else
                    event.replyWarning("That prefix doesn't exists!");
            }
        }
    }
}

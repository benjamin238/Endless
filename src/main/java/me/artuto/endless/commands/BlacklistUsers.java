package me.artuto.endless.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.DatabaseManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.Set;
import java.util.stream.Collectors;

public class BlacklistUsers extends Command
{
    private final DatabaseManager db;

    public BlacklistUsers(DatabaseManager db)
    {
        this.db = db;
        this.name = "blacklistuser";
        this.help = "Adds, removes or displays the list with blacklisted users.";
        this.category = Categories.BOTADM;
        this.children = new Command[]{new Add(), new Remove(), new Check(), new BlacklistList()};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        String prefix = event.getClient().getPrefix();

        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Please choose a subcommand:\n" +
                    "- `"+prefix+"blacklistuser add`: Adds a user ID to the blacklisted users list.\n" +
                    "- `"+prefix+"blacklistuser remove`: Removes a user ID from the blacklisted users list.\n" +
                    "- `"+prefix+"blacklistuser list`: Displays blacklisted users.\n" +
                    "- `"+prefix+"blacklistuser check`: Checks if a user ID is blacklisted.");
        }
        else if(!(event.getArgs().contains("add")) || !(event.getArgs().contains("remove")) || !(event.getArgs().contains("list") || !(event.getArgs().contains("check"))))
        {
            event.replyWarning("Please choose a subcommand:\n" +
                    "- `"+prefix+"blacklistuser add`: Adds a user ID to the blacklisted users list.\n" +
                    "- `"+prefix+"blacklistuser remove`: Removes a user ID from the blacklisted users list.\n" +
                    "- `"+prefix+"blacklistuser list`: Displays blacklisted users.\n" +
                    "- `"+prefix+"blacklistuser check`: Checks if a user ID is blacklisted.");
        }
    }

    private class Add extends Command
    {
        Add()
        {
            this.name = "add";
            this.help = "Adds a user ID to the blacklisted users list.";
            this.arguments = "<user ID>";
            this.category = Categories.BOTADM;
            this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            User user;

            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Please specify a user ID!");
                return;
            }

            try
            {
                user = event.getJDA().retrieveUserById(event.getArgs()).complete();
            }
            catch(Exception e)
            {
                event.replyError("That ID isn't valid!");
                return;
            }

            if(db.isUserBlacklisted(user))
            {
                event.replyError("That user is already on the blacklist!");
                return;
            }

            try
            {
                db.addBlacklistUser(user);
                event.replySuccess("Added **"+user.getName()+"#"+user.getDiscriminator()+"** to the blacklist.");
            }
            catch(Exception e)
            {
                event.replyError("Something went wrong when adding the user: \n```"+e+"```");
            }
        }
    }

    private class Remove extends Command
    {
        Remove()
        {
            this.name = "remove";
            this.help = "Removes a user ID to the blacklisted users list.";
            this.arguments = "<user ID>";
            this.category = Categories.BOTADM;
            this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            User user;

            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Please specify a user ID!");
                return;
            }

            try
            {
                user = event.getJDA().retrieveUserById(event.getArgs()).complete();
            }
            catch(Exception e)
            {
                event.replyError("That ID isn't valid!");
                return;
            }

            try
            {
                if(!(db.isUserBlacklisted(user)))
                {
                    event.replyError("That ID isn't in the blacklist!");
                    return;
                }
            }
            catch(Exception e)
            {
                event.replyError("Something went wrong when getting the blacklisted users list: \n```"+e+"```");
                return;
            }

            try
            {
                db.removeBlacklistedUser(user);
                event.replySuccess("Removed **"+user.getName()+"#"+user.getDiscriminator()+"** from the blacklist.");
            }
            catch(Exception e)
            {
                event.replyError("Something went wrong when writing to the blacklisted users file: \n```"+e+"```");
            }
        }
    }

    private class BlacklistList extends Command
    {
        BlacklistList()
        {
            this.name = "list";
            this.help = "Displays blacklisted users.";
            this.category = Categories.BOTADM;
            this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            Set<User> list;
            EmbedBuilder builder = new EmbedBuilder();
            Color color;

            if(event.isFromType(ChannelType.PRIVATE))
            {
                color = Color.decode("#33ff00");
            }
            else
            {
                color = event.getGuild().getSelfMember().getColor();
            }

            try
            {
                list = db.getBlacklistedUsersList(event.getJDA());

                if(list.isEmpty())
                {
                    event.reply("The list is empty!");
                }
                else
                {
                    builder.setDescription(list.stream().map(u -> u.getName()+"#"+u.getDiscriminator()+" (ID: "+u.getId()+")").collect(Collectors.joining("\n")));
                    builder.setFooter(event.getSelfUser().getName()+"'s Blacklisted Users", event.getSelfUser().getEffectiveAvatarUrl());
                    builder.setColor(color);
                    event.reply(builder.build());
                }
            }
            catch(Exception e)
            {
                event.replyError("Something went wrong when getting the blacklisted users list: \n```"+e+"```");
            }
        }
    }

    private class Check extends Command
    {
        Check()
        {
            this.name = "check";
            this.help = "Checks if a user ID is blacklisted.";
            this.category = Categories.BOTADM;
            this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            User user;

            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Please specify a user ID!");
                return;
            }

            try
            {
                user = event.getJDA().retrieveUserById(event.getArgs()).complete();
            }
            catch(Exception e)
            {
                event.replyError("That ID isn't valid!");
                return;
            }

            try
            {
                if(!(db.isUserBlacklisted(user)))
                {
                    event.replySuccess("**"+user.getName()+"#"+user.getDiscriminator()+"** isn't blacklisted!");
                }
                else
                {
                    event.replySuccess("**"+user.getName()+"#"+user.getDiscriminator()+"** is blacklisted!");
                }
            }
            catch(Exception e)
            {
                event.replyError("Something went wrong when getting the blacklisted users list: \n```"+e+"```");
            }
        }
    }
}

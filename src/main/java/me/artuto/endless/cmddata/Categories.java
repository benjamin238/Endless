package me.artuto.endless.cmddata;

import com.jagrosh.jdautilities.commandclient.Command.Category;
import me.artuto.endless.data.DatabaseManager;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;

public class Categories
{
    private static DatabaseManager db;

    public Categories(DatabaseManager db)
    {
        Categories.db = db;
    }

    public static final Category BOT = new Category("Bot", event ->
    {
        SimpleLog LOG = SimpleLog.getLog("Blacklisted Users");
        User user = event.getAuthor();

        if(event.isOwner() || event.isCoOwner())
            return true;
        else
        {
            if(db.isUserBlacklisted(user))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else
                return true;
        }
    });

    public static final Category BOTADM = new Category("Bot Administration", event ->
    {
        if(event.isOwner() || event.isCoOwner())
            return true;
        else 
        {
            event.replyError("Sorry, but you don't have access to this command! Only Bot owners!");
            return false;
        }
    });

    public static final Category MODERATION = new Category("Moderation", event ->
    {
        SimpleLog LOG = SimpleLog.getLog("Blacklisted Users");
        User user = event.getAuthor();

        if(event.isOwner() || event.isCoOwner())
            return true;
        else
        {
            if(db.isUserBlacklisted(user))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else
                return true;
        }
    });

    public static final Category TOOLS = new Category("Tools", event ->
    {
        SimpleLog LOG = SimpleLog.getLog("Blacklisted Users");
        User user = event.getAuthor();

        if(event.isOwner() || event.isCoOwner())
            return true;
        else
        {
            if(db.isUserBlacklisted(user))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else
                return true;
        }
    });

    public static final Category UTILS = new Category("Utilities", event ->
    {
        SimpleLog LOG = SimpleLog.getLog("Blacklisted Users");
        User user = event.getAuthor();

        if(event.isOwner() || event.isCoOwner())
            return true;
        else
        {
            if(db.isUserBlacklisted(user))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else
                return true;
        }
    });

    public static final Category FUN = new Category("Fun", event ->
    {
        SimpleLog LOG = SimpleLog.getLog("Blacklisted Users");
        User user = event.getAuthor();

        if(event.isOwner() || event.isCoOwner())
            return true;
        else
        {
            if(db.isUserBlacklisted(user))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else
                return true;
        }
    });

    public static final Category OTHERS = new Category("Others", event ->
    {
        SimpleLog LOG = SimpleLog.getLog("Blacklisted Users");
        User user = event.getAuthor();

        if(event.isOwner() || event.isCoOwner())
            return true;
        else
        {
            if(db.isUserBlacklisted(user))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else
                return true;
        }
    });
}

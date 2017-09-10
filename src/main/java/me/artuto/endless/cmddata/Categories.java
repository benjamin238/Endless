package me.artuto.endless.cmddata;

import com.jagrosh.jdautilities.commandclient.Command.Category;
import me.artuto.endless.data.Blacklists;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;
import java.io.IOException;

public class Categories
{
    public static final Category BOT = new Category("Bot", event ->
    {
        SimpleLog LOG = SimpleLog.getLog("Blacklisted Users");
        String userId = event.getAuthor().getId();
        User user = event.getAuthor();

        try
        {
            if(Blacklists.isUserListed(userId))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else
            {
                if(event.isOwner() || event.isCoOwner())
                    return true;
                else
                    return true;
            }
        }
        catch(IOException e)
        {
            LOG.fatal("Failed to load blacklisted users: "+e);
            return true;
        }
    });

    public static final Category BOTADM = new Category("Bot Administration", event ->
    {
        if(event.isOwner()) || event.isCoOwner())
        {
            return true;
        }
        else 
        {
            event.replyError("Sorry, but you don't have access to this command! Only Bot owners!");
            return false;
        }
    });

    public static final Category MODERATION = new Category("Moderation", event ->
    {
        SimpleLog LOG = SimpleLog.getLog("Blacklisted Users");
        String userId = event.getAuthor().getId();
        User user = event.getAuthor();

        try
        {
            if(Blacklists.isUserListed(userId))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else
            {
                if(event.isOwner() || event.isCoOwner())
                    return true;
                else
                    return true;
            }
        }
        catch(IOException e)
        {
            LOG.fatal("Failed to load blacklisted users: "+e);
            return true;
        }
    });

    public static final Category TOOLS = new Category("Tools", event ->
    {
        SimpleLog LOG = SimpleLog.getLog("Blacklisted Users");
        String userId = event.getAuthor().getId();
        User user = event.getAuthor();

        try
        {
            if(Blacklists.isUserListed(userId))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else
            {
                if(event.isOwner() || event.isCoOwner())
                    return true;
                else
                    return true;
            }
        }
        catch(IOException e)
        {
            LOG.fatal("Failed to load blacklisted users: "+e);
            return true;
        }
    });

    public static final Category FUN = new Category("Fun", event ->
    {
        SimpleLog LOG = SimpleLog.getLog("Blacklisted Users");
        String userId = event.getAuthor().getId();
        User user = event.getAuthor();

        try
        {
            if(Blacklists.isUserListed(userId))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else
            {
                if(event.isOwner() || event.isCoOwner())
                    return true;
                else
                    return true;
            }
        }
        catch(IOException e)
        {
            LOG.fatal("Failed to load blacklisted users: "+e);
            return true;
        }
    });

    public static final Category OTHERS = new Category("Others", event ->
    {
        SimpleLog LOG = SimpleLog.getLog("Blacklisted Users");
        String userId = event.getAuthor().getId();
        User user = event.getAuthor();

        try
        {
            if(Blacklists.isUserListed(userId))
            {
                LOG.info("A Blacklisted user executed a command: "+user.getName()+"#"+user.getDiscriminator()+" (ID: "+user.getId()+")");
                event.replyError("I'm sorry, but the owner of this bot has blocked you from using **"+event.getJDA().getSelfUser().getName()+"**'s commands, if you want to know the reason or get un-blacklisted contact the owner.");
                return false;
            }
            else
            {
                if(event.isOwner() || event.isCoOwner())
                    return true;
                else
                    return true;
            }
        }
        catch(IOException e)
        {
            LOG.fatal("Failed to load blacklisted users: "+e);
            return true;
        }
    });
}

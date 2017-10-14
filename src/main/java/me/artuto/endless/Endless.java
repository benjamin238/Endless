/*
 * Copyright (C) 2017 Artu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.artuto.endless;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import java.io.IOException;
import java.sql.SQLException;
import javax.security.auth.login.LoginException;

import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.*;
import me.artuto.endless.data.DatabaseManager;
import me.artuto.endless.events.GuildBotEvents;
import me.artuto.endless.logging.ServerLogging;
import me.artuto.endless.utils.GuildUtils;
import me.artuto.endless.utils.ModLogging;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.SimpleLog;
import me.artuto.endless.loader.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artu
 */

public class Endless extends ListenerAdapter
{   
    private static final SimpleLog LOG = SimpleLog.getLog("Startup Checker");
    private static Config config;

    public static void main(String[] args) throws IOException, LoginException, IllegalArgumentException, RateLimitedException, InterruptedException, SQLException
    {
        try
        {
            config = new Config();
        }
        catch(Exception e)
        {
            LOG.fatal(e);
            e.printStackTrace();
            return;
        }

        //Register Commands and some other things

        EventWaiter waiter = new EventWaiter();
        Bot bot = new Bot(waiter, config);
        ModLogging modlog = new ModLogging(bot);
        CommandClientBuilder client = new CommandClientBuilder();
        Logger log = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        log.setLevel(Level.INFO);
        DatabaseManager db = new DatabaseManager(config.getDatabaseUrl(), config.getDatabaseUsername(), config.getDatabasePassword());
        Categories cat = new Categories(db);
        GuildUtils gutils = new GuildUtils(db);
        Long[] coOwners = config.getCoOwnerIds();
        String[] owners = new String[coOwners.length];

        for(int i = 0; i < owners.length; i++)
        {
            owners[i] = String.valueOf(owners[i]);
        }

        client.setOwnerId(String.valueOf(config.getOwnerId()));
        client.setServerInvite(Const.INVITE);
        client.setEmojis(config.getDoneEmote(), config.getWarnEmote(), config.getErrorEmote());
        client.setPrefix(config.getPrefix());
        client.setStatus(config.getStatus());
        client.setGame(Game.of(config.getGame()));
        if(!(owners.length>16))
        {
            client.setCoOwnerIds(owners);
        }
        if(!(config.getDBotsToken().isEmpty()))
        {
            client.setDiscordBotsKey(config.getDBotsToken());
        }
        if(!(config.getDBotsListToken().isEmpty()))
        {
            client.setDiscordBotListKey(config.getDBotsListToken());
        }
        client.addCommands(
        	    //Bot

                new About(),
                new Donate(),
                new Invite(),
                new Ping(),
                new Stats(),
                
                //Bot Administration
                
                new Bash(),
                new BlacklistUsers(db),
                new BotCPanel(),
                new Eval(),
                new Shutdown(db),
                
                //Moderation
                
                new Ban(),
                new Kick(),
                new Hackban(),
                new SoftBan(),
                new Unban(),
                
                //Settings
                
                new ServerSettings(db),
                
                //Tools
               
                new Avatar(),
                new DBansCheck(config),
                new GuildInfo(),
                new Lookup(),
                new RoleInfo(),
                new UserInfo(),
        
                //Fun

                new Cat(config),
                new Choose(),
                new Say(),

                //Utils

                new GoogleSearch(),
                new Translate());
        
        //JDA Connection

        JDA jda = new JDABuilder(AccountType.BOT)
            .setToken(config.getToken())
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .setGame(Game.of(Const.GAME_0))
            .addEventListener(waiter)
            .addEventListener(client.build())
            .addEventListener(bot)
            .addEventListener(new Endless())
            .addEventListener(new Logging())
            .addEventListener(new GuildBlacklist())
            .addEventListener(new ServerLogging(db))
            .addEventListener(new GuildBotEvents(config))
            .buildBlocking();

        LOG.info("Leaving Pointless Guilds...");
        GuildUtils.leaveBadGuilds(jda);
        LOG.info("Done!");
    }    

    //When ready print the bot info
    
    @Override
    public void onReady(ReadyEvent event)
    {
        SimpleLog LOG = SimpleLog.getLog("Endless");

        User selfuser = event.getJDA().getSelfUser();
        User owner = event.getJDA().getUserById(config.getOwnerId());
        String selfname = selfuser.getName()+"#"+selfuser.getDiscriminator();
        String selfid = selfuser.getId();
        String ownername = owner.getName()+"#"+owner.getDiscriminator();
        String ownerid = owner.getId();

        LOG.info("My robotic body is ready!");
        LOG.info("Logged in as: "+selfname+" ("+selfid+")");
        LOG.info("Using prefix: "+config.getPrefix());
        LOG.info("Owner: "+ownername+" ("+ownerid+")");

        event.getJDA().getPresence().setGame(Game.of("Type "+config.getPrefix()+"help | Version " + Const.VERSION + " | On " + event.getJDA().getGuilds().size() + " Guilds | " + event.getJDA().getUsers().size() + " Users | " + event.getJDA().getTextChannels().size() + " Channels"));
    }
}

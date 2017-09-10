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

import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import javax.security.auth.login.LoginException;

import me.artuto.endless.commands.*;
import me.artuto.endless.management.Optimizer;
import me.artuto.endless.utils.ModLogging;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.SimpleLog;
import me.artuto.endless.loader.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 *
 * @author Artu
 */

public class Endless extends ListenerAdapter
{   
    private static final SimpleLog LOG = SimpleLog.getLog("Startup Checker");

    public static void main(String[] args) throws IOException, LoginException, IllegalArgumentException, RateLimitedException, InterruptedException, SQLException, Exception
    {
        //Register Commands and some other things

        EventWaiter waiter = new EventWaiter();
        Bot bot = new Bot(waiter, new Config());
        ModLogging modlog = new ModLogging(bot);
        CommandClientBuilder client = new CommandClientBuilder();
        Timer time = new Timer();
        Optimizer free = new Optimizer();

        time.schedule(free, 10000,3600000);

        client.setOwnerId(Config.getOwnerId());
        client.setServerInvite(Const.INVITE);
        client.setEmojis(Config.getDoneEmote(), Config.getWarnEmote(), Config.getErrorEmote());
        client.setPrefix(Config.getPrefix());
        client.setStatus(Config.getStatus());
        if(!(Config.getCoOwnerId().isEmpty()))
        {
            client.setCoOwnerIds(Config.getCoOwnerId());
        }
        if(!(Config.getDBotsToken().isEmpty()))
        {
            client.setDiscordBotsKey(Config.getDBotsToken());
        }
        if(!(Config.getDBotsListToken().isEmpty()))
        {
            client.setDiscordBotListKey(Config.getDBotsListToken());
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
                new BlacklistUsers(),
                new BotCPanel(),
                new Eval(),
                new Shutdown(),
                
                //Moderation
                
                new Ban(),
                new Kick(),
                new Hackban(),
                new SoftBan(),
                new Unban(),
                
                //Settings
                
                new ServerSettings(bot),               
                
                //Tools
               
                new Avatar(),
                new DBansCheck(),
                new GuildInfo(),
                new Lookup(),
                new RoleInfo(),
                new UserInfo(),
        
                //Others
                
                new Choose(),
                new Say());
        
        //JDA Connection
          
        new JDABuilder(AccountType.BOT)
            .setToken(Config.getToken())
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .setGame(Game.of(Const.GAME_0))
            .addEventListener(waiter)
            .addEventListener(client.build())
            //.addEventListener(new ServerLogging())
            .addEventListener(bot)
            .addEventListener(new Endless())
            .addEventListener(new Logging())
            .addEventListener(new GuildBlacklist())
            .buildBlocking();                
    }    

    //When ready print the bot info
    
    @Override
    public void onReady(ReadyEvent event)
    {
        SimpleLog LOG = SimpleLog.getLog("Endless");

        User selfuser = event.getJDA().getSelfUser();
        User owner = event.getJDA().retrieveUserById(Config.getOwnerId()).complete();
        String selfname = selfuser.getName()+"#"+selfuser.getDiscriminator();
        String selfid = selfuser.getId();
        String ownername = owner.getName()+"#"+owner.getDiscriminator();
        String ownerid = owner.getId();

        LOG.info("My robotic body is ready!");
        LOG.info("Logged in as: "+selfname+" ("+selfid+")");
        LOG.info("Using prefix: "+Config.getPrefix());
        LOG.info("Owner: "+ownername+" ("+ownerid+")");

        event.getJDA().getPresence().setGame(Game.of("Type "+Config.getPrefix()+"help | Version " + Const.VERSION + " | On " + event.getJDA().getGuilds().size() + " Guilds | " + event.getJDA().getUsers().size() + " Users | " + event.getJDA().getTextChannels().size() + " Channels"));
    }
}

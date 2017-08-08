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
import java.io.File;
import java.io.IOException;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.SimpleLog;
import me.artuto.endless.loader.*;
import me.artuto.endless.commands.bot.*;
import me.artuto.endless.commands.botadm.*;
import me.artuto.endless.commands.moderation.*;
import me.artuto.endless.commands.others.*;
import me.artuto.endless.commands.tools.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 *
 * @author Artu
 */

public class Endless extends ListenerAdapter
{   
    private static final SimpleLog LOG = SimpleLog.getLog("Startup Checker");

    public static void main(String[] args) throws IOException, LoginException, IllegalArgumentException, RateLimitedException, InterruptedException
    {        
        Config config;
        
        try
        {
            config = new Config();
        } 
        catch(Exception e) 
        {
            SimpleLog.getLog("Config").fatal(e);
            return;
        }
        
        //Register Commands and some other things
        
        EventWaiter waiter = new EventWaiter();

        CommandClientBuilder client = new CommandClientBuilder();

        client.useDefaultGame();

        client.setOwnerId(Config.getOwnerId());
             
        client.setCoOwnerIds(Config.getCoOwnerId());
                
        client.setServerInvite(Const.INVITE);
        
        client.setEmojis(Const.DONE_E, Const.WARN_E, Const.FAIL_E);

        client.setPrefix(Config.getPrefix());
        
        client.addCommands(
        		
        	//Bot

                new About(),
                new Donate(),
                new Invite(),
                new Ping(),
                new Stats(),
                
                //Bot Administration
                
                new Bash(),
                new BotCPanel(),
                new Eval(),
                new Shutdown(),
                
                //Moderation
                
                new Ban(),
                new Kick(),
                new Unban(),
                
                //Tools
               
                new DBansCheck(),
                new GuildInfo(),
                new Lookup(),
                new UserInfo(),
        
                //Others
                
                new Choose(),
                new Say());
        
        //JDA Connection
          
        new JDABuilder(AccountType.BOT)
            .setToken(config.getToken())
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .setGame(Game.of(Const.GAME_0))
            .addEventListener(waiter)
            .addEventListener(client.build())
            .addEventListener(new Bot())
            .addEventListener(new Endless())
            .addEventListener(new Logging())
            .buildBlocking();                
    }    
        
        //When ready print the bot info
    
    @Override
    public void onReady(ReadyEvent event)
    {
        System.out.println("[ENDLESS]: My robotic body is ready!\n"
            + "[ENDLESS]: Logged in as: "+event.getJDA().getSelfUser().getName()+"#"+event.getJDA().getSelfUser().getDiscriminator()
            + "("+event.getJDA().getSelfUser().getId()+")\n"
            + "[ENDLESS]: Using prefix: "+Config.getPrefix()+"\n"
            + "[ENDLESS]: Owner: "+Config.getOwnerTag()+"("+Config.getOwnerId()+")\n"
            + "[ENDLESS]: Co-Owner: "+Config.getCoOwnerTag()+" ("+Config.getCoOwnerId()+")\n");
    }
}

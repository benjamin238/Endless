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
import com.jagrosh.jdautilities.commandclient.CommandClient;
import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.security.auth.login.LoginException;

import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.*;
import me.artuto.endless.data.*;
import me.artuto.endless.events.GuildEvents;
import me.artuto.endless.events.UserEvents;
import me.artuto.endless.logging.ServerLogging;
import me.artuto.endless.utils.GuildUtils;
import me.artuto.endless.logging.ModLogging;
import net.dv8tion.jda.core.*;
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
    private static final SimpleLog LOG = SimpleLog.getLog("Endless");
    private static Config config;
    private static ScheduledExecutorService threads = Executors.newSingleThreadScheduledExecutor();
    private static EventWaiter waiter = new EventWaiter();
    private static Bot bot = new Bot(config);
    private static BlacklistDataManager bdm;
    private static DatabaseManager db;
    private static JLDataManager jldm;
    private static LoggingDataManager ldm;
    private static TagDataManager tdm;
    private static Logger log = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private static ModLogging modlog = new ModLogging(ldm);
    private Categories cat = new Categories(bdm);
    private GuildUtils gutils = new GuildUtils(config, db);
    private static JDA jda;

    public static void main(String[] args) throws IOException, SQLException, LoginException, RateLimitedException, InterruptedException
    {
        log.setLevel(Level.INFO);

        LOG.info("Starting Endless "+Const.VERSION+"...");
        LOG.info("Loading config file...");

        try
        {
            config = new Config();
            LOG.info("Successfully loaded config file!");
        }
        catch(Exception e)
        {
            LOG.fatal("No valid config file found! Make sure you edited the config.yml.sample file!");
            e.printStackTrace();
            return;
        }

        LOG.info("Starting Database and Managers...");
        initializeData();
        LOG.info("Successfully loaded Databases and Managers!");

        LOG.info("Starting JDA...");
        startJda();
    }

    public static void initializeData() throws SQLException
    {
        db = new DatabaseManager(config.getDatabaseUrl(), config.getDatabaseUsername(), config.getDatabasePassword());
        bdm = new BlacklistDataManager(db);
        ldm = new LoggingDataManager(db);
        jldm = new JLDataManager(db);
        tdm = new TagDataManager(db);
    }

    public static CommandClient createClient()
    {
        CommandClientBuilder client = new CommandClientBuilder();
        Long[] coOwners = config.getCoOwnerIds();
        String[] owners = new String[coOwners.length];

        for(int i = 0; i < owners.length; i++)
        {
            owners[i] = String.valueOf(coOwners[i]);
        }

        client.setOwnerId(String.valueOf(config.getOwnerId()));
        client.setServerInvite(Const.INVITE);
        client.setEmojis(config.getDoneEmote(), config.getWarnEmote(), config.getErrorEmote());
        client.setPrefix(config.getPrefix());
        client.setStatus(config.getStatus());
        client.setGame(Game.of(config.getGame()));

        if(!(owners.toString().isEmpty()))
            client.setCoOwnerIds(owners);
        if(!(config.getDBotsToken().isEmpty() || config.getDBotsToken()==null))
            client.setDiscordBotsKey(config.getDBotsToken());
        if(!(config.getDBotsListToken().isEmpty() || config.getDBotsListToken()==null))
            client.setDiscordBotListKey(config.getDBotsListToken());

        client.addCommands(
                //Bot

                new About(),
                new Donate(),
                new Invite(),
                new Ping(),
                new Stats(),

                //Bot Administration

                new Bash(),
                new BlacklistUsers(bdm),
                new BotCPanel(),
                new Eval(config, db, ldm, bdm, jldm, tdm, modlog),
                new Shutdown(db),

                //Moderation

                new Ban(modlog, config),
                new Clear(modlog, threads),
                new Kick(modlog, config),
                new Hackban(modlog, config),
                new SoftBan(modlog, config),
                new Unban(modlog, config),

                //Settings

                new Leave(jldm),
                new ServerSettings(ldm, jldm),
                new Welcome(jldm),

                //Tools

                new Afk(),
                new Avatar(),
                new DBansCheck(config),
                new GuildInfo(),
                new Lookup(),
                new RoleInfo(),
                new UserInfo(),

                //Fun

                new Cat(config),
                new Choose(),
                new Dog(config),
                new GiphyGif(config),
                new Say(),
                new Tag(tdm),

                //Utils

                new GoogleSearch(),
                new Translate(config));

        return client.build();

    }

    public static JDA startJda() throws LoginException, RateLimitedException, InterruptedException
    {
        jda = new JDABuilder(AccountType.BOT)
                .setToken(config.getToken())
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.of(Const.GAME_0))
                .addEventListener(waiter)
                .addEventListener(createClient())
                .addEventListener(bot)
                .addEventListener(new Endless())
                .addEventListener(new Logging(config))
                .addEventListener(new GuildBlacklist())
                .addEventListener(new ServerLogging(ldm, jldm))
                .addEventListener(new GuildEvents(config, tdm))
                .addEventListener(new UserEvents(config))
                .buildBlocking();

        return jda;
    }

    //When ready print the bot info
    
    @Override
    public void onReady(ReadyEvent event)
    {
        LOG.info("Leaving Pointless Guilds...");
        GuildUtils.leaveBadGuilds(event.getJDA());
        LOG.info("Done!");

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

        if(event.getJDA().getGuilds().isEmpty())
        {
            SimpleLog.getLog("Startup Checker").warn("Looks like your bot isn't on any guild! Add your bot using the following link:");
            SimpleLog.getLog("Startup Checker").warn(event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR));
        }
    }
}

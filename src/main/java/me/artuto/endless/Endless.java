/*
 * Copyright (C) 2017-2018 Artuto
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
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.bot.*;
import me.artuto.endless.commands.botadm.*;
import me.artuto.endless.commands.botadm.Shutdown;
import me.artuto.endless.commands.fun.*;
import me.artuto.endless.commands.moderation.*;
import me.artuto.endless.commands.tools.*;
import me.artuto.endless.commands.utils.GoogleSearch;
import me.artuto.endless.commands.utils.TimeFor;
import me.artuto.endless.commands.utils.Translate;
import me.artuto.endless.data.*;
import me.artuto.endless.events.GuildEvents;
import me.artuto.endless.events.StarboardEvents;
import me.artuto.endless.events.UserEvents;
import me.artuto.endless.loader.Config;
import me.artuto.endless.loader.Logging;
import me.artuto.endless.logging.ModLogging;
import me.artuto.endless.logging.ServerLogging;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author Artu
 */

public class Endless extends ListenerAdapter
{
    private static Config config;
    private static ScheduledExecutorService threads = Executors.newSingleThreadScheduledExecutor();
    private static EventWaiter waiter = new EventWaiter();
    private static Bot bot;
    private static BlacklistDataManager bdm;
    private static DatabaseManager db;
    private static DonatorsDataManager ddm;
    private static GuildSettingsDataManager gsdm;
    private static ProfileDataManager pdm;
    private static StarboardDataManager sdm;
    private static TagDataManager tdm;
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private static Logger LOG = (Logger) LoggerFactory.getLogger("Endless");
    private static ModLogging modlog;

    // Threads
    private static ScheduledExecutorService cmdThread = new ScheduledThreadPoolExecutor(2, new Bot.EndlessThreadFactory("Command"));

    public static void main(String[] args) throws Exception
    {
        LOGGER.setLevel(Level.INFO);

        LOG.info("Starting Endless "+Const.VERSION+"...");
        LOG.info("Loading config file...");

        try
        {
            config = new Config();
            LOG.info("Successfully loaded config file!");
        }
        catch(Exception e)
        {
            LOG.error("No valid config file found! Make sure you edited the config.yml.sample file!");
            e.printStackTrace();
            return;
        }

        LOG.info("Starting Database and Managers...");
        initializeData();
        LOG.info("Successfully loaded Databases and Managers!");

        LOG.info("Starting JDA...");
        startJda();
    }

    private static void initializeData() throws SQLException
    {
        db = new DatabaseManager(config.getDatabaseUrl(), config.getDatabaseUsername(), config.getDatabasePassword());
        bdm = new BlacklistDataManager(db);
        ddm = new DonatorsDataManager(db);
        gsdm = new GuildSettingsDataManager(db);
        pdm = new ProfileDataManager(db);
        sdm = new StarboardDataManager(db);
        tdm = new TagDataManager(db);
        modlog = new ModLogging(gsdm);
        new GuildUtils(config, db);
        new Categories(bdm);
    }

    /*private static void startAPI()
    {
        API.main(config.getAPIToken(), config, bot);
    }*/

    private static CommandClient createClient()
    {
        CommandClientBuilder client = new CommandClientBuilder();
        Long[] coOwners = config.getCoOwnerIds();
        String[] owners = new String[coOwners.length];

        for(int i = 0; i<owners.length; i++)
            owners[i] = String.valueOf(coOwners[i]);

        client.setOwnerId(String.valueOf(config.getOwnerId()));
        client.setServerInvite(Const.INVITE);
        client.setEmojis(config.getDoneEmote(), config.getWarnEmote(), config.getErrorEmote());
        client.setPrefix(config.getPrefix());
        client.setAlternativePrefix("<@310578566695878658>");
        client.setGuildSettingsManager(new ClientGSDM(db, gsdm));
        client.setStatus(OnlineStatus.ONLINE);
        client.setGame(Game.playing("Type e!help"));
        client.setScheduleExecutor(cmdThread);

        if(!(Arrays.toString(owners).isEmpty())) client.setCoOwnerIds(owners);
        if(!(config.getDBotsToken().isEmpty() || config.getDBotsToken() == null))
            client.setDiscordBotsKey(config.getDBotsToken());
        if(!(config.getDBotsListToken().isEmpty() || config.getDBotsListToken() == null))
            client.setDiscordBotListKey(config.getDBotsListToken());

        client.addCommands(
                //Bot
                new About(), new Donate(ddm), new Invite(), new Ping(), new Stats(),

                //Bot Administration
                new Bash(), new BlacklistUsers(bdm), new BotCPanel(), new Eval(config, db, ddm, gsdm, bdm, sdm, tdm, modlog), new Shutdown(db),

                //Moderation
                new Ban(modlog, config), new Clear(modlog, threads), new DBansCheck(config), new Kick(modlog, config), new Hackban(modlog, config), new SoftBan(modlog, config), new Unban(modlog, config),

                //Settings
                new Leave(gsdm), new Prefix(db, gsdm), new ServerSettings(gsdm), new Starboard(gsdm, waiter), new Welcome(gsdm),

                //Tools
                new Afk(), new Avatar(), new GuildInfo(), new Lookup(), new RoleCmd(), new UserInfo(),

                //Fun
                new Cat(config), new Choose(), new Dog(config), new GiphyGif(config), new Profile(pdm), new Say(), new Tag(tdm),

                //Utils
                new GoogleSearch(), new TimeFor(pdm), new Translate(config));

        return client.build();

    }

    private static void startJda() throws LoginException
    {
        new JDABuilder(AccountType.BOT)
                .setToken(config.getToken())
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.playing("Loading..."))
                .setBulkDeleteSplittingEnabled(false)
                .setAutoReconnect(true)
                .setEnableShutdownHook(true)
                .addEventListener(waiter, createClient(), new Endless(),
                        new Logging(config), new ServerLogging(gsdm), new GuildEvents(config, tdm, gsdm, bdm),
                        new StarboardEvents(gsdm, sdm), new UserEvents(config))
                .buildAsync();
    }

    //When ready print the bot info

    @Override
    public void onReady(ReadyEvent event)
    {
        bot = new Bot(config, event.getJDA());
        if(config.api())
        {
            LOG.info("Starting the API...");
            //startAPI();
            LOG.info("Successfully started the API!");
        }

        LOG.info("Leaving Pointless Guilds...");
        GuildUtils.leaveBadGuilds(event.getJDA());
        LOG.info("Done!");

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

        event.getJDA().getPresence().setGame(Game.playing("Type "+config.getPrefix()+"help | Version "+Const.VERSION+" | On "+event.getJDA().getGuilds().size()+" Guilds | "+event.getJDA().getUsers().size()+" Users | "+event.getJDA().getTextChannels().size()+" Channels"));
        event.getJDA().getPresence().setStatus(config.getStatus());

        if(event.getJDA().getGuilds().isEmpty())
        {
            Logger LOG = (Logger) LoggerFactory.getLogger("Startup Checker");

            LOG.warn("Looks like I'm on any guild! Add me using the following link:");
            LOG.warn(event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR));
        }
    }
}

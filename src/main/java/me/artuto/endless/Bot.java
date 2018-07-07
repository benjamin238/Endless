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

import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.artuto.endless.bootloader.StartupChecker;
import me.artuto.endless.bootloader.ThreadLoader;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.commands.cmddata.CommandHelper;
import me.artuto.endless.commands.bot.*;
import me.artuto.endless.commands.botadm.*;
import me.artuto.endless.commands.fun.*;
import me.artuto.endless.commands.moderation.*;
import me.artuto.endless.commands.serverconfig.*;
import me.artuto.endless.commands.tools.*;
import me.artuto.endless.commands.utils.*;
import me.artuto.endless.core.EndlessCoreBuilder;
import me.artuto.endless.core.EndlessSharded;
import me.artuto.endless.core.EndlessShardedBuilder;
import me.artuto.endless.handlers.IgnoreHandler;
import me.artuto.endless.storage.data.Database;
import me.artuto.endless.storage.data.managers.*;
import me.artuto.endless.core.exceptions.ConfigException;
import me.artuto.endless.handlers.BlacklistHandler;
import me.artuto.endless.handlers.SpecialCaseHandler;
import me.artuto.endless.loader.Config;
import me.artuto.endless.logging.*;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.StatusChangeEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Artuto
 */

public class Bot extends ListenerAdapter
{
    public boolean dataEnabled;
    public boolean maintenance;
    public boolean initialized = false;
    public EndlessSharded endless;
    private EndlessShardedBuilder endlessBuilder;

    // Config
    public Config config;

    // Data Managers
    public BlacklistDataManager bdm;
    public Database db;
    public DonatorsDataManager ddm;
    public GuildSettingsDataManager gsdm;
    public PollsDataManager pldm;
    public PunishmentsDataManager pdm;
    public ProfileDataManager prdm;
    public RemindersDataManager rdm;
    public StarboardDataManager sdm;
    public TagDataManager tdm;

    // Discord
    public CommandClient client;
    public ShardManager shardManager;

    // EventWaiter
    public EventWaiter waiter;

    // Logging
    public BotLogging botlog;
    public ModLogging modlog;
    public ServerLogging serverlog;

    // Loggers
    private final Logger CONFIGLOG = (Logger)LoggerFactory.getLogger("Config Loader");

    // Schedulers;
    private ScheduledExecutorService muteScheduler;
    private ScheduledExecutorService optimizerScheduler;
    private ScheduledExecutorService pollScheduler;
    private ScheduledExecutorService reminderScheduler;

    // Threads
    public ScheduledExecutorService clearThread;
    public ScheduledExecutorService starboardThread;

    // Webhooks
    WebhookClient logWebhook;

    public static Bot getInstance()
    {
        return Endless.bot;
    }

    void boot(boolean dataEnabled, boolean maintenance) throws LoginException, SQLException
    {
        this.dataEnabled = dataEnabled;
        this.maintenance = maintenance;
        Endless.LOG.info("Starting Endless "+Const.VERSION+"...");
        if(!(dataEnabled))
            Endless.LOG.warn("WARNING - Starting on No-data Mode - WARNING");
        if(maintenance)
            Endless.LOG.warn("WARNING - Starting on Maintenance Mode - WARNING");

        CONFIGLOG.info("Loading config file...");

        try
        {
            config = new Config();
            StartupChecker.checkConfig(config);
            CONFIGLOG.info("Successfully loaded config file!");
        }
        catch(Exception e)
        {
            throw new ConfigException(e.getMessage());
        }

        if(dataEnabled)
        {
            db = new Database(config.getDatabaseUrl(), config.getDatabaseUsername(), config.getDatabasePassword());
            bdm = new BlacklistDataManager(this);
            ddm = new DonatorsDataManager(db);
            gsdm = new GuildSettingsDataManager(this);
            pldm = new PollsDataManager(db);
            pdm = new PunishmentsDataManager(db);
            prdm = new ProfileDataManager(db);
            rdm = new RemindersDataManager(db);
            sdm = new StarboardDataManager(db);
            tdm = new TagDataManager(this);
        }
        BlacklistHandler bHandler = new BlacklistHandler(this);
        IgnoreHandler iHandler = new IgnoreHandler(this);
        SpecialCaseHandler sHandler = new SpecialCaseHandler();
        new Categories(maintenance, bHandler, iHandler, sHandler);
        new GuildUtils(this);

        botlog = new BotLogging(this);
        logWebhook = new WebhookClientBuilder(config.getBotlogWebhook())
                .setExecutorService(ThreadLoader.createThread("Botlog")).setDaemon(true).build();
        modlog = new ModLogging(this);
        serverlog = new ServerLogging(this);

        if(dataEnabled)
        {
            clearThread = ThreadLoader.createThread("Clear Command");
            muteScheduler = ThreadLoader.createThread("Mutes");
            pollScheduler = ThreadLoader.createThread("Polls");
            reminderScheduler = ThreadLoader.createThread("Reminders");
            starboardThread = ThreadLoader.createThread("Starboard");
        }
        optimizerScheduler = ThreadLoader.createThread("Optimizer");

        waiter = new EventWaiter();
        Listener listener = new Listener(this);

        CommandClientBuilder clientBuilder = new CommandClientBuilder();
        Long[] coOwners = config.getCoOwnerIds();
        String[] owners = new String[coOwners.length];

        for(int i = 0; i<owners.length; i++)
            owners[i] = String.valueOf(coOwners[i]);

        clientBuilder.setOwnerId(String.valueOf(config.getOwnerId()))
                .setServerInvite(Const.INVITE)
                .setEmojis(config.getDoneEmote(), config.getWarnEmote(), config.getErrorEmote())
                .setGame(null)
                .setStatus(null)
                .setPrefix(config.getPrefix())
                .setAlternativePrefix("@mention");
        if(dataEnabled)
            clientBuilder.setGuildSettingsManager(new ClientGSDM());
        clientBuilder.setScheduleExecutor(ThreadLoader.createThread("Commands"))
                .setListener(new CommandLogging(this))
                .setLinkedCacheSize(6)
                .setHelpConsumer(CommandHelper::getHelp);

        if(!(owners.length==0))
            clientBuilder.setCoOwnerIds(owners);
        if(!(config.getDBotsToken().isEmpty()))
            clientBuilder.setDiscordBotsKey(config.getDBotsToken());
        if(!(config.getDBotsListToken().isEmpty()))
            clientBuilder.setDiscordBotListKey(config.getDBotsListToken());

        clientBuilder.addCommands(
                // Bot
                new AboutCmd(this), new DonateCmd(this), new InviteCmd(), new PingCmd(),

                // Bot Administration
                new BashCmd(), new BlacklistGuildCmd(this), new BlacklistUserCmd(this),
                new BotCPanelCmd(), new EvalCmd(this), new RestartShardCmd(), new ShutdownCmd(this), new StatusCmd(),

                // Fun
                new CatCmd(this), new ChooseCmd(), new DogCmd(this),
                new GiphyGifCmd(this), new ProfileCmd(this), new SayCmd(), new TagCmd(this),

                // Moderation
                new BanCmd(this), new ClearCmd(this), new DBansCheckCmd(this), new KickCmd(this),
                new HackbanCmd(this), new MuteCmd(this), new SoftbanCmd(this), new UnbanCmd(this),

                // Server Settings
                new IgnoreCmd(this), new LeaveCmd(this), new PrefixCmd(this), new ServerSettingsCmd(this),
                new SetupCmd(this), new StarboardCmd(this), new WelcomeCmd(this),

                // Tools
                new AfkCmd(), new AnnouncementCmd(), new AvatarCmd(), new EmoteCmd(), new GuildInfoCmd(),
                new LookupCmd(), new PollCmd(this), new QuoteCmd(), new RoleCmd(), new UserInfoCmd(),

                // Utils
                new GoogleSearchCmd(), new ReminderCmd(this), new RoleMeCmd(this),
                new TimeForCmd(this), new TranslateCmd(this), new WeatherCmd(this), new YouTubeCmd(this));

        client = clientBuilder.build();
        Endless.LOG.info("Starting JDA...");

        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder()
                .setToken(config.getToken())
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.playing("[ENDLESS] Loading..."))
                .setBulkDeleteSplittingEnabled(false)
                .setAutoReconnect(true)
                .setEnableShutdownHook(true);
        if(maintenance)
            builder.addEventListeners(this, client);
        else
            builder.addEventListeners(this, client, listener, waiter);
        shardManager = builder.build();

        endlessBuilder = new EndlessShardedBuilder(this, shardManager);
    }

    @Override
    public void onReady(ReadyEvent event)
    {
        if(endless==null && !(initialized))
            endlessBuilder.addShard(new EndlessCoreBuilder(this, event.getJDA())
                    .setCommandClient(client)
                    .build());
    }

    @Override
    public void onStatusChange(StatusChangeEvent event)
    {
        if(event.getNewStatus()==JDA.Status.CONNECTED)
        {
            if(event.getJDA().asBot().getShardManager().getShards().stream().allMatch(shard -> shard.getStatus()==JDA.Status.CONNECTED)
                    && !(initialized))
            {
                this.endless = endlessBuilder.build();
                logWebhook.close();
                if(dataEnabled)
                {
                    muteScheduler.scheduleWithFixedDelay(() -> pdm.updateTempPunishments(Const.PunishmentType.TEMPMUTE, shardManager),
                            0, 10, TimeUnit.SECONDS);
                    pollScheduler.scheduleWithFixedDelay(() -> pldm.updatePolls(shardManager), 0, 10, TimeUnit.SECONDS);
                    reminderScheduler.scheduleWithFixedDelay(() -> rdm.updateReminders(shardManager), 0, 10, TimeUnit.SECONDS);
                }
                optimizerScheduler.scheduleWithFixedDelay(System::gc, 5, 30, TimeUnit.MINUTES);
            }
        }
    }
}

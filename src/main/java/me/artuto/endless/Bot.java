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

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.artuto.endless.bootloader.EndlessLoader;
import me.artuto.endless.cmddata.CommandHelper;
import me.artuto.endless.commands.bot.*;
import me.artuto.endless.commands.botadm.*;
import me.artuto.endless.commands.fun.*;
import me.artuto.endless.commands.moderation.*;
import me.artuto.endless.commands.serverconfig.*;
import me.artuto.endless.commands.tools.*;
import me.artuto.endless.commands.utils.*;
import me.artuto.endless.data.Database;
import me.artuto.endless.data.managers.*;
import me.artuto.endless.loader.Config;
import me.artuto.endless.logging.*;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Artuto
 */

public class Bot
{
    public boolean maintenance;
    private final EndlessLoader loader = new EndlessLoader(this);

    // Config
    public Config config;

    // Data Managers
    public BlacklistDataManager bdm;
    public Database db;
    public DonatorsDataManager ddm;
    public GuildSettingsDataManager gsdm;
    public PunishmentsDataManager pdm;
    public ProfileDataManager prdm;
    public StarboardDataManager sdm;
    public TagDataManager tdm;

    // Discord
    public CommandClient client;
    public ShardManager shards;

    // EventWaiter
    public EventWaiter waiter;

    // Logging
    public BotLogging botlog;
    public ModLogging modlog;
    public ServerLogging serverlog;

    // Schedulers
    public ScheduledExecutorService muteScheduler;

    // Threads
    public ScheduledExecutorService clearThread;
    public ScheduledExecutorService starboardThread;

    // Webhooks
    public WebhookClient logWebhook;

    public static Bot getInstance()
    {
        return Endless.getBot();
    }

    void boot(boolean maintenance) throws LoginException
    {
        this.maintenance = maintenance;
        Endless.LOG.info("Starting Endless "+Const.VERSION+"...");
        if(maintenance)
            Endless.LOG.warn("WARNING - Starting on Maintenance Mode - WARNING");
        loader.preLoad();
        config = loader.config;

        loader.databaseLoad(maintenance);
        bdm = loader.dbLoader.getBlacklistDataManager();
        db = loader.dbLoader.getDatabaseManager();
        ddm = loader.dbLoader.getDonatorsDataManager();
        gsdm = loader.dbLoader.getGuildSettingsDataManager();
        pdm = loader.dbLoader.getPunishmentsDataManager();
        prdm = loader.dbLoader.getProfileDataManager();
        sdm = loader.dbLoader.getStarbordDataManager();
        tdm = loader.dbLoader.getTagDataManager();

        loader.logLoad();
        botlog = loader.botlog;
        logWebhook = new WebhookClientBuilder(config.getBotlogWebhook()).setExecutorService(loader.botlogThread).setDaemon(true).build();
        modlog = loader.modlog;
        serverlog = loader.serverlog;

        loader.threadLoad();
        clearThread = loader.clearThread;
        muteScheduler = loader.muteScheduler;
        starboardThread = loader.starboardThread;

        loader.waiterLoad();
        waiter = loader.waiter;

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
                .setAlternativePrefix("@mention")
                .setGuildSettingsManager(new ClientGSDM(db))
                .setScheduleExecutor(loader.cmdThread)
                .setListener(new CommandLogging(this))
                .setLinkedCacheSize(6)
                .setHelpConsumer(CommandHelper::getHelp);

        if(!(owners.length==0))
            clientBuilder.setCoOwnerIds(owners);
        if(!(config.getDBotsToken().isEmpty() || config.getDBotsToken()==null))
            clientBuilder.setDiscordBotsKey(config.getDBotsToken());
        if(!(config.getDBotsListToken().isEmpty() || config.getDBotsListToken()==null))
            clientBuilder.setDiscordBotListKey(config.getDBotsListToken());

        clientBuilder.addCommands(
                // Bot
                new AboutCmd(this), new DonateCmd(this), new InviteCmd(), new PingCmd(),

                // Bot Administration
                new BashCmd(), new BlacklistGuildCmd(this), new BlacklistUserCmd(this),
                new BotCPanelCmd(), new EvalCmd(this), new ShutdownCmd(this), new StatusCmd(),

                // Fun
                new CatCmd(this), new ChooseCmd(), new DogCmd(this),
                new GiphyGifCmd(this), new ProfileCmd(this), new SayCmd(), new TagCmd(this),

                // Moderation
                new BanCmd(this), new ClearCmd(this), new DBansCheckCmd(this), new KickCmd(this),
                new HackbanCmd(this), new MuteCmd(this), new SoftbanCmd(this), new UnbanCmd(this),

                // Server Settings
                new LeaveCmd(this), new PrefixCmd(this), new ServerSettingsCmd(this),
                new SetupCmd(this), new StarboardCmd(this), new WelcomeCmd(this),

                // Tools
                new AfkCmd(), new AnnouncementCmd(), new AvatarCmd(), new GuildInfoCmd(),
                new LookupCmd(), new QuoteCmd(), new RoleCmd(), new UserInfoCmd(),

                // Utils
                new GoogleSearchCmd(), new RoleMeCmd(this), new TimeForCmd(this), new TranslateCmd(this));

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
            builder.addEventListeners(client);
        else
            builder.addEventListeners(loader.waiter, client, new Listener(this));

        shards = builder.build();

        /*JDABuilder builder = new JDABuilder(AccountType.BOT)
                .setToken(config.getToken())
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.playing("[ENDLESS] Loading..."))
                .setBulkDeleteSplittingEnabled(false)
                .setAutoReconnect(true)
                .setEnableShutdownHook(true);
        if(maintenance)
            builder.addEventListener(client.build(), new Bot());
        else
            builder.addEventListener(loader.waiter, client.build(), new BotEvents(this, loader.botlogThread, false),
                    new ServerLogging(gsdm), new GuildEvents(this),
                    new StarboardEvents(gsdm, sdm, loader.starboardThread), new UserEvents(config));

        builder.buildAsync();*/
    }
}

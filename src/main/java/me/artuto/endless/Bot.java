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
import me.artuto.endless.events.*;
import me.artuto.endless.loader.Config;
import me.artuto.endless.logging.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Artuto
 */

public class Bot extends ListenerAdapter
{
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

    // EventWaiter
    public EventWaiter waiter;

    // Logging
    public ModLogging modlog;

    // Schedulers
    public ScheduledExecutorService muteScheduler;

    // Threads
    public ScheduledExecutorService clearThread;

    public void boot(boolean maintenance) throws LoginException
    {
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
        modlog = loader.dbLoader.getModlog();

        loader.threadLoad();
        clearThread = loader.clearThread;
        loader.waiterLoad();
        waiter = loader.waiter;

        muteScheduler = loader.muteScheduler;

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
        client.setScheduleExecutor(loader.cmdThread);
        client.setListener(new CommandLogging(this));
        client.setLinkedCacheSize(6);
        client.setHelpConsumer(CommandHelper::getHelp);

        if(maintenance)
        {
            client.setGame(Game.playing("Maintenance mode enabled"));
            client.setStatus(OnlineStatus.DO_NOT_DISTURB);
        }
        else
        {
            client.setGame(Game.playing("Type e!help"));
            client.setStatus(OnlineStatus.ONLINE);
        }

        if(!(Arrays.toString(owners).isEmpty()))
            client.setCoOwnerIds(owners);
        if(!(config.getDBotsToken().isEmpty() || config.getDBotsToken()==null))
            client.setDiscordBotsKey(config.getDBotsToken());
        if(!(config.getDBotsListToken().isEmpty() || config.getDBotsListToken()==null))
            client.setDiscordBotListKey(config.getDBotsListToken());

        client.addCommands(
                //Bot
                new AboutCmd(this), new DonateCmd(this), new InviteCmd(), new PingCmd(), new StatsCmd(),

                //Bot Administration
                new BashCmd(), new BlacklistUsersCmd(this), new BotCPanelCmd(), new EvalCmd(this), new ShutdownCmd(),

                //Fun
                new CatCmd(this), new ChooseCmd(), new DogCmd(this),
                new GiphyGifCmd(this), new ProfileCmd(this), new SayCmd(), new TagCmd(this),

                //Moderation
                new BanCmd(this), new ClearCmd(this), new DBansCheckCmd(this), new KickCmd(this),
                new HackbanCmd(this), new MuteCmd(this), new SoftbanCmd(this), new UnbanCmd(this),

                //Server Settings
                new LeaveCmd(this), new PrefixCmd(this), new ServerSettingsCmd(this),
                new SetupCmd(this), new StarboardCmd(this), new WelcomeCmd(this),

                //Tools
                new AfkCmd(), new AnnouncementCmd(), new AvatarCmd(), new GuildInfoCmd(),
                new LookupCmd(), new QuoteCmd(), new RoleCmd(), new UserInfoCmd(),

                //Utils
                new GoogleSearchCmd(), new RoleMeCmd(this), new TimeForCmd(this), new TranslateCmd(this));

        Endless.LOG.info("Starting JDA...");

        JDABuilder builder = new JDABuilder(AccountType.BOT)
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

        builder.buildAsync();
    }
}

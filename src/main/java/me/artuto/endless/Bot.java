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
import me.artuto.endless.bootloader.EndlessLoader;
import me.artuto.endless.commands.bot.*;
import me.artuto.endless.commands.botadm.*;
import me.artuto.endless.commands.fun.*;
import me.artuto.endless.commands.moderation.*;
import me.artuto.endless.commands.tools.*;
import me.artuto.endless.commands.utils.*;
import me.artuto.endless.data.*;
import me.artuto.endless.events.*;
import me.artuto.endless.handlers.BlacklistHandler;
import me.artuto.endless.handlers.SpecialCaseHandler;
import me.artuto.endless.loader.Config;
import me.artuto.endless.logging.CommandLogging;
import me.artuto.endless.logging.ModLogging;
import me.artuto.endless.logging.ServerLogging;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

/**
 * @author Artuto
 */

public class Bot extends ListenerAdapter
{
    private final EndlessLoader loader = new EndlessLoader();
    public static Config config;

    public void boot(boolean maintenance) throws LoginException
    {
        Endless.LOG.info("Starting Endless "+Const.VERSION+"...");
        if(maintenance)
            Endless.LOG.warn("WARNING - Starting on Maintenance Mode - WARNING");
        loader.preLoad();
        config = loader.config;

        loader.databaseLoad(maintenance);
        BlacklistDataManager bdm = loader.dbLoader.getBlacklistDataManager();
        DatabaseManager db = loader.dbLoader.getDatabaseManager();
        DonatorsDataManager ddm = loader.dbLoader.getDonatorsDataManager();
        GuildSettingsDataManager gsdm = loader.dbLoader.getGuildSettingsDataManager();
        ProfileDataManager pdm = loader.dbLoader.getProfileDataManager();
        StarboardDataManager sdm = loader.dbLoader.getStarbordDataManager();
        TagDataManager tdm = loader.dbLoader.getTagDataManager();
        BlacklistHandler bHandler = loader.dbLoader.getBlacklistHandler();
        SpecialCaseHandler sHandler = loader.dbLoader.getSpecialCaseHandler();
        ModLogging modlog = loader.dbLoader.getModlog();

        loader.threadLoad();
        loader.waiterLoad();

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
        client.setListener(new CommandLogging());
        client.setLinkedCacheSize(6);

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
                new About(config), new Donate(ddm), new Invite(), new Ping(), new Stats(),

                //Bot Administration
                new Bash(), new BlacklistUsers(bdm), new BotCPanel(), new Eval(config, db, ddm, gsdm, bdm, sdm, tdm, modlog), new Shutdown(),

                //Moderation
                new Ban(modlog, config), new Clear(modlog, loader.cleanThread), new DBansCheck(config), new Kick(modlog, config), new Hackban(modlog, config), new SoftBan(modlog, config), new Unban(modlog, config),

                //Settings
                new Leave(gsdm), new Prefix(db, gsdm), new ServerSettings(gsdm), new Starboard(gsdm, loader.waiter), new Welcome(gsdm),

                //Tools
                new Afk(), new Avatar(), new GuildInfo(), new Lookup(), new Quote(), new RoleCmd(), new UserInfo(),

                //Fun
                new Cat(config), new Choose(), new Dog(config), new GiphyGif(config), new Profile(pdm), new Say(), new Tag(tdm),

                //Utils
                new GoogleSearch(), new TimeFor(pdm), new Translate(config));

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
            builder.addEventListener(loader.waiter, client.build(), new BotEvents(config, loader.botlogThread, false, db),
                    new ServerLogging(gsdm), new GuildEvents(config, tdm, gsdm, bdm),
                    new StarboardEvents(gsdm, sdm, loader.starboardThread), new UserEvents(config));

        builder.buildAsync();
    }
}

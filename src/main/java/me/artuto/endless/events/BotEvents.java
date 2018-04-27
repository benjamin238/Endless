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

package me.artuto.endless.events;

import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.Endless;
import me.artuto.endless.bootloader.EndlessLoader;
import me.artuto.endless.bootloader.StartupChecker;
import me.artuto.endless.data.DatabaseManager;
import me.artuto.endless.loader.Config;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Artuto
 */

public class BotEvents extends ListenerAdapter
{
    private final Config config;
    private final boolean maintenance;
    private final ScheduledExecutorService webhookExecutor;
    private final WebhookClient webhook;
    private final DatabaseManager db;

    public BotEvents(Config config, ScheduledExecutorService webhookExecutor, boolean maintenance, DatabaseManager db)
    {
        this.config = config;
        this.maintenance = maintenance;
        this.webhookExecutor = webhookExecutor;
        this.webhook = createWebhook();
        this.db = db;
    }

    private WebhookClient createWebhook()
    {
        return new WebhookClientBuilder(config.getBotlogWebhook()).setExecutorService(webhookExecutor).build();
    }

    @Override
    public void onReady(ReadyEvent event)
    {
        /*if(config.api())
        {
            LOG.info("Starting the API...");
            //startAPI();
            LOG.info("Successfully started the API!");
        }*/

        Endless.LOG.info("Leaving Pointless Guilds...");
        GuildUtils.leaveBadGuilds(event.getJDA());
        Endless.LOG.info("Done!");

        User selfuser = event.getJDA().getSelfUser();
        User owner = event.getJDA().getUserById(config.getOwnerId());

        Endless.LOG.info("My robotic body is ready!");
        Endless.LOG.info("Logged in as: "+selfuser.getName()+"#"+selfuser.getDiscriminator()+" ("+selfuser.getId()+")");
        Endless.LOG.info("Using prefix: "+config.getPrefix());
        Endless.LOG.info("Owner: "+owner.getName()+"#"+owner.getDiscriminator()+" ("+owner.getId()+")");

        if(!(maintenance))
        {
            event.getJDA().getPresence().setGame(Game.playing("Type "+config.getPrefix()+"help | Version "+Const.VERSION+" | On "+event.getJDA().getGuilds().size()+" Guilds | "+event.getJDA().getUsers().size()+" Users | "+event.getJDA().getTextChannels().size()+" Channels"));
            event.getJDA().getPresence().setStatus(config.getStatus());
        }

        if(event.getJDA().getGuildCache().isEmpty())
        {
            StartupChecker.LOG.warn("Looks like I'm on any guild! Add me using the following link:");
            StartupChecker.LOG.warn(event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR));
        }

        if(config.isBotlogEnabled())
            webhook.send("**Endless** is now <@&436352838822658049>!");
    }

    @Override
    public void onResume(ResumedEvent event)
    {
        if(config.isBotlogEnabled())
            webhook.send("**Endless** is now <@&436352909685424134>!");
    }

    @Override
    public void onReconnect(ReconnectedEvent event)
    {
        if(config.isBotlogEnabled())
            webhook.send("**Endless** is now <@&436352708304175106>!");
    }

    @Override
    public void onShutdown(ShutdownEvent event)
    {
        if(config.isBotlogEnabled())
            webhook.send("**Endless** is now <@&436352915469369345>!").thenRun(webhook::close);
        db.shutdown();
    }
}

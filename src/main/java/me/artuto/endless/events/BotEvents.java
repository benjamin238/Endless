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
import me.artuto.endless.bootloader.StartupChecker;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.Presence;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Artuto
 */

public class BotEvents extends ListenerAdapter
{
    private final Bot bot;
    private final boolean maintenance;
    private final ScheduledExecutorService webhookExecutor;
    private final WebhookClient webhook;

    public BotEvents(Bot bot, ScheduledExecutorService webhookExecutor, boolean maintenance)
    {
        this.bot = bot;
        this.maintenance = maintenance;
        this.webhookExecutor = webhookExecutor;
        this.webhook = createWebhook();
    }

    private WebhookClient createWebhook()
    {
        return new WebhookClientBuilder(bot.config.getBotlogWebhook()).setExecutorService(webhookExecutor).build();
    }

    @Override
    public void onReady(ReadyEvent event)
    {
        /*Endless.LOG.info("Leaving Pointless Guilds...");
        GuildUtils.leaveBadGuilds(event.getJDA());
        Endless.LOG.info("Done!");*/
        JDA jda = event.getJDA();
        Presence presence = jda.getPresence();
        JDA.ShardInfo shardInfo = jda.getShardInfo();

        Endless.LOG.info("Shard "+shardInfo.getShardString()+" ready!");

        if(bot.config.isBotlogEnabled())
            webhook.send(":radio_button: Connected to shard `"+shardInfo.getShardString()+"` Guilds: `"+jda.getGuildCache().size()+"` " +
                    "Users: `"+jda.getUserCache().size()+"`");

        bot.muteScheduler.scheduleWithFixedDelay(() -> bot.pdm.updateTempPunishments(Const.PunishmentType.TEMPMUTE, event.getJDA()),
                0, 10, TimeUnit.SECONDS);

        if(!(maintenance))
            presence.setPresence(bot.config.getStatus(), Game.playing("Type "+bot.config.getPrefix()+"help | Version "+Const.VERSION+" | On "+event.getJDA().getGuildCache().size()+" Guilds | "+event.getJDA().getUserCache().size()+
                    " Users | Shard "+(shardInfo.getShardId()+1)));
        else
            presence.setPresence(OnlineStatus.DO_NOT_DISTURB, Game.playing("Maintenance mode enabled"));
    }
}

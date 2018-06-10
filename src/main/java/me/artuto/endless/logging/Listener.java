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

package me.artuto.endless.logging;

import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.Endless;
import me.artuto.endless.bootloader.StartupChecker;
import me.artuto.endless.handlers.ImportedTagHandler;
import me.artuto.endless.handlers.MutedRoleHandler;
import me.artuto.endless.handlers.StarboardHandler;
import me.artuto.endless.tempdata.AfkManager;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.guild.react.*;
import net.dv8tion.jda.core.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.managers.Presence;
import net.dv8tion.jda.webhook.WebhookClient;

import java.util.concurrent.TimeUnit;

/**
 * @author Artuto
 */

public class Listener implements EventListener
{
    private final Bot bot = Bot.getInstance();
    private final boolean maintenance = bot.maintenance;
    private final ModLogging modlog = bot.modlog;
    private final ServerLogging serverlog = bot.serverlog;
    private final WebhookClient webhook = bot.logWebhook;

    @Override
    public void onEvent(Event preEvent)
    {
        if(preEvent instanceof ReadyEvent)
        {
            ReadyEvent event = (ReadyEvent)preEvent;
            Endless.LOG.info("Leaving Pointless Guilds...");
            GuildUtils.leaveBadGuilds(event.getJDA());
            Endless.LOG.info("Done!");

            User selfuser = event.getJDA().getSelfUser();
            User owner = event.getJDA().getUserById(bot.config.getOwnerId());
            Presence presence = event.getJDA().getPresence();

            Endless.LOG.info("My robotic body is ready!\n" +
                    "Logged in as: "+selfuser.getName()+"#"+selfuser.getDiscriminator()+" ("+selfuser.getId()+")\n" +
                    "Using prefix: "+bot.config.getPrefix()+"\n" +
                    "Owner: "+owner.getName()+"#"+owner.getDiscriminator()+" ("+owner.getId()+")");

            if(event.getJDA().getGuildCache().isEmpty())
            {
                StartupChecker.LOG.warn("Looks like I'm on any guild! Add me using the following link:");
                StartupChecker.LOG.warn(event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR));
            }

            if(bot.config.isBotlogEnabled())
                webhook.send("**Endless** is now <@&436352838822658049>!");

            bot.muteScheduler.scheduleWithFixedDelay(() -> bot.pdm.updateTempPunishments(Const.PunishmentType.TEMPMUTE, event.getJDA()),
                    0, 10, TimeUnit.SECONDS);

            if(!(maintenance))
                presence.setPresence(bot.config.getStatus(), Game.playing("Type "+bot.config.getPrefix()+"help | Version "+Const.VERSION+" | On "+event.getJDA().getGuildCache().size()+" Guilds | "+event.getJDA().getUserCache().size()+
                        " Users | "+event.getJDA().getTextChannelCache().size()+" Channels"));
            else
                presence.setPresence(OnlineStatus.DO_NOT_DISTURB, Game.playing("Maintenance mode enabled"));
        }
        else if(preEvent instanceof GuildJoinEvent)
        {
            GuildJoinEvent event = (GuildJoinEvent)preEvent;
            bot.botlog.logJoin(event);
        }
        else if(preEvent instanceof GuildLeaveEvent)
        {
            GuildLeaveEvent event = (GuildLeaveEvent)preEvent;
            bot.botlog.logLeave(event);
        }
        else if(preEvent instanceof GuildMessageReceivedEvent)
        {
            GuildMessageReceivedEvent event = (GuildMessageReceivedEvent)preEvent;
            AfkManager.checkAfk(event);
            AfkManager.checkPings(event);
            serverlog.onGuildMessageReceived(event);
        }
        else if(preEvent instanceof MessageReceivedEvent)
        {
            MessageReceivedEvent event = (MessageReceivedEvent)preEvent;
            ImportedTagHandler.runTag(event);
        }
        else if(preEvent instanceof GuildMemberRoleAddEvent)
        {
            GuildMemberRoleAddEvent event = (GuildMemberRoleAddEvent)preEvent;
            MutedRoleHandler.checkRoleAdd(event);
        }
        else if(preEvent instanceof GuildMemberRoleRemoveEvent)
        {
            GuildMemberRoleRemoveEvent event = (GuildMemberRoleRemoveEvent)preEvent;
            MutedRoleHandler.checkRoleRemove(event);
        }
        else if(preEvent instanceof GuildMemberJoinEvent)
        {
            GuildMemberJoinEvent event = (GuildMemberJoinEvent)preEvent;
            MutedRoleHandler.checkJoin(event);
            serverlog.onGuildMemberJoin(event);
        }
        else if(preEvent instanceof GuildMemberLeaveEvent)
        {
            GuildMemberLeaveEvent event = (GuildMemberLeaveEvent)preEvent;
            serverlog.onGuildMemberLeave(event);
        }
        else if(preEvent instanceof GuildMessageReactionAddEvent)
        {
            GuildMessageReactionAddEvent event = (GuildMessageReactionAddEvent)preEvent;
            StarboardHandler.checkAddReaction(event);
        }
        else if(preEvent instanceof GuildMessageReactionRemoveEvent)
        {
            GuildMessageReactionRemoveEvent event = (GuildMessageReactionRemoveEvent)preEvent;
            StarboardHandler.checkRemoveReaction(event);
        }
        else if(preEvent instanceof GuildMessageReactionRemoveAllEvent)
        {
            GuildMessageReactionRemoveAllEvent event = (GuildMessageReactionRemoveAllEvent)preEvent;
            StarboardHandler.checkRemoveAllReactions(event);
        }
        else if(preEvent instanceof GuildMessageDeleteEvent)
        {
            GuildMessageDeleteEvent event = (GuildMessageDeleteEvent)preEvent;
            StarboardHandler.checkDeleteMessage(event);
            serverlog.onGuildMessageDelete(event);
        }
        else if(preEvent instanceof GuildMessageUpdateEvent)
        {
            GuildMessageUpdateEvent event = (GuildMessageUpdateEvent)preEvent;
            serverlog.onGuildMessageUpdate(event);
        }
        else if(preEvent instanceof UserUpdateAvatarEvent)
        {
            UserUpdateAvatarEvent event = (UserUpdateAvatarEvent)preEvent;
            serverlog.onUserUpdateAvatar(event);
        }
        else if(preEvent instanceof GuildVoiceJoinEvent)
        {
            GuildVoiceJoinEvent event = (GuildVoiceJoinEvent)preEvent;
            serverlog.onGuildVoiceJoin(event);
        }
        else if(preEvent instanceof GuildVoiceMoveEvent)
        {
            GuildVoiceMoveEvent event = (GuildVoiceMoveEvent)preEvent;
            serverlog.onGuildVoiceMove(event);
        }
        else if(preEvent instanceof GuildVoiceLeaveEvent)
        {
            GuildVoiceLeaveEvent event = (GuildVoiceLeaveEvent)preEvent;
            serverlog.onGuildVoiceLeave(event);
        }
        else if(preEvent instanceof GuildBanEvent)
        {
            GuildBanEvent event = (GuildBanEvent)preEvent;
            modlog.onGuildBan(event);
        }
    }
}

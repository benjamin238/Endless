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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import me.artuto.endless.handlers.*;
import me.artuto.endless.logging.ModLogging;
import me.artuto.endless.logging.ServerLogging;
import me.artuto.endless.storage.tempdata.AfkManager;
import me.artuto.endless.utils.TimeUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.*;
import net.dv8tion.jda.core.events.guild.member.*;
import net.dv8tion.jda.core.events.guild.voice.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.*;
import net.dv8tion.jda.core.events.message.guild.react.*;
import net.dv8tion.jda.core.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.webhook.WebhookClient;

import java.util.concurrent.TimeUnit;

/**
 * @author Artuto
 */

public class Listener implements CommandListener, EventListener
{
    protected Bot bot;
    private final ModLogging modlog;
    private final ServerLogging serverlog;
    private final WebhookClient webhook;
    
    Listener(Bot bot)
    {
        this.bot = bot;
        this.modlog = bot.modlog;
        this.serverlog = bot.serverlog;
        this.webhook = bot.logWebhook;
    }

    @Override
    public void onEvent(Event preEvent)
    {
        if(preEvent instanceof ReadyEvent)
        {
            ReadyEvent event = (ReadyEvent)preEvent;
            JDA jda = event.getJDA();
            JDA.ShardInfo shardInfo = jda.getShardInfo();

            if(bot.config.isBotlogEnabled())
                webhook.send(":radio_button: Connected to shard `"+shardInfo.getShardString()+"` Guilds: `"+jda.getGuildCache().size()+"` " +
                        "Users: `"+jda.getUserCache().size()+"`");

            Endless.LOG.info("Shard "+shardInfo.getShardString()+" ready!");
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
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildMessageReceivedEvent event = (GuildMessageReceivedEvent)preEvent;
            AfkManager.checkAfk(event);
            AfkManager.checkPings(event);
            serverlog.onGuildMessageReceived(event);
        }
        else if(preEvent instanceof MessageReceivedEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            MessageReceivedEvent event = (MessageReceivedEvent)preEvent;
            ImportedTagHandler.runTag(bot, event);
        }
        else if(preEvent instanceof GuildMemberRoleAddEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildMemberRoleAddEvent event = (GuildMemberRoleAddEvent)preEvent;
            MutedRoleHandler.checkRoleAdd(event);
        }
        else if(preEvent instanceof GuildMemberRoleRemoveEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildMemberRoleRemoveEvent event = (GuildMemberRoleRemoveEvent)preEvent;
            MutedRoleHandler.checkRoleRemove(event);
        }
        else if(preEvent instanceof GuildMemberJoinEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildMemberJoinEvent event = (GuildMemberJoinEvent)preEvent;
            MutedRoleHandler.checkJoin(event);
            serverlog.onGuildMemberJoin(event);
        }
        else if(preEvent instanceof GuildMemberLeaveEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildMemberLeaveEvent event = (GuildMemberLeaveEvent)preEvent;
            serverlog.onGuildMemberLeave(event);
        }
        else if(preEvent instanceof GuildMessageReactionAddEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildMessageReactionAddEvent event = (GuildMessageReactionAddEvent)preEvent;
            StarboardHandler.checkAddReaction(event);
        }
        else if(preEvent instanceof GuildMessageReactionRemoveEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildMessageReactionRemoveEvent event = (GuildMessageReactionRemoveEvent)preEvent;
            StarboardHandler.checkRemoveReaction(event);
        }
        else if(preEvent instanceof GuildMessageReactionRemoveAllEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildMessageReactionRemoveAllEvent event = (GuildMessageReactionRemoveAllEvent)preEvent;
            StarboardHandler.checkRemoveAllReactions(event);
        }
        else if(preEvent instanceof GuildMessageDeleteEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildMessageDeleteEvent event = (GuildMessageDeleteEvent)preEvent;
            StarboardHandler.checkDeleteMessage(event);
            serverlog.onGuildMessageDelete(event);
        }
        else if(preEvent instanceof GuildMessageUpdateEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildMessageUpdateEvent event = (GuildMessageUpdateEvent)preEvent;
            serverlog.onGuildMessageUpdate(event);
        }
        else if(preEvent instanceof UserUpdateAvatarEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            UserUpdateAvatarEvent event = (UserUpdateAvatarEvent)preEvent;
            serverlog.onUserUpdateAvatar(event);
        }
        else if(preEvent instanceof GuildVoiceJoinEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildVoiceJoinEvent event = (GuildVoiceJoinEvent)preEvent;
            serverlog.onGuildVoiceJoin(event);
        }
        else if(preEvent instanceof GuildVoiceMoveEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildVoiceMoveEvent event = (GuildVoiceMoveEvent)preEvent;
            serverlog.onGuildVoiceMove(event);
        }
        else if(preEvent instanceof GuildVoiceLeaveEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildVoiceLeaveEvent event = (GuildVoiceLeaveEvent)preEvent;
            serverlog.onGuildVoiceLeave(event);
        }
        else if(preEvent instanceof GuildBanEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildBanEvent event = (GuildBanEvent)preEvent;
            modlog.onGuildBan(event);
        }
    }

    @Override
    public void onCommand(CommandEvent event, Command command)
    {
        TextChannel commandLog = bot.shardManager.getTextChannelById(bot.config.getCommandslogChannelId());
        User author = event.getAuthor();
        Guild guild = event.getGuild();

        // If is the help command is null
        if(command==null) return;

        if(event.isOwner()) return;

        if(guild==null)
        {
            commandLog.sendMessage("`"+TimeUtils.getTimeAndDate()+"` :keyboard: **"+author.getName()+"#"+author.getDiscriminator()+"** " +
                    "(ID: "+author.getId()+") used the command `"+command.getName()+"` (`"+event.getMessage().getContentStripped()+"`) in a **Direct message**").queue();
        }
        else
        {
            commandLog.sendMessage("`"+TimeUtils.getTimeAndDate()+"` :keyboard: **"+author.getName()+"#"+author.getDiscriminator()+"** " +
                    "(ID: "+author.getId()+") used the command `"+command.getName()+"` (`"+event.getMessage().getContentStripped()+"`) in **"+guild.getName()+"** (ID: "+guild.getId()+")").queue();
        }
    }

    @Override
    public void onCommandException(CommandEvent event, Command command, Throwable throwable)
    {
        event.replyError("An error occurred while executing this command. Please join the support server by doing `e!help support`.");

        if(event.getGuild()==null)
        {
            Endless.LOG.error(String.format("Command Error: %s | Message ID: %s | Executed in: Direct Message %s",
                    command.getName(), event.getMessage().getIdLong(), event.getPrivateChannel().getIdLong()), throwable);
        }
        else
        {
            Endless.LOG.error(String.format("Command Error: %s | Message ID: %s | Executed in: Text Channel %s",
                    command.getName(), event.getMessage().getIdLong(), event.getTextChannel().getIdLong()), throwable);
        }
    }
}

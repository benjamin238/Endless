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

import com.jagrosh.jagtag.Parser;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.handlers.*;
import me.artuto.endless.logging.ModLogging;
import me.artuto.endless.logging.ServerLogging;
import me.artuto.endless.storage.tempdata.AfkManager;
import me.artuto.endless.utils.FinderUtil;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.utils.TagUtil;
import me.artuto.endless.utils.TimeUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.*;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.*;
import net.dv8tion.jda.core.events.guild.member.*;
import net.dv8tion.jda.core.events.guild.voice.*;
import net.dv8tion.jda.core.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.*;
import net.dv8tion.jda.core.events.message.guild.react.*;
import net.dv8tion.jda.core.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.webhook.WebhookClient;

/**
 * @author Artuto
 */

public class Listener implements CommandListener, EventListener
{
    protected Bot bot;
    private final ModLogging modlog;
    private final Parser parser;
    private final ServerLogging serverlog;
    private final WebhookClient webhook;
    
    Listener(Bot bot)
    {
        this.bot = bot;
        this.modlog = bot.modlog;
        this.parser = TagUtil.parser;
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
                webhook.send("Endless is now "+Const.ONLINE_ROLE+" - Connected to shard `"+shardInfo.getShardString()+"` Guilds: `"
                        +jda.getGuildCache().size()+"` " +"Users: `"+jda.getUserCache().size()+"`");

            Endless.LOG.info("Shard "+shardInfo.getShardString()+" ready!");
        }
        else if(preEvent instanceof ResumedEvent)
        {
            ResumedEvent event = (ResumedEvent)preEvent;
            JDA jda = event.getJDA();
            JDA.ShardInfo shardInfo = jda.getShardInfo();

            if(bot.config.isBotlogEnabled())
                webhook.send("Endless has "+Const.RESUMED_ROLE+" - Shard `"+shardInfo.getShardString()+"`");
        }
        else if(preEvent instanceof ReconnectedEvent)
        {
            ReconnectedEvent event = (ReconnectedEvent)preEvent;
            JDA jda = event.getJDA();
            JDA.ShardInfo shardInfo = jda.getShardInfo();

            if(bot.config.isBotlogEnabled())
                webhook.send("Endless has "+Const.RECONNECTED_ROLE+" - Shard `"+shardInfo.getShardString()+"`");
        }
        else if(preEvent instanceof ShutdownEvent)
        {
            ShutdownEvent event = (ShutdownEvent)preEvent;
            JDA jda = event.getJDA();
            JDA.ShardInfo shardInfo = jda.getShardInfo();

            if(bot.config.isBotlogEnabled())
                webhook.send("Endless has gone "+Const.OFFLINE_ROLE+" - Shard `"+shardInfo.getShardString()+"`");
        }
        else if(preEvent instanceof GuildJoinEvent)
        {
            GuildJoinEvent event = (GuildJoinEvent)preEvent;
            bot.botlog.logJoin(event);
            bot.sendStats(event.getJDA());
        }
        else if(preEvent instanceof GuildLeaveEvent)
        {
            GuildLeaveEvent event = (GuildLeaveEvent)preEvent;
            bot.botlog.logLeave(event);
            bot.sendStats(event.getJDA());
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
            welcomeDm(event);
        }
        else if(preEvent instanceof GuildMemberLeaveEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildMemberLeaveEvent event = (GuildMemberLeaveEvent)preEvent;
            modlog.onGuildMemberLeave(event);
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
        else if(preEvent instanceof MessageBulkDeleteEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            MessageBulkDeleteEvent event = (MessageBulkDeleteEvent)preEvent;
            serverlog.onMessageBulkDeleteEvent(event);
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
        else if(preEvent instanceof GuildUnbanEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            GuildUnbanEvent event = (GuildUnbanEvent)preEvent;
            modlog.onGuildUnban(event);
        }
        else if(preEvent instanceof TextChannelDeleteEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            TextChannelDeleteEvent event = (TextChannelDeleteEvent)preEvent;
            TextChannel tc = event.getChannel();
            bot.rsdm.getRoomsForGuild(event.getGuild().getIdLong()).stream().filter(r -> r.getTextChannelId()==tc.getIdLong()).forEach(r -> {
                if(r.isCombo())
                {
                    VoiceChannel vc = event.getGuild().getVoiceChannelById(r.getVoiceChannelId());
                    if(!(vc==null))
                        vc.delete().reason("[Text Channel Deleted (Combo)]").queue(null, e -> {});
                    bot.rsdm.deleteComboRoom(tc.getIdLong(), r.getVoiceChannelId());
                }
                else
                    bot.rsdm.deleteTextRoom(tc.getIdLong());
            });
        }
        else if(preEvent instanceof VoiceChannelDeleteEvent)
        {
            if(bot.maintenance || !(bot.initialized))
                return;
            VoiceChannelDeleteEvent event = (VoiceChannelDeleteEvent)preEvent;
            VoiceChannel vc = event.getChannel();
            bot.rsdm.getRoomsForGuild(event.getGuild().getIdLong()).stream().filter(r -> r.getVoiceChannelId()==vc.getIdLong()).forEach(r -> {
                if(r.isCombo())
                {
                    TextChannel tc = event.getGuild().getTextChannelById(r.getTextChannelId());
                    if(!(tc==null))
                        tc.delete().reason("[Voice Channel Deleted (Combo)]").queue(null, e -> {});
                    bot.rsdm.deleteComboRoom(r.getTextChannelId(), vc.getIdLong());
                }
                else
                    bot.rsdm.deleteVoiceRoom(vc.getIdLong());
            });
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

    // I didnt found a better place for this method
    private void welcomeDm(GuildMemberJoinEvent event)
    {
        Guild guild = event.getGuild();
        GuildSettings gs = bot.endless.getGuildSettings(guild);
        String welcomeDM = gs.getWelcomeDM();
        User user = event.getUser();

        if(welcomeDM==null)
            return;

        parser.clear().put("user", user).put("guild", guild).put("channel", FinderUtil.getDefaultChannel(guild));
        user.openPrivateChannel().queue(c -> c.sendMessage(FormatUtil.sanitize(parser.parse(welcomeDM))).queue(null, e -> {}));
        parser.clear();
    }
}

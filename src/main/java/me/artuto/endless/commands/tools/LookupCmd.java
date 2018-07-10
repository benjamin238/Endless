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

package me.artuto.endless.commands.tools;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Const;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.utils.MiscUtils;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.WidgetUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Artuto
 */

public class LookupCmd extends EndlessCommand
{
    public LookupCmd()
    {
        this.name = "lookup";
        this.help = "Retrieves info about an invite, a guild or an user using their ID from Discord's servers.";
        this.arguments = "<User ID | Invite code | Invite URL | Guild ID>";
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        MessageBuilder mb = new MessageBuilder();
        ShardManager shardManager = event.getJDA().asBot().getShardManager();
        StringBuilder sb = new StringBuilder();
        String args = event.getArgs();

        event.getTextChannel().sendTyping().queue();
        event.async(() -> {
            try
            {
                // User
                long id = Long.parseLong(event.getArgs());
                User user = shardManager.getUserById(id);
                if(user==null)
                    try
                    {
                        user = event.getJDA().retrieveUserById(id).complete();
                    }
                    catch(ErrorResponseException ignored) {}
                if(!(user==null))
                {
                    boolean nitro = !(user.getAvatarId()==null) && user.getAvatarId().startsWith("a_");
                    sb.append(Const.LINE_START).append(" ID: **").append(user.getId()).append("**\n");
                    sb.append(Const.LINE_START).append(" Account Creation: **").append(user.getCreationTime()
                            .format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**\n");
                    builder.setDescription(sb).setThumbnail(MiscUtils.getImageUrl("png", null, user.getEffectiveAvatarUrl()))
                            .setColor(event.getMember().getColor());
                    event.reply(mb.setContent((user.isBot()?Const.BOT:Const.PEOPLE)+" Info about **"+user.getName()+"#"+user.getDiscriminator()+"**"
                            +(nitro?Const.NITRO:"")).setEmbed(builder.build()).build());
                    return;
                }

                // Widget
                WidgetUtil.Widget widget = WidgetUtil.getWidget(id);
                if(widget==null)
                {
                    event.replyWarning("Nothing found with the provided arguments!");
                    return;
                }
                if(!(widget.isAvailable()))
                {
                    event.replySuccess("Guild with ID `"+id+"` found. No further information found.");
                    return;
                }
                // Widget Invite
                Invite invite = null;
                if(!(widget.getInviteCode()==null))
                {
                    try
                    {
                        invite = Invite.resolve(event.getJDA(), widget.getInviteCode(), true).complete();
                    }
                    catch(ErrorResponseException ignored) {}
                }
                boolean verified = false;
                int memberCount = widget.getMembers().size();
                int onlineCount = (int)widget.getMembers().stream().filter(m -> m.getOnlineStatus()==OnlineStatus.ONLINE).count();
                int idleCount = (int)widget.getMembers().stream().filter(m -> m.getOnlineStatus()==OnlineStatus.IDLE).count();
                int dndCount = (int)widget.getMembers().stream().filter(m -> m.getOnlineStatus()==OnlineStatus.DO_NOT_DISTURB).count();
                int vcCount = widget.getVoiceChannels().size();
                sb.append(Const.LINE_START).append(" ID: **").append(widget.getId()).append("**\n");
                sb.append(Const.LINE_START).append(" Creation: **").append(widget.getCreationTime()
                        .format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**\n");
                sb.append(Const.LINE_START).append(" Voice Channels: **").append(vcCount).append("**\n");
                sb.append(Const.LINE_START).append(" Members: ").append(Const.ONLINE).append(" **").append(onlineCount).append("** - ")
                        .append(Const.IDLE).append(" **").append(idleCount).append("** - ").append(Const.DND).append(" **").append(dndCount)
                        .append("** ").append("(**").append(memberCount).append("**)\n");
                if(!(invite==null))
                {
                    verified = invite.getGuild().getFeatures().contains("VERIFIED");
                    String inviteLink = "["+invite.getCode()+"]("+invite.getURL()+")";
                    sb.append(Const.LINE_START).append(" Invite: **").append(inviteLink).append("** #").append(invite.getChannel().getName())
                            .append(" (ID: ").append(invite.getChannel().getId()).append(")");
                    if(!(invite.getGuild().getSplashUrl()==null))
                    {
                        sb.append("\n_ _\n").append(Const.PARTNER).append(" **Discord Partner** ").append(Const.PARTNER);
                        builder.setImage(invite.getGuild().getSplashUrl()+"?size=2048");
                    }
                }
                builder.setDescription(sb).setThumbnail(invite==null?null:MiscUtils.getImageUrl("png", null, invite.getGuild().getIconUrl()))
                        .setColor(event.getMember().getColor());
                event.reply(mb.setContent(":computer: Info about **"+widget.getName()+"** "+(verified?Const.VERIFIED:"")).setEmbed(builder.build()).build());
                return;
            }
            catch(NumberFormatException ignored) {}
            catch(RateLimitedException e)
            {
                event.reactWarning();
            }

            // Invite
            List<String> invites = event.getMessage().getInvites();
            String code;
            if(invites.isEmpty())
                code = args;
            else
                code = invites.get(0);

            Invite invite = null;
            try
            {
                invite = Invite.resolve(event.getJDA(), code, true).complete();
            }
            catch(ErrorResponseException ignored) {}
            if(invite==null)
            {
                event.replyWarning("Nothing found with the provided arguments!");
                return;
            }
            Invite.Channel channel = invite.getChannel();
            Invite.Guild guild = invite.getGuild();
            User inviter = invite.getInviter();
            boolean verified = guild.getFeatures().contains("VERIFIED");
            int memberCount = invite.getGuild().getMemberCount();
            int onlineCount = invite.getGuild().getOnlineCount();
            String guildName = "["+guild.getName()+"]("+invite.getURL()+")";
            sb.append(Const.LINE_START).append(" Guild: **").append(guildName).append("** ").append(verified?Const.VERIFIED:"").append("\n");
            sb.append(Const.LINE_START).append(" Channel: **#").append(channel.getName()).append("** (ID: ").append(channel.getId()).append(")\n");
            sb.append(Const.LINE_START).append(" Inviter: ");
            if(inviter==null)
                sb.append("N/A\n");
            else
                sb.append("**").append(inviter.getName()).append("#").append(inviter.getDiscriminator()).append("** (ID: ")
                        .append(inviter.getId()).append(")\n");

            StringBuilder gInfo = new StringBuilder();
            gInfo.append(Const.LINE_START).append(" ID: **").append(guild.getId()).append("**\n");
            gInfo.append(Const.LINE_START).append(" Creation: **").append(guild.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**\n");
            gInfo.append(Const.LINE_START).append(" Members: ").append(Const.ONLINE).append(" **").append(onlineCount==0?"N/A":onlineCount).append("** - ")
                    .append(Const.OFFLINE).append(" **").append(memberCount==0?"N/A":memberCount).append("**");
            if(!(guild.getSplashUrl()==null))
            {
                gInfo.append("\n_ _\n").append(Const.PARTNER).append(" **Discord Partner** ").append(Const.PARTNER);
                builder.setImage(MiscUtils.getImageUrl("png", "2048", guild.getSplashUrl()));
            }
            builder.setThumbnail(MiscUtils.getImageUrl("png", null, guild.getIconUrl()))
                    .setDescription(sb).setColor(event.getMember().getColor());
            builder.addField("Guild Info", gInfo.toString(), false);
            event.reply(mb.setContent(":link: Info about invite code **"+code+"**").setEmbed(builder.build()).build());
        });
    }
}

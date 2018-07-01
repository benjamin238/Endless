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
import me.artuto.endless.tools.InfoTools;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
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
        this.guildOnly = false;
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
                    sb.append(Const.LINE_START).append(" ID: **").append(user.getId()).append("**\n");
                    sb.append(Const.LINE_START).append(" Account Creation: **").append(user.getCreationTime()
                            .format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**\n");
                    builder.setDescription(sb).setThumbnail(user.getEffectiveAvatarUrl()).setColor(event.getMember().getColor());
                    event.reply(mb.setContent((user.isBot()?Const.BOT:Const.PEOPLE)+" Info about **"+user.getName()+"#"+user.getDiscriminator()+"**"
                            +(InfoTools.nitroCheck(user)?Const.NITRO:"")).setEmbed(builder.build()).build());
                    return;
                }

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
                Invite invite = null;
                if(!(widget.getInviteCode()==null))
                {
                    try
                    {
                        invite = Invite.resolve(event.getJDA(), widget.getInviteCode(), true).complete();
                    }
                    catch(ErrorResponseException ignored) {}
                }
                int memberCount = invite==null?0:invite.getGuild().getMemberCount();
                int onlineCount = invite==null?0:invite.getGuild().getOnlineCount();
                int offlineCount;
                int vcCount = widget.getVoiceChannels().size();
                if(memberCount>onlineCount)
                    offlineCount = memberCount-onlineCount;
                else
                    offlineCount = onlineCount-memberCount;
                sb.append(Const.LINE_START).append(" ID: **").append(widget.getId()).append("**\n");
                sb.append(Const.LINE_START).append(" Creation: **").append(widget.getCreationTime()
                        .format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**\n");
                sb.append(Const.LINE_START).append(" Voice Channels: **").append(vcCount).append("**\n");
                if(!(onlineCount==0 && offlineCount==0))
                    sb.append(Const.LINE_START).append(" Members: ").append(Const.ONLINE).append(" **").append(onlineCount==0?"N/A":onlineCount).append("** ")
                            .append(Const.OFFLINE).append(" **").append(offlineCount==0?"N/A":offlineCount).append("**\n");
                if(!(invite==null))
                {
                    String inviteLink = "["+invite.getCode()+"]("+invite.getURL()+")";
                    sb.append(Const.LINE_START).append(" Invite: **").append(inviteLink).append("** #").append(invite.getChannel().getName())
                            .append(" (ID: ").append(invite.getChannel().getId()).append(")");
                    if(!(invite.getGuild().getSplashUrl()==null))
                    {
                        sb.append("\n_ _\n").append(Const.PARTNER).append(" **Discord Partner** ").append(Const.PARTNER);
                        builder.setImage(invite.getGuild().getSplashUrl()+"?size=2048");
                    }
                }
                builder.setDescription(sb).setThumbnail(invite==null?null:invite.getGuild().getIconUrl()).setColor(event.getMember().getColor());
                event.reply(mb.setContent(":computer: Info about **"+widget.getName()+"**").setEmbed(builder.build()).build());
                return;
            }
            catch(NumberFormatException ignored) {}
            catch(RateLimitedException e)
            {
                event.reactWarning();
            }

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
            sb.append(Const.LINE_START).append(" Guild: **").append(guild.getName()).append("**\n");
            sb.append(Const.LINE_START).append(" Channel: **#").append(channel.getName()).append("** (ID: ").append(channel.getId()).append(")\n");
            sb.append(Const.LINE_START).append(" Inviter: ");
            if(inviter==null)
                sb.append("N/A\n");
            else
                sb.append("**").append(inviter.getName()).append("#").append(inviter.getDiscriminator()).append("** (ID: ")
                        .append(inviter.getId()).append(")\n");

            StringBuilder gInfo = new StringBuilder();
            gInfo.append(Const.LINE_START).append(" ID: **").append(guild.getId()).append("**\n");
            gInfo.append(Const.LINE_START).append(" Creation: **").append(guild.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**");
            if(!(guild.getSplashUrl()==null))
            {
                gInfo.append("\n_ _\n").append(Const.PARTNER).append(" **Discord Partner** ").append(Const.PARTNER);
                builder.setImage(guild.getSplashUrl()+"?size=2048");
            }
            builder.setThumbnail(guild.getIconUrl()).setDescription(sb).setColor(event.getMember().getColor());
            builder.addField("Guild Info", gInfo.toString(), false);
            event.reply(mb.setContent(":link: Info about invite code **"+code+"**").setEmbed(builder.build()).build());
        });
    }
}

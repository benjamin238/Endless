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
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.tools.InfoTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.utils.WidgetUtil;

import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * @author Artuto
 */

public class Lookup extends EndlessCommand
{
    public Lookup()
    {
        this.name = "lookup";
        this.help = "Retrieves info about an invite, a guild or an user using their ID from Discord's servers.";
        this.arguments = "<User ID | Invite code | Invite URL | Guild ID>";
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildCommand = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        boolean userb = false;
        boolean tcb = false;
        boolean vcb = false;
        boolean guildb = false;
        boolean inviteb = false;
        boolean widgetb = false;
        boolean finished = false;
        EmbedBuilder builder = new EmbedBuilder();
        String args = event.getArgs();
        String title;
        Color color;
        User user;
        Invite inv;
        WidgetUtil.Widget widget;
        JDA jda = event.getJDA();

        if(args.isEmpty())
        {
            event.replyWarning("");
            return;
        }

        if(event.isFromType(ChannelType.PRIVATE)) color = Color.decode("#33ff00");
        else color = event.getGuild().getSelfMember().getColor();

        try
        {
            user = jda.retrieveUserById(args).complete();
            userb = true;
            finished = true;
            String ranks;

            if(InfoTools.nitroCheck(user)) ranks = "<:nitro:334859814566101004>";
            else ranks = "";

            title = (user.isBot() ? ":information_source: Information about the bot **"+user.getName()+"**"+"#"+"**"+user.getDiscriminator()+"** <:bot:334859813915983872>" : ":information_source: Information about the user **"+user.getName()+"**"+"#"+"**"+user.getDiscriminator()+"** "+ranks);
            builder.addField(":1234: ID:", "**"+user.getId()+"**", false);
            builder.addField(":calendar_spiral: Account Creation Time:", "**"+user.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", false);
            if(user.getName().contains("Kyle2000"))
                builder.addField(":shit: Shit Head:", "**C'mon, it's Kyle: A LOT**", true);
            builder.setFooter("Information provided by the Discord API", "https://endless.artuto.me/assets/discord.png");
            builder.setThumbnail(user.getEffectiveAvatarUrl());

            event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build(), s -> builder.clearFields());
            return;
        }
        catch(Exception ignored)
        {
        }

        TextChannel tc;
        VoiceChannel vc;
        Guild guild;

        if(!(finished))
        {
            try
            {
                tc = jda.getTextChannelById(args);
                vc = jda.getVoiceChannelById(args);
                guild = jda.getGuildById(args);
            }
            catch(NumberFormatException e)
            {
                tc = null;
                vc = null;
                guild = null;
            }

            if(!(guild == null))
            {
                guildb = true;
                finished = true;
                title = ":information_source: Information about the guild **"+guild.getName()+"**";
                builder.addField(":1234: ID:", "**"+guild.getId()+"**", true);
                builder.addField(":calendar_spiral: Guild Creation Time:", "**"+guild.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", true);
                builder.addField(":speech_balloon: Text Channels:", "**"+guild.getTextChannels().size()+"**", true);
                builder.addField(":loud_sound: Voice Channels:", "**"+guild.getVoiceChannels().size()+"**", true);
                builder.addField(":performing_acts: Roles:", "**"+guild.getRoles().size()+"**", true);
                builder.setThumbnail(guild.getIconUrl());
                builder.setColor(color);

                event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build(), s -> builder.clearFields());
            }
            else if(!(vc == null))
            {
                vcb = true;
                finished = true;
                title = ":information_source: Information about the voice channel **"+vc.getName()+"**";
                builder.addField(":1234: ID:", "**"+vc.getId()+"**", true);
                builder.addField(":calendar_spiral: Channel Creation Time:", "**"+vc.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", true);
                builder.addField(":map::1234: Guild ID:", "**"+vc.getGuild().getId()+"**", true);
                builder.addField(":map::speech_balloon: Guild Name:", "**"+vc.getGuild().getName()+"**", true);
                builder.setThumbnail(vc.getGuild().getIconUrl());
                builder.setColor(color);

                event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build(), s -> builder.clearFields());
            }
            else if(!(tc == null))
            {
                tcb = true;
                finished = true;
                title = ":information_source: Information about the text channel **"+tc.getAsMention()+"**";
                builder.addField(":1234: ID:", "**"+tc.getId()+"**", false);
                builder.addField(":calendar_spiral: Channel Creation Time:", "**"+tc.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)+"**", true);
                builder.addField(":map::1234: Guild ID:", "**"+tc.getGuild().getId()+"**", true);
                builder.addField(":map::speech_balloon: Guild Name:", "**"+tc.getGuild().getName()+"**", true);
                builder.setThumbnail(tc.getGuild().getIconUrl());
                builder.setColor(color);

                event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build(), s -> builder.clearFields());
            }
        }

        if(!(finished))
        {
            try
            {
                inv = Invite.resolve(jda, args).complete();
                inviteb = true;
                finished = true;
                user = inv.getInviter();
                Invite.Channel invtc = inv.getChannel();
                Invite.Guild invguild = inv.getGuild();
                title = ":information_source: Information about the invite `"+args+"`";

                builder.addField(":bust_in_silhouette: Inviter:", "**"+user.getName()+"#"+user.getDiscriminator()+"**", false);
                builder.addField(":speech_balloon: Channel:", "**#"+invtc.getName()+" (ID: "+invtc.getId()+")**", false);
                builder.addField(":map: Guild:", "**"+invguild.getName()+" (ID: "+invguild.getId()+")**", false);
                builder.setFooter("Information provided by the Discord API", "https://endless.artuto.me/assets/discord.png");
                builder.setThumbnail(invguild.getIconUrl());
                builder.setColor(color);

                event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build(), s -> builder.clearFields());
            }
            catch(ErrorResponseException ignored)
            {
            }
        }

        if(!(finished))
        {
            try
            {
                widget = WidgetUtil.getWidget(args);
                title = ":information_source: Information about the guild **"+widget.getName()+"**";

                if(!(widget == null))
                {
                    widgetb = true;

                    String code = widget.getInviteCode();
                    inv = Invite.resolve(jda, code).complete();

                    builder.addField(":1234: ID:", "**"+widget.getId()+"**", false);
                    builder.addField(":bust_in_silhouette: Members:", "**"+widget.getMembers().size()+"**", true);
                    builder.addField(":loud_sound: Voice Channels:", "**"+widget.getVoiceChannels().size()+"**", true);
                    builder.setThumbnail(inv.getGuild().getIconUrl());

                    event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build(), s -> builder.clearFields());
                }
            }
            catch(Exception ignored)
            {
            }
        }

        if(!(userb) && !(tcb) && !(vcb) && !(guildb) && !(inviteb) && !(widgetb))
            event.replyError("Nothing found with the provided arguments!");
    }
}

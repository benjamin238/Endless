/*
 * Copyright (C) 2017 Artu
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

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import java.awt.Color;
import java.time.format.DateTimeFormatter;

import me.artuto.endless.Const;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.tools.InfoTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.Invite;
import net.dv8tion.jda.core.utils.WidgetUtil;

/**
 *
 * @author Artu
 */

public class Lookup extends Command
{
    public Lookup()
        {
            this.name = "lookup";
            this.help = "Retrieves info about an invite, a guild or an user using their ID from Discord's servers.";
            this.arguments = "<User ID | Invite code | Invite URL (only discord.gg) | Guild ID>";
            this.category = Categories.TOOLS;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildOnly = false;
       }
    
    @Override
    protected void execute(CommandEvent event)
    {
        Color color;
        Invite invite;
        Guild guild;
        String ranks;
        WidgetUtil.Widget widget;
        EmbedBuilder builder = new EmbedBuilder();
        String title;
        String code;

        if(event.isFromType(ChannelType.PRIVATE))
        {
            color = Color.decode("#33ff00");
        }
        else
        {
            color = event.getGuild().getSelfMember().getColor();
        }

        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Please specify something!");
            return;
        }

        try
        {
            try
            {
                //It could be an Invite.

                title = ":information_source: Invite info:";

                if(event.getArgs().startsWith("https://discord.gg"))
                    code = event.getArgs().replace("https://discord.gg/", "");
                else
                    code = event.getArgs();

                invite = Invite.resolve(event.getJDA(), code).complete();

                builder.addField(Const.LINE_START+" Guild: ", invite.getGuild().getName()+" (ID: "+invite.getGuild().getId()+")", false);
                builder.addField(Const.LINE_START+" Channel: ", invite.getChannel().getName()+" (ID: "+invite.getChannel().getId()+")", false);
                builder.addField(Const.LINE_START+" Inviter: ", invite.getInviter().getName()+"#"+invite.getInviter().getDiscriminator()+" (ID: "+invite.getInviter().getId()+")", false);
                builder.setFooter((invite.getCode()+"["+invite.getURL()+"]"), null);
                builder.setThumbnail(invite.getGuild().getIconUrl());
                builder.setColor(color);

                event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());

            }
            catch(Exception ignored){}

            //Then try with a Guild

            try
            {
                guild = event.getJDA().getGuildById(event.getArgs());

                if(guild==null)
                {
                    widget = WidgetUtil.getWidget(event.getArgs());


                    if(!(widget==null))
                    {
                        title = ":information_source: Guild info:";
                        Invite inv = Invite.resolve(event.getJDA(), widget.getInviteCode()).complete();

                        builder.addField(Const.LINE_START+" Name: ", widget.getName(), true);
                        builder.addField(Const.LINE_START+" ID: ", widget.getId(), true);
                        builder.addField(Const.LINE_START+" Creation Time: ", widget.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
                        builder.addField(Const.LINE_START+" Voice Channels: ", String.valueOf(widget.getVoiceChannels().size()), true);
                        builder.addField(Const.LINE_START+" Invite: ", inv.getCode()+" #"+inv.getChannel().getName()+" ("+inv.getChannel().getId()+")", true);
                        builder.setColor(color);

                        event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());
                    }
                }
                else
                {
                    title = ":information_source: Guild info:";
                    builder.addField(Const.LINE_START+" Name: ", guild.getName(), true);
                    builder.addField(Const.LINE_START+" ID: ", guild.getId(), true);
                    builder.addField(Const.LINE_START+" Text Channels: ", String.valueOf(guild.getTextChannels().size()), true);
                    builder.addField(Const.LINE_START+" Voice Channels: ", String.valueOf(guild.getVoiceChannels().size()), true);
                    builder.addField(Const.LINE_START+" Creation Time: ", guild.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
                    builder.addField(Const.LINE_START+" Owner: ", guild.getOwner().getUser().getName(), true);
                    builder.setThumbnail(guild.getIconUrl());
                    builder.setColor(color);

                    event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());
                }
            }
            catch(Exception ignored) {}

            try
            {
                //If that wasn't a Guild, try with a User

                User user = event.getJDA().retrieveUserById(event.getArgs()).complete();

                if(InfoTools.nitroCheck(user))
                {
                    ranks = "<:nitro:334859814566101004>";
                }
                else
                {
                    ranks = "";
                }

                title = (user.isBot()?":information_source: Information about the bot **"+user.getName()+"**"+"#"+"**"+user.getDiscriminator()+"** <:bot:334859813915983872>":":information_source: Information about the user **"+user.getName()+"**"+"#"+"**"+user.getDiscriminator()+"** "+ranks);

                builder.addField(Const.LINE_START+" Name: ", user.getName()+"#"+user.getDiscriminator(), true);
                builder.addField(Const.LINE_START+" ID: ", user.getId(), true);
                builder.addField(Const.LINE_START+" Creation Time: ", user.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
                if(user.getName().contains("Kyle2000"))
                    builder.addField(Const.LINE_START+" Shithead: ", "A LOT", true);
                builder.setThumbnail(user.getEffectiveAvatarUrl());
                builder.setColor(color);

                event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());
            }
            catch(Exception ignored) {}
        }
        catch(Exception e)
        {
            //Then it wasn't anything

            event.replyError("Something went wrong when doing a lookup of this query: `'"+event.getArgs()+"'` \n```"+e+"```");
        }
    }
}

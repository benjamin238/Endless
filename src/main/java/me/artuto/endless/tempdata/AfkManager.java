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

package me.artuto.endless.tempdata;

import me.artuto.endless.Bot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class AfkManager
{
    private static HashMap<Long, String> afk = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger("AFK Manager");

    public static void setAfk(long id, String message)
    {
        afk.put(id, message);
    }

    public static String getMessage(long id)
    {
        return afk.get(id);
    }

    public static HashMap<Long, String> getMap()
    {
        return afk;
    }

    public static void unsetAfk(long id)
    {
        afk.remove(id);
    }

    public static boolean isAfk(long id)
    {
        return afk.containsKey(id);
    }

    public static void checkAfk(GuildMessageReceivedEvent event)
    {
        User author = event.getAuthor();
        if(author.isBot())
            return;
        if(isAfk(author.getIdLong()))
        {
            author.openPrivateChannel().queue(pc -> pc.sendMessage(Bot.getInstance().config.getDoneEmote()+" I've removed your AFK status.")
                    .queue(null, (e) -> LOG.warn("I was not able to DM "+author.getName()+"#"+author.getDiscriminator()+
                            " about removing its AFK status.")));
            unsetAfk(author.getIdLong());
        }
    }

    public static void checkPings(GuildMessageReceivedEvent event)
    {
        Message message = event.getMessage();
        User author = event.getAuthor();

        message.getMentionedUsers().forEach(user -> {
            if(!(isAfk(user.getIdLong())))
                return;
            if(author.isBot())
                return;

            EmbedBuilder builder = new EmbedBuilder();

            builder.setAuthor(author.getName()+"#"+author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
            builder.setDescription(message.getContentDisplay());
            builder.setFooter("#"+message.getTextChannel().getName()+", "+event.getGuild().getName(), event.getGuild().getIconUrl());
            builder.setTimestamp(message.getCreationTime());
            builder.setColor(event.getMember().getColor());

            user.openPrivateChannel().queue(pc -> pc.sendMessage(new MessageBuilder().setEmbed(builder.build())
                    .build()).queue(null, null));
            builder.clear();

            if(!(event.getChannel().canTalk()))
                return;

            if(getMessage(user.getIdLong())==null)
                event.getChannel().sendMessage(":bed: **"+user.getName()+"** is AFK!").queue();
            else
            {
                builder.setDescription(AfkManager.getMessage(user.getIdLong()));
                builder.setColor(event.getGuild().getMember(user).getColor());

                event.getChannel().sendMessage(new MessageBuilder().append(":bed: **").append(user.getName()).append("** is AFK!")
                        .setEmbed(builder.build()).build()).queue();
            }
        });

        /*afk.forEach((id, msg) -> {
            User user = event.getJDA().getUserCache().getElementById(id);
            if(!(user==null))
            {
                if(message.getMentionedUsers().contains(user) && !(author.isBot()))
                {
                    EmbedBuilder builder = new EmbedBuilder();

                    builder.setAuthor(author.getName()+"#"+author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
                    builder.setDescription(message.getContentDisplay());
                    builder.setFooter("#"+message.getTextChannel().getName()+", "+event.getGuild().getName(), event.getGuild().getIconUrl());
                    builder.setTimestamp(message.getCreationTime());
                    builder.setColor(event.getMember().getColor());


                    user.openPrivateChannel().queue(pc -> pc.sendMessage(new MessageBuilder().setEmbed(builder.build())
                            .build()).queue(null, null));
                }

                if(!(event.getChannel().canTalk()))
                    return;

                if(getMessage(user.getIdLong())==null)
                    event.getChannel().sendMessage(":bed: **"+user.getName()+"** is AFK!").queue();
                else
                {
                    EmbedBuilder builder = new EmbedBuilder();

                    builder.setDescription(AfkManager.getMessage(user.getIdLong()));
                    builder.setColor(event.getGuild().getMember(user).getColor());

                    event.getChannel().sendMessage(new MessageBuilder().append(":bed: **").append(user.getName()).append("** is AFK!")
                            .setEmbed(builder.build()).build()).queue();
                }
            }
        });*/
    }
}

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

package me.artuto.endless.handlers;

import me.artuto.endless.Bot;
import me.artuto.endless.data.managers.GuildSettingsDataManager;
import me.artuto.endless.data.managers.StarboardDataManager;
import me.artuto.endless.core.entities.StarboardMessage;
import me.artuto.endless.utils.FinderUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveAllEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * @author Artuto
 */

public class StarboardHandler
{
    private static final GuildSettingsDataManager gsdm = Bot.getInstance().gsdm;
    private static final Logger LOG = LoggerFactory.getLogger("Starboard");
    private static final ScheduledExecutorService thread = Bot.getInstance().starboardThread;
    private static final StarboardDataManager sdm = Bot.getInstance().sdm;

    public static void checkAddReaction(GuildMessageReactionAddEvent event)
    {
        thread.submit(() -> {
            Guild guild = event.getGuild();
            EmbedBuilder eb = new EmbedBuilder();
            MessageBuilder msgB = new MessageBuilder();
            StringBuilder sb = new StringBuilder();

            if(!(event.getChannel().getTopic() == null) && event.getChannel().getTopic().toLowerCase().contains("{ignore:starboard}"))
                return;

            if(!(isConfigured(guild))) return;

            TextChannel starboard = gsdm.getStarboardChannel(guild);
            Message starredMsg = getMessage(event.getMessageIdLong(), event.getChannel());
            if(starredMsg==null)
                return;

            List<Message.Attachment> attachments = starredMsg.getAttachments().stream().filter(a -> !(a.isImage())).collect(Collectors.toList());
            List<Message.Attachment> images = starredMsg.getAttachments().stream().filter(Message.Attachment::isImage).collect(Collectors.toList());

            if(isSameAuthor(starredMsg.getAuthor(), event.getUser()) && event.getReactionEmote().getName().equals("\u2B50"))
            {
                //event.getChannel().sendMessage("Boooooo, "+event.getUser().getAsMention()+" selfstarred! SHAME!").queue();
                return;
            }

            if(!(amountPassed(starredMsg))) return;

            if(!(starboard.canTalk()))
            {
                FinderUtil.getDefaultChannel(guild).sendMessage("I can't talk on the starboard!").queue(null, e -> guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage("I can't talk on the starboard!").queue(null, null)));
                return;
            }

            if(existsOnStarboard(starredMsg.getIdLong()))
            {
                if(!(sdm.updateCount(starredMsg.getIdLong(), getStarCount(starredMsg))))
                    LOG.warn("Error when updating star count. Message ID: "+starredMsg.getId()+" TC ID: "+starredMsg.getTextChannel().getId());
                else
                    updateCount(starredMsg, sdm.getStarboardMessage(starredMsg.getIdLong()).getStarboardMessageIdLong(), getStarCount(starredMsg));
            }
            else
            {
                sb.append(starredMsg.getContentRaw());
                eb.setAuthor(starredMsg.getAuthor().getName(), null, starredMsg.getAuthor().getEffectiveAvatarUrl());
                if(!(attachments.isEmpty())) for(Message.Attachment att : attachments)
                    sb.append("\n").append(att.getUrl());
                if(!(images.isEmpty())) if(images.size()>1) for(Message.Attachment img : images)
                    sb.append("\n").append(img.getUrl());
                else eb.setImage(images.get(0).getUrl());
                eb.setDescription(sb.toString());
                eb.setColor(Color.YELLOW);

                msgB.setContent(getEmote(getStarCount(starredMsg))+" **"+getStarCount(starredMsg)+"** "+starredMsg.getTextChannel().getAsMention()+" ID: "+starredMsg.getId());
                msgB.setEmbed(eb.build());

                if(!(sdm.addMessage(starredMsg, getStarCount(starredMsg))))
                    LOG.warn("Error when adding message to starboard. Message ID: "+starredMsg.getId()+" TC ID: "+starredMsg.getTextChannel().getId());
                starboard.sendMessage(msgB.build()).queue(s -> sdm.setStarboardMessageId(starredMsg, s.getIdLong()));
            }
        });
    }

    public static void checkRemoveReaction(GuildMessageReactionRemoveEvent event)
    {
        thread.submit(() -> {
            if(!(isConfigured(event.getGuild()))) return;

            Message starredMsg = getMessage(event.getMessageIdLong(), event.getChannel());
            if(starredMsg==null)
                return;

            StarboardMessage starboardMsg = sdm.getStarboardMessage(starredMsg.getIdLong());
            TextChannel starboard = gsdm.getStarboardChannel(event.getGuild());

            if(existsOnStarboard(starredMsg.getIdLong()))
            {
                if(!(amountPassed(starredMsg)))
                {
                    delete(starboard, starboardMsg);
                    return;
                }

                if(!(sdm.updateCount(starredMsg.getIdLong(), getStarCount(starredMsg))))
                    LOG.warn("Error when updating star count. Message ID: "+starredMsg.getId()+" TC ID: "+starredMsg.getTextChannel().getId());
                else
                    updateCount(starredMsg, sdm.getStarboardMessage(starredMsg.getIdLong()).getStarboardMessageIdLong(), getStarCount(starredMsg));
            }
        });
    }

    public static void checkRemoveAllReactions(GuildMessageReactionRemoveAllEvent event)
    {
        thread.submit(() -> check(event.getGuild(), event.getMessageIdLong()));
    }

    public static void checkDeleteMessage(GuildMessageDeleteEvent event)
    {
        thread.submit(() -> check(event.getGuild(), event.getMessageIdLong()));
    }

    private static boolean isSameAuthor(User msgAuthor, User user)
    {
        return msgAuthor.equals(user);
    }

    private static boolean isConfigured(Guild guild)
    {
        return !(gsdm.getStarboardChannel(guild) == null) && !(gsdm.getStarboardCount(guild) == null);
    }

    private static boolean amountPassed(Message msg)
    {
        return getStarCount(msg) >= gsdm.getStarboardCount(msg.getGuild());
    }

    private static int getStarCount(Message msg)
    {
        List<MessageReaction> reactions = msg.getReactions().stream().filter(r -> r.getReactionEmote().getName().equals("\u2B50")).collect(Collectors.toList());
        if(reactions.isEmpty()) return 0;

        List<User> users = reactions.get(0).getUsers().complete();

        if(users.contains(msg.getAuthor())) return users.size()-1;
        else return users.size();
    }

    private static boolean existsOnStarboard(Long id)
    {
        return !(sdm.getStarboardMessage(id)==null);
    }

    private static void updateCount(Message msg, Long starboardMsg, Integer amount)
    {
        TextChannel tc = gsdm.getStarboardChannel(msg.getGuild());
        tc.getMessageById(starboardMsg).queue(s -> s.editMessage(getEmote(amount)+" **"+amount+"** "+msg.getTextChannel().getAsMention()+" ID: "+msg.getId()).queue(null, null), null);
    }

    private static String getEmote(Integer count)
    {
        if(count<5) return ":star:";
        else if(count>5 || count<=10) return ":star2:";
        else if(count>15) return ":dizzy:";
        else return ":star:";
    }

    /*private String getColor(Integer count)
    {

    }*/

    private static void delete(TextChannel starboard, StarboardMessage starboardMsg)
    {
        starboard.getMessageById(starboardMsg.getStarboardMessageId()).queue(s -> {
            s.delete().queue();
            sdm.deleteMessage(starboardMsg.getMessageIdLong(), starboardMsg.getStarboardMessageIdLong());
        }, e -> sdm.deleteMessage(starboardMsg.getMessageIdLong(), starboardMsg.getStarboardMessageIdLong()));
    }

    private static void check(Guild guild, long msg)
    {
        TextChannel starboard = gsdm.getStarboardChannel(guild);
        StarboardMessage starboardMsg = sdm.getStarboardMessage(msg);

        if(existsOnStarboard(msg))
            delete(starboard, starboardMsg);
    }

    private static Message getMessage(long id, TextChannel tc)
    {
        try
        {
            return tc.getMessageById(id).complete();
        }
        catch(ErrorResponseException e)
        {
            delete(gsdm.getStarboardChannel(tc.getGuild()), sdm.getStarboardMessage(id));
            return null;
        }
    }
}

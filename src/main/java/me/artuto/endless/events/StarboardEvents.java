package me.artuto.endless.events;

import me.artuto.endless.data.GuildSettingsDataManager;
import me.artuto.endless.data.StarboardDataManager;
import me.artuto.endless.utils.FinderUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveAllEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class StarboardEvents extends ListenerAdapter
{
    private static GuildSettingsDataManager gsdm;
    private static StarboardDataManager sdm;
    private final Logger LOG = LoggerFactory.getLogger("Starboard");

    public StarboardEvents(GuildSettingsDataManager gsdm, StarboardDataManager sdm)
    {
        StarboardEvents.gsdm = gsdm;
        StarboardEvents.sdm = sdm;
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
    {
        Message msg = event.getChannel().getMessageById(event.getMessageId()).complete();
        Guild guild = event.getGuild();
        TextChannel tc = gsdm.getStarboardChannel(guild);
        EmbedBuilder eb = new EmbedBuilder();
        MessageBuilder msgB = new MessageBuilder();
        StringBuilder sb = new StringBuilder();
        List<Message.Attachment> attachments = msg.getAttachments().stream().filter(a -> !(a.isImage())).collect(Collectors.toList());
        List<Message.Attachment> images = msg.getAttachments().stream().filter(a -> a.isImage()).collect(Collectors.toList());

        LOG.info("fired");

        if(!(msg.getEmbeds().isEmpty()))
            return;

        if(isConfigured(guild) && !(isSameAuthor(msg.getAuthor(), event.getUser())))
        {
            LOG.info("pasa check 1");

            if(!(amountPassed(msg)))
                return;

            if(!(tc.canTalk()))
                FinderUtil.getDefaultChannel(guild).sendMessage("I can't talk on the starboard!").queue(null,
                        e -> guild.getOwner().getUser().openPrivateChannel().queue(s -> s.sendMessage("I can't talk on the starboard!").queue(null, null)));
            else
            {
                LOG.info("pasa check 4");

                sb.append(msg.getContentRaw());
                eb.setAuthor(msg.getAuthor().getName(), null, msg.getAuthor().getEffectiveAvatarUrl());
                if(!(attachments.isEmpty()))
                    for(Message.Attachment att : attachments)
                        sb.append("\n"+att.getUrl());
                if(!(images.isEmpty()))
                    if(images.size()>1)
                        for(Message.Attachment img : images)
                            sb.append("\n"+img.getUrl());
                    else
                        eb.setImage(images.get(0).getUrl());
                eb.setDescription(sb.toString());
                eb.setColor(Color.YELLOW);

                msgB.setContent(":star: **"+getStarCount(msg)+"** "+msg.getTextChannel().getAsMention()+" ID: "+msg.getId());
                msgB.setEmbed(eb.build());

                if(!(existsOnStarboard(msg)))
                    if(!(sdm.addMessage(msg, getStarCount(msg))))
                        LOG.warn("Error when adding message to starboard. Message ID: "+msg.getId()+" TC ID: "+msg.getTextChannel().getId());
                else
                    if(!(sdm.updateCount(msg.getIdLong(), getStarCount(msg))))
                        LOG.warn("Error when updating star count. Message ID: "+msg.getId()+" TC ID: "+msg.getTextChannel().getId());
                    else updateCount(msg, sdm.getStarboardMessage(msg.getIdLong()).getStarboardMessageIdLong(), getStarCount(msg));

                tc.sendMessage(msgB.build()).queue(s -> sdm.setStarboardMessageId(msg, s.getIdLong()));

                LOG.info("listo");
            }
        }
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event)
    {

    }

    @Override
    public void onGuildMessageReactionRemoveAll(GuildMessageReactionRemoveAllEvent event)
    {


    }

    private boolean isSameAuthor(User msgAuthor, User user)
    {
        return msgAuthor.equals(user);
    }

    private boolean isConfigured(Guild guild)
    {
        if(gsdm.getStarboardChannel(guild)==null || gsdm.getStarboardCount(guild)==null)
            return false;
        else return true;
    }

    private boolean amountPassed(Message msg)
    {
        return getStarCount(msg)>=gsdm.getStarboardCount(msg.getGuild());
    }

    private Integer getStarCount(Message msg)
    {
        List<MessageReaction> originalStarReactions = msg.getReactions().stream().filter(r -> r.getReactionEmote().getName().equals("\u2B50")).collect(Collectors.toList());
        List<MessageReaction> starReactions = originalStarReactions.stream().filter(r -> r.getUsers().complete().contains(msg.getAuthor())).collect(Collectors.toList());

        if(starReactions.isEmpty())
            return 0;
        else return starReactions.size();
    }

    private boolean existsOnStarboard(Message msg)
    {
        return !(sdm.getStarboardMessage(msg.getIdLong())==null);
    }

    private void updateCount(Message msg, Long starboardMsg, Integer amount)
    {
        TextChannel tc = gsdm.getStarboardChannel(msg.getGuild());
        tc.getMessageById(starboardMsg).queue(s -> s.editMessage(":star: **"+amount+"** "+msg.getTextChannel().getAsMention()+" ID: "+msg.getId()).queue(null, null), null);
    }
}

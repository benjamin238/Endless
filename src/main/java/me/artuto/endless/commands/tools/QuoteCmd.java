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
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Artuto
 */

public class QuoteCmd extends EndlessCommand
{
    // Thanks Dismissed for the RegEx.
    private final Pattern IMAGE_LINK = Pattern.compile("https?://.*.(png|jpg|jpeg|webm|gif)");

    public QuoteCmd()
    {
        this.name = "quote";
        this.help = "Quotes a message";
        this.arguments = "<message id> [channel]";
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY};
        this.userPerms = new Permission[]{Permission.MESSAGE_HISTORY};
    }

    protected void executeCommand(CommandEvent event)
    {
        TextChannel tc;
        String message;
        String textChannel;

        String[] args = ArgsUtils.split(2, event.getArgs());
        message = args[0].trim();
        if(args[1].isEmpty())
            textChannel = event.getTextChannel().getId();
        else
            textChannel = args[1];

        List<TextChannel> tList = FinderUtil.findTextChannels(textChannel, event.getJDA());

        if(tList.isEmpty())
        {
            event.replyWarning("I was not able to found a text channel with the provided arguments: '"+event.getArgs()+"'");
            return;
        }
        else if(tList.size()>1)
        {
            event.replyWarning(FormatUtil.listOfTcChannels(tList, event.getArgs()));
            return;
        }
        else
            tc = tList.get(0);

        if(!(ChecksUtil.hasPermission(tc.getGuild().getMember(event.getSelfUser()), tc, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY)))
        {
            event.replyError("I can't see that channel or I don't have Read Message History permission on it!");
            return;
        }

        if(!(tc.getGuild().getMember(event.getAuthor())==null) && !(ChecksUtil.hasPermission(tc.getGuild().getMember(event.getAuthor()), tc,
                Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY)))
        {
            event.replyError("You can't see that channel or you don't have Read Message History permission on it!");
            return;
        }

        long id;
        try {id = Long.parseLong(message);}
        catch(NumberFormatException ignored)
        {
            event.replyError("The message ID you provided is not a valid ID!");
            return;
        }

        tc.getMessageById(id).queue(msg -> {
            if(msg.getContentRaw().isEmpty() && msg.getAttachments().size()==0)
            {
                event.replyWarning("The given message has no content.");
                return;
            }
            
            EmbedBuilder builder = new EmbedBuilder();
            String content = msg.getContentRaw();
            String image = Arrays.stream(msg.getContentRaw().split("\\s+")).filter(w ->
                    IMAGE_LINK.matcher(w).matches()).findFirst().orElse(null);
            StringBuilder sb = new StringBuilder();
            User author = msg.getAuthor();
            
            if(msg.getAttachments().size()==1 && msg.getAttachments().get(0).isImage())
                builder.setImage(msg.getAttachments().get(0).getUrl());
            else if(!(image==null))
            {
                content = content.replace(image, "");
                builder.setImage(image);
            }
            else
            {
                for(Message.Attachment att : msg.getAttachments())
                    sb.append(":paperclip: **[").append(att.getFileName()).append("](").append(att.getUrl()).append(")**\n");
            }

            sb.append(content).append("\n");

            builder.setAuthor(author.getName()+"#"+author.getDiscriminator(), msg.getJumpUrl(), author.getEffectiveAvatarUrl());
            builder.setColor(tc.getGuild().getMember(author)==null?null:tc.getGuild().getMember(author).getColor());
            builder.setDescription(sb.toString());
            builder.setFooter((msg.isEdited()?"Edited":"Sent")+" in #"+tc.getName(), null);
            builder.setTimestamp(msg.isEdited()?msg.getEditedTime():msg.getCreationTime());
            event.reply(builder.build());
        }, e -> event.replyWarning("I couldn't find the message `"+id+"` in "+tc.getAsMention()+"!"));
    }
}

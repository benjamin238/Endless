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

import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.ChecksUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
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

    protected void executeCommand(EndlessCommandEvent event)
    {
        TextChannel tc;
        String message;

        String[] args = ArgsUtils.split(2, event.getArgs());
        message = args[0].trim();
        tc = args[1].isEmpty()?event.getTextChannel():ArgsUtils.findTextChannel(true, event, args[1]);
        if(tc==null)
            return;

        Member selfm = tc.getGuild().getSelfMember();
        if(!(ChecksUtil.hasPermission(selfm, tc, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY)))
        {
            event.replyError("core.error.cantSee.bot");
            return;
        }
        Member m = tc.getGuild().getMember(event.getAuthor());
        if(!(m==null) && !(ChecksUtil.hasPermission(m, tc, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY)))
        {
            event.replyError("core.error.cantSee.executor");
            return;
        }

        long id;
        try {id = Long.parseLong(message);}
        catch(NumberFormatException ignored)
        {
            event.replyError("command.quote.invalidId");
            return;
        }

        tc.getMessageById(id).queue(msg -> {
            if(msg.getContentRaw().isEmpty() && msg.getAttachments().isEmpty())
            {
                event.replyWarning("command.quote.empty");
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

            builder.setAuthor(author.getName()+"#"+author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
            builder.setColor(tc.getGuild().getMember(author)==null?null:tc.getGuild().getMember(author).getColor());
            builder.setDescription(sb.toString());
            builder.addField(event.localize("command.quote.jump"), "["+event.localize("misc.msg")+"]("+msg.getJumpUrl()+")", false);
            builder.setFooter((msg.isEdited()?event.localize("command.quote.edited"):event.localize("command.quote.sent"))+
                    " #"+tc.getName(), null);
            builder.setTimestamp(msg.isEdited()?msg.getEditedTime():msg.getCreationTime());
            event.reply(builder.build());
        }, e -> event.replyWarning("command.quote.error", id, tc.getAsMention()));
    }
}

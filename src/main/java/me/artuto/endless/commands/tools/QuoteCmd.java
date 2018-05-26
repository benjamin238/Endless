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
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.utils.Checks;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

/**
 * @author Artuto
 */

public class QuoteCmd extends EndlessCommand
{
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
        if(event.getArgs().isEmpty())
        {
            event.replyWarning("You didn't provided a Message ID!");
            return;
        }

        TextChannel tc;
        String message;
        String textChannel;

        try
        {
            String[] args = event.getArgs().split(" ", 2);
            message = args[0].trim();
            textChannel = args[1];
        }
        catch(IndexOutOfBoundsException e)
        {
            message = event.getArgs().trim();
            textChannel = event.getTextChannel().getId();
        }

        List<TextChannel> tList = FinderUtil.findTextChannels(textChannel, event.getGuild());

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
        else tc = tList.get(0);

        if(!(Checks.hasPermission(event.getSelfMember(), tc, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY)))
        {
            event.replyError("I can't see that channel or I don't have Read Message History permission on it!");
            return;
        }

        if(!(Checks.hasPermission(event.getMember(), tc, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY)))
        {
            event.replyError("You can't see that channel or you don't have Read Message History permission on it!");
            return;
        }

        final String id = message;

        tc.getMessageById(message).queue(s -> {
            EmbedBuilder builder = new EmbedBuilder();
            StringBuilder sb = new StringBuilder();
            User author = s.getAuthor();

            sb.append(s.getContentRaw()).append("\n");

            if(s.getAttachments().size()==1)
                builder.setImage(s.getAttachments().get(0).getUrl());
            else
            {
                for(Message.Attachment att : s.getAttachments())
                    sb.append(att.getUrl()).append("\n");
            }

            builder.setAuthor(author.getName()+"#"+author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
            builder.setColor(event.getGuild().getMember(author)==null?event.getSelfMember().getColor():event.getGuild().getMember(author).getColor());
            builder.setDescription(sb.toString());
            event.reply(builder.build());
        }, e -> event.replyWarning("I couldn't find the message `"+id+"` in "+tc.getAsMention()+"!"));
    }
}

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

package me.artuto.endless.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Messages;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.loader.Config;
import me.artuto.endless.logging.ModLogging;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.util.List;

/**
 * @author Artu
 */

public class SoftBan extends Command
{
    private final ModLogging modlog;
    private final Config config;

    public SoftBan(ModLogging modlog, Config config)
    {
        this.modlog = modlog;
        this.config = config;
        this.name = "softban";
        this.help = "Softbans the specified user";
        this.arguments = "<@user|ID|niokname|username> for [reason]";
        this.category = Categories.MODERATION;
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.ownerCommand = false;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        Member member;
        User author = event.getAuthor();
        String target;
        String reason;

        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Invalid Syntax: "+event.getClient().getPrefix()+"softban <@user|ID|nickname|username> for [reason]");
            return;
        }

        try
        {
            String[] args = event.getArgs().split(" for", 2);
            target = args[0];
            reason = args[1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            target = event.getArgs();
            reason = "[no reason specified]";
        }

        List<Member> list = FinderUtil.findMembers(target, event.getGuild());

        if(list.isEmpty())
        {
            event.replyWarning("I was not able to found a user with the provided arguments: '"+target+"'");
            return;
        }
        else if(list.size()>1)
        {
            event.replyWarning(FormatUtil.listOfMembers(list, target));
            return;
        }
        else member = list.get(0);

        if(!event.getSelfMember().canInteract(member))
        {
            event.replyError("I can't softban the specified user!");
            return;
        }

        if(!event.getMember().canInteract(member))
        {
            event.replyError("You can't softban the specified user!");
            return;
        }

        String username = "**"+member.getUser().getName()+"#"+member.getUser().getDiscriminator()+"**";
        String r = reason;

        try
        {
            if(!(member.getUser().isBot()))
            {
                builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
                builder.setTitle("Softban");
                builder.setDescription("You were softbanned on the guild **"+event.getGuild().getName()+"** by **"+event.getAuthor().getName()+"#"+event.getAuthor().getDiscriminator()+"**\n"+"They gave the following reason: **"+reason+"**\n"+"You can join again.\n");
                builder.setFooter("Time", null);
                builder.setTimestamp(Instant.now());
                builder.setColor(Color.ORANGE);
                builder.setThumbnail(event.getGuild().getIconUrl());

                member.getUser().openPrivateChannel().queue(s -> s.sendMessage(new MessageBuilder().setEmbed(builder.build()).build()).queue((d) ->
                {
                    event.getGuild().getController().ban(member, 7).reason("[SOFTBAN - 7 DAYS]["+author.getName()+"#"+author.getDiscriminator()+"]: "+r).complete();
                    event.getGuild().getController().unban(member.getUser()).reason("[SOFTBAN - 7 DAYS]["+author.getName()+"#"+author.getDiscriminator()+"]: "+r).complete();
                    event.replySuccess(Messages.SOFTBAN_SUCCESS+username);
                }, (e) ->
                {
                    event.replySuccess(Messages.SOFTBAN_SUCCESS+username);
                    event.getGuild().getController().ban(member, 7).reason("[SOFTBAN - 7 DAYS]["+author.getName()+"#"+author.getDiscriminator()+"]: "+r).complete();
                    event.getGuild().getController().unban(member.getUser()).reason("[SOFTBAN - 7 DAYS]["+author.getName()+"#"+author.getDiscriminator()+"]: "+r).complete();
                    event.replySuccess(Messages.SOFTBAN_SUCCESS+username);
                }));
            }
            else
            {
                event.getGuild().getController().ban(member, 7).reason("[SOFTBAN - 7 DAYS]["+author.getName()+"#"+author.getDiscriminator()+"]: "+reason).complete();
                event.getGuild().getController().unban(member.getUser()).reason("[SOFTBAN - 7 DAYS]["+author.getName()+"#"+author.getDiscriminator()+"]: "+reason).complete();
                event.replySuccess(Messages.SOFTBAN_SUCCESS+username);
            }

            modlog.logSoftban(event.getAuthor(), member, reason, event.getGuild(), event.getTextChannel());
        }
        catch(Exception e)
        {
            event.replyError(Messages.SOFTBAN_ERROR+username);
            LoggerFactory.getLogger("Softban Command").error(e.toString());
            if(config.isDebugEnabled()) e.printStackTrace();
        }
    }
}

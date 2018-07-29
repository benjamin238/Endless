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

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Action;
import me.artuto.endless.Bot;
import me.artuto.endless.Endless;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.ChecksUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

import java.time.OffsetDateTime;

/**
 * @author Artuto
 */

public class SoftbanCmd extends EndlessCommand
{
    private final Bot bot;

    public SoftbanCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "softban";
        this.help = "Softbans the specified user";
        this.arguments = "<@user|ID|niokname|username> for [reason]";
        this.category = Categories.MODERATION;
        this.botPerms = new Permission[]{Permission.BAN_MEMBERS};
        this.userPerms = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        String[] args = ArgsUtils.splitWithReason(2, event.getArgs(), " for ");
        String query = args[0];
        String reason = args[1];

        Member target = ArgsUtils.findMember(event, query);
        User author = event.getAuthor();
        if(target == null) return;

        if(!(ChecksUtil.canMemberInteract(event.getSelfMember(), target)))
        {
            event.replyError("I can't ban the specified user!");
            return;
        }
        if(!(ChecksUtil.canMemberInteract(event.getMember(), target)))
        {
            event.replyError("You can't ban the specified user!");
            return;
        }

        String username = "**"+target.getUser().getName()+"**#"+target.getUser().getDiscriminator();
        event.async(() ->
        {
            // Ban
            try
            {
                event.getGuild().getController().ban(target, 1).reason(author.getName()+"#"+author.getDiscriminator()+": "+reason).complete();
            }
            catch(ErrorResponseException e)
            {
                event.replyError(String.format("An error happened when softbanning %s", username));
                Endless.LOG.error("Could not softban user {} in guild {}", target.getUser().getId(), event.getGuild().getId(), e);
            }

            // Unban
            try
            {
                event.getGuild().getController().unban(target.getUser()).reason(author.getName()+"#"+author.getDiscriminator()+": Softban Unban").complete();
            }
            catch(ErrorResponseException e)
            {
                event.replyError(String.format("Error while unbanning %s", username));
                Endless.LOG.error("Could not unban user {} in guild {}", target.getUser().getId(), event.getGuild().getId(), e);
            }

            event.replySuccess(String.format("Successfully softbanned user %s", username));
            bot.modlog.logGeneral(Action.SOFTBAN, event, OffsetDateTime.now(), reason, target.getUser());
        });
    }
}

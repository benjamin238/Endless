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
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

import java.time.OffsetDateTime;

public class HackbanCmd extends EndlessCommand
{
    private final Bot bot;

    public HackbanCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "hackban";
        this.help = "Hackbans the specified user";
        this.arguments = "<ID> for [reason]";
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

        User target = ArgsUtils.findUser(true, event, query);
        User author = event.getAuthor();
        if(target==null)
            return;

        if(!(event.getGuild().getMember(target)==null))
        {
            event.replyWarning("This user is on this Guild! Please use `"+event.getClient().getPrefix()+"ban` instead.");
            return;
        }

        String username = "**"+target.getName()+"**#"+target.getDiscriminator();
        event.getGuild().getController().ban(target, GuildUtils.getBanDeleteDays(event.getGuild()))
                .reason(author.getName()+"#"+author.getDiscriminator()+": "+reason).queue(s -> {
                    event.replySuccess(String.format("Successfully hackbanned user %s", username));
                    bot.modlog.logGeneral(Action.BAN, event, OffsetDateTime.now(), reason, target);
            }, e -> {
                    event.replyError(String.format("An error happened when hackbanning %s", username));
                    Endless.LOG.error("Could not hackban user {} in guild {}", target.getId(), event.getGuild().getId(), e);
        });
    }
}

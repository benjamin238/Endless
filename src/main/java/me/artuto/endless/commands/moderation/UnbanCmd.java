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

import me.artuto.endless.Action;
import me.artuto.endless.Bot;
import me.artuto.endless.Endless;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

import java.time.OffsetDateTime;

/**
 * @author Artuto
 */

public class UnbanCmd extends EndlessCommand
{
    private final Bot bot;

    public UnbanCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "unban";
        this.help = "Unbans the specified user";
        this.arguments = "<@user|ID|username> for [reason]";
        this.category = Categories.MODERATION;
        this.botPerms = new Permission[]{Permission.BAN_MEMBERS};
        this.userPerms = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        String[] args = ArgsUtils.splitWithReason(2, event.getArgs(), " for ");
        String query = args[0];
        String reason = args[1];

        User target = ArgsUtils.findBannedUser(event, query);
        User author = event.getAuthor();
        if(target==null)
            return;

        String username = "**"+target.getName()+"**#"+target.getDiscriminator();
        event.getGuild().getController().unban(target).reason(author.getName()+"#"+author.getDiscriminator()+": "+reason).queue(s -> {
            event.replySuccess("command.unban.success", username);
            bot.modlog.logGeneral(Action.UNBAN, event, OffsetDateTime.now(), reason, target);
        }, e -> {
            event.replyError("command.unban.error", username);
            Endless.LOG.error("Could not unban user {} in guild {}", target.getId(), event.getGuild().getId(), e);
        });
    }
}

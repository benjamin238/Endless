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
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Artuto
 */

public class NickCmd extends EndlessCommand
{
    public NickCmd()
    {
        this.name = "nick";
        this.help = "Changes your nickname in the current guild";
        this.aliases = new String[]{"nickname"};
        this.arguments = "<nick>";
        this.category = Categories.TOOLS;
        this.userPerms = new Permission[]{Permission.NICKNAME_CHANGE};
        this.botPerms = new Permission[]{Permission.NICKNAME_MANAGE};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        Member member = event.getMember();
        String nickname = FormatUtil.sanitize(event.getArgs());
        User author = event.getAuthor();

        if(!(ChecksUtil.canMemberInteract(event.getSelfMember(), member)))
        {
            event.replyError("I can't interact with you!");
            return;
        }
        if(nickname.length()>32)
        {
            event.replyError("Your nickname can't be longer than 32 characters!");
            return;
        }

        event.getGuild().getController().setNickname(member, nickname).reason(author.getName()+"#"+author.getDiscriminator()+": Nickname change")
                .queue(s -> event.replySuccess("Successfully changed your nickname to **"+nickname+"**"),
                        e -> event.replyError("Could not change your nickname to **"+nickname+"**"));
    }
}

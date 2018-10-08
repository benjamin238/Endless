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
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.utils.MiscUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

/**
 * @author Artuto
 */

public class AvatarCmd extends EndlessCommand
{
    public AvatarCmd()
    {
        this.name = "avatar";
        this.help = "Displays the avatar of the specified user.";
        this.aliases = new String[]{"avy", "pfp"};
        this.arguments = "[user]";
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        User target;

        if(event.getArgs().isEmpty() || !(event.isFromType(ChannelType.TEXT)))
            target = event.getAuthor();
        else
        {
            target = ArgsUtils.findUser(false, event, event.getArgs());
            if(target==null)
                return;
        }

        String title = FormatUtil.sanitize(":frame_photo: "+event.localize("command.avatar", target.getName()+"#"+target.getDiscriminator()));

        builder.setImage(MiscUtils.getImageUrl("png", "512", target.getEffectiveAvatarUrl()));
        builder.setColor(event.isFromType(ChannelType.TEXT)?event.getGuild().getMember(target).getColor():Color.decode("#33ff00"));
        event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());
    }
}

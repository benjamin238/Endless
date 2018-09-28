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

package me.artuto.endless.commands.fun;

import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.IOUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import org.json.JSONObject;

import java.awt.*;

public class DogCmd extends EndlessCommand
{
    public DogCmd()
    {
        this.name = "dog";
        this.help = "Displays a cute pupper.";
        this.category = Categories.FUN;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.ownerCommand = false;
        this.guildOnly = false;
        this.cooldown = 10;
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        Color color;

        if(event.isFromType(ChannelType.PRIVATE))
            color = Color.decode("#33ff00");
        else
            color = event.getMember().getColor();

        EmbedBuilder builder = new EmbedBuilder();
        JSONObject json = IOUtils.makeGETRequest(null, "https://random.dog/woof.json");
        if(json==null)
        {
            event.replyError("core.error.retrievingImage");
            return;
        }

        String cat = json.getString("url");

        builder.setAuthor(event.localize("misc.requestedBy", event.getAuthor().getName()), null, event.getAuthor().getEffectiveAvatarUrl());
        builder.setImage(cat);
        builder.setFooter(event.localize("misc.imageProvided", "random.dog API"), null);
        builder.setColor(color);

        event.reply(builder.build());
    }
}

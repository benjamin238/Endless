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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;

public class Dog extends Command
{
    private final Logger LOG = LoggerFactory.getLogger("Dog Image Command");
    private Config config;

    public Dog(Config config)
    {
        this.config = config;
        this.name = "dog";
        this.help = "Displays a cute pupper.";
        this.category = Categories.FUN;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
        this.cooldown = 10;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        try
        {
            Color color;

            if(event.isFromType(ChannelType.PRIVATE)) color = Color.decode("#33ff00");
            else color = event.getMember().getColor();

            EmbedBuilder builder = new EmbedBuilder();
            OkHttpClient client = new OkHttpClient();
            Request req = new Request.Builder().url("https://random.dog/woof.json").get().build();
            Response res = client.newCall(req).execute();
            if(!res.isSuccessful()) throw new RuntimeException("Error while fetching remote resource");
            ResponseBody body = res.body();
            String data = body.string();
            JSONObject json = new JSONObject(data);
            String cat = json.getString("url");

            builder.setAuthor("Requested by "+event.getAuthor().getName(), null, event.getAuthor().getEffectiveAvatarUrl());
            builder.setImage(cat);
            builder.setFooter("Image provided by random.dog API", null);
            builder.setColor(color);

            event.reply(builder.build());
        }
        catch(IOException|RuntimeException e)
        {
            event.replyError("An error was thrown when getting the image! Ask the Owner to check the Console.");
            LOG.error(e.getMessage());

            if(config.isDebugEnabled()) e.printStackTrace();
        }
    }
}

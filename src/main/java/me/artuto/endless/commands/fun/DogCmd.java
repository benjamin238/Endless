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

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
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

public class DogCmd extends EndlessCommand
{
    private final Logger LOG = LoggerFactory.getLogger("Dog Image Command");
    private Bot bot;

    public DogCmd(Bot bot)
    {
        this.bot = bot;
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
    protected void executeCommand(CommandEvent event)
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

            if(bot.config.isDebugEnabled()) e.printStackTrace();
        }
    }
}

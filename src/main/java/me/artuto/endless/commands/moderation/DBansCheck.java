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
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Bot;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import okhttp3.*;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * @author Artuto
 */

public class DBansCheck extends EndlessCommand
{
    private final Logger LOG = LoggerFactory.getLogger("DiscordBans Command");
    private final Bot bot;

    public DBansCheck(Bot bot)
    {
        this.bot = bot;
        this.name = "discordbans";
        this.help = "Checks if the specified user ID is registered on Discord Bans";
        this.arguments = "<@user|ID|nickname|username>";
        this.category = Categories.MODERATION;
        this.aliases = new String[]{"checkbans", "dbans"};
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.cooldown = 10;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        User user;
        String info;
        String title = "<:discordBans:368565133619757068> Info from Discord Bans:";

        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Please specify a user!");
            return;
        }

        if(bot.config.getDBansToken().isEmpty())
        {
            event.replyError("This command has been disabled due a faulty parameter on the config file, ask the Owner to check the Console");
            LOG.warn("Someone triggered the Discord Bans Check command, but there isn't a token in the config file. In order to stop this message add a token to the config file.");
            return;
        }

        List<User> list = FinderUtil.findUsers(event.getArgs(), event.getJDA());

        if(list.isEmpty())
        {
            try
            {
                user = event.getJDA().retrieveUserById(event.getArgs()).complete();
            }
            catch(Exception e)
            {
                event.replyError("Invalid user");
                return;
            }
        }
        else if(list.size()>1)
        {
            event.replyWarning(FormatUtil.listOfUsers(list, event.getArgs()));
            return;
        }
        else user = list.get(0);

        try
        {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder().add("token", bot.config.getDBansToken()).add("userid", user.getId()).add("version", "3").build();
            Request request = new Request.Builder().url("https://bans.discordlist.net/api").post(formBody).build();
            Response response = client.newCall(request).execute();

            info = response.body().string();

            if(info.equals("False"))
            {
                builder.addField("User: ", user.getName(), true);
                builder.addField("ID: ", user.getId(), true);
                builder.addField("Status: ", "Not on the list", false);
                builder.setThumbnail(user.getEffectiveAvatarUrl());
                builder.setFooter("Information provided by DiscordBans API", null);
                builder.setColor(Color.GREEN);
            }
            else
            {
                JSONArray output = new JSONArray(info);
                String proof = output.get(4).toString().replace("<a href=\"", "").replace("\">Proof</a>", "").replace("\\", "");

                builder.addField("User: ", user.getName(), true);
                builder.addField("ID: ", user.getId(), true);
                builder.addField("Status: ", "On the list - Report ID: #"+output.get(0), false);
                builder.addField("Reason: ", "`"+output.get(3)+"`", false);
                builder.addField("Proof: ", proof, false);
                builder.setThumbnail(user.getEffectiveAvatarUrl());
                builder.setFooter("Information provided by DiscordBans API", null);
                builder.setColor(Color.RED);
            }

            event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());
        }
        catch(IOException e)
        {
            event.replyError("An error was thrown when doing the check! Ask the Owner to check the Console.");
            LOG.error(e.toString());

            if(bot.config.isDebugEnabled()) e.printStackTrace();
        }
    }
}

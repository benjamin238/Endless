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
import com.kdotj.simplegiphy.SimpleGiphy;
import com.kdotj.simplegiphy.data.Giphy;
import com.kdotj.simplegiphy.data.GiphyListResponse;
import com.kdotj.simplegiphy.data.RandomGiphy;
import com.kdotj.simplegiphy.data.RandomGiphyResponse;
import me.artuto.endless.Bot;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class GiphyGifCmd extends EndlessCommand
{
    private final Logger LOG = LoggerFactory.getLogger("Giphy Command");
    private Bot bot;

    public GiphyGifCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "giphy";
        this.aliases = new String[]{"gif"};
        this.children = new Command[]{new RandomGif()};
        this.help = "Searches a gif on Giphy using the specified serarch terms.";
        this.arguments = "[keyword]";
        this.category = Categories.FUN;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.userPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        String args = event.getArgs();

        if(bot.config.getGihpyKey().isEmpty())
        {
            event.replyError("This command has been disabled due a faulty parameter on the config file, ask the Owner to check the Console");
            LOG.warn("Someone triggered the Giphy command, but there isn't a key in the config file. In order to stop this message add a key to the config file.");
            return;
        }

        try
        {
            SimpleGiphy.setApiKey(bot.config.getGihpyKey());
            GiphyListResponse r;
            EmbedBuilder builder = new EmbedBuilder();
            String title;

            Color color;

            if(event.isFromType(ChannelType.PRIVATE)) color = Color.decode("#33ff00");
            else color = event.getMember().getColor();

            if(args.isEmpty())
            {
                title = "<:giphy:373675520099090436> Trending GIF:";
                r = SimpleGiphy.getInstance().trending("50", "pg-13");
                List<Giphy> list = r.getData();
                if(list.isEmpty()) event.replyWarning("No results found!");
                else
                {
                    Integer rand = new Random().nextInt(list.size());
                    Giphy gif = r.getData().get(rand);

                    builder.setImage(gif.getImages().getOriginal().getUrl());
                    builder.setFooter("GIF provided by Giphy API", "https://cdn.discordapp.com/attachments/304027425509998593/373674151472267265/Poweredby_640px_Badge.gif");
                    builder.setColor(color);

                    event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());
                }
            }
            else
            {
                title = "<:giphy:373675520099090436> Results for `"+args+"`:";
                r = SimpleGiphy.getInstance().search(args, "50", "0", "pg-13");
                List<Giphy> list = r.getData();
                if(list.isEmpty()) event.replyWarning("No results found!");
                else
                {
                    Integer rand = new Random().nextInt(list.size());
                    Giphy gif = r.getData().get(rand);

                    builder.setImage(gif.getImages().getOriginal().getUrl());
                    builder.setFooter("GIF provided by Giphy API", "https://cdn.discordapp.com/attachments/304027425509998593/373674151472267265/Poweredby_640px_Badge.gif");
                    builder.setColor(color);

                    event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());
                }
            }
        }
        catch(Exception e)
        {
            event.replyError("An error was thrown when getting a gif! Ask the Owner to check the Console.");
            LOG.error(e.getMessage());
        }
    }

    private class RandomGif extends EndlessCommand
    {
        RandomGif()
        {
            this.name = "random";
            this.help = "Retrieves a random GIF from Giphy.";
            this.category = Categories.FUN;
            this.arguments = "[keyword]";
            this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
            this.guildOnly = false;
            this.needsArgumentsMessage = "No search terms specified!";
        }

        protected void executeCommand(CommandEvent event)
        {
            String args = event.getArgs();

            if(bot.config.getGihpyKey().isEmpty())
            {
                event.replyError("This command has been disabled due a faulty parameter on the config file, ask the Owner to check the Console");
                LOG.warn("Someone triggered the Giphy command, but there isn't a key in the config file. In order to stop this message add a key to the config file.");
                return;
            }

            Color color;

            if(event.isFromType(ChannelType.PRIVATE)) color = Color.decode("#33ff00");
            else color = event.getMember().getColor();

            SimpleGiphy.setApiKey(bot.config.getGihpyKey());
            RandomGiphyResponse r;
            EmbedBuilder builder = new EmbedBuilder();
            String title = "<:giphy:373675520099090436> Random Giphy Image:";
            r = SimpleGiphy.getInstance().random(args, "pg-13");
            RandomGiphy gif = r.getRandomGiphy();

            builder.setImage(gif.getImageOriginalUrl());
            builder.setFooter("GIF provided by Giphy API", "https://cdn.discordapp.com/attachments/304027425509998593/373674151472267265/Poweredby_640px_Badge.gif");
            builder.setColor(color);

            event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());
        }
    }

}

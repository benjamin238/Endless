package me.artuto.endless.commands.fun;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.kdotj.simplegiphy.SimpleGiphy;
import com.kdotj.simplegiphy.data.Giphy;
import com.kdotj.simplegiphy.data.GiphyListResponse;
import com.kdotj.simplegiphy.data.RandomGiphy;
import com.kdotj.simplegiphy.data.RandomGiphyResponse;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class GiphyGif extends Command
{
    private final Logger LOG = LoggerFactory.getLogger("Giphy Command");
    private Config config;

    public GiphyGif(Config config)
    {
        this.config = config;
        this.name = "giphy";
        this.aliases = new String[]{"gif"};
        this.children = new Command[]{new RandomGif()};
        this.help = "Searches a gif on Giphy using the specified serarch terms.";
        this.arguments = "[keyword]";
        this.category = Categories.FUN;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
        this.userPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.ownerCommand = false;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        String args = event.getArgs();

        if (config.getGihpyKey().isEmpty())
        {
            event.replyError("This command has been disabled due a faulty parameter on the config file, ask the Owner to check the Console");
            LOG.warn("Someone triggered the Giphy command, but there isn't a key in the config file. In order to stop this message add a key to the config file.");
            return;
        }

        try
        {
            SimpleGiphy.setApiKey(config.getGihpyKey());
            GiphyListResponse r;
            EmbedBuilder builder = new EmbedBuilder();
            String title;

            Color color;

            if(event.isFromType(ChannelType.PRIVATE))
                color = Color.decode("#33ff00");
            else
                color = event.getMember().getColor();

            if(args.isEmpty())
            {
                title = "<:giphy:373675520099090436> Trending GIF:";
                r = SimpleGiphy.getInstance().trending("50", "pg-13");
                List<Giphy> list = r.getData();
                if (list.isEmpty())
                    event.replyWarning("No results found!");
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
                title = "<:giphy:373675520099090436> Results for `" + args + "`:";
                r = SimpleGiphy.getInstance().search(args, "50", "0", "pg-13");
                List<Giphy> list = r.getData();
                if (list.isEmpty())
                    event.replyWarning("No results found!");
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

            if (config.isDebugEnabled())
                e.printStackTrace();
        }
    }

    private class RandomGif extends Command
    {
        private RandomGif()
        {
            this.name = "random";
            this.help = "Retrieves a random GIF from Giphy.";
            this.category = Categories.FUN;
            this.arguments = "[keyword]";
            this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
            this.userPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
            this.ownerCommand = false;
            this.guildOnly = false;
        }

        protected void execute(CommandEvent event)
        {
            String args = event.getArgs();

            if(config.getGihpyKey().isEmpty())
            {
                event.replyError("This command has been disabled due a faulty parameter on the config file, ask the Owner to check the Console");
                LOG.warn("Someone triggered the Giphy command, but there isn't a key in the config file. In order to stop this message add a key to the config file.");
                return;
            }

            if(args.isEmpty())
            {
                event.replyWarning("No search terms specified!");
                return;
            }

            Color color;

            if(event.isFromType(ChannelType.PRIVATE))
                color = Color.decode("#33ff00");
            else
                color = event.getMember().getColor();

            SimpleGiphy.setApiKey(config.getGihpyKey());
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

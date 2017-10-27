package me.artuto.endless.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.kdotj.simplegiphy.SimpleGiphy;
import com.kdotj.simplegiphy.data.Giphy;
import com.kdotj.simplegiphy.data.GiphyListResponse;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.util.List;
import java.util.Random;

public class GiphyGif extends Command
{
    private final SimpleLog LOG = SimpleLog.getLog("Giphy");
    private Config config;

    public GiphyGif(Config config)
    {
        this.config = config;
        this.name = "giphy";
        this.aliases = new String[]{"gif"};
        this.help = "Searches a gif on Giphy using the specified serarch terms";
        this.category = Categories.FUN;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.userPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.ownerCommand = false;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        String args = event.getArgs();

        if(config.getGihpyKey().isEmpty())
        {
            event.replyError("This command has been disabled due a faulty parameter on the config file, ask the Owner to check the Console");
            LOG.warn("Someone triggered the Giphy command, but there isn't a key in the config file. In order to stop this message add a key to the config file.");
            return;
        }

        try
        {
            SimpleGiphy.setApiKey(config.getGihpyKey());
            GiphyListResponse r;


            if(args.isEmpty())
            {
                r = SimpleGiphy.getInstance().trending("50", "pg-13");
                List<Giphy> list = r.getData();

                Integer rand = new Random().nextInt(list.size());

                event.reply(r.getData().get(rand).getEmbedUrl());
            }
            else
            {
                r = SimpleGiphy.getInstance().search(args, "50", "0", "pg-13");
                List<Giphy> list = r.getData();

                Integer rand = new Random().nextInt(list.size());

                event.reply(r.getData().get(rand).getEmbedUrl());
            }

        }
        catch(Exception e)
        {
            event.replyError("An error was thrown when getting a gif! Ask the Owner to check the Console.");
            LOG.fatal(e);

            if(config.isDebugEnabled())
                e.printStackTrace();
        }
    }
}

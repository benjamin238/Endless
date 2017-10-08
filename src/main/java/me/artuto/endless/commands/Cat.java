package me.artuto.endless.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.utils.SimpleLog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;

public class Cat extends Command
{
    private final SimpleLog LOG = SimpleLog.getLog("Cat Image");
    private final Config config;

    public Cat(Config config)
    {
        this.config = config;
        this.name = "cat";
        this.help = "Displays a cute kitty.";
        this.category = Categories.FUN;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }

    protected void execute(CommandEvent event)
    {
        try
        {
            Color color;

            if(event.isFromType(ChannelType.PRIVATE))
            {
                color = Color.decode("#33ff00");
            }
            else
            {
                color = event.getGuild().getSelfMember().getColor();
            }

            EmbedBuilder builder = new EmbedBuilder();
            OkHttpClient client = new OkHttpClient();
            Request req = new Request.Builder().url("http://random.cat/meow").get().build();
            Response res = client.newCall(req).execute();
            if(!res.isSuccessful())
                throw new RuntimeException("Error while fetching remote resource");
            ResponseBody body = res.body();
            String data = body.string();
            JSONObject json = new JSONObject(data);
            String cat = json.getString("file");
            
            builder.setAuthor("Requested by "+event.getAuthor().getName(), null, event.getAuthor().getEffectiveAvatarUrl());
            builder.setImage(cat);
            builder.setFooter("Image provided by random.cat API", null);
            builder.setColor(color);

            event.reply(builder.build());
        }
        catch(IOException | RuntimeException e)
        {
            event.replyError("An error was thrown when getting the image!! Ask the Owner to check the Console.");
            LOG.fatal(e);

            if (config.isDebugEnabled())
                e.printStackTrace();
        }
    }
}

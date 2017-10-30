package me.artuto.endless.commands;

import com.github.vbauer.yta.model.Language;
import com.github.vbauer.yta.model.Translation;
import com.github.vbauer.yta.service.YTranslateApi;
import com.github.vbauer.yta.service.YTranslateApiImpl;
import com.github.vbauer.yta.service.basic.exception.YTranslateException;
import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.awt.*;

public class Translate extends Command
{
    private final SimpleLog LOG = SimpleLog.getLog("Giphy");
    private final Config config;

    public Translate(Config config)
    {
        this.config = config;
        this.name = "translate";
        this.help = "Translate something!";
        this.arguments = "<target language> <text>";
        this.category = Categories.UTILS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        String args = event.getArgs();
        String language;
        String text;

        if(config.getGihpyKey().isEmpty())
        {
            event.replyError("This command has been disabled due a faulty parameter on the config file, ask the Owner to check the Console");
            LOG.warn("Someone triggered the Translate command, but there isn't a key in the config file. In order to stop this message add a key to the config file.");
            return;
        }

        if(args.isEmpty())
        {
            event.replyWarning("Invalid sytnax: `"+this.getArguments()+"`");
            return;
        }

        try
        {
            String[] arguments = args.split(" ", 2);
            language = arguments[0].toUpperCase().trim();
            text = arguments[1].trim();
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            event.replyWarning("Invalid sytnax: `"+this.getArguments()+"`");
            return;
        }

        if(text.length()>2048)
        {
            event.replyError("The text to translate length is over than 2048 characters!");
            return;
        }

        try
        {
            Color color;

            if(event.isFromType(ChannelType.PRIVATE))
                color = Color.decode("#33ff00");
            else
                color = event.getMember().getColor();

            String title = "<:yandexTranslate:374422013437149186> Text Translated successfully: ";
            EmbedBuilder builder = new EmbedBuilder();
            YTranslateApi api = new YTranslateApiImpl(config.getTranslateKey());
            Language target = Language.of(language);
            Translation translated = api.translationApi().translate(text, target);
            Language lang = api.detectionApi().detect(text).get();

            builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getEffectiveAvatarUrl());
            builder.addField("Text in `"+lang.code()+"`", "```"+text+"```", false);
            builder.addField("Text Translated in `"+target.code()+"`", "```"+translated.text()+"```", false);
            builder.setFooter("Translation provided by Yandex Translate API", "https://cdn.discordapp.com/emojis/374422013437149186.png");
            builder.setColor(color);

            event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());
        }
        catch(YTranslateException e)
        {
            event.reply("That language isn't valid!");
        }
        catch(Exception e)
        {
            event.replyError("Something went wrong when translating the text: \n```"+e+"```");

            if(config.isDebugEnabled())
                e.printStackTrace();
        }
    }
}

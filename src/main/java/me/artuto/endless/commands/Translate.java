package me.artuto.endless.commands;

import com.gtranslate.Translator;
import com.gtranslate.text.TextTranslate;
import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;

public class Translate extends Command
{
    public Translate()
    {
        this.name = "translate";
        this.help = "Translate something!";
        this.arguments = "<target language> <text>";
        this.category = Categories.UTILS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
        this.cooldown = 10;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        /*event.replyError("Command Disabled due Google having issues");
        return;*/

        EmbedBuilder builder = new EmbedBuilder();
        Color color = Color.BLUE;
        Translator t;
        String title = "<:googleTranslate:364207511944953866> Translation finished:";
        String target;
        String lang;
        String text;
        String output;

        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Please specify something!");
            return;
        }

        try
        {
            String[] args = event.getArgs().split(" ", 2);
            target = args[0];
            text = args[1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            event.replyFormatted("%s Inavlid arguments, usage: %s+translate <target language> <text>", event.getClient().getWarning(), event.getClient().getPrefix());
            return;
        }

        try
        {
            t = Translator.getInstance();
            lang = t.detect(text);
            output = t.translate(text, lang, target);

            builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getEffectiveAvatarUrl());
            builder.addField("Text in **"+lang+"**", "```text```", false);
            builder.addField("Text Translated in **"+target+"**", "```"+output+"```", false);
            builder.setThumbnail("https://cdn.discordapp.com/emojis/364207511944953866.png");
            builder.setFooter("Translation provided by Google Translate API", null);
            builder.setColor(color);

            event.reply(new MessageBuilder().append(title).setEmbed(builder.build()).build());
        }
        catch(Exception e)
        {
            event.replyError("Something went wrong when translating the text: \n```"+e+"```");
            e.printStackTrace();
        }
    }
}

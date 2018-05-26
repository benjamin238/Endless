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

package me.artuto.endless.commands.utils;

import com.github.vbauer.yta.model.Language;
import com.github.vbauer.yta.model.Translation;
import com.github.vbauer.yta.service.YTranslateApi;
import com.github.vbauer.yta.service.YTranslateApiImpl;
import com.github.vbauer.yta.service.basic.exception.YTranslateException;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class TranslateCmd extends EndlessCommand
{
    private final Logger LOG = LoggerFactory.getLogger("Translate Command");
    private final Bot bot;

    public TranslateCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "translate";
        this.help = "Translate something!";
        this.arguments = "<target language> <text>";
        this.category = Categories.UTILS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        String args = event.getArgs();
        String language;
        String text;

        if(bot.config.getGihpyKey().isEmpty())
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

        if(text.length()>2000)
        {
            event.replyError("The text to translate length is over than 2000 characters!");
            return;
        }

        try
        {
            Color color;

            if(event.isFromType(ChannelType.PRIVATE)) color = Color.decode("#33ff00");
            else color = event.getMember().getColor();

            String title = "<:yandexTranslate:374422013437149186> Text Translated successfully: ";
            EmbedBuilder builder = new EmbedBuilder();
            YTranslateApi api = new YTranslateApiImpl(bot.config.getTranslateKey());
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
    }
}

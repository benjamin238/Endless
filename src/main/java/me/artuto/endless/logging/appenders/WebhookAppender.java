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

package me.artuto.endless.logging.appenders;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import me.artuto.endless.utils.LogUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

/**
 * @author Artuto
 */

public class WebhookAppender extends AppenderBase<ILoggingEvent>
{
    private WebhookClient client;

    public WebhookAppender() throws IOException
    {
        List<String> config = Files.readAllLines(Paths.get("webhook.txt"));
        client = new WebhookClientBuilder(config.get(0)).setDaemon(true).build();
    }

    @Override
    protected void append(ILoggingEvent event)
    {
        Color color = null;
        Level level = event.getLevel();

        if(level==Level.INFO)
            color = Color.blue;
        else if(level==Level.ERROR)
            color = Color.RED;
        else if(level==Level.WARN)
            color = Color.ORANGE;

        EmbedBuilder eBuilder = new EmbedBuilder();
        eBuilder.setColor(color);
        eBuilder.setDescription(LogUtils.getStackTrace(event));
        eBuilder.setFooter("Endless Log", "https://cdn.discordapp.com/avatars/328625129309339649/2c1ca7ef1e1b58e5a686b413b6d756c5.png");
        eBuilder.setTimestamp(Instant.now());
        eBuilder.setTitle(event.getLoggerName());

        client.send(eBuilder.build());
    }
}

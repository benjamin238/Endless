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

package me.artuto.endless.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.dv8tion.jda.core.OnlineStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;

/**
 * @author Artuto
 * Thanks Spotlight for the code.
 */

public class Config
{
    private final Logger LOG = LoggerFactory.getLogger("Config");
    private static ConfigFormat format;

    public Config() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        format = mapper.readValue(new File("config.yml"), ConfigFormat.class);

        for(Field field : format.getClass().getDeclaredFields())
        {
            if(field.get(format) == null)
            {
                LOG.error("Error when reading the config!");
                throw new Exception(field.getName()+" in your config was null!");
            }
        }
    }

    public String getToken()
    {
        return format.token;
    }

    public String getPrefix()
    {
        return format.prefix;
    }

    public String getGame()
    {
        return format.game;
    }

    public String getDBotsToken()
    {
        return format.discordBotsToken;
    }

    public String getDBotsListToken()
    {
        return format.discordBotListToken;
    }

    public String getBingMapsKey()
    {
        return format.bingMapsKey;
    }

    public String getDarkSkyKey()
    {
        return format.darkskyKey;
    }

    public String getGihpyKey()
    {
        return format.giphyKey;
    }

    public String getGoogleKey()
    {
        return format.googleKey;
    }

    public String getGoogleSearcherId()
    {
        return format.googleSearcherId;
    }

    public String getTranslateKey()
    {
        return format.yandexTranslateKey;
    }

    public String getYouTubeKey()
    {
        return format.youtubeKey;
    }

    public String getDoneEmote()
    {
        return format.doneEmote;
    }

    public String getWarnEmote()
    {
        return format.warnEmote;
    }

    public String getErrorEmote()
    {
        return format.errorEmote;
    }

    public String getDatabaseUrl()
    {
        return format.dbUrl;
    }

    public String getDatabaseUsername()
    {
        return format.dbUsername;
    }

    public String getDatabasePassword()
    {
        return format.dbPassword;
    }

    public Long getOwnerId()
    {
        return format.ownerId;
    }

    public Long[] getCoOwnerIds()
    {
        return format.coOwnerIds;
    }

    public Long getRootGuildId()
    {
        return format.rootGuildId;
    }

    public String getCommandlogWebhook()
    {
        return format.commandlogWebhook;
    }

    public String getBotlogWebhook()
    {
        return format.botlogWebhook;
    }

    public Boolean api()
    {
        return format.api;
    }

    /*public int getAPIPort()
    {
        return format.apiPort;
    }

    public String getAPIToken()
    {
        return format.apiToken;
    }*/

    public OnlineStatus getStatus()
    {
        return format.status;
    }

    public Boolean isBotlogEnabled()
    {
        return format.botlog;
    }

    public Boolean isDebugEnabled()
    {
        return format.debug;
    }

    public Boolean isDeepDebugEnabled()
    {
        return format.deepDebug;
    }

    public boolean isSentryEnabled()
    {
        return format.sentryEnabled;
    }

    public String getSentryDSN()
    {
        return format.sentryDSN;
    }
}

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

import net.dv8tion.jda.core.OnlineStatus;

@SuppressWarnings("WeakerAccess")
public class ConfigFormat
{
    public String token;
    public String prefix;
    public String game;
    public String discordBotsToken;
    public String discordBotListToken;
    public String discordBansToken;
    public String giphyKey;
    public String yandexTranslateKey;
    public String youtubeKey;
    public String doneEmote;
    public String warnEmote;
    public String errorEmote;
    public String dbUrl;
    public String dbUsername;
    public String dbPassword;
    public Long ownerId;
    public Long[] coOwnerIds;
    public Long rootGuildId;
    public Long botlogChannelId;
    public String botlogWebhook;
    public Boolean api;
    /*public int apiPort;
    public String apiToken;*/
    public OnlineStatus status;
    public Boolean botlog;
    public Boolean debug;
    public Boolean deepDebug;
    public boolean sentryEnabled;
    public String sentryDSN;
    public Long commandslogChannelId;
}
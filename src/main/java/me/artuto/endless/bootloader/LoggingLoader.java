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

package me.artuto.endless.bootloader;

import me.artuto.endless.Bot;
import me.artuto.endless.logging.BotLogging;
import me.artuto.endless.logging.ModLogging;
import me.artuto.endless.logging.ServerLogging;

/**
 * @author Artuto
 */

class LoggingLoader
{
    BotLogging botlog;
    ModLogging modlog;
    ServerLogging serverlog;

    void initialize(Bot bot)
    {
        botlog = new BotLogging(bot);
        modlog = new ModLogging(bot);
        serverlog = new ServerLogging(bot);
    }
}

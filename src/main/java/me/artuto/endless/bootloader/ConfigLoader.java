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

import ch.qos.logback.classic.Logger;
import me.artuto.endless.exceptions.ConfigException;
import me.artuto.endless.loader.Config;
import org.slf4j.LoggerFactory;

/**
 * @author Artuto
 */

public class ConfigLoader
{
    private final Logger LOG = (Logger) LoggerFactory.getLogger("Config Loader");

    public Config loadConfig()
    {
        LOG.info("Loading config file...");

        try
        {
            Config config = new Config();
            LOG.info("Successfully loaded config file!");
            return config;
        }
        catch(Exception e)
        {
            throw new ConfigException();
        }
    }
}
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

package me.artuto.endless;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Artuto
 */

public class Endless
{
    public static Bot bot;
    public static Logger LOG = (Logger) LoggerFactory.getLogger("Endless");

    public static void main(String[] args)
    {
        bot = new Bot();

        if(args.length==0)
            LOG.error("No arguments provided!");
        else try
        {
            switch(args[0])
            {
                case "normalBoot":
                    bot.boot(false);
                    break;
                case "maintenance":
                    bot.boot(true);
                    break;
                case "none":
                    LOG.info("Goodbye! :wave:");
                    System.exit(0);
                    break;
                default:
                    LOG.error("Invalid argument!");
            }
        }
        catch(Exception e)
        {
            LOG.error("Error while starting Endless!", e);
            System.exit(0);
        }
    }
}

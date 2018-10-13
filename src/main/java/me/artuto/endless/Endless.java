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

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Artuto
 */

public class Endless
{
    public static Bot bot;
    public static Logger LOG = (Logger)LoggerFactory.getLogger("Endless");

    public static Logger getLog(Class c)
    {
        return (Logger)LoggerFactory.getLogger(c);
    }

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
                    if(args.length>1)
                        if(args[1].equals("dataDisabled"))
                        {
                            bot.boot(false, false);
                            break;
                        }
                    bot.boot(true, false);
                    break;
                case "maintenance":
                    if(args.length>1)
                        if(args[1].equals("dataDisabled"))
                        {
                            bot.boot(false, true);
                            break;
                        }
                    bot.boot(true, true);
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

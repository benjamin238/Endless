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

package me.artuto.endless.utils;

import com.jagrosh.jagtag.Parser;
import com.jagrosh.jagtag.ParserBuilder;
import com.jagrosh.jagtag.libraries.*;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.libraries.DiscordJagTag;
import net.dv8tion.jda.core.entities.ChannelType;

/**
 * @author Artuto
 */

public class TagUtil
{
    public static final Parser parser = new ParserBuilder().addMethods(DiscordJagTag.getMethods())
        .addMethods(Arguments.getMethods()).addMethods(Functional.getMethods()).addMethods(Miscellaneous.getMethods())
        .addMethods(Strings.getMethods()).addMethods(Time.getMethods()).addMethods(Variables.getMethods())
        .setMaxOutput(2000).setMaxIterations(1000).build();

    public static boolean isNSFWAllowed(CommandEvent event)
    {
        if(event.isFromType(ChannelType.TEXT))
            return event.getTextChannel().isNSFW();
        return true;
    }
}

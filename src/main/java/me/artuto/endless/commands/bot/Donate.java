/*
 * Copyright (C) 2017 Artu
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

package me.artuto.endless.commands.bot;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;

/**
 *
 * @author Artu
 */

public class Donate extends Command
{
    public Donate()
    {
        this.name = "donate";
        this.help = "Info about donations";
        this.arguments = "";
        this.category = new Command.Category("Bot");
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
      EmbedBuilder builder = new EmbedBuilder();
           builder.setColor(event.getSelfMember().getColor());
           builder.addField(":moneybag: Donations:", "Actually, I host Endless on a very basic VPS, which can cause some lag sometimes.\n"
                   + "I'll appreciate your donation. All the recauded money will be for get a new and better VPS.\n", false);
           builder.addField(":money_mouth: How to donate:", "If you want donate please go to **https://paypal.me/artuto**\n"
                   + "You'll get a special role on my server and some perks!", false);
           builder.addField(":heart: Donators:", "Thanks to all donators!\n"
                   + "-**mogana from persona 5** - VPS Donator\n", false);
           event.getChannel().sendMessage(new MessageBuilder().setEmbed(builder.build()).build()).queue();
    }
}

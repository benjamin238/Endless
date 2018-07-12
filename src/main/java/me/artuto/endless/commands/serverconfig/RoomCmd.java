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

package me.artuto.endless.commands.serverconfig;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.core.entities.Room;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Artuto
 */

public class RoomCmd extends EndlessCommand
{
    private final Bot bot;

    public RoomCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "room";
        this.help = "Rooms are private text or voice channels that can be created by normal users.";
        this.category = Categories.SERVER_CONFIG;
        this.userPerms = new Permission[]{Permission.MANAGE_CHANNEL, Permission.MANAGE_SERVER};
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        String prefix = event.getClient().getPrefix();

        if(event.getArgs().equalsIgnoreCase("create"))
        {
            event.replyWarning("You can create three types of rooms:\n" +
                    "- *Text Rooms:* `"+prefix+"room text` Creates a private text room\n" +
                    "- *Voice Rooms:* `"+prefix+"room voice` Creates a private voice room\n" +
                    "- *Combo Rooms:* `"+prefix+"room combo` Creates a private text and voice rooms. They are also linked");
            return;
        }

        Guild guild = event.getGuild();
        List<Room> rooms = bot.rsdm.getRoomsForGuild(guild.getIdLong());

        if(rooms.isEmpty())
        {
            event.replyWarning("This guild doesn't have any rooms created! Create one by doing `"+prefix+"room create`");
            return;
        }

        List<Room> comboRooms = rooms.stream().filter(Room::isCombo).filter(r -> r.canAccess(event)).collect(Collectors.toList());
        List<Room> textRooms = rooms.stream().filter(Room::isText).filter(r -> r.canAccess(event)).collect(Collectors.toList());
        List<Room> voiceRooms = rooms.stream().filter(Room::isVoice).filter(r -> r.canAccess(event)).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder("Rooms in **").append(guild.getName()).append("**\n");

        if(!(comboRooms.isEmpty()))
        {
            sb.append("**Combo Rooms: **").append(comboRooms.size()).append("**\n");
            comboRooms.forEach(r -> {
                TextChannel tc = guild.getTextChannelById(r.getTextChannelId());
                VoiceChannel vc = guild.getVoiceChannelById(r.getVoiceChannelId());
                sb.append("- ").append(tc.getAsMention()).append(": ").append(vc.getName()).append("\n");
            });
        }
        if(!(textRooms.isEmpty()))
        {
            sb.append("**Text Rooms: **").append(comboRooms.size()).append("**\n");
            comboRooms.forEach(r -> {
                TextChannel tc = guild.getTextChannelById(r.getTextChannelId());
                sb.append("- ").append(tc.getAsMention()).append("\n");
            });
        }
        if(!(voiceRooms.isEmpty()))
        {
            sb.append("**Voice Rooms: **").append(comboRooms.size()).append("**\n");
            comboRooms.forEach(r -> {
                VoiceChannel vc = guild.getVoiceChannelById(r.getVoiceChannelId());
                sb.append("- ").append(vc.getName()).append("\n");
            });
        }

        event.replySuccess(sb.toString());
    }
}

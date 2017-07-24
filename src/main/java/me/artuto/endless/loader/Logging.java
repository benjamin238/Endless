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

package me.artuto.endless.loader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 *
 * @author Artu
 */

public class Logging extends ListenerAdapter
{
    DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
    Date date = new Date();

    //Guild Join
    @Override
    public void onGuildJoin(GuildJoinEvent event) 
    {
        System.out.printf("[GUILD JOIN]: "+event.getGuild().getName()+" ("+event.getGuild().getId()+")\n");
    }
    
    //Guild Leave
    @Override
    public void onGuildLeave(GuildLeaveEvent event) 
    {
        System.out.printf("[GUILD LEAVE]: "+event.getGuild().getName()+" ("+event.getGuild().getId()+")\n");
    }
    
    //Command logger
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if(event.getMessage().getContent().startsWith(Config.getPrefix()))
        {
            if(event.isFromType(ChannelType.PRIVATE))
            {
                try
                {
                    Writer output;
                    output = new BufferedWriter(new FileWriter("logs/commands.log", true));
                    output.append("\nCommand executed on a Direct Message:\n"
                            + "User: "+event.getMessage().getAuthor().getName()+"#"+event.getMessage().getAuthor().getDiscriminator()
                                + " ("+event.getMessage().getAuthor().getId()+")\n"
                            + "Command: '"+event.getMessage().getContent()+"' ("+event.getMessage().getId()+")\n");
                    output.close();
                }
                catch(IOException e)
                {
                    System.out.println("Error when creating the commands log!\n "+e);
                }
            }
            
            else
            {
                try
                {                  
                    Writer output;
                    output = new BufferedWriter(new FileWriter("logs/commands.log", true));
                    output.append("\nGuild: "+event.getGuild().getName()+" ("+event.getGuild().getId()+")\n"
                            + "Channel: "+event.getChannel().getName()+" ("+event.getChannel().getId()+")\n"
                            + "User: "+event.getMessage().getAuthor().getName()+"#"+event.getMessage().getAuthor().getDiscriminator()
                                + " ("+event.getMessage().getAuthor().getId()+")\n"
                            + "Command: '"+event.getMessage().getContent()+"' ("+event.getMessage().getId()+")\n");
                    output.close();
                }
                catch(IOException e)
                {
                    System.out.println("Error when creating the commands log!\n "+e);
                }    
            }
        }
        
        else
        {
            return;
        }
    }
}

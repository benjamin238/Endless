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

package me.artuto.endless.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import java.util.List;
import java.util.function.BiConsumer;

import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

/**
 *
 * @author Artu
 */

public class Avatar extends Command
{
    public Avatar()
    {
        this.name = "avatar";
        this.help = "Displays the avatar of the specified user.";
        this.arguments = "<user>";
        this.category = Categories.TOOLS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = true;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
        Member target;
        EmbedBuilder builder = new EmbedBuilder();
        
        if(event.getArgs().isEmpty())
        {
            target = event.getMessage().getMember();
        }
        else
        {
            List<Member> list = FinderUtil.findMembers(event.getArgs(), event.getGuild());
            
            if(list.isEmpty())
            {
                event.replyWarning("I was not able to found a user with the provided arguments: '"+event.getArgs()+"'");
                return;
            }
            else if(list.size()>1)
            {
                event.replyWarning(FormatUtil.listOfMembers(list, event.getArgs()));
                return;
            }
    	    else
            {
                target = list.get(0);
            }
        }   
            
        String title = ":frame_photo: Avatar of **"+target.getUser().getName()+"**"+"#"+"**"+target.getUser().getDiscriminator()+"**";
            
        try
        {
                builder.setImage(target.getUser().getEffectiveAvatarUrl());
                builder.setColor(target.getColor());
                event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue(); 
        }
        catch(Exception e)
        {
                event.replyError("An error happened when getting the avatar!");
        }
    }
}

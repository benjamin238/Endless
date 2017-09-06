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

import com.jagrosh.jdautilities.JDAUtilitiesInfo;
import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import java.awt.Color;
import me.artuto.endless.Const;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;

/**
 *
 * @author Artu
 */

public class About extends Command
{
    public About()
    {
        this.name = "about";
        this.help = "Info about the bot";
        this.category = Categories.BOT;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
        Color color;
        Config config;
        
        if(event.isFromType(ChannelType.PRIVATE))
        {
            color = Color.decode("#33ff00");
        }
        else
        {
            color = event.getGuild().getSelfMember().getColor();
        }

        try
        {
            config = new Config();
        }
        catch(Exception e)
        {
            SimpleLog.getLog("Config").fatal(e);
            return;
        }
        
       String title = ":information_source: Information about **"+event.getSelfUser().getName()+"**";
       EmbedBuilder builder = new EmbedBuilder();
       User owner = event.getJDA().retrieveUserById(config.getOwnerId()).complete();
       String ownername = owner.getName()+"#"+owner.getDiscriminator();
       String ownerid = owner.getId();


              builder.setDescription("Hi, I'm Endless! A multipurpose bot designed to be smart.\n"
              		+ "If you found a bug please contact my dad\n"
              		+ "("+Const.DEV+")!\n");
              builder.addField(":bust_in_silhouette: Owner:", "**"+ownername+"** (**"+ownerid+"**)", false);
              builder.addField("<:jda:325395909347115008>  Library:", "Java Discord API (JDA) "+JDAInfo.VERSION+" and JDA Utilities "+JDAUtilitiesInfo.VERSION, false);
              builder.addField("<:github:326118305062584321> GitHub:", "Did you found a bug? Want improve something?\n"
              		+ "Please open an Issue or create a PR on GitHub\n"
              		+ "**https://github.com/ArtutoGamer/Endless**\n", false);
              builder.addField(":link: Support Guild:", "**[Support]("+Const.INVITE+")**\n", false);
              builder.setFooter("Version: "+Const.VERSION+" | Latest Start", null);
              builder.setColor(color);
              builder.setTimestamp(event.getClient().getStartTime());
              builder.setThumbnail(event.getSelfUser().getAvatarUrl());
              event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();
    }
}

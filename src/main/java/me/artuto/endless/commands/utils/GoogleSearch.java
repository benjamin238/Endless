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

package me.artuto.endless.commands.utils;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.managers.GoogleSearcher;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.util.ArrayList;

public class GoogleSearch extends EndlessCommand
{
    private final GoogleSearcher searcher;

    public GoogleSearch()
    {
        searcher = new GoogleSearcher();
        this.name = "google";
        this.aliases = new String[]{"g", "search", "googlesearch"};
        this.help = "Search something on Google!";
        this.arguments = "[num] <query to search>";
        this.category = Categories.UTILS;
        this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildCommand = false;
        this.cooldown = 10;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        String[] inputs = event.getArgs().split("\\s+", 2);
        int num = 1;
        String query;

        if(inputs.length>1 && inputs[0].matches("\\d+"))
        {
            num = Integer.parseInt(inputs[0]);
            query = inputs[1];
        }
        else
        {
            query = event.getArgs();
        }

        if(num<1 || num>10)
        {
            event.replyWarning("I can only get 1 to 10 results at once!");
            return;
        }

        ArrayList<String> results = searcher.getGoogleData(query);

        if(results == null)
        {
            event.replyWarning("An error ocurred when using Google Search. Ask the bot owner to see the console.");
        }
        else if(results.isEmpty())
        {
            event.replyWarning("Any results found for `"+query+"`!");
        }
        else
        {
            StringBuilder output = new StringBuilder("`"+query+"` \uD83D\uDD0E "+results.get(0));

            if(num>1 && results.size()>1)
            {
                output.append("\n See also:");

                for(int i = 1; i<num && i<results.size(); i++)
                {
                    output.append("\n<").append(results.get(i)).append(">");
                }
            }

            builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getEffectiveAvatarUrl());
            builder.setDescription(output.toString());
            builder.setColor(event.getMessage().getMember().getColor());
            builder.setFooter("Results from Google Search API", null);

            event.reply(builder.build());
        }
    }
}
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

package me.artuto.endless.commands.bot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Const;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.DonatorsDataManager;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.List;

/**
 * @author Artuto
 */

public class Donate extends Command
{
    private final DonatorsDataManager db;

    public Donate(DonatorsDataManager db)
    {
        this.db = db;
        this.name = "donate";
        this.children = new Command[]{new Add(), new Remove()};
        this.help = "Info about donations";
        this.category = Categories.BOT;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        Color color;
        EmbedBuilder builder = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();
        List<User> list = db.getUsersThatDonated(event.getJDA());

        if(event.isFromType(ChannelType.PRIVATE)) color = Color.decode("#33ff00");
        else color = event.getGuild().getSelfMember().getColor();

        if(list.isEmpty()) sb.append("None has donated yet ):");
        else for(User user : list)
            sb.append(String.format("%#s - %s\n", user, db.getAmount(user)));

        builder.setColor(color);
        builder.addField(":moneybag: Donations:", "Actually, I host Endless on a very basic VPS, which can cause some lag sometimes.\n"+"I'll appreciate your donation. All the recauded money will be for get a new and better VPS.\n", false);
        builder.addField(":money_mouth: How to donate:", "If you want donate please go to **https://paypal.me/artuto**\n"+"You'll get a special role on my server and some perks!", false);
        builder.addField(":heart: Donators:", sb.toString(), false);

        event.reply(new MessageBuilder().append(":information_source: List of donators:").setEmbed(builder.build()).build());
    }

    private class Add extends Command
    {
        public Add()
        {
            this.name = "add";
            this.help = "Adds a donator to the list";
            this.category = Categories.BOTADM;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = true;
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(!(event.getClient().getOwnerId().equals(Const.ARTUTO_ID)))
            {
                event.replyError("This command is not available on a selfhosted instance!");
                return;
            }

            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Please specify the user ID and a donated amount!");
                return;
            }

            String[] args;
            String id;
            String amount;
            User user;

            try
            {
                args = event.getArgs().split(" ", 2);
                id = args[0];
                amount = args[1];
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                event.replyWarning("Please specify the user ID and a donated amount!");
                return;
            }

            List<Member> list = FinderUtil.findMembers(id, event.getGuild());

            if(list.isEmpty())
            {
                event.getJDA().retrieveUserById(id).queue(s ->
                {
                    db.setDonation(s, amount);
                    event.replySuccess(String.format("Successfully added %#s to the donators list!", s));
                }, e -> event.replyError("Invalid ID!"));
            }
            else if(list.size()>1) event.replyWarning(FormatUtil.listOfMembers(list, id));
            else
            {
                user = list.get(0).getUser();
                db.setDonation(user, amount);
                event.replySuccess(String.format("Successfully added %#s to the donators list!", user));
            }
        }
    }

    private class Remove extends Command
    {
        public Remove()
        {
            this.name = "remove";
            this.help = "Removes a donator from the list";
            this.category = Categories.BOTADM;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = true;
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(!(event.getClient().getOwnerId().equals(Const.ARTUTO_ID)))
            {
                event.replyError("This command is not available on a selfhosted instance!");
                return;
            }

            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Please specify the user ID!");
                return;
            }

            User user;
            List<Member> list = FinderUtil.findMembers(event.getArgs(), event.getGuild());

            if(list.isEmpty())
            {
                event.getJDA().retrieveUserById(event.getArgs()).queue(s ->
                {
                    if(!(db.hasDonated(s)))
                    {
                        db.setDonation(s, null);
                        event.replyError("This user hasn't donated!");
                    }
                    else
                    {
                        db.setDonation(s, null);
                        event.replySuccess(String.format("Successfully removed %#s from the donators list!", s));
                    }
                }, e -> event.replyError("Invalid ID!"));
            }
            else if(list.size()>1) event.replyWarning(FormatUtil.listOfMembers(list, event.getArgs()));
            else
            {
                user = list.get(0).getUser();

                if(!(db.hasDonated(user)))
                {
                    db.setDonation(user, null);
                    event.replyError("This user hasn't donated!");
                }
                else
                {
                    db.setDonation(user, null);
                    event.replySuccess(String.format("Successfully removed %#s from the donators list!", user));
                }
            }
        }
    }
}

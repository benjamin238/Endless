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
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
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

public class DonateCmd extends EndlessCommand
{
    private final Bot bot;

    public DonateCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "donate";
        this.aliases = new String[]{"donators"};
        this.children = new Command[]{new AddCmd(), new RemoveCmd()};
        this.help = "Info about donations";
        this.category = Categories.BOT;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        Color color;
        EmbedBuilder builder = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();
        List<User> list = bot.ddm.getUsersThatDonated(event.getJDA());

        if(event.isFromType(ChannelType.PRIVATE)) color = Color.decode("#33ff00");
        else color = event.getGuild().getSelfMember().getColor();

        if(list.isEmpty())
            sb.append("None has donated yet ):");
        else for(User user : list)
            sb.append(String.format("**%#s** - %s\n", user, bot.ddm.getDonation(user)));

        builder.setColor(color);
        builder.addField(":moneybag: Donations:", "Endless is developed in my spare time and its free for everyone to " +
                "use, however, if you like the bot, its functions or simply want to support me or the development," +
                "please consider donating. All the money goes into getting a better hosting.", false);
        builder.addField(":money_mouth: How to donate:", "If you want donate please go to **https://paypal.me/artuto**\n"+
                "You'll get a special role on my server and some perks!\n" +
                "After you've donated please join the support server by doing `e!help support` and contact Artuto#0424.", false);
        builder.addField(":heart: Donators:", sb.toString(), false);

        event.reply(new MessageBuilder().append(":information_source: Donations:").setEmbed(builder.build()).build());
    }

    private class AddCmd extends EndlessCommand
    {
        AddCmd()
        {
            this.name = "add";
            this.help = "Adds a donator to the list";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.parent = DonateCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }
            if(!(event.getClient().getOwnerId().equals(String.valueOf(Const.ARTUTO_ID))))
            {
                event.replyError("This command is not available on a selfhosted instance!");
                return;
            }

            String[] args;
            String id;
            String donation;
            User user;

            try
            {
                args = event.getArgs().split(" ", 2);
                id = args[0];
                donation = args[1];
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
                    bot.ddm.setDonation(s.getIdLong(), donation);
                    event.replySuccess(String.format("Successfully added %#s to the donators list!", s));
                }, e -> event.replyError("Invalid ID!"));
            }
            else if(list.size()>1) event.replyWarning(FormatUtil.listOfMembers(list, id));
            else
            {
                user = list.get(0).getUser();
                bot.ddm.setDonation(user.getIdLong(), donation);
                event.replySuccess(String.format("Successfully added %#s to the donators list!", user));
            }
        }
    }

    private class RemoveCmd extends EndlessCommand
    {
        RemoveCmd()
        {
            this.name = "remove";
            this.help = "Removes a donator from the list";
            this.category = Categories.BOTADM;
            this.ownerCommand = true;
            this.guildOnly = false;
            this.parent = DonateCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            if(!(event.getClient().getOwnerId().equals(String.valueOf(Const.ARTUTO_ID))))
            {
                event.replyError("This command is not available on a selfhosted instance!");
                return;
            }

            User user;
            List<Member> list = FinderUtil.findMembers(event.getArgs(), event.getGuild());

            if(list.isEmpty())
            {
                event.getJDA().retrieveUserById(event.getArgs()).queue(s ->
                {
                    if(!(bot.ddm.hasDonated(s)))
                    {
                        bot.ddm.setDonation(s.getIdLong(), null);
                        event.replyError("This user hasn't donated!");
                    }
                    else
                    {
                        bot.ddm.setDonation(s.getIdLong(), null);
                        event.replySuccess(String.format("Successfully removed %#s from the donators list!", s));
                    }
                }, e -> event.replyError("Invalid ID!"));
            }
            else if(list.size()>1) event.replyWarning(FormatUtil.listOfMembers(list, event.getArgs()));
            else
            {
                user = list.get(0).getUser();

                if(!(bot.ddm.hasDonated(user)))
                {
                    bot.ddm.setDonation(user.getIdLong(), null);
                    event.replyError("This user hasn't donated!");
                }
                else
                {
                    bot.ddm.setDonation(user.getIdLong(), null);
                    event.replySuccess(String.format("Successfully removed %#s from the donators list!", user));
                }
            }
        }
    }
}

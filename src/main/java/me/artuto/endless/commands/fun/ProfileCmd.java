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

package me.artuto.endless.commands.fun;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.core.entities.Profile;
import me.artuto.endless.core.entities.impl.ProfileImpl;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

public class ProfileCmd extends EndlessCommand
{
    private final Bot bot;

    public ProfileCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "profile";
        this.children = new Command[]{new Set()};
        this.aliases = new String[]{"p"};
        this.help = "Displays or edits the profile of the specified user";
        this.arguments = "<user>";
        this.category = Categories.FUN;
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        MessageBuilder messageBuilder = new MessageBuilder();
        Profile p;
        User user;
        if(event.getArgs().isEmpty())
        {
            user = event.getAuthor();
            if(!(bot.prdm.hasProfile(user)))
            {
                event.replyError("You don't have a profile!");
                return;
            }
            p = bot.prdm.getProfile(user);
            if(p.isEmpty())
            {
                event.replyError("You don't have a profile!");
                return;
            }
            builder.setColor(event.getMember().getColor());
            builder.setDescription(buildProfile(p));
            messageBuilder.setContent(Const.INFO+" **"+user.getName()+"#"+user.getDiscriminator()+"**'s profile:");
            event.reply(messageBuilder.setEmbed(builder.build()).build());
        }
        else
        {
            List<Member> list = FinderUtil.findMembers(event.getArgs(), event.getGuild());
            if(list.isEmpty())
            {
                event.replyWarning("No Members found matching \""+event.getArgs()+"\"");
                return;
            }
            else if(list.size()>1)
            {
                event.replyWarning(FormatUtil.listOfMembers(list, event.getArgs()));
                return;
            }
            else
                user = list.get(0).getUser();
            if(!(bot.prdm.hasProfile(user)))
            {
                event.replyError("You don't have a profile!");
                return;
            }
            p = bot.prdm.getProfile(user);
            if(p.isEmpty())
            {
                event.replyError("**"+user.getName()+"#"+user.getDiscriminator()+"** doesn't has a profile!");
                return;
            }
            builder.setColor(event.getMember().getColor());
            builder.setDescription(buildProfile(p));
            messageBuilder.setContent(Const.INFO+" **"+user.getName()+"#"+user.getDiscriminator()+"**'s profile:");
            event.reply(messageBuilder.setEmbed(builder.build()).build());
        }
    }

    private class Set extends EndlessCommand
    {
        Set()
        {
            this.name = "set";
            this.aliases = new String[]{"change"};
            this.help = "sets the specified value";
            this.arguments = "<field> <value|NONE>";
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            String[] args = splitArgs(event.getArgs());
            String field = getField(args[0]);
            String value = args[1];
            if(value.isEmpty())
            {
                event.replyError("Invalid input for the value!");
                return;
            }
            if(field==null)
            {
                event.replyError("The field `"+args[0]+"` is invalid!");
                return;
            }
            bot.prdm.setValue(event.getAuthor(), field, value);
            event.replySuccess("Successfully set `"+field+"`.");
        }
    }

    private String[] splitArgs(String preArgs)
    {
        try
        {
            String[] args = preArgs.split(" ", 2);
            return new String[]{args[0].toLowerCase().trim(), args[1].trim()};
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return new String[]{preArgs.toLowerCase().trim(), ""};
        }
    }

    private String buildProfile(Profile p)
    {
        StringBuilder sb = new StringBuilder();
        ((ProfileImpl)p).fields.forEach((f, v) -> {
            if(!(v==null))
                sb.append(Const.LINE_START).append(" **").append(f).append("**: ").append(v.trim()).append("\n");
        });
        return sb.toString();
    }

    private String getField(String preField)
    {
        if(preField.equals("timezone"))
            return "timezone";
        else if(preField.equals("twitter"))
            return "twitter";
        else if(preField.equals("steam"))
            return "steam";
        else if(preField.equals("wii"))
            return "wii";
        else if(preField.equals("nnid"))
            return "nnid";
        else if(preField.equals("xboxlive"))
            return "xboxlive";
        else if(preField.equals("psn"))
            return "psn";
        else if(preField.equals("3ds"))
            return "threeds";
        else if(preField.equals("skype"))
            return "skype";
        else if(preField.equals("youtube"))
            return "youtube";
        else if(preField.equals("about"))
            return "about";
        else if(preField.equals("twitch"))
            return "twitch";
        else if(preField.equals("minecraft"))
            return "minecraft";
        else if(preField.equals("email"))
            return "email";
        else if(preField.equals( "lol"))
            return "lol";
        else if(preField.equals("wow"))
            return "wow";
        else if(preField.equals("battle"))
            return "battle";
        else if(preField.equals("splatoon"))
            return "splatoon";
        else if(preField.equals("mkwii"))
            return "mkwii";
        else if(preField.equals("reddit"))
           return "reddit";
        else
            return null;
    }
}

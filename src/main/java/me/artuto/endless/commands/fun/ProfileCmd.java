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
import com.jagrosh.jdautilities.menu.Paginator;
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.core.entities.Profile;
import me.artuto.endless.core.entities.impl.ProfileImpl;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProfileCmd extends EndlessCommand
{
    private final Bot bot;

    public ProfileCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "profile";
        this.children = new Command[]{new FieldsCmd(), new SetCmd()};
        this.aliases = new String[]{"p"};
        this.help = "Displays or edits the profile of the specified user";
        this.arguments = "<user>";
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.category = Categories.FUN;
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        if(!(bot.dataEnabled))
        {
            event.replyError("Endless is running on No-data mode.");
            return;
        }

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
            builder.setDescription(buildProfile(p, user));
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
                event.replyError("**"+user.getName()+"#"+user.getDiscriminator()+"** doesn't has a profile!");
                return;
            }
            p = bot.prdm.getProfile(user);
            if(p.isEmpty())
            {
                event.replyError("**"+user.getName()+"#"+user.getDiscriminator()+"** doesn't has a profile!");
                return;
            }
            builder.setColor(event.getMember().getColor());
            builder.setDescription(buildProfile(p, user));
            messageBuilder.setContent(Const.INFO+" **"+user.getName()+"#"+user.getDiscriminator()+"**'s profile:");
            event.reply(messageBuilder.setEmbed(builder.build()).build());
        }
    }

    private class FieldsCmd extends EndlessCommand
    {
        private final Paginator.Builder menu;

        FieldsCmd()
        {
            this.name = "fields";
            this.help = "List of valid profile fields";
            this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
            this.needsArguments = false;
            this.parent = ProfileCmd.this;
            this.menu = new Paginator.Builder().setColumns(1)
                    .setItemsPerPage(10)
                    .showPageNumbers(true)
                    .waitOnSinglePage(false)
                    .useNumberedItems(false)
                    .setFinalAction(m -> {
                        try
                        {
                            m.clearReactions().queue();
                        } catch(PermissionException ex)
                        {
                            m.delete().queue();
                        }
                    })
                    .setEventWaiter(bot.waiter)
                    .setTimeout(1, TimeUnit.MINUTES);
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            menu.clearItems();
            Arrays.stream(Const.PROFILE_FIELDS).forEach(menu::addItems);
            Paginator p = menu.setColor(event.isFromType(ChannelType.TEXT)?event.getSelfMember().getColor():Color.decode("#33ff00"))
                    .setText(event.getClient().getSuccess()+" Valid profile fields")
                    .setUsers(event.getAuthor())
                    .build();
            p.paginate(event.getChannel(), 1);
        }
    }

    private class SetCmd extends EndlessCommand
    {
        SetCmd()
        {
            this.name = "set";
            this.aliases = new String[]{"change"};
            this.help = "sets the specified value";
            this.arguments = "<field> <value|NONE>";
            this.parent = ProfileCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

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

    private String buildProfile(Profile p, User user)
    {
        StringBuilder sb = new StringBuilder();
        ((ProfileImpl)p).fields.forEach((f, v) -> {
            if(!(v==null))
                sb.append(Const.LINE_START).append(" **").append(f).append("**: ").append(v.trim()).append("\n");
        });
        Guild guild = bot.shardManager.getGuildById(Const.MAIN_GUILD);
        if(!(guild.getMember(user)==null))
        {
            Member m = guild.getMember(user);
            Role role = m.getRoles().stream().filter(r -> r.getIdLong()==318524910894841857L).findFirst().orElse(null);
            if(!(role==null))
                sb.append("\n_ _\n:money_mouth: Donator");
        }
        if(user.getIdLong()==Const.ARTUTO_ID || user.getIdLong()==Const.ARTUTO_ALT_ID)
            sb.append("\n_ _\n").append(Const.BOTADM).append(" Endless Developer");
        return sb.toString();
    }

    private String getField(String field)
    {
        switch(field)
        {
            case "timezone":
                return "timezone";
            case "twitter":
                return "twitter";
            case "steam":
                return "steam";
            case "wii":
                return "wii";
            case "nnid":
                return "nnid";
            case "xboxlive":
                return "xboxlive";
            case "psn":
                return "psn";
            case "3ds":
                return "threeds";
            case "skype":
                return "skype";
            case "youtube":
                return "youtube";
            case "about":
                return "about";
            case "twitch":
                return "twitch";
            case "minecraft":
                return "minecraft";
            case "email":
                return "email";
            case "lol":
                return "lol";
            case "wow":
                return "wow";
            case "battle":
                return "battle";
            case "splatoon":
                return "splatoon";
            case "mkwii":
                return "mkwii";
            case "reddit":
                return "reddit";
            default:
                return null;
        }
    }
}

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

import com.vdurmont.emoji.EmojiParser;
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.ChecksUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StarboardCmd extends EndlessCommand
{
    private final Bot bot;

    public StarboardCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "starboard";
        this.children = new EndlessCommand[]{new SetChannelCmd(), new SetCountCmd(), new SetEmoteCmd()};
        this.aliases = new String[]{"sb"};
        this.help = "If no valid arguments are given the setup to install the starboard is launched.";
        this.category = Categories.SERVER_CONFIG;
        this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        this.needsArguments = false;
        this.cooldown = 60;
        this.cooldownScope = CooldownScope.GUILD;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        event.replySuccess(event.localize("command.starboard.setup"), m -> waitForChannel(event));
    }

    private void waitForChannel(EndlessCommandEvent event)
    {
        event.replySuccess("core.setup.starboard");

        bot.waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> event.getAuthor().equals(e.getAuthor()) && event.getTextChannel().equals(e.getChannel()), e ->
        {
            switch(e.getMessage().getContentRaw().split(" ", 2)[0].toLowerCase())
            {
                case "cancel":
                    event.replyWarning("core.setup.starboard.cancelled");
                    break;
                case "create":
                    if(!(ChecksUtil.hasPermission(event.getSelfMember(), event.getTextChannel(), Permission.MANAGE_CHANNEL)
                            || ChecksUtil.hasPermission(event.getSelfMember(), event.getTextChannel(), Permission.MANAGE_PERMISSIONS)))
                    {
                        event.replyError("core.setup.starboard.create.error.missingP");
                        waitForChannel(event);
                        return;
                    }

                    event.replySuccess("core.setup.starboard.create.confirm",
                            m -> m.editMessage(Const.LOADING+" "+event.localize("core.setup.starting")).queueAfter(3, TimeUnit.SECONDS, (s) ->
                    {
                        event.getGuild().getController().createTextChannel("starboard")
                                .addPermissionOverride(event.getGuild().getPublicRole(), 0L, Permission.MESSAGE_WRITE.getRawValue())
                                .addPermissionOverride(event.getSelfMember(), EnumSet.of(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE), null)
                                .reason(event.getAuthor().getName()+"#"+event.getAuthor().getDiscriminator()+": Starboard setup")
                                .queue(next -> {
                                    s.editMessage(event.getClient().getSuccess()+"\n"+event.localize("core.setup.starboard.create.created")).queue();
                                    bot.gsdm.setStarboardChannel(event.getGuild(), (TextChannel)next);
                                    waitForStarCount(event);
                        });
                    }));
                    break;
                case "created":
                    String query;

                    try
                    {
                        String[] args = e.getMessage().getContentRaw().split(" ", 2);
                        query = args[1].trim();
                    }
                    catch(ArrayIndexOutOfBoundsException ex)
                    {
                        event.replyWarning("core.setup.starboard.created.noArgs");
                        waitForChannel(event);
                        return;
                    }

                    TextChannel tc = ArgsUtils.findTextChannel(event, query);
                    if(tc==null)
                    {
                        waitForChannel(event);
                        return;
                    }

                    bot.gsdm.setStarboardChannel(event.getGuild(), tc);
                    event.replySuccess("core.setup.starboard.created.set", tc.getAsMention());
                    waitForStarCount(event);
                    break;
                default:
                    event.replyWarning("core.setup.starboard.invalid");
                    waitForChannel(event);
            }
        }, 2, TimeUnit.MINUTES, () -> event.replyWarning("core.setup.starboard.tooLong", event.getAuthor().getAsMention()));
    }

    private void waitForEmote(EndlessCommandEvent event)
    {
        event.replySuccess("core.setup.starboard.emote");

        bot.waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> event.getAuthor().equals(e.getAuthor()) && event.getTextChannel().equals(e.getChannel()), e ->
        {
            String args;

            if(e.getMessage().getContentRaw().isEmpty())
            {
                event.replyError("core.setup.starboard.emote.noArgs");
                waitForEmote(event);
                return;
            }

            if(e.getMessage().getContentRaw().equalsIgnoreCase("default"))
                args = "\u2B50";
            else
            {
                List<String> emojis = EmojiParser.extractEmojis(e.getMessage().getContentRaw());
                if(!(e.getMessage().getEmotes().isEmpty()))
                    args = e.getMessage().getEmotes().get(0).getId();
                else if(!(emojis.isEmpty()))
                    args = emojis.get(0);
                else
                {
                    event.replyError("core.setup.starboard.emote.notFound");
                    waitForEmote(event);
                    return;
                }
            }

            bot.gsdm.setStarboardEmote(event.getGuild(), args);
            event.replySuccess("core.setup.starboard.emote.set");
            finished(event);
        }, 2, TimeUnit.MINUTES, () -> event.replyWarning("core.setup.starboard.tooLong", event.getAuthor().getAsMention()));
    }

    private void waitForStarCount(EndlessCommandEvent event)
    {
        event.replySuccess("core.setup.starboard.count");

        bot.waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> event.getAuthor().equals(e.getAuthor()) && event.getTextChannel().equals(e.getChannel()), e ->
        {
            int args;

            if(e.getMessage().getContentRaw().isEmpty())
            {
                event.replyError("core.setup.starboard.count.noArgs");
                waitForStarCount(event);
                return;
            }

            try {args = Integer.valueOf(e.getMessage().getContentRaw());}
            catch(NumberFormatException ex)
            {
                event.replyError("core.setup.starboard.count.invalid");
                waitForStarCount(event);
                return;
            }

            if(args<1 || args>20)
            {
                event.replyError("core.setup.starboard.count.limit");
                waitForStarCount(event);
            }
            else
            {
                event.replySuccess("core.setup.starboard.count.set", args);
                bot.gsdm.setStarboardCount(event.getGuild(), args);
                waitForEmote(event);
            }

        }, 2, TimeUnit.MINUTES, () -> event.replyWarning("core.setup.starboard.tooLong", event.getAuthor().getAsMention()));
    }

    private void finished(EndlessCommandEvent event)
    {
        event.replySuccess("core.setup.starboard.done");
    }

    private class SetChannelCmd extends EndlessCommand
    {
        SetChannelCmd()
        {
            this.name = "setchannel";
            this.aliases = new String[]{"channel"};
            this.help = "Changes the channel of the starboard.";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Please include a text channel or NONE";
            this.parent = StarboardCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.gsdm.setStarboardChannel(event.getGuild(), null);
                event.replySuccess("command.starboard.setchannel.disabled");
            }
            else
            {
                TextChannel tc = ArgsUtils.findTextChannel(event, event.getArgs());
                if(tc==null)
                    return;

                bot.gsdm.setStarboardChannel(event.getGuild(), tc);
                event.replySuccess("command.starboard.setchannel.set", tc.getAsMention());
            }
        }
    }

    private class SetCountCmd extends EndlessCommand
    {
        SetCountCmd()
        {
            this.name = "setcount";
            this.aliases = new String[]{"count", "amount", "setamount"};
            this.help = "Changes the amount of stars required to be in the starboard.";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Please include a number between 1 and 20";
            this.parent = StarboardCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            int args;

            try {args = Integer.valueOf(event.getArgs());}
            catch(NumberFormatException ex)
            {
                event.replyError("command.starboard.setcount.invalid");
                return;
            }

            if(args<1 || args>20)
                event.replyError("command.starboard.setcount.limits");
            else
            {
                event.replySuccess("command.starboard.setcount.set", args);
                bot.gsdm.setStarboardCount(event.getGuild(), args);
            }
        }
    }

    private class SetEmoteCmd extends EndlessCommand
    {
        SetEmoteCmd()
        {
            this.name = "setemote";
            this.aliases = new String[]{"setemoji"};
            this.help = "Changes the emote of the starboard.";
            this.category = Categories.SERVER_CONFIG;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Please include a emote or DEFAULT";
            this.parent = StarboardCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            if(event.getArgs().equalsIgnoreCase("default"))
            {
                bot.gsdm.setStarboardEmote(event.getGuild(), "\u2B50");
                event.replySuccess("command.starboard.setemote.set");
            }
            else
            {
                String args;
                List<String> emojis = EmojiParser.extractEmojis(event.getArgs());
                if(!(event.getMessage().getEmotes().isEmpty()))
                    args = event.getMessage().getEmotes().get(0).getId();
                else if(!(emojis.isEmpty()))
                    args = emojis.get(0);
                else
                {
                    event.replyError("command.starboard.setemote.noArgs");
                    return;
                }

                bot.gsdm.setStarboardEmote(event.getGuild(), args);
                event.replySuccess("command.starboard.setemote.set");
            }
        }
    }
}

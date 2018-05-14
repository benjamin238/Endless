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

package me.artuto.endless.events;

import com.jagrosh.jagtag.Parser;
import com.jagrosh.jagtag.ParserBuilder;
import com.jagrosh.jagtag.libraries.*;
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.entities.ImportedTag;
import me.artuto.endless.entities.ParsedAuditLog;
import me.artuto.endless.entities.Punishment;
import me.artuto.endless.entities.TempPunishment;
import me.artuto.endless.tempdata.AfkManager;
import me.artuto.endless.tools.Variables;
import me.artuto.endless.utils.FinderUtil;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audit.ActionType;
import net.dv8tion.jda.core.audit.AuditLogEntry;
import net.dv8tion.jda.core.audit.AuditLogKey;
import net.dv8tion.jda.core.audit.AuditLogOption;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GuildEvents extends ListenerAdapter
{
    private final Bot bot;
    private final Parser parser;

    public GuildEvents(Bot bot)
    {
        this.bot = bot;
        this.parser = new ParserBuilder().addMethods(Variables.getMethods()).addMethods(Arguments.getMethods()).addMethods(Functional.getMethods()).addMethods(Miscellaneous.getMethods()).addMethods(Strings.getMethods()).addMethods(Time.getMethods()).addMethods(com.jagrosh.jagtag.libraries.Variables.getMethods()).setMaxOutput(2000).setMaxIterations(1000).build();
    }

    private String getReason(Guild guild)
    {
        String reason = GuildUtils.checkBadGuild(guild);

        switch(reason)
        {
            case "LEFT: BOTS":
                return "Too many bots!";
            case "LEFT: BOT LIST":
                return "Hey! You can't have this bot on a bot list!";
            default:
                return null;
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        Guild guild = event.getGuild();
        User owner = guild.getOwner().getUser();
        long botCount = guild.getMembers().stream().map(Member::getUser).filter(User::isBot).count();
        long userCount = guild.getMembers().stream().map(Member::getUser).filter(u -> !(u.isBot())).count();
        long totalCount = guild.getMembers().size();
        GuildUtils.checkBadGuild(guild);
        TextChannel tc = event.getJDA().getTextChannelById(bot.config.getBotlogChannelId());

        if(bot.bdm.isBlacklisted(guild.getIdLong()) || bot.bdm.isBlacklisted(owner.getIdLong()))
        {
            LoggerFactory.getLogger("Logging").info("[BLACKLISTED GUILD/OWNER JOIN]: "+guild.getName()+" (ID: "+guild.getId()+")\n" +
                    "Owner: "+owner.getName()+"#"+owner.getDiscriminator()+" (ID: "+owner.getId()+")");
            guild.leave().queue();
            return;
        }

        if(!(GuildUtils.isBadGuild(guild)) && bot.config.isBotlogEnabled() && !(tc == null) && tc.canTalk())
        {
            tc.sendMessage(":inbox_tray: `[New Guild]:` "+guild.getName()+" (ID: "+guild.getId()+")\n"+"`[Owner]:` **"+owner.getName()+"**#**"+owner.getDiscriminator()+"** (ID: "+owner.getId()+"\n"+
                    "`[Members]:` Humans: **"+userCount+"** Bots: **"+botCount+"** Total Count: **"+totalCount+"**\n").queue();
            LoggerFactory.getLogger("Logging").info("[GUILD JOIN]: "+guild.getName()+" (ID: "+guild.getId()+")\n");
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event)
    {
        Guild guild = event.getGuild();
        User owner = guild.getOwner().getUser();
        LoggerFactory.getLogger("Logging").info("[GUILD LEFT]: "+guild.getName()+" (ID: "+guild.getId()+")\n");
        long botCount = guild.getMembers().stream().map(Member::getUser).filter(User::isBot).count();
        long userCount = guild.getMembers().stream().map(Member::getUser).filter(u -> !(u.isBot())).count();
        long totalCount = guild.getMembers().size();
        TextChannel tc = event.getJDA().getTextChannelById(bot.config.getBotlogChannelId());
        String reason = getReason(guild);

        if(bot.config.isBotlogEnabled() && !(tc == null) && tc.canTalk())
        {
            StringBuilder builder = new StringBuilder().append(":outbox_tray: `[Left Guild]:` "+guild.getName()+" (ID: "+guild.getId()+")\n"+"`[Owner]:` **"+owner.getName()+"**#**"+owner.getDiscriminator()+"** (ID: "+owner.getId()+"\n"+"`[Members]:` Humans: **"+userCount+"** Bots: **"+botCount+"** Total Count: **"+totalCount+"**\n");

            if(!(reason == null)) builder.append("`[Reason]:` ").append(reason);

            tc.sendMessage(builder.toString()).queue();
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        Logger LOG = LoggerFactory.getLogger("AFK Manager");
        User author = event.getAuthor();
        Message msg = event.getMessage();
        String message;
        List<ImportedTag> importedT = bot.tdm.getImportedTagsForGuild(event.getGuild().getIdLong());
        TextChannel modlog = bot.gsdm.getModlogChannel(event.getGuild());

        if(!(msg.getAttachments().isEmpty()))
        {
            for(Message.Attachment at : msg.getAttachments())
            {
                if(at.getFileName().endsWith(".js"))
                {
                    at.download(new File(at.getFileName()));

                    List<String> lines;
                    try
                    {
                        lines = Files.readAllLines(Paths.get(at.getFileName()));
                        for(String str : lines)
                        {
                            if(str.contains("proxy.contentWindow.localStorage.token"))
                            {
                                if(!(modlog == null))
                                    modlog.sendMessage(":warning: **"+author.getName()+"#"+author.getDiscriminator()+"** ("+author.getId()+") has sent a suspicious file with code to steal tokens. It has been deleted. Message ID: "+msg.getId()).queue(s -> msg.delete().queue(), e -> msg.delete().queue());
                                break;
                            }
                            new File(at.getFileName()).delete();
                        }
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        if(!(importedT == null))
        {
            for(ImportedTag tag : importedT)
            {
                String name = tag.getName();
                String command = bot.config.getPrefix().toLowerCase()+name;
                String tagargs;
                String[] args;

                if(msg.getContentDisplay().startsWith(command) && event.getChannel().canTalk())
                {
                    try
                    {
                        args = msg.getContentDisplay().split(command+" ", 2);
                        tagargs = args[1];
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        tagargs = "";
                    }

                    parser.clear().put("user", event.getAuthor()).put("guild", event.getGuild()).put("channel", event.getChannel()).put("args", tagargs);
                    event.getChannel().sendMessage(parser.parse(tag.getContent())).queue();
                }
            }
        }

        if(AfkManager.isAfk(author.getIdLong()))
        {
            author.openPrivateChannel().queue(pc -> pc.sendMessage(bot.config.getDoneEmote()+" I've removed your AFK status.").queue(null, (e) -> LOG.warn("I was not able to DM "+author.getName()+"#"+author.getDiscriminator()+" about removing its AFK status.")));
            AfkManager.unsetAfk(author.getIdLong());
        }

        for(Member afk : event.getGuild().getMembers())
        {
            User user = afk.getUser();
            message = ":bed: **"+user.getName()+"** is AFK!";

            if(AfkManager.isAfk(user.getIdLong()))
            {
                if(msg.getMentionedUsers().contains(user))
                {
                    if(!(user.isBot()) || !(bot.bdm.isBlacklisted(author.getIdLong())))
                    {
                        EmbedBuilder builder = new EmbedBuilder();

                        builder.setAuthor(author.getName()+"#"+author.getDiscriminator(), null, author.getEffectiveAvatarUrl());
                        builder.setDescription(msg.getContentDisplay());
                        builder.setFooter("#"+msg.getTextChannel().getName()+", "+event.getGuild().getName(), event.getGuild().getIconUrl());
                        builder.setTimestamp(msg.getCreationTime());
                        builder.setColor(event.getMember().getColor());


                        user.openPrivateChannel().queue(pc -> pc.sendMessage(new MessageBuilder().setEmbed(builder.build()).build()).queue(null, null));
                    }

                    if(AfkManager.getMessage(user.getIdLong()) == null) event.getChannel().sendMessage(message).queue();
                    else
                    {
                        EmbedBuilder builder = new EmbedBuilder();

                        builder.setDescription(AfkManager.getMessage(user.getIdLong()));
                        builder.setColor(event.getGuild().getMember(user).getColor());

                        event.getChannel().sendMessage(new MessageBuilder().append(message).setEmbed(builder.build()).build()).queue();
                    }
                }
            }
        }
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event)
    {
        Guild guild = event.getGuild();
        Role mutedRole = GuildUtils.getMutedRole(guild);

        if(!(event.getRoles().contains(mutedRole)))
            return;

        if(!(guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS)))
            return;

        guild.getAuditLogs().type(ActionType.MEMBER_ROLE_UPDATE).limit(1).queue(entries -> {
            if(entries.isEmpty())
                return;

            ParsedAuditLog parsedAuditLog = GuildUtils.getAuditLog(entries.get(0), AuditLogKey.MEMBER_ROLES_ADD);
            String reason = parsedAuditLog.getReason();
            User author = parsedAuditLog.getAuthor();
            User target = parsedAuditLog.getTarget();

            if(author.equals(event.getJDA().getSelfUser()))
                return;

            bot.modlog.logMute(author, guild.getMember(target), reason, guild, FinderUtil.getDefaultChannel(guild));
            bot.pdm.addPunishment(target.getIdLong(), guild.getIdLong(), Const.PunishmentType.TEMPMUTE);
        }, e -> {});
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event)
    {
        Guild guild = event.getGuild();
        Role mutedRole = GuildUtils.getMutedRole(guild);

        if(!(event.getRoles().contains(mutedRole)))
            return;

        if(!(guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS)))
            return;

        guild.getAuditLogs().type(ActionType.MEMBER_ROLE_UPDATE).limit(1).queue(entries -> {
            if(entries.isEmpty())
                return;

            ParsedAuditLog parsedAuditLog = GuildUtils.getAuditLog(entries.get(0), AuditLogKey.MEMBER_ROLES_REMOVE);
            String reason = parsedAuditLog.getReason();
            User author = parsedAuditLog.getAuthor();
            User target = parsedAuditLog.getTarget();

            bot.modlog.logUnmute(author, guild.getMember(target), reason, guild, FinderUtil.getDefaultChannel(guild));
            bot.pdm.removePunishment(target.getIdLong(), guild.getIdLong(), Const.PunishmentType.TEMPMUTE);
        }, e -> {});
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        Guild guild = event.getGuild();
        Punishment punishment = bot.pdm.getPunishment(event.getUser().getIdLong(), event.getGuild().getIdLong(), Const.PunishmentType.MUTE);
        TempPunishment tempPunishment = (TempPunishment)bot.pdm.getPunishment(event.getUser().getIdLong(), event.getGuild().getIdLong(), Const.PunishmentType.TEMPMUTE);

        if(!(punishment==null))
        {
            Role mutedRole = GuildUtils.getMutedRole(event.getGuild());

            if(!(mutedRole==null) && guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES) && guild.getSelfMember().canInteract(mutedRole))
                guild.getController().addSingleRoleToMember(event.getMember(), mutedRole).reason("[Mute restore]").queue(s -> {}, e -> {});
        }
        else if(!(tempPunishment==null))
        {
            Role mutedRole = GuildUtils.getMutedRole(event.getGuild());

            if(!(mutedRole==null) && guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES) && guild.getSelfMember().canInteract(mutedRole))
                event.getGuild().getController().addSingleRoleToMember(event.getMember(), mutedRole).reason("[Mute restore]").queue(s -> {}, e-> {});
        }
    }

     /** @Override
     * public void onGuildJoin(GuildJoinEvent event)
     * {
     * Guild guild = event.getGuild();
     * User owner = event.getJDA().getUserById(config.getOwnerId());
     * String leavemsg = "Hi! Sorry, but you can't have a copy of Endless on Discord Bots, this is for my own security.\n"
     * + "Please remove this Account from the Discord Bots list or I'll take further actions.\n"
     * + "If you think this is an error, please contact the Developer. ~Artuto";
     * String warnmsg = "<@264499432538505217>, **"+owner.getName()+"#"+owner.getDiscriminator()+"** has a copy of Endless here!";
     * Long ownerId = config.getOwnerId();
     * <p>
     * if(event.getGuild().getId().equals("110373943822540800") || event.getGuild().getId().equals("264445053596991498") && !(ownerId==264499432538505217L))
     * {
     * event.getJDA().getTextChannelById("119222314964353025").sendMessage(warnmsg).complete();
     * owner.openPrivateChannel().queue(s -> s.sendMessage(leavemsg).queue(null, (e) -> SimpleLog.getLog("DISCORD BOTS").fatal(leavemsg)));
     * guild.leave().complete();
     * }
     * }
     */
}

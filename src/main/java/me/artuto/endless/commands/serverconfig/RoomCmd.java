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

import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.Command;
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.Endless;
import me.artuto.endless.PermLevel;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.core.entities.Room;
import me.artuto.endless.utils.ArgsUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Artuto
 */

public class RoomCmd extends EndlessCommand
{
    private final Bot bot;
    private final Logger LOG = Endless.LOG;
    private final String[] MODES = new String[]{Room.Mode.ALL.name(), Room.Mode.COMBO_ONLY.name(), Room.Mode.NO_CREATION.name(),
            Room.Mode.TEXT_ONLY.name(), Room.Mode.VOICE_ONLY.name()};

    public RoomCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "room";
        this.help = "Rooms are private text or voice channels that can be created by normal users.";
        this.children = new Command[]{new CreateComboCmd(), new CreateTextCmd(), new CreateVoiceCmd(), new InviteCmd(), new JoinCmd(), new KickCmd(),
                new LeaveCmd(), new LockCmd(), new ModeCmd(), new TakeCmd(), new TransferCmd(), new UnLockCmd()};
        this.category = Categories.SERVER_CONFIG;
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        String prefix = event.getClient().getPrefix();

        if(event.getArgs().equalsIgnoreCase("create"))
        {
            event.replyWarning("command.room.create", prefix);
            return;
        }

        Guild guild = event.getGuild();
        List<Room> rooms = bot.rsdm.getRoomsForGuild(guild.getIdLong());
        if(rooms.isEmpty())
        {
            event.replyWarning("command.room.noRooms");
            return;
        }

        List<Room> comboRooms = rooms.stream().filter(Room::isCombo).filter(r -> r.canAccess(event)).collect(Collectors.toList());
        List<Room> textRooms = rooms.stream().filter(Room::isText).filter(r -> r.canAccess(event)).collect(Collectors.toList());
        List<Room> voiceRooms = rooms.stream().filter(Room::isVoice).filter(r -> r.canAccess(event)).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder(event.localize("command.room.list", guild.getName()));

        if(!(comboRooms.isEmpty()))
        {
            sb.append(event.localize("command.room.list.combo", comboRooms.size())).append("\n");
            for(Room r : comboRooms)
            {
                TextChannel tc = guild.getTextChannelById(r.getTextChannelId());
                VoiceChannel vc = guild.getVoiceChannelById(r.getVoiceChannelId());
                sb.append(Const.LINE_START).append(" ").append(tc.getAsMention()).append(": ").append(vc.getName()).append("\n");
            }
        }
        if(!(textRooms.isEmpty()))
        {
            sb.append(event.localize("command.room.list.text", textRooms.size())).append("\n");
            for(Room r : textRooms)
            {
                TextChannel tc = guild.getTextChannelById(r.getTextChannelId());
                sb.append(Const.LINE_START).append(" ").append(tc.getAsMention()).append("\n");
            }
        }
        if(!(voiceRooms.isEmpty()))
        {
            sb.append(event.localize("command.room.list.voice", voiceRooms.size())).append("\n");
            for(Room r : voiceRooms)
            {
                VoiceChannel vc = guild.getVoiceChannelById(r.getVoiceChannelId());
                sb.append(Const.LINE_START).append(" ").append(vc.getName()).append("\n");
            }
        }

        event.replySuccess(false, sb.toString());
    }

    private class CreateComboCmd extends EndlessCommand
    {
        CreateComboCmd()
        {
            this.name = "combo";
            this.help = "Creates a text and a voice private room";
            this.arguments = "<room name>";
            this.botPerms = new Permission[]{Permission.MANAGE_CHANNEL};
            this.userPerms = new Permission[]{Permission.MANAGE_CHANNEL, Permission.MANAGE_SERVER};
            this.cooldown = 60;
            this.cooldownScope = CooldownScope.GUILD;
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            String args = event.getArgs();
            Guild guild = event.getGuild();
            String p = event.getClient().getPrefix();
            String tcName = args.replace(" ", "_");
            User owner = event.getAuthor();

            GuildSettings settings = bot.endless.getGuildSettings(guild);
            Room.Mode roomMode = settings.getRoomMode();

            if(roomMode==Room.Mode.NO_CREATION || roomMode==Room.Mode.TEXT_ONLY || roomMode==Room.Mode.VOICE_ONLY)
            {
                event.replyError("command.room.create.mode", roomMode.getName());
                return;
            }
            if(tcName.length()<2 || tcName.length()>100)
            {
                event.replyError("command.room.create.name");
                return;
            }

            for(TextChannel tc : guild.getTextChannels())
            {
                if(tc.getName().equalsIgnoreCase(tcName))
                {
                    event.replyError("command.room.create.tcName");
                    return;
                }
            }
            for(VoiceChannel vc : guild.getVoiceChannels())
            {
                if(vc.getName().equalsIgnoreCase(args))
                {
                    event.replyError("command.room.create.vcName");
                    return;
                }
            }

            Collection<Permission> publicRoleP = EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MESSAGE_READ, Permission.VIEW_CHANNEL);
            Collection<Permission> selfMemberP = EnumSet.of(Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS,
                    Permission.MESSAGE_READ, Permission.VIEW_CHANNEL);
            Collection<Permission> memberP = EnumSet.of(Permission.MESSAGE_READ, Permission.VIEW_CHANNEL);
            String reason = "["+owner.getName()+"#"+owner.getDiscriminator()+"] Combo Room Creation";

            event.async(() -> {
                TextChannel tc;
                VoiceChannel vc;
                try
                {
                    tc = (TextChannel)guild.getController().createTextChannel(tcName)
                            .setTopic("Room Owner: "+owner.getAsMention()+"\nUse `"+p+"room leave` to leave this room")
                            .addPermissionOverride(guild.getPublicRole(), null, publicRoleP)
                            .addPermissionOverride(event.getSelfMember(), selfMemberP,null)
                            .addPermissionOverride(event.getMember(), memberP, null)
                            .reason(reason).complete();

                    vc = (VoiceChannel)guild.getController().createVoiceChannel(args)
                            .addPermissionOverride(guild.getPublicRole(), null, publicRoleP)
                            .addPermissionOverride(event.getSelfMember(), selfMemberP,null)
                            .addPermissionOverride(event.getMember(), memberP, null)
                            .reason(reason).complete();
                }
                catch(Exception e)
                {
                    event.replyError("command.room.create.error");
                    LOG.error("Could not create a combo room in guild {}", guild.getId(), e);
                    return;
                }

                tc.sendMessageFormat("%s %s, %s", event.getClient().getSuccess(), owner, event.localize("command.room.create.created")).queue();
                bot.rsdm.createComboRoom(false, guild.getIdLong(), tc.getIdLong(), owner.getIdLong(), vc.getIdLong());
                event.replySuccess("command.room.create.created.combo", tc.getAsMention(), vc.getName());
            });
        }
    }

    private class CreateTextCmd extends EndlessCommand
    {
        CreateTextCmd()
        {
            this.name = "text";
            this.help = "Creates a text private room";
            this.arguments = "<room name>";
            this.botPerms = new Permission[]{Permission.MANAGE_CHANNEL};
            this.userPerms = new Permission[]{Permission.MANAGE_CHANNEL, Permission.MANAGE_SERVER};
            this.cooldown = 60;
            this.cooldownScope = CooldownScope.GUILD;
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            Guild guild = event.getGuild();
            String p = event.getClient().getPrefix();
            String tcName = event.getArgs().replace(" ", "_");
            User owner = event.getAuthor();

            GuildSettings settings = bot.endless.getGuildSettings(guild);
            Room.Mode roomMode = settings.getRoomMode();

            if(roomMode==Room.Mode.COMBO_ONLY || roomMode==Room.Mode.NO_CREATION || roomMode==Room.Mode.VOICE_ONLY)
            {
                event.replyError("command.room.create.mode", roomMode.getName());
                return;
            }
            if(tcName.length()<2 || tcName.length()>100)
            {
                event.replyError("command.room.create.name");
                return;
            }

            for(TextChannel tc : guild.getTextChannels())
            {
                if(tc.getName().equalsIgnoreCase(tcName))
                {
                    event.replyError("command.room.create.tcName");
                    return;
                }
            }

            Collection<Permission> publicRoleP = EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MESSAGE_READ, Permission.VIEW_CHANNEL);
            Collection<Permission> selfMemberP = EnumSet.of(Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS,
                    Permission.MESSAGE_READ, Permission.VIEW_CHANNEL);
            Collection<Permission> memberP = EnumSet.of(Permission.MESSAGE_READ, Permission.VIEW_CHANNEL);
            String reason = "["+owner.getName()+"#"+owner.getDiscriminator()+"] Text Room Creation";

            guild.getController().createTextChannel(tcName).setTopic("Room Owner: "+owner.getAsMention()+"\nUse `"+p+"room leave` to leave this room")
                    .addPermissionOverride(guild.getPublicRole(), null, publicRoleP)
                    .addPermissionOverride(event.getSelfMember(), selfMemberP,null)
                    .addPermissionOverride(event.getMember(), memberP, null)
                    .reason(reason).queue(s -> {
                        TextChannel tc = (TextChannel)s;
                        tc.sendMessageFormat("%s %s, ", event.getClient().getSuccess(), owner).queue();
                        bot.rsdm.createTextRoom(false, guild.getIdLong(), tc.getIdLong(), owner.getIdLong());
                        event.replySuccess("command.room.create.created.text", tc.getAsMention());
            }, e -> {
                        event.replyError("command.room.create.error");
                        LOG.error("Could not create a text room in guild {}", guild.getId(), e);
            });
        }
    }

    private class CreateVoiceCmd extends EndlessCommand
    {
        CreateVoiceCmd()
        {
            this.name = "voice";
            this.help = "Creates a voice private room";
            this.arguments = "<room name> | [expiry time]";
            this.botPerms = new Permission[]{Permission.MANAGE_CHANNEL};
            this.userPerms = new Permission[]{Permission.MANAGE_CHANNEL, Permission.MANAGE_SERVER};
            this.cooldown = 60;
            this.cooldownScope = CooldownScope.GUILD;
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            Guild guild = event.getGuild();
            String vcName = event.getArgs();
            User owner = event.getAuthor();
            GuildSettings settings = bot.endless.getGuildSettings(guild);
            Room.Mode roomMode = settings.getRoomMode();

            if(roomMode==Room.Mode.COMBO_ONLY || roomMode==Room.Mode.NO_CREATION || roomMode==Room.Mode.TEXT_ONLY)
            {
                event.replyError("command.room.create.mode", roomMode.getName());
                return;
            }
            if(vcName.length()<2 || vcName.length()>100)
            {
                event.replyError("command.room.create.name");
                return;
            }

            for(VoiceChannel vc : guild.getVoiceChannels())
            {
                if(vc.getName().equalsIgnoreCase(vcName))
                {
                    event.replyError("command.room.create.vcName");
                    return;
                }
            }

            Collection<Permission> publicRoleP = EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MESSAGE_READ, Permission.VIEW_CHANNEL);
            Collection<Permission> selfMemberP = EnumSet.of(Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS,
                    Permission.MESSAGE_READ, Permission.VIEW_CHANNEL);
            Collection<Permission> memberP = EnumSet.of(Permission.MESSAGE_READ, Permission.VIEW_CHANNEL);
            String reason = "["+owner.getName()+"#"+owner.getDiscriminator()+"] Voice Room Creation";

            guild.getController().createVoiceChannel(vcName)
                    .addPermissionOverride(guild.getPublicRole(), null, publicRoleP)
                    .addPermissionOverride(event.getSelfMember(), selfMemberP, null)
                    .addPermissionOverride(event.getMember(), memberP, null)
                    .reason(reason).queue(s -> {
                        VoiceChannel vc = (VoiceChannel)s;
                        bot.rsdm.createVoiceRoom(false, guild.getIdLong(), owner.getIdLong(), vc.getIdLong());
                        event.replySuccess("command.room.create.created.voice", vc.getName());
            }, e -> {
                        event.replyError("command.room.create.error");
                        LOG.error("Could not create a text room in guild {}", guild.getId(), e);
            });
        }
    }

    private class InviteCmd extends EndlessCommand
    {
        InviteCmd()
        {
            this.name = "invite";
            this.help = "Invites the specified user to the current room";
            this.arguments = "<user> to [room]";
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            String[] args = ArgsUtils.splitWithSeparator(2, event.getArgs(), " to ");
            Member member = ArgsUtils.findMember(event, args[0]);
            Channel channel = args[1].isEmpty()?event.getTextChannel():ArgsUtils.findChannel(event, args[1]);
            if(channel==null || member==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError("command.room.notARoom", channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName());
                return;
            }
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            if(room.isRestricted() && !(room.getOwnerId()==event.getAuthor().getIdLong()) && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("command.room.invite.locked");
                return;
            }
            TextChannel tc = event.getGuild().getTextChannelById(room.getTextChannelId());
            VoiceChannel vc = event.getGuild().getVoiceChannelById(room.getVoiceChannelId());
            if(room.isCombo())
            {
                if(!(tc==null))
                {
                    if(!(tc.getMembers().contains(event.getMember())) && !(room.getOwnerId()==event.getAuthor().getIdLong())
                            && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
                    {
                        event.replyError("command.room.invite.notJoined");
                        return;
                    }
                    if(tc.getMembers().contains(member))
                    {
                        event.replyError("command.room.invite.already");
                        return;
                    }
                }
            }
            
            event.async(() -> {
                try
                {
                    if(channel instanceof TextChannel)
                    {
                        channel.putPermissionOverride(member).setAllow(Permission.MESSAGE_READ).reason("[Room Invite]").complete();
                        if(room.isCombo() && !(vc==null))
                            vc.putPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).reason("[Room Invite]").complete();
                        event.reactSuccess();
                        ((TextChannel) channel).sendMessage(event.localize("core.room.welcome", member.getUser().getAsMention())).queue();
                    }
                    else
                    {
                        channel.putPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).reason("[Room Invite]").complete();
                        if(room.isCombo() && !(tc==null))
                        {
                            tc.putPermissionOverride(member).setAllow(Permission.MESSAGE_READ).reason("[Room Invite]").complete();
                            tc.sendMessage(event.localize("core.room.welcome", member.getUser().getAsMention())).queue();
                        }
                        event.reactSuccess();
                    }
                }
                catch(Exception e)
                {
                    String name = member.getUser().getName()+"#"+member.getUser().getDiscriminator();
                    event.replyError("command.room.invite.error", name);
                }
            });
        }
    }

    private class JoinCmd extends EndlessCommand
    {
        JoinCmd()
        {
            this.name = "join";
            this.help = "Joins the specified room";
            this.arguments = "<room>";
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            Channel channel = ArgsUtils.findChannel(event, event.getArgs());
            Member member = event.getMember();
            if(channel==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError("command.room.notARoom", channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName());
                return;
            }
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            if(room.isRestricted() && !(room.getOwnerId()==event.getAuthor().getIdLong()) && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("command.room.join.locked");
                return;
            }
            TextChannel tc = event.getGuild().getTextChannelById(room.getTextChannelId());
            VoiceChannel vc = event.getGuild().getVoiceChannelById(room.getVoiceChannelId());
            if(room.isCombo())
            {
                if(!(tc==null))
                {
                    if(tc.getMembers().contains(member))
                    {
                        event.replyError("command.room.join.already");
                        return;
                    }
                }
            }

            event.async(() -> {
                try
                {
                    if(channel instanceof TextChannel)
                    {
                        channel.putPermissionOverride(member).setAllow(Permission.MESSAGE_READ).reason("[Room Join]").complete();
                        if(room.isCombo() && !(vc==null))
                            vc.putPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).reason("[Room Join]").complete();
                        event.reactSuccess();
                        ((TextChannel)channel).sendMessage(event.localize("core.room.welcome", member.getUser().getAsMention())).queue();
                    }
                    else
                    {
                        channel.putPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).reason("[Room Join]").complete();
                        if(room.isCombo() && !(tc==null))
                        {
                            tc.putPermissionOverride(member).setAllow(Permission.MESSAGE_READ).reason("[Room Join]").complete();
                            tc.sendMessage(event.localize("core.room.welcome", member.getUser().getAsMention())).queue();
                        }
                        event.reactSuccess();
                    }
                }
                catch(Exception e)
                {
                    event.replyError("command.room.join.error");
                }
            });
        }
    }

    private class KickCmd extends EndlessCommand
    {
        KickCmd()
        {
            this.name = "kick";
            this.help = "Kicks the specified user from the current room";
            this.arguments = "<user> from [room]";
            this.needsArguments = false;
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            String[] args = ArgsUtils.splitWithSeparator(2, event.getArgs(), " from ");
            Member member = ArgsUtils.findMember(event, args[0]);
            Channel channel = args[1].isEmpty()?event.getTextChannel():ArgsUtils.findChannel(event, args[1]);
            if(channel==null || member==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError("command.room.notARoom", channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName());
                return;
            }
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            TextChannel tc = event.getGuild().getTextChannelById(room.getTextChannelId());
            VoiceChannel vc = event.getGuild().getVoiceChannelById(room.getVoiceChannelId());
            if(room.isCombo())
            {
                if(!(tc==null))
                {
                    if(!(tc.getMembers().contains(event.getMember())) && !(room.getOwnerId()==event.getAuthor().getIdLong())
                            && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
                    {
                        event.replyError("command.room.kick.notJoined");
                        return;
                    }
                    if(!(tc.getMembers().contains(member)))
                    {
                        event.replyError("command.room.kick.notThere");
                        return;
                    }
                }
            }
            if(!(room.getOwnerId()==member.getUser().getIdLong()) && !(permLevel.isAtLeast(PermLevel.MODERATOR)))
            {
                event.replyError("command.room.kick.missingP");
                return;
            }
            if(room.getOwnerId()==member.getUser().getIdLong() && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("command.room.kick.owner", event.getClient().getPrefix());
                return;
            }

            if(room.getOwnerId()==member.getUser().getIdLong())
                bot.rsdm.transferProperty(event.getAuthor().getIdLong(), channel.getIdLong());

            event.async(() -> {
                try
                {
                    if(!(channel.getPermissionOverride(event.getMember())==null))
                    {
                        if(channel instanceof TextChannel)
                        {
                            channel.getPermissionOverride(member).delete().reason("[Room Kick]").complete();
                            if(room.isCombo() && !(vc==null))
                                vc.getPermissionOverride(member).delete().reason("[Room Kick]").complete();
                            event.reactSuccess();
                        }
                        else
                        {
                            channel.getPermissionOverride(member).delete().reason("[Room Kick]").complete();
                            if(room.isCombo() && !(tc==null))
                                tc.getPermissionOverride(member).delete().reason("[Room Kick]").complete();
                            event.reactSuccess();
                        }
                    }
                    else
                        event.replyError("command.room.kick.noOverride");
                }
                catch(Exception e)
                {
                    event.replyError("command.room.kick.error");
                }
            });
        }
    }

    private class LeaveCmd extends EndlessCommand
    {
        LeaveCmd()
        {
            this.name = "leave";
            this.help = "Leaves current room";
            this.needsArguments = false;
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            Member member = event.getMember();
            Channel channel = event.getArgs().isEmpty()?event.getTextChannel():ArgsUtils.findChannel(event, event.getArgs());
            if(channel==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError("command.room.notARoom", channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName());
                return;
            }
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), member);
            TextChannel tc = event.getGuild().getTextChannelById(room.getTextChannelId());
            VoiceChannel vc = event.getGuild().getVoiceChannelById(room.getVoiceChannelId());
            if(room.isCombo())
            {
                if(!(tc==null))
                {
                    if(!(tc.getMembers().contains(event.getMember())) && !(room.getOwnerId()==event.getAuthor().getIdLong())
                            && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
                    {
                        event.replyError("command.room.leave.notJoined");
                        return;
                    }
                    if(!(tc.getMembers().contains(member)))
                    {
                        event.replyError("command.room.leave.notJoined");
                        return;
                    }
                }
            }
            if(room.getOwnerId()==event.getAuthor().getIdLong())
            {
                event.replyError("command.room.leave.owner", event.getClient().getPrefix());
                return;
            }

            event.async(() -> {
                try
                {
                    if(!(channel.getPermissionOverride(event.getMember())==null))
                    {
                        if(channel instanceof TextChannel)
                        {
                            channel.getPermissionOverride(member).delete().reason("[Room Leave]").complete();
                            if(room.isCombo() && !(vc==null))
                                vc.getPermissionOverride(member).delete().reason("[Room Leave]").complete();
                            event.reactSuccess();
                        }
                        else
                        {
                            channel.getPermissionOverride(member).delete().reason("[Room Leave]").complete();
                            if(room.isCombo() && !(tc==null))
                                tc.getPermissionOverride(member).delete().reason("[Room Leave]").complete();
                            event.reactSuccess();
                        }
                    }
                    else
                        event.replyError("command.room.leave.noOverride");
                }
                catch(Exception e)
                {
                    event.replyError("command.room.leave.error");
                }
            });
        }
    }

    private class LockCmd extends EndlessCommand
    {
        LockCmd()
        {
            this.name = "lock";
            this.help = "Locks the specified or current room";
            this.arguments = "[room]";
            this.needsArguments = false;
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            Channel channel = event.getArgs().isEmpty()?event.getTextChannel():ArgsUtils.findChannel(event, event.getArgs());
            if(channel==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError("command.room.notARoom", (channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName()));
                return;
            }
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            if(!(room.getOwnerId()==event.getAuthor().getIdLong()) && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("command.room.lock.missingP");
                return;
            }
            if(room.isRestricted())
            {
                event.replyError("command.room.lock.locked", event.getClient().getPrefix());
                return;
            }
            bot.rsdm.lockRoom(true, channel.getIdLong());
            event.replySuccess("command.room.lock.success");
        }
    }

    private class ModeCmd extends EndlessCommand
    {
        ModeCmd()
        {
            this.name = "mode";
            this.help = "Change the current room mode";
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            String args = event.getArgs().toUpperCase();
            
            if(Arrays.asList(MODES).contains(args))
            {
                bot.gsdm.setRoomMode(event.getGuild(), Room.Mode.valueOf(args));
                event.replySuccess("command.room.mode.success", args);
            }
            else
                event.replyError("command.room.mode.invalid", String.join(", ", MODES));
        }
    }

    private class TakeCmd extends EndlessCommand
    {
        TakeCmd()
        {
            this.name = "take";
            this.help = "Takes the property of the specified room";
            this.arguments = "[room]";
            this.needsArguments = false;
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            Channel channel = event.getArgs().isEmpty()?event.getTextChannel():ArgsUtils.findChannel(event, event.getArgs());
            if(channel==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError("command.room.notARoom", (channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName()));
                return;
            }
            
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            if(!(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("command.room.take.admin");
                return;
            }
            if(!(channel.getMembers().contains(event.getMember())))
            {
                event.replyError("command.room.take.notJoined");
                return;
            }

            if(channel instanceof TextChannel)
            {
                channel.getManager().setTopic(((TextChannel) channel).getTopic().replace("<@"+room.getOwnerId()+">", event.getAuthor().getAsMention()))
                        .queue(null, e -> event.replyError("command.room.take.topicUpdate"));
            }
            bot.rsdm.transferProperty(event.getAuthor().getIdLong(), channel.getIdLong());
            event.replySuccess("command.room.take.success", event.getAuthor().getAsMention());
        }
    }

    private class TransferCmd extends EndlessCommand
    {
        TransferCmd()
        {
            this.name = "transfer";
            this.help = "Transfer the property of the specified room";
            this.arguments = "<user>";
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            Member member = ArgsUtils.findMember(event, event.getArgs());
            Channel channel = event.getArgs().isEmpty()?event.getTextChannel():ArgsUtils.findChannel(event, event.getArgs());
            if(member==null || channel==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError("command.room.notARoom", (channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName()));
                return;
            }
            
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            if(!(room.getOwnerId()==event.getAuthor().getIdLong()) && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("command.room.transfer.owner");
                return;
            }
            if(!(channel.getMembers().contains(member)))
            {
                event.replyError("command.room.transfer.notJoined");
                return;
            }

            if(channel instanceof TextChannel)
            {
                channel.getManager().setTopic(((TextChannel) channel).getTopic().replace("<@"+room.getOwnerId()+">", member.getUser().getAsMention()))
                        .queue(null, e -> event.replyError("command.room.take.topicUpdate"));
            }
            
            bot.rsdm.transferProperty(member.getUser().getIdLong(), channel.getIdLong());
            event.replySuccess("command.room.take.success", member.getUser().getAsMention());
        }
    }

    private class UnLockCmd extends EndlessCommand
    {
        UnLockCmd()
        {
            this.name = "unlock";
            this.help = "Unlocks the specified or current room";
            this.arguments = "[room]";
            this.needsArguments = false;
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            Channel channel = event.getArgs().isEmpty()?event.getTextChannel():ArgsUtils.findChannel(event, event.getArgs());
            if(channel==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError("command.room.notARoom", (channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName()));
                return;
            }
            
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            if(!(room.getOwnerId()==event.getAuthor().getIdLong()) && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("command.room.unlock.missingP");
                return;
            }
            
            if(room.isRestricted())
            {
                event.replyError("command.room.unlock.unlocked", event.getClient().getPrefix());
                return;
            }
            bot.rsdm.lockRoom(false, channel.getIdLong());
            event.replySuccess("command.room.unlock.success");
        }
    }
}

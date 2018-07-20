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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.PermLevel;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.core.entities.Room;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Artuto
 */

public class RoomCmd extends EndlessCommand
{
    private final Bot bot;
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
    protected void executeCommand(CommandEvent event)
    {
        String prefix = event.getClient().getPrefix();

        if(event.getArgs().equalsIgnoreCase("create"))
        {
            event.replyWarning("You can create three types of rooms:\n" +
                    "- *Text Rooms:* `"+prefix+"room text` Creates a private text room\n" +
                    "- *Voice Rooms:* `"+prefix+"room voice` Creates a private voice room\n" +
                    "- *Combo Rooms:* `"+prefix+"room combo` Creates a private text and voice rooms. They are also linked");
            return;
        }

        Guild guild = event.getGuild();
        List<Room> rooms = bot.rsdm.getRoomsForGuild(guild.getIdLong());

        if(rooms.isEmpty())
        {
            event.replyWarning("This guild doesn't have any rooms created! Create one by doing `"+prefix+"room create`");
            return;
        }

        List<Room> comboRooms = rooms.stream().filter(Room::isCombo).filter(r -> r.canAccess(event)).collect(Collectors.toList());
        List<Room> textRooms = rooms.stream().filter(Room::isText).filter(r -> r.canAccess(event)).collect(Collectors.toList());
        List<Room> voiceRooms = rooms.stream().filter(Room::isVoice).filter(r -> r.canAccess(event)).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder("Rooms in **").append(guild.getName()).append("** you can join:\n");

        if(!(comboRooms.isEmpty()))
        {
            sb.append("**Combo Rooms**: *").append(comboRooms.size()).append("*\n");
            comboRooms.forEach(r -> {
                TextChannel tc = guild.getTextChannelById(r.getTextChannelId());
                VoiceChannel vc = guild.getVoiceChannelById(r.getVoiceChannelId());
                sb.append(Const.LINE_START).append(" ").append(tc.getAsMention()).append(": ").append(vc.getName())
                        .append(r.getExpiryTime()==null?"":" - Expires in "+FormatUtil.formatTimeFromSeconds(OffsetDateTime.now().until(r.getExpiryTime(),
                                ChronoUnit.SECONDS))).append("\n");
            });
        }
        if(!(textRooms.isEmpty()))
        {
            sb.append("**Text Rooms**: *").append(textRooms.size()).append("*\n");
            textRooms.forEach(r -> {
                TextChannel tc = guild.getTextChannelById(r.getTextChannelId());
                sb.append(Const.LINE_START).append(" ").append(tc.getAsMention()).append(r.getExpiryTime()==null?"":" - Expires in "+
                        FormatUtil.formatTimeFromSeconds(OffsetDateTime.now().until(r.getExpiryTime(), ChronoUnit.SECONDS))).append("\n");
            });
        }
        if(!(voiceRooms.isEmpty()))
        {
            sb.append("**Voice Rooms**: *").append(voiceRooms.size()).append("*\n");
            voiceRooms.forEach(r -> {
                VoiceChannel vc = guild.getVoiceChannelById(r.getVoiceChannelId());
                sb.append(Const.LINE_START).append(" ").append(vc.getName()).append(r.getExpiryTime()==null?"":" - Expires in "+
                        FormatUtil.formatTimeFromSeconds(OffsetDateTime.now().until(r.getExpiryTime(), ChronoUnit.SECONDS))).append("\n");
            });
        }

        event.replySuccess(sb.toString());
    }

    private class CreateComboCmd extends EndlessCommand
    {
        CreateComboCmd()
        {
            this.name = "combo";
            this.help = "Creates a text and a voice private room";
            this.arguments = "<rooms name> | [expiry time]";
            this.botPerms = new Permission[]{Permission.MANAGE_CHANNEL};
            this.userPerms = new Permission[]{Permission.MANAGE_CHANNEL, Permission.MANAGE_SERVER};
            this.cooldown = 1000;
            this.cooldownScope = CooldownScope.GUILD;
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            boolean expiry = false;
            Instant expiryTime = null;
            String formattedTime = "";
            String[] args = splitArgsWithTime(event.getArgs());

            Guild guild = event.getGuild();
            String p = event.getClient().getPrefix();
            String tcName = args[0].replace(" ", "_");
            String vcName = args[0];
            User owner = event.getAuthor();

            GuildSettings settings = bot.endless.getGuildSettings(guild);
            Room.Mode roomMode = settings.getRoomMode();

            if(roomMode==Room.Mode.NO_CREATION || roomMode==Room.Mode.TEXT_ONLY || roomMode==Room.Mode.VOICE_ONLY)
            {
                event.replyError("This guild room mode is set to `"+roomMode.getName()+"`!");
                return;
            }

            if(tcName.length()<2 || tcName.length()>100)
            {
                event.replyError("The channel name can't be longer than 100 characters and shorter than 2 characters!");
                return;
            }
            if(!(args[1].isEmpty()))
            {
                expiry = true;
                int time = Integer.valueOf(args[1]);
                int minutes = time/60;
                expiryTime = Instant.now().plus(minutes, ChronoUnit.MINUTES);
                formattedTime = FormatUtil.formatTimeFromSeconds(time);
                if(time<0)
                {
                    event.replyError("The time cannot be negative!");
                    return;
                }
            }

            for(TextChannel tc : guild.getTextChannels())
            {
                if(tc.getName().equalsIgnoreCase(tcName))
                {
                    event.replyError("A Text Channel with that name already exists!");
                    return;
                }
            }
            for(VoiceChannel vc : guild.getVoiceChannels())
            {
                if(vc.getName().equalsIgnoreCase(vcName))
                {
                    event.replyError("A Voice Channel with that name already exists!");
                    return;
                }
            }

            boolean fExpiry = expiry;
            Instant fExpiryTime = expiryTime;
            String fFormattedTime = formattedTime;
            event.async(() -> {
                TextChannel tc;
                VoiceChannel vc;
                try
                {
                    tc = (TextChannel)guild.getController().createTextChannel(tcName).setTopic("Room Owner: "+owner.getAsMention()+"\nUse `"+p+"room leave` to leave this room")
                            .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MESSAGE_READ))
                            .addPermissionOverride(event.getSelfMember(), EnumSet.of(Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS, Permission.MESSAGE_READ),null)
                            .addPermissionOverride(event.getMember(), Permission.MESSAGE_READ.getRawValue(), 0L)
                            .reason("["+owner.getName()+"#"+owner.getDiscriminator()+"] Combo Room Creation").complete();

                    vc = (VoiceChannel)guild.getController().createVoiceChannel(vcName)
                            .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.VIEW_CHANNEL))
                            .addPermissionOverride(event.getSelfMember(), EnumSet.of(Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS, Permission.VIEW_CHANNEL),null)
                            .addPermissionOverride(event.getMember(), Permission.VIEW_CHANNEL.getRawValue(),0L)
                            .reason("["+owner.getName()+"#"+owner.getDiscriminator()+"] Combo Room Creation").complete();
                }
                catch(Exception e)
                {
                    event.replyError("Something went wrong while creating the channels. Check that I have " +
                            "Manage Channels permission.");
                    return;
                }

                if(fExpiry)
                {
                    tc.sendMessageFormat("%s %s, Your Room has been created.", event.getClient().getSuccess(), owner).queue();
                    bot.rsdm.createComboRoom(false, fExpiryTime.toEpochMilli(), guild.getIdLong(), tc.getIdLong(), owner.getIdLong(), vc.getIdLong());
                    event.replySuccess("Successfully created Combo Room (**"+tc.getAsMention()+"** and **"+vc.getName()+"**) that will expire in "
                            +fFormattedTime);
                }
                else
                {
                    tc.sendMessageFormat("%s %s, Your Room has been created.", event.getClient().getSuccess(), owner).queue();
                    bot.rsdm.createComboRoom(false, 0L, guild.getIdLong(), tc.getIdLong(), owner.getIdLong(), vc.getIdLong());
                    event.replySuccess("Successfully created Combo Room (**"+tc.getAsMention()+"** and **"+vc.getName()+"**)");
                }
            });
        }
    }

    private class CreateTextCmd extends EndlessCommand
    {
        CreateTextCmd()
        {
            this.name = "text";
            this.help = "Creates a text private room";
            this.arguments = "<room name> | [expiry time]";
            this.botPerms = new Permission[]{Permission.MANAGE_CHANNEL};
            this.userPerms = new Permission[]{Permission.MANAGE_CHANNEL, Permission.MANAGE_SERVER};
            this.cooldown = 1000;
            this.cooldownScope = CooldownScope.GUILD;
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            boolean expiry = false;
            Instant expiryTime = null;
            String formattedTime = "";
            String[] args = splitArgsWithTime(event.getArgs());

            Guild guild = event.getGuild();
            String p = event.getClient().getPrefix();
            String tcName = args[0].replace(" ", "_");
            User owner = event.getAuthor();

            GuildSettings settings = bot.endless.getGuildSettings(guild);
            Room.Mode roomMode = settings.getRoomMode();

            if(roomMode==Room.Mode.COMBO_ONLY || roomMode==Room.Mode.NO_CREATION || roomMode==Room.Mode.VOICE_ONLY)
            {
                event.replyError("This guild room mode is set to `"+roomMode.getName()+"`!");
                return;
            }

            if(tcName.length()<2 || tcName.length()>100)
            {
                event.replyError("The channel name can't be longer than 100 characters and shorter than 2 characters!");
                return;
            }
            if(!(args[1].isEmpty()))
            {
                expiry = true;
                int time = Integer.valueOf(args[1]);
                int minutes = time/60;
                expiryTime = Instant.now().plus(minutes, ChronoUnit.MINUTES);
                formattedTime = FormatUtil.formatTimeFromSeconds(time);
                if(time<0)
                {
                    event.replyError("The time cannot be negative!");
                    return;
                }
            }

            for(TextChannel tc : guild.getTextChannels())
            {
                if(tc.getName().equalsIgnoreCase(tcName))
                {
                    event.replyError("A Text Channel with that name already exists!");
                    return;
                }
            }

            boolean fExpiry = expiry;
            Instant fExpiryTime = expiryTime;
            String fFormattedTime = formattedTime;
            event.async(() -> {
                TextChannel tc;
                try
                {
                    tc = (TextChannel)guild.getController().createTextChannel(tcName).setTopic("Room Owner: "+owner.getAsMention()+"\nUse `"+p+"room leave` to leave this room")
                            .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MESSAGE_READ))
                            .addPermissionOverride(event.getSelfMember(), EnumSet.of(Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS, Permission.MESSAGE_READ),null)
                            .addPermissionOverride(event.getMember(), Permission.MESSAGE_READ.getRawValue(), 0L)
                            .reason("["+owner.getName()+"#"+owner.getDiscriminator()+"] Text Room Creation").complete();
                }
                catch(Exception e)
                {
                    event.replyError("Something went wrong while creating the channel. Check that I have " +
                            "Manage Channels permission.");
                    return;
                }

                if(fExpiry)
                {
                    tc.sendMessageFormat("%s %s, Your Room has been created.", event.getClient().getSuccess(), owner).queue();
                    bot.rsdm.createTextRoom(false, fExpiryTime.toEpochMilli(), guild.getIdLong(), tc.getIdLong(), owner.getIdLong());
                    event.replySuccess("Successfully created Text Room **"+tc.getAsMention()+"** that will expire in "+fFormattedTime);
                }
                else
                {
                    tc.sendMessageFormat("%s %s, Your Room has been created.", event.getClient().getSuccess(), owner).queue();
                    bot.rsdm.createTextRoom(false, 0L, guild.getIdLong(), tc.getIdLong(), owner.getIdLong());
                    event.replySuccess("Successfully created Text Room **"+tc.getAsMention()+"**");
                }
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
            this.cooldown = 1000;
            this.cooldownScope = CooldownScope.GUILD;
            this.parent = RoomCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            boolean expiry = false;
            Instant expiryTime = null;
            String formattedTime = "";
            String[] args = splitArgsWithTime(event.getArgs());

            Guild guild = event.getGuild();
            String vcName = args[0];
            User owner = event.getAuthor();

            GuildSettings settings = bot.endless.getGuildSettings(guild);
            Room.Mode roomMode = settings.getRoomMode();

            if(roomMode==Room.Mode.COMBO_ONLY || roomMode==Room.Mode.NO_CREATION || roomMode==Room.Mode.TEXT_ONLY)
            {
                event.replyError("This guild room mode is set to `"+roomMode.getName()+"`!");
                return;
            }

            if(vcName.length()<2 || vcName.length()>100)
            {
                event.replyError("The channel name can't be longer than 100 characters and shorter than 2 characters!");
                return;
            }
            if(!(args[1].isEmpty()))
            {
                expiry = true;
                int time = Integer.valueOf(args[1]);
                int minutes = time/60;
                expiryTime = Instant.now().plus(minutes, ChronoUnit.MINUTES);
                formattedTime = FormatUtil.formatTimeFromSeconds(time);
                if(time<0)
                {
                    event.replyError("The time cannot be negative!");
                    return;
                }
            }

            for(VoiceChannel vc : guild.getVoiceChannels())
            {
                if(vc.getName().equalsIgnoreCase(vcName))
                {
                    event.replyError("A Voice Channel with that name already exists!");
                    return;
                }
            }

            boolean fExpiry = expiry;
            Instant fExpiryTime = expiryTime;
            String fFormattedTime = formattedTime;
            event.async(() -> {
                VoiceChannel vc;
                try
                {
                    vc = (VoiceChannel)guild.getController().createVoiceChannel(vcName)
                            .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.VIEW_CHANNEL))
                            .addPermissionOverride(event.getSelfMember(), EnumSet.of(Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS, Permission.VIEW_CHANNEL), null)
                            .addPermissionOverride(event.getMember(), Permission.VIEW_CHANNEL.getRawValue(),0L)
                            .reason("["+owner.getName()+"#"+owner.getDiscriminator()+"] Voice Room Creation").complete();
                }
                catch(Exception e)
                {
                    event.replyError("Something went wrong while creating the channel. Check that I have " +
                            "Manage Channels permission.");
                    return;
                }

                if(fExpiry)
                {
                    bot.rsdm.createVoiceRoom(false, fExpiryTime.toEpochMilli(), guild.getIdLong(), owner.getIdLong(), vc.getIdLong());
                    event.replySuccess("Successfully created Voice Room **"+vc.getName()+"** that will expire in "+fFormattedTime);
                }
                else
                {
                    bot.rsdm.createVoiceRoom(false, 0L, guild.getIdLong(), owner.getIdLong(), vc.getIdLong());
                    event.replySuccess("Successfully created Voice Room **"+vc.getName()+"**");
                }
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
        protected void executeCommand(CommandEvent event)
        {
            String[] args = splitArgs(event.getArgs(), " to ");
            Member member = findMember(event, args[0]);
            Channel channel;
            if(args[1].isEmpty())
                channel = event.getTextChannel();
            else
                channel = findChannel(event, args[1]);
            if(member==null)
                return;
            if(channel==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError((channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName())+" is not an Endless room!");
                return;
            }
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            if(room.isRestricted() && !(room.getOwnerId()==event.getAuthor().getIdLong()) && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("You can't invite someone to a room you don't own if its locked!");
                return;
            }
            TextChannel tc = event.getGuild().getTextChannelById(room.getTextChannelId());
            VoiceChannel vc = event.getGuild().getVoiceChannelById(room.getVoiceChannelId());
            if(room.isCombo())
            {
                if(!(tc==null))
                {
                    if(!(tc.getMembers().contains(event.getMember())) && !(room.getOwnerId()==event.getAuthor().getIdLong()) && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
                    {
                        event.replyError("You must be on the room to invite someone!");
                        return;
                    }
                    if(tc.getMembers().contains(member))
                    {
                        event.replyError("The specified user is already on that room!");
                        return;
                    }
                }
            }

            Channel fChannel = channel;
            event.async(() -> {
                try
                {
                    if(fChannel instanceof TextChannel)
                    {
                        fChannel.putPermissionOverride(member).setAllow(Permission.MESSAGE_READ).reason("[Room Invite]").complete();
                        if(room.isCombo() && !(vc==null))
                            vc.putPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).reason("[Room Invite]").complete();
                        event.reactSuccess();
                        ((TextChannel)fChannel).sendMessageFormat("Welcome, %s to the room!", member.getUser()).queue();
                    }
                    else
                    {
                        fChannel.putPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).reason("[Room Invite]").complete();
                        if(room.isCombo() && !(tc==null))
                        {
                            tc.putPermissionOverride(member).setAllow(Permission.MESSAGE_READ).reason("[Room Invite]").complete();
                            tc.sendMessageFormat("Welcome, %s to the room!", member.getUser()).queue();
                        }
                        event.reactSuccess();
                    }
                }
                catch(Exception e)
                {
                    event.replyError("Could not add **"+member.getUser().getName()+"#"+member.getUser().getDiscriminator()+"** to the room!");
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
        protected void executeCommand(CommandEvent event)
        {
            Channel channel = findChannel(event, event.getArgs());
            Member member = event.getMember();
            if(channel==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError((channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName())+" is not an Endless room!");
                return;
            }
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            if(room.isRestricted() && !(room.getOwnerId()==event.getAuthor().getIdLong()) && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("You can't join a room if its locked!");
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
                        event.replyError("You are already on that room!");
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
                        ((TextChannel)channel).sendMessageFormat("Welcome, %s to the room!", member.getUser()).queue();
                    }
                    else
                    {
                        channel.putPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).reason("[Room Join]").complete();
                        if(room.isCombo() && !(tc==null))
                        {
                            tc.putPermissionOverride(member).setAllow(Permission.MESSAGE_READ).reason("[Room Join]").complete();
                            tc.sendMessageFormat("Welcome, %s to the room!", member.getUser()).queue();
                        }
                        event.reactSuccess();
                    }
                }
                catch(Exception e)
                {
                    event.replyError("Could not join to the room!");
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
        protected void executeCommand(CommandEvent event)
        {
            String[] args = splitArgs(event.getArgs(), " from ");
            Member member = findMember(event, args[0]);
            Channel channel;
            if(args[1].isEmpty())
                channel = event.getTextChannel();
            else
                channel = findChannel(event, args[1]);
            if(member==null)
                return;
            if(channel==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError((channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName())+" is not an Endless room!");
                return;
            }
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            TextChannel tc = event.getGuild().getTextChannelById(room.getTextChannelId());
            VoiceChannel vc = event.getGuild().getVoiceChannelById(room.getVoiceChannelId());
            if(room.isCombo())
            {
                if(!(tc==null))
                {
                    if(!(tc.getMembers().contains(event.getMember())) && !(room.getOwnerId()==event.getAuthor().getIdLong()) && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
                    {
                        event.replyError("You must be on the room to invite someone!");
                        return;
                    }
                    if(!(tc.getMembers().contains(member)))
                    {
                        event.replyError("The specified user isn't on that room!");
                        return;
                    }
                }
            }
            if(!(room.getOwnerId()==member.getUser().getIdLong()) && !(permLevel.isAtLeast(PermLevel.MODERATOR)))
            {
                event.replyError("You can't kick users from this room!");
                return;
            }
            if(room.getOwnerId()==member.getUser().getIdLong() && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("You can't kick the room owner! Use `"+event.getClient().getPrefix()+"room transfer` to transfer the property.");
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
                        event.replyError("Could not find a permission override for the specified user!");
                }
                catch(Exception e)
                {
                    event.replyError("Could not remove permission override for the specified user!");
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
        protected void executeCommand(CommandEvent event)
        {
            Member member = event.getMember();
            Channel channel;
            if(event.getArgs().isEmpty())
                channel = event.getTextChannel();
            else
                channel = findChannel(event, event.getArgs());
            if(channel==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError((channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName())+" is not an Endless room!");
                return;
            }
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), member);
            TextChannel tc = event.getGuild().getTextChannelById(room.getTextChannelId());
            VoiceChannel vc = event.getGuild().getVoiceChannelById(room.getVoiceChannelId());
            if(room.isCombo())
            {
                if(!(tc==null))
                {
                    if(!(tc.getMembers().contains(event.getMember())) && !(room.getOwnerId()==event.getAuthor().getIdLong()) && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
                    {
                        event.replyError("You must be on the room to leave!");
                        return;
                    }
                    if(!(tc.getMembers().contains(member)))
                    {
                        event.replyError("You aren't on the room!");
                        return;
                    }
                }
            }
            if(room.getOwnerId()==event.getAuthor().getIdLong())
            {
                event.replyError("You can't leave a room you own! Use `"+event.getClient().getPrefix()+"room transfer` to transfer the property.");
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
                        event.replyError("Could not find a permission override for you!");
                }
                catch(Exception e)
                {
                    event.replyError("Could not remove permission override for you!");
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
        protected void executeCommand(CommandEvent event)
        {
            Channel channel;
            if(event.getArgs().isEmpty())
                channel = event.getTextChannel();
            else
                channel = findChannel(event, event.getArgs());
            if(channel==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError((channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName())+" is not an Endless room!");
                return;
            }
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            if(!(room.getOwnerId()==event.getAuthor().getIdLong()) && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("You can't lock this room if you aren't the owner!");
                return;
            }
            if(room.isRestricted())
            {
                event.replyError("This room is locked already!");
                return;
            }
            bot.rsdm.lockRoom(true, channel.getIdLong());
            event.replySuccess("Successfully locked room.");
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
        protected void executeCommand(CommandEvent event)
        {
            for(String mode : MODES)
            {
                if(mode.equalsIgnoreCase(event.getArgs()))
                {
                    bot.gsdm.setRoomMode(event.getGuild(), Room.Mode.valueOf(event.getArgs().toUpperCase()));
                    event.replySuccess("Successfully set Room Mode to "+mode);
                    break;
                }
                else
                {
                    event.replyError("That isn't a valid mode! Valid modes are `"+Arrays.stream(MODES).collect(Collectors.joining(", "))+"`");
                    break;
                }
            }
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
        protected void executeCommand(CommandEvent event)
        {
            Channel channel;
            if(event.getArgs().isEmpty())
                channel = event.getTextChannel();
            else
                channel = findChannel(event, event.getArgs());
            if(channel==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError(((TextChannel)channel).getAsMention()+" is not an Endless room!");
                return;
            }
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            if(!(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("Only Administrators can take the property!");
                return;
            }
            if(!(channel.getMembers().contains(event.getMember())))
            {
                event.replyError("You must be on the room to take the property!");
                return;
            }

            channel.getManager().setTopic(((TextChannel) channel).getTopic().replace("<@"+room.getOwnerId()+">", event.getAuthor().getAsMention()))
                    .queue(null, e-> event.replyError("Could not remove old owner from topic!"));
            bot.rsdm.transferProperty(event.getAuthor().getIdLong(), channel.getIdLong());
            event.replySuccess("Successfully transferred room property to "+event.getAuthor().getAsMention());
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
        protected void executeCommand(CommandEvent event)
        {
            Member member = findMember(event, event.getArgs());
            Channel channel = event.getTextChannel();
            if(member==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError(((TextChannel)channel).getAsMention()+" is not an Endless room!");
                return;
            }
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            if(!(room.getOwnerId()==event.getAuthor().getIdLong()) && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("Only the owner can transfer the property!");
                return;
            }
            if(!(channel.getMembers().contains(member)))
            {
                event.replyError("The specifed user must be on the room to transfer property!");
                return;
            }

            channel.getManager().setTopic(((TextChannel) channel).getTopic().replace("<@"+room.getOwnerId()+">", member.getUser().getAsMention()))
                    .queue(null, e-> event.replyError("Could not remove old owner from topic!"));
            bot.rsdm.transferProperty(member.getUser().getIdLong(), channel.getIdLong());
            event.replySuccess("Successfully transferred room property to "+member.getUser().getAsMention());
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
        protected void executeCommand(CommandEvent event)
        {
            Channel channel;
            if(event.getArgs().isEmpty())
                channel = event.getTextChannel();
            else
                channel = findChannel(event, event.getArgs());
            if(channel==null)
                return;

            Room room = bot.rsdm.getRoom(channel.getIdLong());
            if(room==null)
            {
                event.replyError((channel instanceof TextChannel?((TextChannel)channel).getAsMention():channel.getName())+" is not an Endless room!");
                return;
            }
            PermLevel permLevel = PermLevel.getLevel(bot.endless.getGuildSettings(event.getGuild()), event.getMember());
            if(!(room.getOwnerId()==event.getAuthor().getIdLong()) && !(permLevel.isAtLeast(PermLevel.ADMINISTRATOR)))
            {
                event.replyError("You can't unlock this room if you aren't the owner!");
                return;
            }
            if(!(room.isRestricted()))
            {
                event.replyError("This room isn't locked!");
                return;
            }
            bot.rsdm.lockRoom(false, channel.getIdLong());
            event.replySuccess("Successfully unlocked room.");
        }
    }

    private Member findMember(CommandEvent event, String query)
    {
        List<Member> list = FinderUtil.findMembers(query, event.getGuild());

        if(list.isEmpty())
        {
            event.replyWarning("I was not able to found a user with the provided arguments: '"+query+"'");
            return null;
        }
        else if(list.size()>1)
        {
            event.replyWarning(FormatUtil.listOfMembers(list, query));
            return null;
        }
        else
            return list.get(0);
    }

    private Channel findChannel(CommandEvent event, String query)
    {
        List<TextChannel> textChannels = FinderUtil.findTextChannels(query, event.getGuild());

        if(textChannels.isEmpty())
        {
            List<VoiceChannel> voiceChannels = FinderUtil.findVoiceChannels(query, event.getGuild());

            if(voiceChannels.isEmpty())
            {
                event.replyWarning("I was not able to found a channel with the provided arguments: '"+query+"'");
                return null;
            }
            else if(voiceChannels.size()>1)
            {
                event.replyWarning(FormatUtil.listOfVcChannels(voiceChannels, query));
                return null;
            }
            else
                return voiceChannels.get(0);
        }
        else if(textChannels.size()>1)
        {
            event.replyWarning(FormatUtil.listOfTcChannels(textChannels, query));
            return null;
        }
        else
            return textChannels.get(0);
    }

    private String[] splitArgs(String preArgs, String separator)
    {
        try
        {
            String[] args = preArgs.split(separator, 2);
            return new String[]{args[0], args[1]};
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return new String[]{preArgs, ""};
        }
    }

    private String[] splitArgsWithTime(String preArgs)
    {
        try
        {
            String[] args = preArgs.split(" \\| ", 2);
            return new String[]{args[0], String.valueOf(ArgsUtils.parseTime(args[1]))};
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return new String[]{preArgs, ""};
        }
    }
}

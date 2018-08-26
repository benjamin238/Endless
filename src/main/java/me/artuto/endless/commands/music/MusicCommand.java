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

package me.artuto.endless.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.music.AudioPlayerSendHandler;
import me.artuto.endless.utils.ChecksUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.PermissionException;

/**
 * @author Artuto
 */

public abstract class MusicCommand extends EndlessCommand
{
    protected final Bot bot;
    protected boolean listening;
    protected boolean playing;

    public MusicCommand(Bot bot)
    {
        this.bot = bot;
        this.category = Categories.MUSIC;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        Guild guild = event.getGuild();
        GuildSettings gs = event.getClient().getSettingsFor(guild);
        GuildVoiceState state = event.getMember().getVoiceState();
        Message msg = event.getMessage();
        TextChannel currentTc = event.getTextChannel();
        TextChannel musicTc = guild.getTextChannelById(gs.getTextChannelMusic());
        VoiceChannel currentVc = event.getSelfMember().getVoiceState().getChannel();

        if(!(musicTc==null) && !(currentTc.equals(musicTc)))
        {
            try{msg.delete().queue();}
            catch(PermissionException ignored){}
            event.replyInDm(event.getClient().getError()+" You can only use this command in "+musicTc.getAsMention());
            return;
        }
        if(playing && (guild.getAudioManager().getSendingHandler()==null || !(((AudioPlayerSendHandler)guild.getAudioManager().getSendingHandler()).isMusicPlaying())))
        {
            event.replyError("There must be music playing to use that command!");
            return;
        }
        if(listening)
        {
            if(currentVc==null)
                currentVc = guild.getVoiceChannelById(gs.getVoiceChannelMusic());
            if(!(state.inVoiceChannel()) || state.isDeafened() || (!(currentVc==null) && !(currentVc.equals(state.getChannel()))))
            {
                event.replyError("You need to be listening in "+(currentVc==null?"a voice channel":"**"+currentVc.getName()+"**")+" to use that!");
                return;
            }
            if(!(event.getSelfMember().getVoiceState().inVoiceChannel()))
            {
                try{guild.getAudioManager().openAudioConnection(state.getChannel());}
                catch(PermissionException ignored)
                {
                    event.replyError("I can't connect to **"+state.getChannel().getName()+"**! (Permissions?)");
                    return;
                }
            }
        }

        executeMusicCommand(event);
    }

    public abstract void executeMusicCommand(CommandEvent event);

    public static Category DJ = new Category("DJ", event -> {
       GuildSettings gs = event.getClient().getSettingsFor(event.getGuild());
       Member member = event.getMember();
       Role role = event.getGuild().getRoleById(gs.getDJRole());

       if(!(role==null) && member.getRoles().contains(role))
           return true;
       else if(ChecksUtil.hasPermission(member, null, Permission.MANAGE_SERVER))
           return true;
       else
           return event.isOwner();
    });

    protected boolean isDJ(CommandEvent event)
    {
        GuildSettings gs = event.getClient().getSettingsFor(event.getGuild());
        Member member = event.getMember();
        Role role = event.getGuild().getRoleById(gs.getDJRole());

        if(!(role==null) && member.getRoles().contains(role))
            return true;
        else if(ChecksUtil.hasPermission(member, null, Permission.MANAGE_SERVER))
            return true;
        else
            return event.isOwner();
    }
}

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
import me.artuto.endless.music.AudioPlayerSendHandler;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Artuto
 */

public class SkipCmd extends MusicCommand
{
    public SkipCmd(Bot bot)
    {
        super(bot);
        this.name = "skip";
        this.aliases = new String[]{"voteskip"};
        this.help = "Votes to skip the current playing song.";
        this.needsArguments = false;
        this.listening = true;
        this.playing = true;
    }

    @Override
    public void executeMusicCommand(CommandEvent event)
    {
        AudioPlayerSendHandler handler = (AudioPlayerSendHandler)event.getGuild().getAudioManager().getSendingHandler();
        User author = event.getAuthor();
        if(handler.getRequester()==author.getIdLong())
        {
            event.replySuccess("Successfully skipped **"+handler.getPlayer().getPlayingTrack().getInfo().title+"**");
            handler.getPlayer().stopTrack();
        }
        else
        {
            long people = event.getSelfMember().getVoiceState().getChannel().getMembers().stream().filter(m ->
                    !(m.getUser().isBot()) && !(m.getVoiceState().isDeafened())).count();
            String msg;
            if(handler.getVotes().contains(author.getIdLong()))
                msg = "You already voted to skip this song, "+author.getAsMention()+" `[";
            else
            {
                msg = "You voted to skip the current song. `[";
                handler.getVotes().add(author.getIdLong());
            }
            long skippers = event.getSelfMember().getVoiceState().getChannel().getMembers().stream().filter(m ->
                    handler.getVotes().contains(m.getUser().getIdLong())).count();
            long requiredSkippers = (long)Math.ceil(people*.55);
            msg += skippers+" votes of "+requiredSkippers+"/"+people+" needed]`";
            if(skippers>=requiredSkippers)
            {
                User requester = bot.shardManager.getUserById(handler.getRequester());
                msg += "\n"+event.getClient().getSuccess()+" Successfully skipped **"+handler.getPlayer().getPlayingTrack().getInfo().title+"**"+
                        (requester==null?"":" (Requested by **"+requester.getName()+"**#**"+requester.getDiscriminator()+"**)");
                handler.getPlayer().stopTrack();
            }
            event.reply(msg);
        }
    }
}

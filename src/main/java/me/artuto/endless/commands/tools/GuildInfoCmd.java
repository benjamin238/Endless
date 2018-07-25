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

package me.artuto.endless.commands.tools;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Const;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.MiscUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.time.format.DateTimeFormatter;

/**
 * @author Artuto
 */

public class GuildInfoCmd extends EndlessCommand
{
    public GuildInfoCmd()
    {
        this.name = "guild";
        this.aliases = new String[]{"server", "serverinfo", "guildinfo", "ginfo", "sinfo"};
        this.help = "Shows info about the current guild";
        this.category = Categories.TOOLS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        EmbedBuilder builder = new EmbedBuilder();
        MessageBuilder mb = new MessageBuilder();
        StringBuilder sb = new StringBuilder();

        Guild guild = event.getGuild();
        int memberCount = (int)guild.getMemberCache().size();
        int botCount = (int)guild.getMemberCache().stream().filter(m -> m.getUser().isBot()).count();
        int onlineCount = (int)guild.getMemberCache().stream().filter(m -> m.getOnlineStatus()==OnlineStatus.ONLINE).count();
        int idleCount = (int)guild.getMemberCache().stream().filter(m -> m.getOnlineStatus()==OnlineStatus.IDLE).count();
        int dndCount = (int)guild.getMemberCache().stream().filter(m -> m.getOnlineStatus()==OnlineStatus.DO_NOT_DISTURB).count();
        int offlineCount = (int)guild.getMemberCache().stream().filter(m -> m.getOnlineStatus()==OnlineStatus.OFFLINE).count();
        User owner = guild.getOwner().getUser();

        sb.append(Const.LINE_START).append(" ID: **").append(guild.getId()).append("**\n");
        sb.append(Const.LINE_START).append(" Owner: **").append(owner.getName()).append("#").append(owner.getDiscriminator()).append("**\n");
        sb.append(Const.LINE_START).append(" Voice Region: **").append(guild.getRegion().getName()).append(" ")
                .append(guild.getRegion().getEmoji()).append("**\n");
        sb.append(Const.LINE_START).append(" Creation: **").append(guild.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("**\n");
        sb.append(Const.LINE_START).append(" Members: ").append(Const.ONLINE).append(" **").append(onlineCount).append("** - ")
                .append(Const.IDLE).append(" **").append(idleCount).append("** - ").append(Const.DND).append(" **").append(dndCount)
                .append("** - ").append(Const.OFFLINE).append(" **").append(offlineCount).append("** (**").append(memberCount).append("**, ")
                .append(Const.BOT).append(" **").append(botCount).append("**)\n");
        sb.append(Const.LINE_START).append(" Channels: Text: **").append(guild.getTextChannelCache().size()).append("** - Voice: **")
                .append(guild.getVoiceChannelCache().size()).append("**\n");
        sb.append(Const.LINE_START).append(" Verification Level: **").append(getVerificationLevel(guild.getVerificationLevel().getKey())).append("**\n");
        sb.append(Const.LINE_START).append(" Explicit Content Level: **").append(guild.getExplicitContentLevel().getDescription()).append("**");

        if(!(guild.getSplashId()==null))
        {
            sb.append("\n_ _\n").append(Const.PARTNER).append(" **Discord Partner** ").append(Const.PARTNER);
            builder.setImage(MiscUtils.getImageUrl("png", "2048", guild.getSplashUrl()));
        }

        builder.setColor(guild.getMember(owner).getColor()).setDescription(sb)
                .setThumbnail(MiscUtils.getImageUrl("png", null, guild.getIconUrl()));
        boolean verified = guild.getFeatures().contains("VERIFIED");
        String title = ":computer: Info about **"+guild.getName()+"** "+(verified?Const.VERIFIED:"");
        event.reply(mb.setContent(title).setEmbed(builder.build()).build());
    }

    private String getVerificationLevel(int level)
    {
        switch(level)
        {
            case 0:
                return "None";
            case 1:
                return "Low";
            case 2:
                return "Medium";
            case 3:
                return "(╯°□°）╯︵ ┻━┻";
            case 4:
                return "┻━┻ ﾐヽ(ಠ益ಠ)ノ彡┻━┻";
            default:
                return "Unknown";
        }
    }
}

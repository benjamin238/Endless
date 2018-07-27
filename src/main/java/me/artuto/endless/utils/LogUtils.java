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

package me.artuto.endless.utils;

import me.artuto.endless.Action;
import me.artuto.endless.Endless;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.user.update.UserUpdateAvatarEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Objects;

/**
 * @author Artuto
 */

public class LogUtils
{
    public static File createMessagesTextFile(List<Message> msgs, String name)
    {
        StringBuilder content = new StringBuilder();
        msgs.forEach(m -> {
            User a = m.getAuthor();
            content.append(a.getName()).append("#").append(a.getDiscriminator()).append(": ").append(m.getContentRaw()).append("\n");
            if(!(m.getAttachments().isEmpty()))
            {
                content.append("\nAttachments:");
                m.getAttachments().forEach(att -> content.append("\n").append(att.getFileName()).append(": ").append(att.getUrl()));
            }
            content.append("\n");
        });

        Writer output = null;
        try
        {
            output = new BufferedWriter(new FileWriter(name, true));
            output.append(content).close();
            return new File(name);
        }
        catch(IOException e)
        {
            Endless.getLog(LogUtils.class).error("Could not create messages file: ", e);
            return null;
        }
    }

    public static boolean isActionIgnored(Action action, TextChannel modlog)
    {
        return !(modlog.getTopic()==null) && modlog.getTopic().toLowerCase().contains("{-ex:"+action.getInternalAction()+"}");
    }

    public static boolean isIssuerIgnored(long id, TextChannel channel)
    {
        return !(channel.getTopic()==null) && channel.getTopic().toLowerCase().contains("{ignore:"+id+"}");
    }

    public static boolean isTargetIgnored(long id, TextChannel channel)
    {
        return !(channel.getTopic()==null) && channel.getTopic().toLowerCase().contains("{ignore:"+id+"}");
    }

    public static boolean isTypeIgnored(String type, TextChannel modlog)
    {
        return !(modlog.getTopic()==null) && modlog.getTopic().toLowerCase().contains("{-ex:"+type+"}");
    }

    public static File getAvatarUpdateImage(UserUpdateAvatarEvent event)
    {
        String newA = event.getNewAvatarUrl()==null?event.getUser().getDefaultAvatarUrl():event.getUser().getEffectiveAvatarUrl();
        String oldA = event.getOldAvatarUrl()==null?event.getUser().getDefaultAvatarUrl():event.getOldAvatarUrl();

        try
        {
            BufferedImage img1 = ImageIO.read(Objects.requireNonNull(MiscUtils.getInputStream(oldA)));
            BufferedImage img2 = ImageIO.read(Objects.requireNonNull(MiscUtils.getInputStream(newA)));
            BufferedImage combo = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = combo.createGraphics();
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, 256, 128);
            g2.drawImage(img1, 0, 0, 128, 128, null);
            g2.drawImage(img2, 128, 0, 128, 128, null);

            File f = new File("avatarchange"+event.getUser().getId()+".png");
            ImageIO.write(combo, "png", f);
            return f;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static class UploadedText
    {
        private final String id, channel;

        public UploadedText(String id, String channel)
        {
            this.id = id;
            this.channel = channel;
        }

        public String getCDNUrl()
        {
            return "https://cdn.discordapp.com/attachments/470068055322525708/"+id+"/Messages"+channel+".txt";
        }

        public String getViewUrl()
        {
            return "http://txt.discord.website/?txt=470068055322525708/"+id+"/Messages"+channel;
        }
    }
}

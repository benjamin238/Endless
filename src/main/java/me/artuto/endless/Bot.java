/*
 * Copyright (C) 2017 Artu
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

package me.artuto.endless;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import me.artuto.endless.data.Settings;
import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Artu
 */

public class Bot extends ListenerAdapter
{
    private final HashMap<String,Settings> settings;
    private final Config config;
    private final EventWaiter waiter;
    private JDA jda;
    
    public Bot(EventWaiter waiter, Config config)
    {
        this.config = config;
        this.waiter = waiter;
        this.settings = new HashMap<>();

        try
        {
            JSONObject loadedSettings = new JSONObject(new String(Files.readAllBytes(Paths.get("data/serversettings.json"))));
            loadedSettings.keySet().forEach((id) -> {
                JSONObject o = loadedSettings.getJSONObject(id);
                
                settings.put(id, new Settings(
                        o.has("modlog_channel_id") ? o.getString("modlog_channel_id") : null,
                        o.has("serverlog_channel_id")? o.getString("serverlog_channel_id"): null));});
        }
        catch(IOException | JSONException e)
        {
            SimpleLog.getLog("Settings").warn("Failed to load server settings: "+e);
        }
    }

    public EventWaiter getWaiter()
    {
        return waiter;
    }
    
    public Settings getSettings(Guild guild)
    {
        return settings.getOrDefault(guild.getId(), Settings.DEFAULT_SETTINGS);
    }
    
    public void setModLogChannel(TextChannel channel)
    {
        Settings s = settings.get(channel.getGuild().getId());

        if(s==null)
        {
            settings.put(channel.getGuild().getId(), new Settings(channel.getId(),null));
        }
        else
        {
            s.setModLogId(channel.getIdLong());
        }

        writeSettings();
    }
    
    public void setServerLogChannel(TextChannel channel)
    {
        Settings s = settings.get(channel.getGuild().getId());

        if(s==null)
        {
            settings.put(channel.getGuild().getId(), new Settings(null, channel.getId()));
        }
        else
        {
            s.setServerLogId(channel.getIdLong());
        }

        writeSettings();
    }

    public void clearModLogChannel(Guild guild)
    {
        Settings s = getSettings(guild);
        if(s!=Settings.DEFAULT_SETTINGS)
        {
            if(s.getServerLogId()==0)
                settings.remove(guild.getId());
            else
                s.setModLogId(0);
            writeSettings();
        }
    }

    public void clearServerLogChannel(Guild guild)
    {
        Settings s = getSettings(guild);
        if(s!=Settings.DEFAULT_SETTINGS)
        {
            if(s.getModLogId()==0)
                settings.remove(guild.getId());
            else
                s.setServerLogId(0);
            writeSettings();
        }
    }
    
    private void writeSettings()
    {
        JSONObject obj = new JSONObject();
        settings.keySet().stream().forEach(key -> {
            JSONObject o = new JSONObject();
            Settings s = settings.get(key);
            if(s.getModLogId()!=0)
                o.put("modlog_channel_id", Long.toString(s.getModLogId()));
            if(s.getServerLogId()!=0)
                o.put("serverlog_channel_id", Long.toString(s.getServerLogId()));
            obj.put(key, o);});

        try
        {
            Files.write(Paths.get("data/serversettings.json"), obj.toString(4).getBytes());
        }
        catch(IOException ex)
        {
            SimpleLog.getLog("Settings").warn("Failed to write to file: "+ex);
        }
    }

    
    @Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        Guild guild;
        guild = event.getGuild();
        
        User owner;
        owner = event.getJDA().getUserById(Config.getOwnerId());
        
        String leavemsg;
        leavemsg = "Hi! Sorry, but you can't have a copy of Endless on Discord Bots, this is for my own security.\n"
                    + "Please remove this Account from the Discord Bots list or I'll take further actions.\n"
                    + "If you think this is an error, please contact the Developer. ~Artuto";
        
        String warnmsg;
        warnmsg = "<@264499432538505217>, **"+owner.getName()+"#"+owner.getDiscriminator()+"** has a copy of Endless here!";
        
        if(event.getGuild().getId().equals("110373943822540800"))
        {
            event.getJDA().getTextChannelById("119222314964353025").sendMessage(warnmsg).complete();
            owner.openPrivateChannel().queue(s -> s.sendMessage(leavemsg).queue(null, (e) -> SimpleLog.getLog("DISCORD BANS").fatal(leavemsg)));
            guild.leave().complete();
        }
    }
    
    @Override
    public void onReady(ReadyEvent event)
    {
        File logs = new File("logs");
        if(!logs.exists())
        {
            logs.mkdir();
            SimpleLog.getLog("Startup").info("'logs' directory created!");
        }
        
        File data = new File("data");
        if(!data.exists())
        {
            data.mkdir();
            SimpleLog.getLog("Startup").info("'data' directory created!");
        }
    }
}

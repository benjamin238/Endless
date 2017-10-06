/*
 * Copyright (C) 2017 Artuto
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

package me.artuto.endless.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import java.io.IOException;

import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.loader.Config;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.utils.SimpleLog;
import net.dv8tion.jda.core.entities.User;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * @author Artuto
 */

public class DBansCheck extends Command
{
    private final SimpleLog LOG = SimpleLog.getLog("Discord Bans");
    private static Config config;
    
    public DBansCheck()
    {
        this.name = "discordbans";
        this.help = "Checks if the specified user ID is registered on Discord Bans";
        this.arguments = "<User ID>";
        this.category = Categories.TOOLS;
        this.aliases = new String[]{"checkbans", "dbans"};
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
        this.cooldown = 10;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
        User user;

        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Please specify a user ID!");
            return;
        }

        try
        {
           user = event.getJDA().retrieveUserById(event.getArgs()).complete(); 
        }
        catch(Exception e)
        {
            event.replyError("That user was not found!");
            return;
        }
         
        if(config.getDBansToken().isEmpty())
        {
            event.replyError("This command has been disabled due a faulty parameter on the config file, ask the Owner to check the Console");
            LOG.warn("Someone triggered the Discord Bans Check command, but there's not a token in the config file. In order to stop this message add a token to the config file.");
            return;
        }
        
       try
       {
            OkHttpClient client = new OkHttpClient();
           
            RequestBody formBody = new FormBody.Builder()
                .add("token", config.getDBansToken())
                .add("userid", user.getId())
                .build();
            
            Request request = new Request.Builder()
                .url("https://bans.discordlist.net/api")
                .post(formBody)
                .build();
    
            Response response = client.newCall(request).execute();
            
            if(response.body().string().equalsIgnoreCase("True"))
            {
                event.reply("The user "+user.getName()+"#"+user.getDiscriminator()+" (`"+user.getId()+"`) is listed on Discord Bans! <:banhammer:270222913234272257>");
            }    
            else
            {
                event.reply("The user "+user.getName()+"#"+user.getDiscriminator()+" (`"+user.getId()+"`) isn't listed on Discord Bans! <:blobthumbsup:317004148564426758>");
            }
       }
       catch(IOException e)
       {
            event.replyError("An error was thrown when doing the check! Ask the Owner to check the Console.");
            LOG.fatal(e);
       }              
       
       
    }
       
}

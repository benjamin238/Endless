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

package me.artuto.endless.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import me.artuto.endless.cmddata.Categories;
import net.dv8tion.jda.core.Permission;

/**
 *
 * @author Artu
 */

public class Bash extends Command
{
    public Bash()
    {
        this.name = "bash";
        this.help = "Executes a bash command";
        this.category = Categories.BOTADM;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = true;
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        if(event.getArgs().isEmpty())
        {
            event.replyError("Cannot execute a empty command");
            return;
        }

        StringBuilder output = new StringBuilder();
        String finalOutput = null;
        try 
        {
            ProcessBuilder builder = new ProcessBuilder(event.getArgs().split(" "));
	    Process p = builder.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String runningLineOutput;
            while ((runningLineOutput = reader.readLine())!= null) {
                output.append(runningLineOutput).append("\n");
            }
            System.out.println(output.toString());

            if (output.toString().isEmpty()) {
                event.replySuccess("Done, with no output!");
                return;
            }

            // Remove linebreak
            finalOutput = output.substring(0, output.length() - 1);
	    reader.close();
        } 
        catch(IOException e) 
        {
            event.replyError("I wasn't able to find the command `" + event.getArgs() + "`!");
            return;
        } 
        catch(IllegalArgumentException e) 
        {
            event.replyError("Command output too long!");
        }
        catch(Exception e) 
        {
            e.printStackTrace();
            event.replyError("An unknown error occurred! Check the bot console.");
            return;
        }
                
        event.reply("Output: \n```\n"+finalOutput+" ```");
    }
}

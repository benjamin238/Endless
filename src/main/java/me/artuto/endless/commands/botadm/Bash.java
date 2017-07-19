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

package me.artuto.endless.commands.botadm;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
        this.category = new Command.Category("Bot Administration");
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {       
        if(!(event.isOwner()) || event.isCoOwner())
        {
            event.replyError("Sorry, but you don't have access to this command! Only Bot owners!");
            return;
        }
        if(event.getArgs().isEmpty())
        {
            event.replyError("Cannot execute a empty command");
            return;
        }
        
        Bash obj = new Bash();

	String output = obj.executeCommand(event.getArgs());
                
        event.reply("Output: \n```\n"+output+" ```");      
    }
    
    	private String executeCommand(String command) {

		StringBuilder output = new StringBuilder();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader =
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();
	}
}

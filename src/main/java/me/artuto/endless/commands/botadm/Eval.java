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
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

/**
 *
 * @author Artu
 */

public class Eval extends Command
{
    private ScriptEngine engine;
    
    public Eval()
    {
        this.name = "eval";
        this.help = "Executes Nashorn code";
        this.category = new Category("Bot Administration");
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
        
        engine = new ScriptEngineManager().getEngineByName("Nashorn");
        try
        {
            engine.eval("var imports = new JavaImporter("
                    + "java.io,"
                    + "java.lang,"
                    + "java.util,"
                    + "Packages.net.dv8tion.jda.core,"
                    + "Packages.net.dv8tion.jda.core.entities,"
                    + "Packages.net.dv8tion.jda.core.entities.impl,"
                    + "Packages.net.dv8tion.jda.core.managers,"
                    + "Packages.net.dv8tion.jda.core.managers.impl,"
                    + "Packages.net.dv8tion.jda.core.utils,"
                    + "Packages.me.artuto.endless,"
                    + "Packages.me.artuto.endless.commands.bot,"
                    + "Packages.me.artuto.endless.botadm,"
                    + "Packages.me.artuto.endless.moderation,"
                    + "Packages.me.artuto.endless.others,"
                    + "Packages.me.artuto.endless.tools,"
                    + "Packages.me.artuto.endless.loader,"
                    + "Packages.me.artuto.endless.utils,"
                    + "Packages.com.jagrosh.jdautilities,"
                    + "Packages.com.jagrosh.jdautilities.commandclient,"
                    + "Packages.com.jagrosh.jdautilities.commandclient.impl,"
                    + "Packages.com.jagrosh.jdautilities.entities,"
                    + "Packages.com.jagrosh.jdautilities.menu,"
                    + "Packages.com.jagrosh.jdautilities.utils,"
                    + "Packages.com.jagrosh.jdautilities.waiter);");
        }
        catch(ScriptException e)
        {
        }
    }
	
    @Override
    protected void execute(CommandEvent event) 
    {
        if(!(event.isOwner()) && !(event.isCoOwner()))
        {
            event.replyError("Sorry, but you don't have access to this command! Only Bot owners!");
            return;   
        }
        
        try
        {
            engine.put("event", event);
            engine.put("jda", event.getJDA());
            engine.put("channel", event.getChannel());
            engine.put("message", event.getMessage());
            if(event.isFromType(ChannelType.TEXT))
            {
                engine.put("member", event.getMember());
                engine.put("guild", event.getGuild());
            }
            
            Object out = engine.eval(
                    "(function() {"
                        + "with (imports) {"
                            + event.getArgs()
                        + "}"
                        + "})();");
            
            if(out==null)
            {
                event.reactSuccess();
            }
            else
            {
                event.replySuccess("Done! Output:\n```java\n"+out.toString()+" ```");
            }
        } 
        catch(Exception e2)
        {
            event.replyError("Error! Output:\n```java\n"+e2+" ```");
        }
    }
}

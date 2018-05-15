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

package me.artuto.endless.commands.botadm;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Artuto
 */

public class Eval extends EndlessCommand
{
    private ScriptEngine engine;
    private List<String> imports;
    private final Bot bot;

    public Eval(Bot bot)
    {
        this.bot = bot;
        this.name = "eval";
        this.help = "Executes Groovy code";
        this.category = Categories.BOTADM;
        this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = true;
        this.guildCommand = false;

        engine = new ScriptEngineManager().getEngineByName("Groovy");

        try
        {
            imports = Arrays.asList("com.jagrosh.jdautilities", "com.jagrosh.jdautilities.command", "com.jagrosh.jdautilities.command.impl", "com.jagrosh.jdautilities.entities", "com.jagrosh.jdautilities.menu", "com.jagrosh.jdautilities.utils", "com.jagrosh.jdautilities.waiter", "java.awt", "java.io", "java.lang", "java.util", "java.util.stream", "me.artuto.endless", "me.artuto.endless.cmddata", "me.artuto.endless.commands", "me.artuto.endless.data", "me.artuto.endless.events", "me.artuto.endless.loader", "me.artuto.endless.logging", "me.artuto.endless.managers", "me.artuto.endless.tools", "me.artuto.endless.utils", "net.dv8tion.jda.bot", "net.dv8tion.jda.bot.entities", "net.dv8tion.jda.bot.entities.impl", "net.dv8tion.jda.core", "net.dv8tion.jda.core.entities", "net.dv8tion.jda.core.entities.impl", "net.dv8tion.jda.core.managers", "net.dv8tion.jda.core.managers.impl", "net.dv8tion.jda.core.utils", "net.dv8tion.jda.webhook");
        }
        catch(Exception ignored)
        {
        }
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        String importString = "";
        String eval;

        try
        {
            engine.put("event", event);
            engine.put("jda", event.getJDA());
            engine.put("channel", event.getChannel());
            engine.put("message", event.getMessage());
            engine.put("bot", event.getSelfUser());
            engine.put("client", event.getClient());
            engine.put("author", event.getAuthor());
            engine.put("endless", bot);
            if(event.isFromType(ChannelType.TEXT))
            {
                engine.put("member", event.getMember());
                engine.put("guild", event.getGuild());
                engine.put("tc", event.getTextChannel());
                engine.put("selfmember", event.getGuild().getSelfMember());
            }

            for(final String s : imports)
                importString += "import "+s+".*;";

            eval = event.getArgs().replaceAll("getToken", "getSelfUser");
            Object out = engine.eval(importString+eval);

            if(out == null || String.valueOf(out).isEmpty()) event.reactSuccess();
            else
                event.replySuccess("Done! Output:\n```"+out.toString().replaceAll(event.getJDA().getToken(), "Nice try.")+"```");
        }
        catch(ScriptException e2)
        {
            event.replyError("Error! Output:\n```"+e2+" ```");
        }
    }
}

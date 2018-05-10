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

package me.artuto.endless.commands.fun;

import com.jagrosh.jagtag.Parser;
import com.jagrosh.jagtag.ParserBuilder;
import com.jagrosh.jagtag.libraries.*;
import com.jagrosh.jdautilities.command.Command;
import me.artuto.endless.commands.EndlessCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.TagDataManager;
import me.artuto.endless.tools.Variables;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

public class Tag extends EndlessCommand
{
    private final TagDataManager db;
    private final Parser parser;

    public Tag(TagDataManager db)
    {
        this.db = db;
        this.parser = new ParserBuilder().addMethods(Variables.getMethods()).addMethods(Arguments.getMethods()).addMethods(Functional.getMethods()).addMethods(Miscellaneous.getMethods()).addMethods(Strings.getMethods()).addMethods(Time.getMethods()).addMethods(com.jagrosh.jagtag.libraries.Variables.getMethods()).setMaxOutput(2000).setMaxIterations(1000).build();
        this.name = "tag";
        this.aliases = new String[]{"t"};
        this.help = "Retrieves a tag with the specified name";
        this.helpBiConsumer = (event, command) ->
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Help for **"+command.getName()+"**:\n");

            for(Command c : command.getChildren())
                sb.append("`"+event.getClient().getPrefix()+c.getName()+" "+c.getArguments()+"` - "+c.getHelp()+"\n");

            event.replyInDm(sb.toString());
            event.reactSuccess();
        };
        this.arguments = "<name>";
        this.children = new Command[]{new Add(), new Delete(), new Edit(), new Import(), new Owner(), new Raw(), new Raw2(), new UnImport()};
        this.category = Categories.FUN;
        this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildCommand = false;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Specify a tag name!");
            return;
        }

        String[] args;
        String tagname;
        String tagargs;

        try
        {
            args = event.getArgs().split(" ", 2);
            tagname = args[0].toLowerCase().trim();
            tagargs = args[1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            tagname = event.getArgs().toLowerCase().trim();
            tagargs = "";
        }

        String tag = db.getTagContent(tagname);

        if(tag == null)
        {
            event.replyError("No tag found with that name!");
            return;
        }

        parser.clear().put("user", event.getAuthor()).put("guild", event.getGuild()).put("channel", event.getTextChannel()).put("args", tagargs);
        event.reply(parser.parse(tag));
    }

    private class Add extends EndlessCommand
    {
        public Add()
        {
            this.name = "add";
            this.aliases = new String[]{"create"};
            this.help = "Creates a new tag";
            this.arguments = "<name> <content>";
            this.category = Categories.FUN;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildCommand = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            String[] args;
            String name;
            String content;

            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Please specify a tag name and content!");
                return;
            }

            try
            {
                args = event.getArgs().split(" ", 2);
                name = args[0].trim().toLowerCase();
                content = args[1].trim();
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                event.replyWarning("Please specify a tag name and content!");
                return;
            }

            String tag = db.getTagContent(name);

            if(tag == null)
            {
                db.addTag(name, content, event.getAuthor().getIdLong());
                event.replySuccess("Tag `"+name+"` was created successfully!");
            }
            else event.replyError("A tag already exists with that name!");
        }
    }

    private class Delete extends EndlessCommand
    {
        public Delete()
        {
            this.name = "delete";
            this.aliases = new String[]{"remove"};
            this.help = "Removes a existant tag";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildCommand = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a tag name!");
                return;
            }

            String tag = db.getTagContent(event.getArgs().trim().toLowerCase());
            Long owner = db.getTagOwner(event.getArgs().trim().toLowerCase());

            if(tag == null) event.replyError("No tag found with that name!");
            else
            {
                if(owner.equals(event.getAuthor().getIdLong()) || event.isOwner())
                {
                    db.removeTag(event.getArgs().trim().toLowerCase());
                    event.replySuccess("Tag successfully deleted");
                }
                else event.replyError("You aren't the owner of the tag!");
            }
        }
    }

    private class Edit extends EndlessCommand
    {
        public Edit()
        {
            this.name = "edit";
            this.help = "Edits an existant tag";
            this.arguments = "<name> <new content>";
            this.category = Categories.FUN;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildCommand = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            String[] args;
            String name;
            String content;

            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Please specify a tag name and content!");
                return;
            }

            try
            {
                args = event.getArgs().split(" ", 2);
                name = args[0].trim().toLowerCase();
                content = args[1].trim();
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                event.replyWarning("Please specify a tag name and content!");
                return;
            }

            String tag = db.getTagContent(name);
            Long owner = db.getTagOwner(name);

            if(tag == null) event.replyError("No tag found with that name!");
            else
            {
                if(owner.equals(event.getAuthor().getIdLong()) || event.isOwner())
                {
                    db.editTag(name, content);
                    event.replySuccess("Tag successfully edited!");
                }
                else event.replyError("You aren't the owner of the tag!");
            }
        }
    }

    private class Owner extends EndlessCommand
    {
        public Owner()
        {
            this.name = "owner";
            this.help = "Gets the owner of a existant tag";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildCommand = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a tag name!");
                return;
            }

            String tag = db.getTagContent(event.getArgs().trim().toLowerCase());
            User owner = event.getJDA().retrieveUserById(db.getTagOwner(event.getArgs())).complete();

            if(tag == null) event.replyError("No tag found with that name!");
            else
                event.reply("The owner of the tag `"+event.getArgs().trim().toLowerCase()+"` is **"+owner.getName()+"#"+owner.getDiscriminator()+"** (ID: **"+owner.getId()+"**)");
        }
    }

    private class Import extends EndlessCommand
    {
        public Import()
        {
            this.name = "import";
            this.help = "Imports a tag";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildCommand = true;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a tag name!");
                return;
            }

            String tag = db.getTagContent(event.getArgs().trim().toLowerCase());

            if(tag == null) event.replyError("No tag found with that name!");
            else
            {
                if(db.isTagImported(event.getArgs().trim().toLowerCase(), event.getGuild().getIdLong()))
                    event.replyError("This tag is already imported!");
                else
                {
                    db.importTag(event.getArgs().trim().toLowerCase(), tag, db.getTagOwner(event.getArgs().trim().toLowerCase()), event.getGuild().getIdLong());
                    event.replySuccess("Successfully imported tag!");
                }
            }
        }
    }

    private class Raw extends EndlessCommand
    {
        public Raw()
        {
            this.name = "raw";
            this.help = "Shows the content of a tag without parsing the args";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildCommand = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a tag name!");
                return;
            }

            String tag = db.getTagContent(event.getArgs().trim().toLowerCase());

            if(tag == null) event.replyError("No tag found with that name!");
            else event.reply(tag);
        }
    }

    private class Raw2 extends EndlessCommand
    {
        public Raw2()
        {
            this.name = "raw2";
            this.help = "Shows the content of a tag without parsing the args on a codeblock";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildCommand = false;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a tag name!");
                return;
            }

            String tag = db.getTagContent(event.getArgs().trim().toLowerCase());

            if(tag == null) event.replyError("No tag found with that name!");
            else event.reply("```"+tag+"```");
        }
    }

    private class UnImport extends EndlessCommand
    {
        public UnImport()
        {
            this.name = "unimport";
            this.help = "Unimports a tag";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.botPerms = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildCommand = true;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a tag name!");
                return;
            }

            String tag = db.getTagContent(event.getArgs().trim().toLowerCase());

            if(tag == null) event.replyError("No tag found with that name!");
            else
            {
                if(!(db.isTagImported(event.getArgs().trim().toLowerCase(), event.getGuild().getIdLong())))
                    event.replyError("This tag isn't imported!");
                else
                {
                    db.unImportTag(event.getArgs().trim().toLowerCase(), event.getGuild().getIdLong());
                    event.replySuccess("Successfully unimported tag!");
                }
            }
        }
    }
}

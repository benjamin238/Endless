package me.artuto.endless.commands.fun;

import com.jagrosh.jagtag.Parser;
import com.jagrosh.jagtag.ParserBuilder;
import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.TagDataManager;
import me.artuto.endless.tools.Variables;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

public class Tag extends Command
{
    private final TagDataManager db;
    private final Parser parser;

    public Tag(TagDataManager db)
    {
        this.db = db;
        this.parser = new ParserBuilder()
                .addMethods(Variables.getMethods())
                .setMaxOutput(2000)
                .setMaxIterations(1000)
                .build();
        this.name = "tag";
        this.aliases = new String[]{"t"};
        this.help = "Retrieves a tag with the specified name";
        this.helpBiConsumer = (event, command) -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Help for **"+command.getName()+"**:\n");

            for(Command c : command.getChildren())
                sb.append("`"+event.getClient().getPrefix()+c.getName()+" "+c.getArguments()+"` - "+c.getHelp()+"\n");

            event.replyInDM(sb.toString());
            event.reactSuccess();
        };
        this.arguments = "<name>";
        this.children = new Command[]{new Add(), new Delete(), new Edit(), new Import(), new Owner(), new Raw(), new Raw2(), new UnImport()};
        this.category = Categories.FUN;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        if(event.getArgs().isEmpty())
        {
            event.replyWarning("Specify a tag name!");
            return;
        }

        String tag = db.getTagContent(event.getArgs().trim().toLowerCase());

        if(tag==null)
        {
            event.replyError("No tag found with that name!");
            return;
        }

        parser.clear().put("user", event.getAuthor()).put("guild", event.getGuild()).put("channel", event.getTextChannel());

        event.reply(parser.parse(tag));
    }

    private class Add extends Command
    {
        public Add()
        {
            this.name = "add";
            this.aliases = new String[]{"create"};
            this.help = "Creates a new tag";
            this.arguments = "<name> <content>";
            this.category = Categories.FUN;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
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

            if(tag==null)
            {
                db.addTag(name, content, event.getAuthor().getIdLong());
                event.replySuccess("Tag `"+name+"` was created successfully!");
            }
            else
                event.replyError("A tag already exists with that name!");
        }
    }

    private class Delete extends Command
    {
        public Delete()
        {
            this.name = "delete";
            this.aliases = new String[]{"remove"};
            this.help = "Removes a existant tag";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a tag name!");
                return;
            }

            String tag = db.getTagContent(event.getArgs().trim().toLowerCase());
            Long owner = db.getTagOwner(event.getArgs().trim().toLowerCase());

            if(tag==null)
                event.replyError("No tag found with that name!");
            else
            {
                if(owner.equals(event.getAuthor().getIdLong()) || event.isOwner() || event.isCoOwner())
                {
                    db.removeTag(event.getArgs().trim().toLowerCase());
                    event.replySuccess("Tag successfully deleted");
                }
                else
                    event.replyError("You aren't the owner of the tag!");
            }
        }
    }

    private class Edit extends Command
    {
        public Edit()
        {
            this.name = "edit";
            this.help = "Edits an existant tag";
            this.arguments = "<name> <new content>";
            this.category = Categories.FUN;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
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

            if(tag==null)
                event.replyError("No tag found with that name!");
            else
            {
                if(owner.equals(event.getAuthor().getIdLong()) || event.isOwner() || (event.isCoOwner()))
                {
                    db.editTag(name, content);
                    event.replySuccess("Tag successfully edited!");
                }
                else
                    event.replyError("You aren't the owner of the tag!");
            }
        }
    }

    private class Owner extends Command
    {
        public Owner()
        {
            this.name = "owner";
            this.help = "Gets the owner of a existant tag";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.ownerCommand = false;
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a tag name!");
                return;
            }

            String tag = db.getTagContent(event.getArgs().trim().toLowerCase());
            User owner = event.getJDA().retrieveUserById(db.getTagOwner(event.getArgs())).complete();

            if(tag==null)
                event.replyError("No tag found with that name!");
            else
                event.reply("The owner of the tag `"+event.getArgs().trim().toLowerCase()+"` is **"+owner.getName()+"#"+owner.getDiscriminator()+"** (ID: **"+owner.getId()+"**)");
        }
    }

    private class Import extends Command
    {
        public Import()
        {
            this.name = "import";
            this.help = "Imports a tag";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a tag name!");
                return;
            }

            String tag = db.getTagContent(event.getArgs().trim().toLowerCase());

            if(tag==null)
                event.replyError("No tag found with that name!");
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

    private class Raw extends Command
    {
        public Raw()
        {
            this.name = "raw";
            this.help = "Shows the content of a tag without parsing the args";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a tag name!");
                return;
            }

            String tag = db.getTagContent(event.getArgs().trim().toLowerCase());

            if(tag==null)
                event.replyError("No tag found with that name!");
            else
                event.reply(tag);
        }
    }

    private class Raw2 extends Command
    {
        public Raw2()
        {
            this.name = "raw2";
            this.help = "Shows the content of a tag without parsing the args on a codeblock";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a tag name!");
                return;
            }

            String tag = db.getTagContent(event.getArgs().trim().toLowerCase());

            if(tag==null)
                event.replyError("No tag found with that name!");
            else
                event.reply("```"+tag+"```");
        }
    }

    private class UnImport extends Command
    {
        public UnImport()
        {
            this.name = "unimport";
            this.help = "Unimports a tag";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
            this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
            this.ownerCommand = false;
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyWarning("Specify a tag name!");
                return;
            }

            String tag = db.getTagContent(event.getArgs().trim().toLowerCase());

            if(tag==null)
                event.replyError("No tag found with that name!");
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

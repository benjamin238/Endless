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
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.core.entities.LocalTag;
import me.artuto.endless.core.entities.Tag;
import me.artuto.endless.utils.FormatUtil;
import me.artuto.endless.utils.TagUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class TagCmd extends EndlessCommand
{
    private final Bot bot;
    private final Parser parser;

    public TagCmd(Bot bot)
    {
        this.bot = bot;
        this.parser = TagUtil.parser;
        this.name = "tag";
        this.aliases = new String[]{"t"};
        this.help = "Retrieves a tag with the specified name";
        this.arguments = "<name>";
        this.children = new Command[]{new CreateGlobalCmd(), new CreateLocalCmd(), new DeleteCmd(), new EditCmd(), new ExecCmd(),
                new ImportCmd(), new ListCmd(), new OverrideCmd(), new OwnerCmd(), new RawCmd(), new Raw2Cmd(), new UnImportCmd()};
        this.category = Categories.FUN;
        this.guildOnly = false;
        this.needsArgumentsMessage = "Specify a tag name!";
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        if(!(bot.dataEnabled))
        {
            event.replyError("Endless is running on No-data mode.");
            return;
        }

        String[] args = splitArgs(event.getArgs());
        String tagName;
        String tagArgs;

        try
        {
            tagName = args[0].trim().toLowerCase();
            tagArgs = args[1];
        }
        catch(IndexOutOfBoundsException e)
        {
            tagName = event.getArgs().trim().toLowerCase();
            tagArgs = "";
        }

        Tag tag;
        if(event.isFromType(ChannelType.TEXT))
        {
            tag = bot.endless.getLocalTag(event.getGuild().getIdLong(), tagName);
            if(tag==null)
            {
                tag = bot.endless.getGlobalTag(tagName);
                if(tag==null)
                {
                    event.replyError("Tag \""+tagName+"\" not found.");
                    return;
                }
            }
        }
        else
        {
            tag = bot.endless.getGlobalTag(tagName);
            if(tag==null)
            {
                event.replyError("Tag \""+tagName+"\" not found.");
                return;
            }
        }

        if(tag.isOverriden() && tag.getContent().isEmpty())
        {
            event.replyError("This tag has been deleted by a Server Moderator!");
            return;
        }
        if(tag.isNSFW() && !(TagUtil.isNSFWAllowed(event)))
        {
            event.replyError("This tag has been marked as NSFW! To use this tag mark this channel as NSFW or try in DMs.");
            return;
        }

        EmbedBuilder tagEmbed = new EmbedBuilder();
        parser.clear().put("user", event.getAuthor()).put("guild", event.getGuild()).put("channel", event.getChannel())
                .put("args", tagArgs).put("builder", tagEmbed);
        String parsed = parser.parse(tag.getContent());
        if(!(tagEmbed.isEmpty()))
            event.reply(new MessageBuilder().setContent(parsed).setEmbed(tagEmbed.build()).build());
        else
            event.reply(parsed);
    }

    private class CreateGlobalCmd extends EndlessCommand
    {
        CreateGlobalCmd()
        {
            this.name = "createglobal";
            this.help = "Creates a new global tag";
            this.arguments = "<name> <content>";
            this.category = Categories.FUN;
            this.guildOnly = false;
            this.parent = TagCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            String[] args = splitArgs(event.getArgs());
            String name;
            String content;

            try
            {
                name = args[0].trim().toLowerCase();
                content = args[1].trim();
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                event.replyWarning("Please specify a tag name and content!");
                return;
            }

            for(Message.Attachment att : event.getMessage().getAttachments())
                content += "\n"+att.getUrl();

            Tag tag = bot.endless.getGlobalTag(name);

            if(tag==null)
            {
                bot.tdm.createGlobalTag(event.getAuthor().getIdLong(), content, name);
                event.replySuccess("Tag \""+name+"\" was created successfully!");
            }
            else
                event.replyError("A tag already exists with that name!");
        }
    }

    private class CreateLocalCmd extends EndlessCommand
    {
        CreateLocalCmd()
        {
            this.name = "createlocal";
            this.aliases = new String[]{"create"};
            this.help = "Creates a new local tag";
            this.arguments = "<name> <content>";
            this.category = Categories.FUN;
            this.parent = TagCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            String[] args = splitArgs(event.getArgs());
            String name;
            String content;

            try
            {
                name = args[0].trim().toLowerCase();
                content = args[1].trim();
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                event.replyWarning("Please specify a tag name and content!");
                return;
            }

            for(Message.Attachment att : event.getMessage().getAttachments())
                content += "\n"+att.getUrl();

            Tag tag = bot.endless.getLocalTag(event.getGuild().getIdLong(), name);

            if(tag==null)
            {
                bot.tdm.createLocalTag(event.getGuild().getIdLong(), event.getAuthor().getIdLong(), content, name);
                event.replySuccess("Tag \""+name+"\" was created successfully!");
            }
            else
                event.replyError("A tag already exists with that name!");
        }
    }

    private class DeleteCmd extends EndlessCommand
    {
        DeleteCmd()
        {
            this.name = "delete";
            this.aliases = new String[]{"remove"};
            this.help = "Removes a existant tag";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Specify a tag name!";
            this.parent = TagCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            if(event.isFromType(ChannelType.TEXT))
            {
                Tag tag = bot.endless.getLocalTag(event.getGuild().getIdLong(), event.getArgs().trim().toLowerCase());

                if(tag == null)
                {
                    tag = bot.endless.getGlobalTag(event.getArgs().trim().toLowerCase());
                    if(tag==null)
                    {
                        event.replyError("No tag found with that name!");
                        return;
                    }
                }

                if(tag.getOwnerId()==event.getAuthor().getIdLong() || event.isOwner())
                {
                    if(tag.isGlobal())
                        bot.tdm.deleteGlobalTag(event.getArgs().trim().toLowerCase());
                    else
                        bot.tdm.deleteLocalTag(event.getGuild().getIdLong(), event.getArgs().trim().toLowerCase());
                    event.replySuccess("Tag successfully deleted");
                }
                else
                    event.replyError("You aren't the owner of the tag!");
            }
            else
            {
                Tag tag = bot.endless.getGlobalTag(event.getArgs().trim().toLowerCase());
                if(tag==null)
                {
                    event.replyError("No tag found with that name!");
                    return;
                }

                if(tag.getOwnerId()==event.getAuthor().getIdLong() || event.isOwner())
                {
                    bot.tdm.deleteGlobalTag(event.getArgs().trim().toLowerCase());
                    event.replySuccess("Tag successfully deleted");
                }
                else
                    event.replyError("You aren't the owner of the tag!");
            }
        }
    }

    private class EditCmd extends EndlessCommand
    {
        EditCmd()
        {
            this.name = "edit";
            this.help = "Edits an existant tag";
            this.arguments = "<name> <new content>";
            this.category = Categories.FUN;
            this.guildOnly = false;
            this.parent = TagCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            String[] args = splitArgs(event.getArgs());
            String name;
            String content;

            try
            {
                name = args[0].trim().toLowerCase();
                content = args[1].trim();
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                event.replyWarning("Please specify a tag name and content!");
                return;
            }

            if(event.isFromType(ChannelType.TEXT))
            {
                Tag tag = bot.endless.getLocalTag(event.getGuild().getIdLong(), name);
                if(tag==null)
                {
                    tag = bot.endless.getGlobalTag(name);
                    if(tag==null)
                    {
                        event.replyError("No tag found with that name!");
                        return;
                    }
                }

                if(tag.getOwnerId()==event.getAuthor().getIdLong() || event.isOwner())
                {
                    if(tag.isGlobal())
                        bot.tdm.updateGlobalTagContent(name, content);
                    else
                        bot.tdm.updateLocalTagContent(event.getGuild().getIdLong(), name, content);
                    event.replySuccess("Tag successfully edited!");
                }
                else
                    event.replyError("You aren't the owner of the tag!");
            }
            else
            {
                Tag tag = bot.endless.getGlobalTag(name);
                if(tag==null)
                {
                    event.replyError("No tag found with that name!");
                    return;
                }

                for(Message.Attachment att : event.getMessage().getAttachments())
                    content += "\n"+att.getUrl();

                if(tag.getOwnerId()==event.getAuthor().getIdLong() || event.isOwner())
                {
                    bot.tdm.updateGlobalTagContent(name, content);
                    event.replySuccess("Tag successfully edited!");
                }
                else
                    event.replyError("You aren't the owner of the tag!");
            }
        }
    }

    private class ExecCmd extends EndlessCommand
    {
        ExecCmd()
        {
            this.name = "exec";
            this.help = "Parses the specified content";
            this.aliases = new String[]{"execute", "test"};
            this.arguments = "<content>";
            this.category = Categories.FUN;
            this.guildOnly = false;
            this.parent = TagCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            EmbedBuilder tagEmbed = new EmbedBuilder();
            parser.clear().put("user", event.getAuthor()).put("guild", event.getGuild()).put("channel", event.getTextChannel())
                    .put("builder", tagEmbed);
            String parsed = parser.parse(event.getArgs());
            if(!(tagEmbed.isEmpty()))
                event.reply(new MessageBuilder().setContent(parsed).setEmbed(tagEmbed.build()).build());
            else
                event.reply(parsed);
        }
    }

    private class ImportCmd extends EndlessCommand
    {
        ImportCmd()
        {
            this.name = "import";
            this.help = "Imports a tag";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Specify a tag name!";
            this.parent = TagCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            if(event.isFromType(ChannelType.TEXT))
            {
                Tag tag = bot.endless.getLocalTag(event.getGuild().getIdLong(), event.getArgs().trim().toLowerCase());

                if(tag==null)
                {
                    tag = bot.endless.getGlobalTag(event.getArgs().trim().toLowerCase());
                    if(tag==null)
                    {
                        event.replyError("No tag found with that name!");
                        return;
                    }
                }

                if(bot.tdm.isImported(event.getGuild().getIdLong(), String.valueOf(tag.getId())))
                    event.replyError("This tag is already imported!");
                else
                {
                    bot.tdm.importTag(event.getGuild().getIdLong(), tag);
                    event.replySuccess("Successfully imported tag!");
                }
            }
        }
    }

    private class ListCmd extends EndlessCommand
    {
        ListCmd()
        {
            this.name = "list";
            this.help = "List of tags of the specified user";
            this.arguments = "[user]";
            this.category = Categories.FUN;
            this.guildOnly = false;
            this.needsArguments = false;
            this.parent = TagCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            User user;

            if(event.isFromType(ChannelType.TEXT))
            {
                if(event.getArgs().isEmpty())
                    user = event.getAuthor();
                else
                {
                    user = searchUser(event);
                    if(user==null)
                        return;
                }

                List<Tag> globalTags = bot.endless.getGlobalTags().stream().filter(t -> t.getOwnerId()==user.getIdLong()).collect(Collectors.toList());
                List<LocalTag> localTags = bot.endless.getLocalTags().stream().filter(t -> t.getOwnerId()==user.getIdLong() &&
                        t.getGuildId()==event.getGuild().getIdLong()).collect(Collectors.toList());

                if(!(globalTags.isEmpty()))
                {
                    sb.append("\n").append(event.getClient().getSuccess()).append(" **").append(globalTags.size()).append("** tags owned by **")
                            .append(user.getName()).append("**:\n");
                    globalTags.forEach(t -> sb.append(t.getName()).append(" "));
                }
                if(!(localTags.isEmpty()))
                {
                    sb.append("\n").append(event.getClient().getSuccess()).append(" **").append(localTags.size()).append("** tags in **")
                            .append(event.getGuild().getName()).append("**:\n");
                    localTags.forEach(t -> sb.append(t.getName()).append(" "));
                }

                if(sb.toString().isEmpty())
                {
                    if(user==event.getAuthor())
                        event.replyWarning("You don't own any tag!");
                    else
                        event.replyWarning("**"+user.getName()+"#"+user.getDiscriminator()+"** doesn't own any tag!");
                    return;
                }
                event.reply(sb.toString());
            }
            else
            {
                user = event.getAuthor();

                List<Tag> globalTags = bot.endless.getGlobalTags().stream().filter(t -> t.getOwnerId()==user.getIdLong()).collect(Collectors.toList());

                if(!(globalTags.isEmpty()))
                {
                    sb.append(event.getClient().getSuccess()).append(" **").append(globalTags.size()).append("** tags owned by **")
                            .append(user.getName()).append("**:\n");
                    globalTags.forEach(t -> sb.append(t.getName()).append(" "));
                }

                if(sb.toString().isEmpty())
                {
                    event.replyWarning("You don't own any tag!");
                    return;
                }
                event.reply(sb.toString());
            }
        }

        private User searchUser(CommandEvent event)
        {
            if(event.isFromType(ChannelType.TEXT))
            {
                java.util.List<Member> members = FinderUtil.findMembers(event.getArgs(), event.getGuild());

                if(members.isEmpty())
                {
                    java.util.List<User> users = FinderUtil.findUsers(event.getArgs(), event.getJDA());

                    if(users.isEmpty())
                    {
                        event.replyWarning("I was not able to found a user with the provided arguments: '"+event.getArgs()+"'");
                        return null;
                    }
                    else if(users.size()>1)
                    {
                        event.replyWarning(FormatUtil.listOfUsers(users, event.getArgs()));
                        return null;
                    }
                    else
                        return users.get(0);
                }
                else if(members.size()>1)
                {
                    event.replyWarning(FormatUtil.listOfMembers(members, event.getArgs()));
                    return null;
                }
                else
                    return members.get(0).getUser();
            }
            else
            {
                java.util.List<User> users = FinderUtil.findUsers(event.getArgs(), event.getJDA());

                if(users.isEmpty())
                {
                    event.replyWarning("I was not able to found a user with the provided arguments: '"+event.getArgs()+"'");
                    return null;
                }
                else if(users.size()>1)
                {
                    event.replyWarning(FormatUtil.listOfUsers(users, event.getArgs()));
                    return null;
                }
                else
                    return users.get(0);
            }
        }
    }

    private class OverrideCmd extends EndlessCommand
    {
        OverrideCmd()
        {
            this.name = "override";
            this.help = "Overrides a tag in the current server.";
            this.arguments = "<tag> [new content]";
            this.category = Categories.FUN;
            this.needsArgumentsMessage = "Specify a tag name!";
            this.parent = TagCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            String[] args = splitArgs(event.getArgs());
            String tagName;
            String tagContent;

            try
            {
                tagName = args[0].trim().toLowerCase();
                tagContent = args[1];
            }
            catch(IndexOutOfBoundsException e)
            {
                tagName = event.getArgs().trim().toLowerCase();
                tagContent = "";
            }

            Tag tag = bot.endless.getLocalTag(event.getGuild().getIdLong(), tagName);
            if(tag==null)
            {
                tag = bot.endless.getGlobalTag(tagName);
                if(tag==null)
                {
                    event.replyError("No tag found with that name!");
                    return;
                }
            }

            for(Message.Attachment att : event.getMessage().getAttachments())
                tagContent += "\n"+att.getUrl();

            if(tag.isOverriden())
            {
                bot.tdm.deleteLocalTag(event.getGuild().getIdLong(), tagName);
                event.replySuccess("Succesfully deleted local tag");
            }
            else
            {
                if(tag.isGlobal())
                {
                    if(tagContent.isEmpty())
                        bot.tdm.createLocalTag(true, event.getGuild().getIdLong(), event.getGuild().getIdLong(), "", tagName);
                    else
                        bot.tdm.createLocalTag(event.getGuild().getIdLong(), event.getGuild().getIdLong(), tagContent, tagName);
                    event.replySuccess("Successfully overriden global tag");
                }
                else
                {
                    if(tagContent.isEmpty())
                        bot.tdm.deleteLocalTag(event.getGuild().getIdLong(), tagName);
                    else
                    {
                        bot.tdm.deleteLocalTag(event.getGuild().getIdLong(), tagName);
                        bot.tdm.createLocalTag(event.getGuild().getIdLong(), event.getGuild().getIdLong(), tagContent, tagName);
                    }
                    event.replySuccess("Successfully overriden local tag");
                }
            }
        }
    }

    private class OwnerCmd extends EndlessCommand
    {
        OwnerCmd()
        {
            this.name = "owner";
            this.help = "Gets the owner of a existant tag";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Specify a tag name!";
            this.parent = TagCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            if(event.isFromType(ChannelType.TEXT))
            {
                Tag tag = bot.endless.getLocalTag(event.getGuild().getIdLong(), event.getArgs().trim().toLowerCase());
                if(tag==null)
                {
                    tag = bot.endless.getGlobalTag(event.getArgs().trim().toLowerCase());
                    if(tag==null)
                    {
                        event.replyError("No tag found with that name!");
                        return;
                    }
                }
                Tag fTag = tag;
                if(tag.getOwnerId()==event.getGuild().getIdLong())
                {
                    event.reply("The owner of the tag `"+tag.getName()+"` is the server *("+event.getGuild().getName()+")*");
                    return;
                }
                event.getJDA().retrieveUserById(tag.getOwnerId()).queue(user -> event.reply("The owner of the tag `"+fTag.getName()+
                        "` is **"+user.getName()+"#"+user.getDiscriminator()+
                        "** (ID: **"+user.getId()+"**)"), e -> event.reply("The owner of the tag `"+fTag.getName()+"` is **Unknown**."));
            }
            else
            {
                Tag tag = bot.endless.getGlobalTag(event.getArgs().trim().toLowerCase());
                if(tag==null)
                {
                    event.replyError("No tag found with that name!");
                    return;
                }
                event.getJDA().retrieveUserById(tag.getOwnerId()).queue(user -> event.reply("The owner of the tag `"+tag.getName()+
                        "` is **"+user.getName()+"#"+user.getDiscriminator()+
                        "** (ID: **"+user.getId()+"**)"), e -> event.reply("The owner of the tag `"+tag.getName()+"` is **Unknown**."));
            }
        }
    }

    private class RawCmd extends EndlessCommand
    {
        RawCmd()
        {
            this.name = "raw";
            this.help = "Shows the content of a tag without parsing the args";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Specify a tag name!";
            this.parent = TagCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            if(event.isFromType(ChannelType.TEXT))
            {
                Tag tag = bot.endless.getLocalTag(event.getGuild().getIdLong(), event.getArgs().trim().toLowerCase());

                if(tag==null)
                {
                    tag = bot.endless.getGlobalTag(event.getArgs().trim().toLowerCase());
                    if(tag==null)
                    {
                        event.replyError("No tag found with that name!");
                        return;
                    }
                }

                event.reply("```"+tag.getContent()+"```");
            }
            else
            {
                Tag tag = bot.endless.getGlobalTag(event.getArgs().trim().toLowerCase());

                if(tag==null)
                {
                    event.replyError("No tag found with that name!");
                    return;
                }

                event.reply("```"+tag.getContent()+"```");
            }
        }
    }

    private class Raw2Cmd extends EndlessCommand
    {
        Raw2Cmd()
        {
            this.name = "raw2";
            this.help = "Shows the content of a tag without parsing the args on a codeblock";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.guildOnly = false;
            this.needsArgumentsMessage = "Specify a tag name!";
            this.parent = TagCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            if(event.isFromType(ChannelType.TEXT))
            {
                Tag tag = bot.endless.getLocalTag(event.getGuild().getIdLong(), event.getArgs().trim().toLowerCase());

                if(tag==null)
                {
                    tag = bot.endless.getGlobalTag(event.getArgs().trim().toLowerCase());
                    if(tag==null)
                    {
                        event.replyError("No tag found with that name!");
                        return;
                    }
                }

                event.reply("```"+tag.getContent()+"```");
            }
            else
            {
                Tag tag = bot.endless.getGlobalTag(event.getArgs().trim().toLowerCase());

                if(tag==null)
                {
                    event.replyError("No tag found with that name!");
                    return;
                }

                event.reply("```"+tag.getContent()+"```");
            }
        }
    }

    private class UnImportCmd extends EndlessCommand
    {
        UnImportCmd()
        {
            this.name = "unimport";
            this.help = "Unimports a tag";
            this.arguments = "<name>";
            this.category = Categories.FUN;
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArgumentsMessage = "Specify a tag name!";
            this.parent = TagCmd.this;
        }

        @Override
        protected void executeCommand(CommandEvent event)
        {
            if(!(bot.dataEnabled))
            {
                event.replyError("Endless is running on No-data mode.");
                return;
            }

            if(event.isFromType(ChannelType.TEXT))
            {
                Tag tag = bot.endless.getLocalTag(event.getGuild().getIdLong(), event.getArgs().trim().toLowerCase());

                if(tag==null)
                {
                    tag = bot.endless.getGlobalTag(event.getArgs().trim().toLowerCase());
                    if(tag==null)
                    {
                        event.replyError("No tag found with that name!");
                        return;
                    }
                }

                if(!(bot.tdm.isImported(event.getGuild().getIdLong(), String.valueOf(tag.getId()))))
                    event.replyError("This tag isn't imported!");
                else
                {
                    bot.tdm.unImportTag(event.getGuild().getIdLong(), tag);
                    event.replySuccess("Successfully unimported tag!");
                }
            }
        }
    }

    private String[] splitArgs(String args)
    {
        return args.split(" ", 2);
    }
}

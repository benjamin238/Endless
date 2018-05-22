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

package me.artuto.endless.commands.moderation;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.Messages;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.commands.EndlessCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clear extends EndlessCommand
{
    private final Bot bot;
    private final Pattern LINK = Pattern.compile("https?://\\/\\/.+");
    private final Pattern MESSAGE = Pattern.compile("\"(.*?)\"", Pattern.DOTALL);
    private final Pattern REGEX = Pattern.compile("`(.*?)`", Pattern.DOTALL);
    private final Pattern MENTION = Pattern.compile("<@!?(\\d{17,22})>");
    private final Pattern ID = Pattern.compile("(?:^|\\s)(\\d{17,22})(?:$|\\s)");
    private final Pattern NUM = Pattern.compile("(?:^|\\s)(\\d{1,4})(?:$|\\s)");
    private final String limit = "This command, due a Discord API limitation, can't clear messages older than a week.";

    public Clear(Bot bot)
    {
        this.bot = bot;
        this.name = "clear";
        this.aliases = new String[]{"clean", "prune"};
        this.help = "Clears the specified range of message using the specified parameters";
        this.category = Categories.MODERATION;
        this.botPerms = new Permission[]{Permission.MESSAGE_MANAGE, Permission.MESSAGE_HISTORY};
        this.userPerms = new Permission[]{Permission.MESSAGE_MANAGE, Permission.MESSAGE_HISTORY};
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        String params = event.getArgs();
        String r;

        String noparams = "**No valid parameters detected:**\n"+"Pinned messages are ignored.\n"+"**You can following parameters, the order doesn't matters:**\n"+"-`<numberOfPosts>`: Number of post to clean, min. 2 and max. 1000.\n"+"-`bots`: Clears messages by bots.\n"+"-`embeds`: Clears messages with embeds.\n"+"-`links`: Clears messages which contains links.\n"+"-`images`: Clears messages with images.\n"+"-`<@user|ID|nickname|username>`: Clears messages sent by the specified user.\n"+"-`\"text\"`: Clears messages with the text specified in quotes.\n"+"-` `regex` `: Clears messages that match the specified regex.";
        if(params.isEmpty())
        {
            event.replyWarning(noparams);
            return;
        }

        try
        {
            String[] args = event.getArgs().split(" for", 2);
            params = args[0];
            r = args[1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            params = event.getArgs();
            r = "[no reason specified]";
        }

        int num = -1;
        List<String> text = new LinkedList<>();
        String pattern = null;
        List<String> ids = new LinkedList<>();
        String finalR = r;
        String finalParams = params;

        Matcher m = MESSAGE.matcher(params);
        while(m.find()) text.add(m.group(1).trim().toLowerCase());
        params = params.replaceAll(MESSAGE.pattern(), " ");

        m = REGEX.matcher(params);
        while(m.find()) pattern = m.group(1);
        params = params.replaceAll(REGEX.pattern(), " ");

        m = MENTION.matcher(params);
        while(m.find()) ids.add(m.group(1));
        params = params.replaceAll(MENTION.pattern(), " ");

        m = ID.matcher(params);
        while(m.find()) ids.add(m.group(1));
        params = params.replaceAll(ID.pattern(), " ");

        m = NUM.matcher(params);
        while(m.find()) num = Integer.parseInt(m.group(1));
        params = params.replaceAll(NUM.pattern(), " ");

        boolean bots = params.contains("bots");
        boolean embed = params.contains("embeds");
        boolean link = params.contains("links");
        boolean image = params.contains("image");
        boolean all = text.isEmpty() && pattern == null && ids.isEmpty() && !(bots) && !(embed) && !(link) && !(image);

        if(num == -1) if(all)
        {
            event.replyWarning(noparams);
            return;
        }
        else num = 100;

        if(num>1000 || num<2)
        {
            String numberOfPosts = "The number of messages must be between `2` and `1000`";
            event.replyError(numberOfPosts);
            return;
        }

        int val2 = num+1;
        String p = pattern;

        bot.clearThread.submit(() ->
        {
            int val = val2;
            List<Message> msgs = new LinkedList<>();
            MessageHistory history = event.getTextChannel().getHistory();
            OffsetDateTime limitCheck = event.getMessage().getCreationTime().minusWeeks(2).plusMinutes(1);

            while(val>100)
            {
                msgs.addAll(history.retrievePast(100).complete());
                val -= 100;

                if(msgs.get(msgs.size()-1).getCreationTime().isBefore(limitCheck))
                {
                    val = 0;
                    break;
                }
            }

            if(val>0) msgs.addAll(history.retrievePast(val).complete());

            msgs.remove(event.getMessage());

            boolean weeks2 = false;
            List<Message> deletion = new LinkedList<>();

            for(Message msg : msgs)
            {
                if(msg.getCreationTime().isBefore(limitCheck))
                {
                    weeks2 = true;
                    break;
                }

                if(all || ids.contains(msg.getAuthor().getId()) || (bots && msg.getAuthor().isBot()) || (embed && !(msg.getEmbeds().isEmpty())) || (link && LINK.matcher(msg.getContentRaw()).find()) || (image && hasImage(msg)))
                {
                    deletion.add(msg);
                    continue;
                }

                String lowerCaseContent = msg.getContentDisplay().toLowerCase();

                if(text.stream().anyMatch(lowerCaseContent::contains))
                {
                    deletion.add(msg);
                    continue;
                }

                try
                {
                    if(!(p == null) && msg.getContentRaw().matches(p)) deletion.add(msg);
                }
                catch(Exception ignored)
                {
                }
            }

            if(deletion.isEmpty())
            {
                event.replyWarning("There were no messages to clear!"+(weeks2 ? limit : ""));
                return;
            }

            try
            {
                int index = 0;

                while(index<deletion.size())
                {
                    if(index+100>deletion.size()) if(index+1 == deletion.size())
                        deletion.get(deletion.size()-1).delete().reason("[CLEAR]["+event.getAuthor().getName()+"#"+event.getAuthor().getDiscriminator()+"]").complete();
                    else event.getTextChannel().deleteMessages(deletion.subList(index, deletion.size())).complete();
                    else event.getTextChannel().deleteMessages(deletion.subList(index, index+100)).complete();

                    index += 100;
                }
            }
            catch(Exception e)
            {
                event.replyError(Messages.CLEAR_ERROR+"**"+deletion.size()+"** messages!");
                LoggerFactory.getLogger("MySQL Database").error(e.toString());
                e.printStackTrace();
                return;
            }

            event.replySuccess(Messages.CLEAR_SUCCESS+"**"+deletion.size()+"** messages!");
            bot.modlog.logClear(event.getAuthor(), event.getTextChannel(), finalR, event.getGuild(), deletion, finalParams);
        });
    }

    private static boolean hasImage(Message msg)
    {
        if(msg.getAttachments().stream().anyMatch(Message.Attachment::isImage)) return true;
        if(msg.getEmbeds().stream().anyMatch(e -> !(e.getImage() == null) || !(e.getVideoInfo() == null))) return true;
        return false;
    }
}

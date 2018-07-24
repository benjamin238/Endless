package me.artuto.endless.commands.utils;

import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.ChecksUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.LinkedList;
import java.util.List;

public class ArchiveCmd extends EndlessCommand
{
    private final Bot bot;

    public ArchiveCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "archive";
        this.help = "Command to retrieve all the messages on the specified channel.";
        this.arguments = "[number of messages] <channel>";
        this.category = Categories.UTILS;
        this.botPerms = new Permission[]{Permission.MESSAGE_HISTORY};
        this.userPerms = new Permission[]{Permission.MESSAGE_HISTORY};
        this.cooldown = 600;
        this.cooldownScope = CooldownScope.GUILD;
    }

    @Override
    protected void executeCommand(CommandEvent event)
    {
        String[] inputs = event.getArgs().split("\\s+", 2);
        int num = 100;
        String query;

        if(inputs.length>1 && inputs[0].matches("\\d+"))
        {
            num = Integer.parseInt(inputs[0]);
            query = inputs[1];
        }
        else
            query = event.getArgs();

        TextChannel tc = ArgsUtils.findTextChannel(event, query);
        if(tc==null)
            return;
        if(!(ChecksUtil.hasPermission(event.getMember(), tc, Permission.MESSAGE_HISTORY)))
        {
            event.replyError("I don't have `Message History` permission on that channel!");
            return;
        }

        if(num>100)
            num = 100;
        int limit = num;
        bot.archiveThread.submit(() -> {
            List<Message> msgs = new LinkedList<>();
            MessageHistory history = tc.getHistory();
            boolean finished = false;

            while(!(finished))
            {
                List<Message> retrieved = history.retrievePast(limit).complete();
                msgs.addAll(retrieved);
                if(retrieved.size()<limit)
                    finished = true;
            }
    });
    }
}

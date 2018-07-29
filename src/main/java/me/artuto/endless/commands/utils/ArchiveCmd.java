package me.artuto.endless.commands.utils;

import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.Endless;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ArgsUtils;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.LogUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.IOUtil;
import okhttp3.*;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ArchiveCmd extends EndlessCommand
{
    private final Bot bot;
    private final Logger LOG = Endless.getLog(ArchiveCmd.class);

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
        if(!(ChecksUtil.hasPermission(event.getSelfMember(), tc, Permission.MESSAGE_HISTORY)))
        {
            event.replyError("I don't have `Message History` permission on that channel!");
            return;
        }
        if(!(ChecksUtil.hasPermission(event.getMember(), tc, Permission.MESSAGE_HISTORY)))
        {
            event.replyError("You don't have `Message History` permission on that channel!");
            return;
        }

        if(num>100)
            num = 100;
        int limit = num;
        bot.archiveThread.submit(() -> {
            event.reply(Const.LOADING+" Working on it!");
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
            if(msgs.isEmpty())
            {
                event.replyWarning("That channel is empty!");
                return;
            }

            msgs.sort(Comparator.comparing(Message::getCreationTime));
            String name = String.format("archive-%s.txt", tc.getId());
            File file = LogUtils.createMessagesTextFile(msgs, name);
            String toSend = event.getAuthor().getAsMention()+": Here is your dump of "+tc.getAsMention();
            if(file==null)
            {
                event.replyError("Could not generate text file!");
                return;
            }
            if(file.length()<=event.getJDA().getSelfUser().getAllowedFileSize())
            {
                try
                {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file",
                            name, RequestBody.create(MediaType.parse("application/octet-stream"), IOUtil.readFully(file))).build();
                    Request request = new Request.Builder().url("https://file.io").post(formBody).build();
                    client.newCall(request).enqueue(new Callback()
                    {
                        @ParametersAreNonnullByDefault
                        @Override
                        public void onResponse(Call call, Response response)
                        {
                            JSONObject json = new JSONObject(new JSONTokener(response.body().byteStream()));
                            event.replySuccess(toSend+": <**"+json.getString("link")+"**>");
                            file.delete();
                        }

                        @ParametersAreNonnullByDefault
                        @Override
                        public void onFailure(Call call, IOException e)
                        {
                            event.replyError(event.getAuthor().getAsMention()+": Hey, there was an error while uploading the file, however, you can join "+
                                    "the support server and ask for the file. Use `"+event.getClient().getPrefix()+"help support` to join.");
                            LOG.error("Error while uploading the dump {} to file.io", name, e);
                        }
                    });
                }
                catch(IOException e)
                {
                    event.replyError(event.getAuthor().getAsMention()+": Hey, there was an error while uploading the file, however, you can join " +
                            "the support server and ask for the file. Use `"+event.getClient().getPrefix()+"help support` to join.");
                    LOG.error("Error while uploading the dump {} to file.io", name, e);
                }
            }
            else
            {
                event.getTextChannel().sendFile(file, name, new MessageBuilder().append(toSend).build()).queue(s -> {
                    event.reactSuccess();
                    file.delete();
                }, e -> {
                    event.replyError(event.getAuthor().getAsMention()+": Hey, there was an error while uploading the file, however, you can join " +
                            "the support server and ask for the file. Use `"+event.getClient().getPrefix()+"help support` to join.");
                    LOG.error("Error while uploading the dump {} to Discord", name, e);
                });
            }
    });
    }
}

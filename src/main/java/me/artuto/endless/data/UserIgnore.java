package me.artuto.endless.data;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class UserIgnore
{
    private final SimpleLog LOG = SimpleLog.getLog("Ignored Users");
    public List<User> list;

    public UserIgnore(CommandEvent event) throws IOException
    {


        RandomAccessFile file = new RandomAccessFile("data/ignored_users.txt", "r");
        FileChannel channel = file.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
        channel.read(buffer);
        buffer.flip();

        LOG.info("Loading ignored users list...");

        try
        {
            for(int i = 0; i < channel.size(); i++)
            {
                list = (List<User>) event.getJDA().retrieveUserById((char) buffer.get()).complete();
            }

            LOG.info("Loaded "+channel.size()+" entries");
        }
        catch(IOException e)
        {
            LOG.fatal("Error when loading the ignored users list: "+e);
            e.printStackTrace();
        }

        channel.close();
        file.close();
    }

    public UserIgnore() {

    }
}

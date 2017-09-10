package me.artuto.endless.data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Blacklists
{
    private static Writer output;

    public static List<String> getUsersList() throws IOException
    {
        List<String> lines;
        lines = Files.readAllLines(Paths.get("data/blacklisted_users.txt"));

        return lines;
    }

    public static List<String> getGuildsList() throws IOException
    {
        List<String> lines;
        lines = Files.readAllLines(Paths.get("data/blacklisted_guilds.txt"));

        return lines;
    }

    public static boolean isUserListed(String id) throws IOException
    {
        List<String> lines;
        lines = Files.readAllLines(Paths.get("data/blacklisted_users.txt"));

        return lines.contains(id);
    }

    public static boolean isGuildListed(String id) throws IOException
    {
        List<String> lines;
        lines = Files.readAllLines(Paths.get("data/blacklisted_guilds.txt"));

        return lines.contains(id);
    }

    public static void addUser(String id) throws IOException
    {
        output = new BufferedWriter(new FileWriter("data/blacklisted_users.txt", true));
        output.append("\n"+id);
        output.close();
    }

    public static void addGuild(String id) throws IOException
    {
        output = new BufferedWriter(new FileWriter("data/blacklisted_guilds.txt", true));
        output.append(id);
        output.close();
    }

    public static void removeUser(String id) throws IOException
    {
        File list = new File("data/blacklisted_users.txt");
        File tempList = new File("data/tmp_blacklisted_users.txt");
        BufferedReader reader = new BufferedReader(new FileReader(list));
        Writer writer = new BufferedWriter(new FileWriter(tempList, true));
        String line;
        String trim;
        boolean success;

        while((line = reader.readLine())!=null)
        {
            trim = line.trim();
            if(trim.equals(id)) continue;
            writer.append(line);
        }

        writer.close();
        reader.close();
        list.delete();
        success = tempList.renameTo(list);
    }

    public static void removeGuild(String id) throws IOException
    {
        File list = new File("data/blacklisted_guilds.txt");
        File tempList = new File("data/tmp_blacklisted_guilds.txt");
        BufferedReader reader = new BufferedReader(new FileReader(list));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempList));
        String line;
        String trim;
        boolean success;

        while((line = reader.readLine())!=null)
        {
            trim = line.trim();
            if(trim.equals(id)) continue;
            writer.write(line + System.getProperty("line.separator"));
        }

        writer.close();
        reader.close();
        success = tempList.renameTo(list);
    }
}

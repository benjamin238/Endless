package me.artuto.endless.tempdata;

import java.util.HashMap;

public class AfkManager
{
    private static HashMap<Long,String> afk = new HashMap<>();

    public static void setAfk(Long id, String message)
    {
        afk.put(id, message);
    }

    public static String getMessage(Long id)
    {
        return afk.get(id);
    }

    public static HashMap<Long,String> getMap()
    {
        return afk;
    }

    public static void unsetAfk(Long id)
    {
        afk.remove(id);
    }

    public static boolean isAfk(Long id)
    {
        return afk.containsKey(id);
    }
}

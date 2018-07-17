package me.artuto.endless.storage.data.managers;

import me.artuto.endless.Bot;

import java.sql.Connection;

public class UserDataManager
{
    private final Bot bot;
    private final Connection connection;

    public UserDataManager(Bot bot)
    {
        this.bot = bot;
        this.connection = bot.db.getConnection();
    }


}

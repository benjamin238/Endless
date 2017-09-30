package me.artuto.endless.loader;

import net.dv8tion.jda.core.OnlineStatus;

public class ConfigFormat
{
    public String token;
    public String prefix;
    public String game;
    public String discordBotsToken;
    public String discordBotListToken;
    public String discordBansToken;
    public String doneEmote;
    public String warnEmote;
    public String errorEmote;
    public String dbUrl;
    public String dbPort;
    public String database;
    public String dbUsername;
    public String dbPassword;
    public int poolSize;
    public Long ownerId;
    public Long[] coOwnerIds;
    public Long rootGuildId;
    public Long botlogChannelId;
    public OnlineStatus status;
    public Boolean botlog;
    public Boolean debug;
    public Boolean deepDebug;
}
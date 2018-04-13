package me.artuto.endless.data;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Collection;

public class ClientGSDMProvider implements GuildSettingsProvider
{
    private final Long guild;
    private final DatabaseManager db;
    private final GuildSettingsDataManager gsdm;
    private final Logger LOG = LoggerFactory.getLogger("MySQL Database");

    public ClientGSDMProvider(Long guild, DatabaseManager db, GuildSettingsDataManager gsdm)
    {
        this.guild = guild;
        this.db = db;
        this.gsdm = gsdm;
    }

    @Nullable
    @Override
    public Collection<String> getPrefixes()
    {
        return db.getSettings(guild).getPrefixes();
    }
}

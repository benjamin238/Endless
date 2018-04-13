package me.artuto.endless.data;

import com.jagrosh.jdautilities.command.GuildSettingsManager;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class ClientGSDM implements GuildSettingsManager<ClientGSDMProvider>
{
    private final DatabaseManager db;
    private final GuildSettingsDataManager gsdm;
    private final Logger LOG = LoggerFactory.getLogger("MySQL Database");

    public ClientGSDM(DatabaseManager db, GuildSettingsDataManager gsdm)
    {
        this.db = db;
        this.gsdm = gsdm;
    }

    @Nullable
    @Override
    public ClientGSDMProvider getSettings(Guild guild)
    {
        return new ClientGSDMProvider(guild.getIdLong(), db, gsdm);
    }
}

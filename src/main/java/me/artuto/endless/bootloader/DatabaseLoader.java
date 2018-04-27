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

package me.artuto.endless.bootloader;

import ch.qos.logback.classic.Logger;
import me.artuto.endless.cmddata.Categories;
import me.artuto.endless.data.*;
import me.artuto.endless.exceptions.DatabaseException;
import me.artuto.endless.handlers.BlacklistHandler;
import me.artuto.endless.handlers.SpecialCaseHandler;
import me.artuto.endless.loader.Config;
import me.artuto.endless.logging.ModLogging;
import me.artuto.endless.utils.GuildUtils;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author Artuto
 */

public class DatabaseLoader
{
    private Config config;
    private BlacklistDataManager bdm;
    private DatabaseManager db;
    private DonatorsDataManager ddm;
    private GuildSettingsDataManager gsdm;
    private ProfileDataManager pdm;
    private StarboardDataManager sdm;
    private TagDataManager tdm;
    private ModLogging modlog;
    private BlacklistHandler bHandler;
    private SpecialCaseHandler sHandler;
    private Categories categories;
    public static Logger LOG = (Logger) LoggerFactory.getLogger("Database Loader");

    DatabaseLoader(Config config)
    {
        this.config = config;
    }

    public DatabaseLoader initialize(boolean maintenance)
    {
        LOG.info("Loading Database Managers...");

        try
        {
            db = new DatabaseManager(config.getDatabaseUrl(), config.getDatabaseUsername(), config.getDatabasePassword());
            bdm = new BlacklistDataManager(db);
            ddm = new DonatorsDataManager(db);
            gsdm = new GuildSettingsDataManager(db);
            pdm = new ProfileDataManager(db);
            sdm = new StarboardDataManager(db);
            tdm = new TagDataManager(db);
            modlog = new ModLogging(gsdm);
            bHandler = new BlacklistHandler(bdm);
            sHandler = new SpecialCaseHandler();
            categories = new Categories(bHandler, sHandler, maintenance);
            new GuildUtils(config, db);
            return this;
        }
        catch(SQLException e)
        {
            throw new DatabaseException();
        }
    }

    public DatabaseManager getDatabaseManager()
    {
        return db;
    }

    public BlacklistDataManager getBlacklistDataManager()
    {
        return bdm;
    }

    public DonatorsDataManager getDonatorsDataManager()
    {
        return ddm;
    }

    public GuildSettingsDataManager getGuildSettingsDataManager()
    {
        return gsdm;
    }

    public ProfileDataManager getProfileDataManager()
    {
        return pdm;
    }

    public StarboardDataManager getStarbordDataManager()
    {
        return sdm;
    }

    public TagDataManager getTagDataManager()
    {
        return tdm;
    }

    public BlacklistHandler getBlacklistHandler()
    {
        return bHandler;
    }

    public SpecialCaseHandler getSpecialCaseHandler()
    {
        return sHandler;
    }

    public ModLogging getModlog()
    {
        return modlog;
    }

    public Categories getCategories()
    {
        return categories;
    }
}

/*
 * Copyright (C) 2017 Artu
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

package me.artuto.endless.website;

import me.artuto.endless.loader.Config;

import static spark.Spark.*;

public class Manager
{
    private final Config config;

    public Manager(Config config)
    {
        this.config = config;
    }

    public void prepare()
    {
        port(config.getDashboardPort());
        staticFiles.location("/public");
        get("/", (req, res) -> /*this.getClass().getResource("/public/index.html")*/ "hola!");
    }

    public void start()
    {
        init();
    }

    public void shutdown()
    {
        stop();
    }
}

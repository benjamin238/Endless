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

package me.artuto.endless;

import me.artuto.endless.Bot;
import me.artuto.endless.loader.Config;

import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Spark;


public class API
{
    public static void main(String authToken, Config config, Bot bot)
    {
        Spark.port(config.getDashboardPort());

        Spark.get("/api/users/:id/guilds", (req, res) -> {
            if(!(isAuthenticated(req, authToken)))
                return notAuthenticated(res);

            Long id;

            try
            {
                id = Long.parseLong(req.params("id"));
            }
            catch(NumberFormatException e)
            {
                return error(res, "Invalid ID");
            }

            return "WIP";
        });
    }

    private static boolean isAuthenticated(Request req, String authToken)
    {
        return !(req.headers("Authentication")==null) && req.headers("Authentication").equals(authToken);
    }

    private static String notAuthenticated(Response res)
    {
        res.status(401);
        res.body(new JSONObject().put("message", "Invalid auth token").toString());
        return res.body();
    }

    private static String error(Response res, String message)
    {
        res.status(400);
        res.body(new JSONObject().put("message", message).toString());
        return res.body();
    }
}

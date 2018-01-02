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

import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Spark;


public class API
{
    public static void main(String authToken, Config config, Bot bot)
    {
        Spark.port(config.getAPIPort());

        Spark.get("/api/users/:id/guilds", (req, res) -> {
            if(!(isAuthorizated(req, authToken)))
                return notAuthorizated(res);

            Long id;
            JSONArray array = new JSONArray();

            try
            {
                id = Long.parseLong(req.params("id"));
            }
            catch(NumberFormatException e)
            {
                return error(res, "Invalid ID");
            }

            bot.getManagedGuildsForUser(id).stream().map(g -> {
                JSONObject gObj = new JSONObject()
                        .put("id", g.getIdLong())
                        .put("name", g.getName())
                        .put("ownerId", g.getOwner().getUser().getIdLong())
                        .put("owner", g.getOwner().getUser().getName()+"#"+g.getOwner().getUser().getDiscriminator())
                        .put("icon", g.getIconUrl()==null?JSONObject.NULL:g.getIconUrl());
                return gObj;
            }).forEachOrdered(g -> array.put(g));

            res.status(200);
            res.header("Content-Type", "applkcation-json");
            res.body(array.toString());
            return res.body();
        });
    }

    private static boolean isAuthorizated(Request req, String authToken)
    {
        return !(req.headers("Authorization")==null) && req.headers("Authorization").equals(authToken);
    }

    private static String notAuthorizated(Response res)
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

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

package me.artuto.endless.commands.utils;

import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.IOUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import org.json.JSONArray;
import org.json.JSONObject;
import tk.plogitech.darksky.forecast.*;
import tk.plogitech.darksky.forecast.model.Latitude;
import tk.plogitech.darksky.forecast.model.Longitude;

import java.awt.*;

/**
 * @author Artuto
 */

public class WeatherCmd extends EndlessCommand
{
    private final Bot bot;
    private final DarkSkyClient darkskyClient;

    public WeatherCmd(Bot bot)
    {
        this.darkskyClient = new DarkSkyClient();
        this.bot = bot;
        this.name = "weather";
        this.help = "Gets the current weather of the specified location";
        this.arguments = "<location>";
        this.category = Categories.UTILS;
        this.botPerms = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
        this.cooldown = 10;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        if(bot.config.getBingMapsKey().isEmpty())
        {
            event.replyError("Bing Maps API Key is not configured!");
            return;
        }
        else if(bot.config.getDarkSkyKey().isEmpty())
        {
            event.replyError("DarkSky API Key is not configured!");
            return;
        }

        event.async(() -> {
            String url = String.format(Const.BING_MAPS, event.getArgs(), bot.config.getBingMapsKey());
            JSONObject mapsObj = IOUtils.makeGETRequest(null, url);
            if(mapsObj==null)
            {
                event.replyError("Could not contact Bing Maps API to get the specified location!");
                return;
            }
            JSONObject resSet = (JSONObject)mapsObj.getJSONArray("resourceSets").get(0);
            if(resSet.getJSONArray("resources").length()==0)
            {
                event.replyWarning("The specified location could not be found!");
                return;
            }
            JSONObject res = (JSONObject)resSet.getJSONArray("resources").get(0);
            JSONObject names = res.getJSONObject("address");
            JSONArray coords = res.getJSONObject("point").getJSONArray("coordinates");

            ForecastRequest request = new ForecastRequestBuilder()
                    .key(new APIKey(bot.config.getDarkSkyKey()))
                    .location(new GeoCoordinates(new Longitude((double)coords.get(1)), new Latitude((double)coords.get(0))))
                    .language(ForecastRequestBuilder.Language.en)
                    .units(ForecastRequestBuilder.Units.si).build();

            JSONObject forecast;
            try
            {
                forecast = new JSONObject(darkskyClient.forecastJsonString(request)).getJSONObject("currently");
            }
            catch(ForecastException e)
            {
                event.replyError("Could not contact DarkSky API to get the weather of the specified location!");
                return;
            }

            EmbedBuilder builder = new EmbedBuilder();
            MessageBuilder mb = new MessageBuilder();
            StringBuilder sb = new StringBuilder();

            mb.setContent(":sunny: Weather for **"+names.getString("formattedAddress")+"** ("+names.getString("adminDistrict")+", "
                    +names.getString("countryRegion")+")");

            double celsius = forecast.getDouble("temperature");
            double flCelsius = forecast.getDouble("apparentTemperature");
            double farenheit = 32 + (celsius*9/5);
            double flFarenheit = 32 + (flCelsius*9/5);
            sb.append(Const.LINE_START).append(" Weather: **").append(forecast.getString("summary")).append("**\n");
            sb.append(Const.LINE_START).append(" Temperature: **").append(celsius).append("**°C (**").append(farenheit).append("**°F)\n");
            sb.append(Const.LINE_START).append(" Feels like:tm:: **").append(flCelsius).append("**°C (**").append(flFarenheit).append("**°F)\n");
            sb.append(Const.LINE_START).append(" Humidity: **").append(Math.round(forecast.getDouble("humidity")*100)).append("**%\n");
            sb.append(Const.LINE_START).append(" Wind Speed: **").append(forecast.getDouble("windSpeed")).append("**kph\n");
            sb.append(Const.LINE_START).append(" UV Index: **").append(forecast.getInt("uvIndex")).append("**\n");
            sb.append(Const.LINE_START).append(" Nebulosity: **").append(Math.round(forecast.getDouble("cloudCover")*100)).append("**%\n");
            sb.append(Const.LINE_START).append(" Visibility: **").append(Math.round(forecast.getDouble("visibility"))).append("**%\n");

            builder.setDescription(sb).setColor(event.getSelfMember()==null? Color.decode("#33ff00"):event.getSelfMember().getColor());
            builder.setThumbnail("https://homer.idroid.me/assets/weather/"+forecast.getString("icon")+".png");
            builder.setFooter("Data from DarkSky API", "https://cdn.discordapp.com/emojis/464245557049950238.png");
            event.reply(mb.setEmbed(builder.build()).build());
        });
    }
}

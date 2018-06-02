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

package me.artuto.endless.managers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GoogleSearcher
{
    private final Logger LOG = LoggerFactory.getLogger(GoogleSearcher.class);

    public ArrayList<String> getGoogleData(String query)
    {
        String url;
        try
        {
            url = "https://www.google.com/search?q="+URLEncoder.encode(query, "UTF-8")+"&num=20";
        }
        catch(UnsupportedEncodingException e)
        {
            LOG.error("Error while encoding the needed URL to search in Google!", e);
            return null;
        }

        LOG.debug("Sending Google Request with query: "+query);

        ArrayList<String> result;

        try
        {
            Document docs = Jsoup.connect(url).userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)").timeout(5000).get();

            Elements links = docs.select("a[href]");
            result = new ArrayList<>();
            links.stream().map((link) -> link.attr("href")).filter((temp) -> (temp.startsWith("/url?q="))).forEach((temp) ->
            {
                try
                {
                    String results = URLDecoder.decode(temp.substring(7, temp.indexOf("&sa=")), "UTF-8");
                    if(!(results.contains("/settings/ads/preferences") && !(results.startsWith("http://webcache.googleusercontent.com"))))
                        result.add(results);
                }
                catch(UnsupportedEncodingException ignored) { }
            });
        }
        catch(IOException e)
        {
            LOG.error("Search went wrong!", e);
            return null;
        }

        return result;
    }
}

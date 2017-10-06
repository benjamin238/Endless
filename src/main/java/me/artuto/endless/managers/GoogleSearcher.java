package me.artuto.endless.managers;

import net.dv8tion.jda.core.utils.SimpleLog;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GoogleSearcher
{
    private final SimpleLog LOG = SimpleLog.getLog("Google Searcher");

    public ArrayList<String> getGoogleData(String query)
    {
        String url;
        try
        {
            url = "https://www.google.com/search?q="+URLEncoder.encode(query, "UTF-8")+"&num=20";
        }
        catch(UnsupportedEncodingException e)
        {
            LOG.fatal(e);
            e.printStackTrace();
            return null;
        }

        LOG.debug("Sending Google Request with query: "+query);

        ArrayList<String> result;

        try
        {
            Document docs = Jsoup.connect(url).userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)").timeout(5000).get();

            Elements links = docs.select("a[href]");
            result = new ArrayList<>();
            links.stream().map((link) -> link.attr("href")).filter((temp) -> (temp.startsWith("/url?q="))).forEach((temp) -> {
                try
                {
                    String results = URLDecoder.decode(temp.substring(7, temp.indexOf("&sa=")), "UTF-8");
                    if(!(results.contains("/settings/ads/preferences") && !(results.startsWith("http://webcache.googleusercontent.com"))))
                        result.add(results);
                }
                catch(UnsupportedEncodingException e)
                { }
            });
        }
        catch(IOException e)
        {
            LOG.fatal("Search went wrong!: "+e);
            e.printStackTrace();
            return null;
        }

        return result;
    }
}

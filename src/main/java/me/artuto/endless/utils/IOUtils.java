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

package me.artuto.endless.utils;

import ch.qos.logback.classic.Logger;
import me.artuto.endless.Const;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author Artuto
 */

public class IOUtils
{
    private final static Logger LOG = (Logger)LoggerFactory.getLogger(IOUtils.class);

    public static JSONObject makeGETRequest(@Nullable Map<String, String> headers, @Nonnull String url)
    {
        try
        {
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(url).method("GET", null)
                    .header("User-Agent", Const.USER_AGENT);
            if(!(headers==null))
                headers.forEach(requestBuilder::header);
            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();

            if(response.body()==null)
                return null;
            else
                return new JSONObject(response.body().string());
        }
        catch(UnsupportedEncodingException ignored) {}
        catch(IOException e)
        {
            LOG.error("Error while making a REST request to {}", url, e);
            return null;
        }
        return null;
    }
}

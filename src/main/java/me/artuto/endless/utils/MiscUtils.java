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

import me.artuto.endless.Const;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Artuto
 */

public class MiscUtils
{
    public static InputStream getInputStream(String url)
    {
        try
        {
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder().url(url)
                    .method("GET", null)
                    .header("user-agent", Const.USER_AGENT)
                    .build();

            return client.newCall(request).execute().body().byteStream();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}

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

package me.artuto.endless.tools;

import java.util.concurrent.ThreadFactory;

/**
 * @author Artuto
 */

public class EndlessThreadFactory implements ThreadFactory
{
    private final String name;

    public EndlessThreadFactory(String name)
    {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r)
    {
        final Thread thread = new Thread(r, "Endless-Thread "+name);
        thread.setDaemon(true);
        return thread;
    }
}

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

package me.artuto.endless.core.entities;

import net.dv8tion.jda.core.entities.User;

/**
 * @author Artuto
 */

public interface Profile
{
    boolean isEmpty();

    String getDonatedAmount();

    String getTimezone();

    String getTwitter();

    String getSteam();

    String getWii();

    String getNNID();

    String getXboxLive();

    String getPSN();

    String get3DS();

    String getSkype();

    String getAbout();

    String getReddit();

    String getMKWii();

    String getSplatoon();

    String getBattle();

    String getWOW();

    String getLOL();

    String getMinecraft();

    String getEmail();

    String getTwitch();

    String getYouTube();

    User getUser();
}

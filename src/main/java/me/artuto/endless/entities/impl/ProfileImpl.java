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

package me.artuto.endless.entities.impl;

import me.artuto.endless.entities.Profile;

/**
 * @author Artuto
 */

public class ProfileImpl implements Profile
{
    private final String timezone;
    private final int donatedAmount;
    private final String twitter;
    private final String steam;
    private final String wii;
    private final String nnid;
    private final String xboxLive;
    private final String psn;
    private final String threeds;
    private final String skype;
    private final String youtube;
    private final String about;
    private final String twitch;
    private final String minecraft;
    private final String email;
    private final String lol;
    private final String wow;
    private final String battle;
    private final String splatoon;
    private final String mkwii;
    private final String reddit;

    public ProfileImpl(int donatedAmount, String timezone, String twitter, String steam, String wii, String nnid, String xboxLive, String psn, String threeds, String skype, String youtube, String about, String twitch, String minecraft, String email, String lol, String wow, String battle, String splatoon, String mkwii, String reddit)
    {
        this.donatedAmount = donatedAmount;
        this.timezone = timezone;
        this.twitter = twitter;
        this.steam = steam;
        this.wii = wii;
        this.nnid = nnid;
        this.xboxLive = xboxLive;
        this.psn = psn;
        this.threeds = threeds;
        this.skype = skype;
        this.youtube = youtube;
        this.about = about;
        this.twitch = twitch;
        this.minecraft = minecraft;
        this.email = email;
        this.lol = lol;
        this.wow = wow;
        this.battle = battle;
        this.splatoon = splatoon;
        this.mkwii = mkwii;
        this.reddit = reddit;
    }

    @Override
    public String getTimezone()
    {
        return timezone;
    }

    @Override
    public int getDonatedAmount()
    {
        return donatedAmount;
    }

    @Override
    public String getTwitter()
    {
        return twitter;
    }

    @Override
    public String getSteam()
    {
        return steam;
    }

    @Override
    public String getWii()
    {
        return wii;
    }

    @Override
    public String getNNID()
    {
        return nnid;
    }

    @Override
    public String getXboxLive()
    {
        return xboxLive;
    }

    @Override
    public String getPSN()
    {
        return psn;
    }

    @Override
    public String get3DS()
    {
        return threeds;
    }

    @Override
    public String getSkype()
    {
        return skype;
    }

    @Override
    public String getAbout()
    {
        return about;
    }

    @Override
    public String getReddit()
    {
        return reddit;
    }

    @Override
    public String getMKWii()
    {
        return mkwii;
    }

    @Override
    public String getSplatoon()
    {
        return splatoon;
    }

    @Override
    public String getBattle()
    {
        return battle;
    }

    @Override
    public String getWOW()
    {
        return wow;
    }

    @Override
    public String getLOL()
    {
        return lol;
    }

    @Override
    public String getMinecraft()
    {
        return minecraft;
    }

    @Override
    public String getEmail()
    {
        return email;
    }

    @Override
    public String getTwitch()
    {
        return twitch;
    }

    @Override
    public String getYouTube()
    {
        return youtube;
    }
}

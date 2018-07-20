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

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;

import javax.annotation.Nullable;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

public interface GuildSettings
{
    boolean isDefault();

    boolean isEmpty();

    @Nullable
    Collection<String> getPrefixes();

    Guild getGuild();

    int getBanDeleteDays();

    int getStarboardCount();

    List<Ignore> getIgnoredEntities();

    List<Role> getRoleMeRoles();

    List<Tag> getImportedTags();

    long getAdminRole();

    long getLeaveChannel();

    long getModlog();

    long getModRole();

    long getMutedRole();

    long getServerlog();

    long getStarboard();

    long getWelcomeChannel();

    Room.Mode getRoomMode();

    String getLeaveMsg();

    ZoneId getTimezone();

    String getWelcomeMsg();
}

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

package me.artuto.endless.core.entities.impl;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.core.entities.Ignore;
import me.artuto.endless.core.entities.Room;
import me.artuto.endless.core.entities.Tag;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;

import javax.annotation.Nullable;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GuildSettingsImpl implements GuildSettings, GuildSettingsProvider
{
    private boolean isDefault;
    private Collection<String> prefixes;
    private Guild guild;
    private int banDeleteDays, starboardCount;
    private List<Ignore> ignoredEntities;
    private List<Role> roleMeRoles;
    private List<Tag> importedTags;
    private long adminRoleId, modlogId, modRoleId, serverlogId, welcomeId, leaveId, starboardId, mutedRoleId;
    private Room.Mode roomMode;
    private String leaveMsg, welcomeMsg;
    private ZoneId tz;

    public GuildSettingsImpl(boolean isDefault, Collection<String> prefixes, Guild guild, int banDeleteDays, int starboardCount,
                             List<Ignore> ignoredEntities, List<Role> roleMeRoles, List<Tag> importedTags, long adminRoleId,
                             long leaveId, long modlogId, long modRoleId, long mutedRoleId, long serverlogId,
                             long starboardId, long welcomeId, Room.Mode roomMode, String leaveMsg, String welcomeMsg, ZoneId tz)
    {
        this.isDefault = isDefault;
        this.prefixes = prefixes;
        this.guild = guild;
        this.banDeleteDays = banDeleteDays;
        this.starboardCount = starboardCount;
        this.ignoredEntities = ignoredEntities;
        this.roleMeRoles = roleMeRoles;
        this.importedTags = importedTags;
        this.adminRoleId = adminRoleId;
        this.leaveId = leaveId;
        this.modlogId = modlogId;
        this.modRoleId = modRoleId;
        this.mutedRoleId = mutedRoleId;
        this.serverlogId = serverlogId;
        this.starboardId = starboardId;
        this.welcomeId = welcomeId;
        this.roomMode = roomMode;
        this.leaveMsg = leaveMsg;
        this.welcomeMsg = welcomeMsg;
        this.tz = tz;
    }

    @Override
    public boolean isDefault()
    {
        return isDefault && isEmpty();
    }

    @Override
    public boolean isEmpty()
    {
        return prefixes.isEmpty() && banDeleteDays==0 && starboardCount==0 && roleMeRoles.isEmpty() && ignoredEntities.isEmpty()
                && importedTags.isEmpty() && leaveId==0L && modlogId==0L && modRoleId==0L && serverlogId==0L && starboardId==0L
                && welcomeId==0L && roomMode==Room.Mode.NO_CREATION && leaveMsg==null && welcomeMsg==null;
    }

    @Nullable
    @Override
    public Collection<String> getPrefixes()
    {
        return Collections.unmodifiableCollection(prefixes);
    }

    @Override
    public Guild getGuild()
    {
        return guild;
    }

    @Override
    public int getBanDeleteDays()
    {
        return banDeleteDays;
    }

    @Override
    public int getStarboardCount()
    {
        return starboardCount;
    }

    @Override
    public List<Ignore> getIgnoredEntities()
    {
        return Collections.unmodifiableList(ignoredEntities);
    }

    @Override
    public List<Role> getRoleMeRoles()
    {
        return Collections.unmodifiableList(roleMeRoles);
    }

    @Override
    public List<Tag> getImportedTags()
    {
        return Collections.unmodifiableList(importedTags);
    }

    @Override
    public long getAdminRole()
    {
        return adminRoleId;
    }

    @Override
    public long getLeaveChannel()
    {
        return leaveId;
    }

    @Override
    public long getModlog()
    {
        return modlogId;
    }

    @Override
    public long getModRole()
    {
        return modRoleId;
    }

    @Override
    public long getMutedRole()
    {
        return mutedRoleId;
    }

    @Override
    public long getServerlog()
    {
        return serverlogId;
    }

    @Override
    public long getStarboard()
    {
        return starboardId;
    }

    @Override
    public long getWelcomeChannel()
    {
        return welcomeId;
    }

    @Override
    public Room.Mode getRoomMode()
    {
        return roomMode;
    }

    @Override
    public String getLeaveMsg()
    {
        return leaveMsg;
    }

    @Override
    public String getWelcomeMsg()
    {
        return welcomeMsg;
    }

    @Override
    public ZoneId getTimezone()
    {
        return tz;
    }

    @Override
    public String toString()
    {
        if(isDefault())
            return String.format("GS DEFAULT:%s(%s)", guild.getName(), guild.getId());
        else
            return String.format("GS:%s(%s)", guild.getName(), guild.getId());
    }

    public void addIgnoredEntity(Ignore ignore)
    {
        ignoredEntities.add(ignore);
    }

    public void addImportedTag(Tag tag)
    {
        importedTags.add(tag);
    }

    public void addPrefix(String prefix)
    {
        prefixes.add(prefix);
    }

    public void addRoleMeRole(Role role)
    {
        roleMeRoles.add(role);
    }

    public void removeIgnoredEntity(Ignore ignore)
    {
        ignoredEntities.remove(ignore);
    }

    public void removeImportedTag(Tag tag)
    {
        importedTags.remove(tag);
    }

    public void removePrefix(String prefix)
    {
        prefixes.remove(prefix);
    }

    public void removeRoleMeRole(Role role)
    {
        roleMeRoles.remove(role);
    }

    public void setAdminRoleId(long adminRoleId)
    {
        this.adminRoleId = adminRoleId;
    }

    public void setBanDeleteDays(int banDeleteDays)
    {
        this.banDeleteDays = banDeleteDays;
    }

    public void setGuild(Guild guild)
    {
        this.guild = guild;
    }

    public void setLeaveId(long leaveId)
    {
        this.leaveId = leaveId;
    }

    public void setLeaveMsg(String leaveMsg)
    {
        this.leaveMsg = leaveMsg;
    }

    public void setModlogId(long modlogId)
    {
        this.modlogId = modlogId;
    }

    public void setModRoleId(long modRoleId)
    {
        this.modRoleId = modRoleId;
    }

    public void setMutedRoleId(long mutedRoleId)
    {
        this.mutedRoleId = mutedRoleId;
    }

    public void setRoomMode(Room.Mode mode)
    {
        this.roomMode = mode;
    }

    public void setServerlogId(long serverlogId)
    {
        this.serverlogId = serverlogId;
    }

    public void setStarboardCount(int starboardCount)
    {
        this.starboardCount = starboardCount;
    }

    public void setStarboardId(long starboardId)
    {
        this.starboardId = starboardId;
    }

    public void setWelcomeId(long welcomeId)
    {
        this.welcomeId = welcomeId;
    }

    public void setWelcomeMsg(String welcomeMsg)
    {
        this.welcomeMsg = welcomeMsg;
    }

    public void setTimeZone(ZoneId tz)
    {
        this.tz = tz;
    }
}

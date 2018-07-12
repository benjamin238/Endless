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

package me.artuto.endless.core;

import com.jagrosh.jdautilities.command.CommandClient;
import me.artuto.endless.Bot;
import me.artuto.endless.core.entities.GuildSettings;
import me.artuto.endless.core.entities.LocalTag;
import me.artuto.endless.core.entities.Tag;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import java.util.List;

/**
 * @author Artuto
 */

public interface EndlessCore
{
    Bot getBot();

    CommandClient getClient();

    Tag getGlobalTag(String name);

    GuildSettings getGuildSettings(Guild guild);

    GuildSettings getGuildSettingsById(long id);

    GuildSettings getGuildSettingsById(String id);

    JDA getJDA();

    List<Tag> getGlobalTags();

    List<GuildSettings> getGuildSettings();

    List<LocalTag> getLocalTags();

    LocalTag getLocalTag(long guildId, String name);
}

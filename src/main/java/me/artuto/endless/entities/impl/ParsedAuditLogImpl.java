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

import me.artuto.endless.entities.ParsedAuditLog;
import net.dv8tion.jda.core.audit.AuditLogKey;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Artuto
 */

public class ParsedAuditLogImpl implements ParsedAuditLog
{
    private final AuditLogKey key;
    private final Object newValue, oldValue;
    private final String reason;
    private final User author, target;

    public ParsedAuditLogImpl(AuditLogKey key, Object newValue, Object oldValue, String reason, User author, User target)
    {
        this.key = key;
        this.author = author;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.reason = reason;
        this.target = target;
    }

    @Override
    public User getAuthor()
    {
        return author;
    }

    @Override
    public AuditLogKey getKey()
    {
        return key;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getNewValue()
    {
        return (T)newValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOldValue()
    {
        return (T)oldValue;
    }

    @Override
    public String getReason()
    {
        return reason==null?"[no reason specified]":reason;
    }

    @Override
    public User getTarget()
    {
        return target;
    }
}

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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.util.Arrays;
import java.util.List;

/**
 * @author Artuto
 */

public class LogUtils
{
    public static StringBuilder getStackTrace(ILoggingEvent event)
    {
        IThrowableProxy proxy = event.getThrowableProxy();
        ThrowableProxy throwableImpl = (ThrowableProxy)proxy;
        StringBuilder stacktrace = new StringBuilder(event.getFormattedMessage());

        if(!(proxy==null))
        {
            Throwable throwable = throwableImpl.getThrowable();

            List<StackTraceElementProxy> list = Arrays.asList(proxy.getStackTraceElementProxyArray());
            String message = proxy.getMessage();
            if(!(message==null))
            {
                stacktrace.append("\n\n```java\n");
                if(!(throwable==null))
                    stacktrace.append(throwable.getClass().getName()).append(": ");
                stacktrace.append(message);
            }
            for(StackTraceElementProxy element : list)
            {
                String call = element.getSTEAsString();
                if(call.length()+stacktrace.length()>MessageEmbed.TEXT_MAX_LENGTH)
                {
                    stacktrace.append("\n... (").append(list.size()-list.indexOf(element)+1).append(" more calls)");
                    break;
                }
                stacktrace.append("\n").append(call).append("\n");
            }
            stacktrace.append("```");
        }

        return stacktrace;
    }
}

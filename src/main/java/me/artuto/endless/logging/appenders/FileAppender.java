package me.artuto.endless.logging.appenders;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.RolloverFailure;

@NoAutoStart
public class FileAppender extends DefaultTimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent>
{
    @Override
    public void start()
    {
        super.start();
        nextCheck = 0L;
        isTriggeringEvent(null, null);
        try
        {
            tbrp.rollover();
        }
        catch(RolloverFailure ignored) {}
    }
}

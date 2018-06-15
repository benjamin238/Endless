package me.artuto.endless.core.hooks;

import me.artuto.endless.core.events.EndlessEvent;

public interface EndlessListener
{
    default void onEvent(EndlessEvent event) {}
}

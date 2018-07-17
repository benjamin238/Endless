package me.artuto.endless.core.entities;

import java.time.OffsetDateTime;
import java.util.List;

public interface EndlessUser
{
    List<String> getHighlightWords();

    List<String> getNames();

    long getUserId();

    OffsetDateTime getLastActive();
}

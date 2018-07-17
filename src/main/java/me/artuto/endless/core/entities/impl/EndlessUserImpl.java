package me.artuto.endless.core.entities.impl;

import me.artuto.endless.core.entities.EndlessUser;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

public class EndlessUserImpl implements EndlessUser
{
    private List<String> highlightWords, names;
    private long userId;
    private OffsetDateTime lastActive;

    public EndlessUserImpl(List<String> highlightWords, List<String> names, long userId, OffsetDateTime lastActive)
    {
        this.highlightWords = highlightWords;
        this.names = names;
        this.userId = userId;
        this.lastActive = lastActive;
    }

    @Override
    public List<String> getHighlightWords()
    {
        return Collections.unmodifiableList(highlightWords);
    }

    @Override
    public List<String> getNames()
    {
        return Collections.unmodifiableList(names);
    }

    @Override
    public long getUserId()
    {
        return userId;
    }

    @Override
    public OffsetDateTime getLastActive()
    {
        return lastActive;
    }

    @Override
    public String toString()
    {
        return String.format("EU:%s", userId);
    }

    public void addHighlightWord(String word)
    {
        highlightWords.add(word);
    }

    public void addName(String name)
    {
        names.add(name);
    }

    public void setLastActive(OffsetDateTime lastActive)
    {
        this.lastActive = lastActive;
    }

    public void removeHighlightWord(String word)
    {
        highlightWords.remove(word);
    }

    public void removeName(String name)
    {
        names.remove(name);
    }
}

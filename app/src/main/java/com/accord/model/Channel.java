package com.accord.model;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Channel
{
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_MESSAGE = "message";
    private String name;
    private String id;
    protected PropertyChangeSupport listeners;
    private List<Message> message;
    private int unreadMessagesCounter;

    public String getName()
    {
        return this.name;
    }

    public Channel setName(String value)
    {
        if (Objects.equals(value, this.name))
        {
            return this;
        }

        final String oldValue = this.name;
        this.name = value;
        this.firePropertyChange(PROPERTY_NAME, oldValue, value);
        return this;
    }

    public String getId()
    {
        return this.id;
    }

    public Channel setId(String value)
    {
        if (Objects.equals(value, this.id))
        {
            return this;
        }

        final String oldValue = this.id;
        this.id = value;
        this.firePropertyChange(PROPERTY_ID, oldValue, value);
        return this;
    }

    public List<Message> getMessage()
    {
        return this.message != null ? Collections.unmodifiableList(this.message) : Collections.emptyList();
    }

    public Channel withMessage(Message value)
    {
        if (this.message == null)
        {
            this.message = new ArrayList<>();
        }
        if (!this.message.contains(value))
        {
            this.message.add(value);
            this.firePropertyChange(PROPERTY_MESSAGE, null, value);
        }
        return this;
    }

    public Channel withMessage(Message... value)
    {
        for (final Message item : value)
        {
            this.withMessage(item);
        }
        return this;
    }

    public Channel withMessage(Collection<? extends Message> value)
    {
        for (final Message item : value)
        {
            this.withMessage(item);
        }
        return this;
    }

    public Channel withoutMessage(Message value)
    {
        if (this.message != null && this.message.remove(value))
        {
            this.firePropertyChange(PROPERTY_MESSAGE, value, null);
        }
        return this;
    }

    public Channel withoutMessage(Message... value)
    {
        for (final Message item : value)
        {
            this.withoutMessage(item);
        }
        return this;
    }

    public Channel withoutMessage(Collection<? extends Message> value)
    {
        for (final Message item : value)
        {
            this.withoutMessage(item);
        }
        return this;
    }

    public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        if (this.listeners != null)
        {
            this.listeners.firePropertyChange(propertyName, oldValue, newValue);
            return true;
        }
        return false;
    }

    public PropertyChangeSupport listeners()
    {
        if (this.listeners == null)
        {
            this.listeners = new PropertyChangeSupport(this);
        }
        return this.listeners;
    }

    @Override
    public String toString()
    {
        final StringBuilder result = new StringBuilder();
        result.append(' ').append(this.getName());
        result.append(' ').append(this.getId());
        return result.substring(1);
    }

    public void removeYou()
    {
        this.withoutMessage(new ArrayList<>(this.getMessage()));
    }

    public int getUnreadMessagesCounter() {
        return unreadMessagesCounter;
    }

    public Channel setUnreadMessagesCounter(int unreadMessagesCounter) {
        this.unreadMessagesCounter = unreadMessagesCounter;
        return this;
    }

    public Channel setMessages(List<Message> list) {
        this.message = list;
        return this;
    }

    public List<Message> getMessages() {
        return this.message;
    }
}

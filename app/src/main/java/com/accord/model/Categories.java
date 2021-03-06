package com.accord.model;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Categories {
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_CHANNEL = "channel";
    private String name;
    private String id;
    protected PropertyChangeSupport listeners;
    private List<ServerChannel> channel;

    public String getName() {
        return this.name;
    }

    public Categories setName(String value) {
        if (Objects.equals(value, this.name)) {
            return this;
        }

        final String oldValue = this.name;
        this.name = value;
        this.firePropertyChange(PROPERTY_NAME, oldValue, value);
        return this;
    }

    public String getId() {
        return this.id;
    }

    public Categories setId(String value) {
        if (Objects.equals(value, this.id)) {
            return this;
        }

        final String oldValue = this.id;
        this.id = value;
        this.firePropertyChange(PROPERTY_ID, oldValue, value);
        return this;
    }

    public List<ServerChannel> getChannel() {
        return this.channel != null ? Collections.unmodifiableList(this.channel) : Collections.emptyList();
    }

    public Categories withChannel(ServerChannel value) {
        if (this.channel == null) {
            this.channel = new ArrayList<>();
        }
        if (!this.channel.contains(value)) {
            this.channel.add(value);
            this.firePropertyChange(PROPERTY_CHANNEL, null, value);
        }
        return this;
    }

    public Categories withChannel(ServerChannel... value) {
        for (final ServerChannel item : value) {
            this.withChannel(item);
        }
        return this;
    }

    public Categories withChannel(Collection<? extends ServerChannel> value) {
        for (final ServerChannel item : value) {
            this.withChannel(item);
        }
        return this;
    }

    public Categories withoutChannel(ServerChannel value) {
        if (this.channel != null && this.channel.remove(value)) {
            this.firePropertyChange(PROPERTY_CHANNEL, value, null);
        }
        return this;
    }

    public Categories withoutChannel(ServerChannel... value) {
        for (final ServerChannel item : value) {
            this.withoutChannel(item);
        }
        return this;
    }

    public Categories withoutChannel(Collection<? extends ServerChannel> value) {
        for (final ServerChannel item : value) {
            this.withoutChannel(item);
        }
        return this;
    }

    public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (this.listeners != null) {
            this.listeners.firePropertyChange(propertyName, oldValue, newValue);
            return true;
        }
        return false;
    }

    public PropertyChangeSupport listeners() {
        if (this.listeners == null) {
            this.listeners = new PropertyChangeSupport(this);
        }
        return this.listeners;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(' ').append(this.getName());
        result.append(' ').append(this.getId());
        return result.substring(1);
    }

    public void removeYou() {
        this.withoutChannel(new ArrayList<>(this.getChannel()));
    }
}

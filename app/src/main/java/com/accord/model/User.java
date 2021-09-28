package com.accord.model;

import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class User
{
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_STATUS = "status";
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_USER_VOLUME = "userVolume";
    private String name;
    private String id;
    private boolean status;
    private String description;
    private double userVolume;
    protected PropertyChangeSupport listeners;

    public String getName()
    {
        return this.name;
    }

    public User setName(String value)
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

    public User setId(String value)
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

    public boolean isStatus()
    {
        return this.status;
    }

    public User setStatus(boolean value)
    {
        if (value == this.status)
        {
            return this;
        }

        final boolean oldValue = this.status;
        this.status = value;
        this.firePropertyChange(PROPERTY_STATUS, oldValue, value);
        return this;
    }

    public String getDescription()
    {
        return this.description;
    }

    public User setDescription(String value)
    {
        if (Objects.equals(value, this.description))
        {
            return this;
        }

        final String oldValue = this.description;
        this.description = value;
        this.firePropertyChange(PROPERTY_DESCRIPTION, oldValue, value);
        return this;
    }

    public double getUserVolume()
    {
        return this.userVolume;
    }

    public User setUserVolume(double value)
    {
        if (value == this.userVolume)
        {
            return this;
        }

        final double oldValue = this.userVolume;
        this.userVolume = value;
        this.firePropertyChange(PROPERTY_USER_VOLUME, oldValue, value);
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
}

package com.accord.model;

import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.Random;

public class Message {
    public static final String PROPERTY_FROM = "from";
    public static final String PROPERTY_MESSAGE = "message";
    public static final String PROPERTY_TIMESTAMP = "timestamp";
    public static final String PROPERTY_ID = "id";
    private String from;
    private String message;
    private long timestamp;
    private String id;
    private final Random random = new Random();
    private final int notificationId = random.nextInt();
    protected PropertyChangeSupport listeners;

    private String currentTime;

    public String getFrom() {
        return this.from;
    }

    public Message setFrom(String value) {
        if (Objects.equals(value, this.from)) {
            return this;
        }

        final String oldValue = this.from;
        this.from = value;
        this.firePropertyChange(PROPERTY_FROM, oldValue, value);
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    public Message setMessage(String value) {
        if (Objects.equals(value, this.message)) {
            return this;
        }

        final String oldValue = this.message;
        this.message = value;
        this.firePropertyChange(PROPERTY_MESSAGE, oldValue, value);
        return this;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public Message setTimestamp(long value) {
        if (value == this.timestamp) {
            return this;
        }

        final long oldValue = this.timestamp;
        this.timestamp = value;
        this.firePropertyChange(PROPERTY_TIMESTAMP, oldValue, value);
        return this;
    }

    public String getId() {
        return this.id;
    }

    public Message setId(String value) {
        if (Objects.equals(value, this.id)) {
            return this;
        }

        final String oldValue = this.id;
        this.id = value;
        this.firePropertyChange(PROPERTY_ID, oldValue, value);
        return this;
    }

    public int getNotificationId() {
        return notificationId;
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
        result.append(' ').append(this.getFrom());
        result.append(' ').append(this.getMessage());
        return result.substring(1);
    }

    public Message setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
        return this;
    }

    public String getCurrentTime() {
        return currentTime;
    }
}

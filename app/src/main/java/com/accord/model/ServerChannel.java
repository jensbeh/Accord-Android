package com.accord.model;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServerChannel {
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_MESSAGE = "message";
    public static final String PROPERTY_PRIVILEGE = "privilege";
    public static final String PROPERTY_TYPE = "type";
    public static final String PROPERTY_AUDIO_MEMBER = "audioMember";
    public static final String PROPERTY_PRIVILEGED_USERS = "privilegedUsers";
    private String name;
    private String id;
    protected PropertyChangeSupport listeners;
    private List<Message> message;
    private int unreadMessagesCounter;
    private boolean privilege;
    private String type;
    private List<User> audioMember;
    private List<User> privilegedUsers;

    public String getName() {
        return this.name;
    }

    public ServerChannel setName(String value) {
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

    public ServerChannel setId(String value) {
        if (Objects.equals(value, this.id)) {
            return this;
        }

        final String oldValue = this.id;
        this.id = value;
        this.firePropertyChange(PROPERTY_ID, oldValue, value);
        return this;
    }

    public List<Message> getMessage() {
        return this.message != null ? Collections.unmodifiableList(this.message) : Collections.emptyList();
    }

    public ServerChannel withMessage(Message value) {
        if (this.message == null) {
            this.message = new ArrayList<>();
        }
        if (!this.message.contains(value)) {
            this.message.add(value);
            this.firePropertyChange(PROPERTY_MESSAGE, null, value);
        }
        return this;
    }

    public ServerChannel withMessage(Message... value) {
        for (final Message item : value) {
            this.withMessage(item);
        }
        return this;
    }

    public ServerChannel withMessage(Collection<? extends Message> value) {
        for (final Message item : value) {
            this.withMessage(item);
        }
        return this;
    }

    public ServerChannel withoutMessage(Message value) {
        if (this.message != null && this.message.remove(value)) {
            this.firePropertyChange(PROPERTY_MESSAGE, value, null);
        }
        return this;
    }

    public ServerChannel withoutMessage(Message... value) {
        for (final Message item : value) {
            this.withoutMessage(item);
        }
        return this;
    }

    public ServerChannel withoutMessage(Collection<? extends Message> value) {
        for (final Message item : value) {
            this.withoutMessage(item);
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

    public int getUnreadMessagesCounter() {
        return unreadMessagesCounter;
    }

    public ServerChannel setUnreadMessagesCounter(int unreadMessagesCounter) {
        this.unreadMessagesCounter = unreadMessagesCounter;
        return this;
    }

    public ServerChannel setMessages(List<Message> list) {
        this.message = list;
        return this;
    }

    public List<Message> getMessages() {
        return this.message;
    }

    public boolean isPrivilege() {
        return this.privilege;
    }

    public ServerChannel setPrivilege(boolean value) {
        if (value == this.privilege) {
            return this;
        }

        final boolean oldValue = this.privilege;
        this.privilege = value;
        this.firePropertyChange(PROPERTY_PRIVILEGE, oldValue, value);
        return this;
    }

    public String getType() {
        return this.type;
    }

    public ServerChannel setType(String value) {
        if (Objects.equals(value, this.type)) {
            return this;
        }

        final String oldValue = this.type;
        this.type = value;
        this.firePropertyChange(PROPERTY_TYPE, oldValue, value);
        return this;
    }

    public List<User> getAudioMember() {
        return this.audioMember != null ? Collections.unmodifiableList(this.audioMember) : Collections.emptyList();
    }

    public ServerChannel withAudioMember(User value) {
        if (this.audioMember == null) {
            this.audioMember = new ArrayList<>();
        }
        if (!this.audioMember.contains(value)) {
            this.audioMember.add(value);
            this.firePropertyChange(PROPERTY_AUDIO_MEMBER, null, value);
        }
        return this;
    }

    public ServerChannel withAudioMember(User... value) {
        for (final User item : value) {
            this.withAudioMember(item);
        }
        return this;
    }

    public ServerChannel withAudioMember(Collection<? extends User> value) {
        for (final User item : value) {
            this.withAudioMember(item);
        }
        return this;
    }

    public ServerChannel withoutAudioMember(User value) {
        if (this.audioMember != null && this.audioMember.remove(value)) {
            this.firePropertyChange(PROPERTY_AUDIO_MEMBER, value, null);
        }
        return this;
    }

    public ServerChannel withoutAudioMember(User... value) {
        for (final User item : value) {
            this.withoutAudioMember(item);
        }
        return this;
    }

    public ServerChannel withoutAudioMember(Collection<? extends User> value) {
        for (final User item : value) {
            this.withoutAudioMember(item);
        }
        return this;
    }

    public List<User> getPrivilegedUsers() {
        return this.privilegedUsers != null ? Collections.unmodifiableList(this.privilegedUsers) : Collections.emptyList();
    }

    public ServerChannel withPrivilegedUsers(User value) {
        if (this.privilegedUsers == null) {
            this.privilegedUsers = new ArrayList<>();
        }
        if (!this.privilegedUsers.contains(value)) {
            this.privilegedUsers.add(value);
            this.firePropertyChange(PROPERTY_PRIVILEGED_USERS, null, value);
        }
        return this;
    }

    public ServerChannel withPrivilegedUsers(User... value) {
        for (final User item : value) {
            this.withPrivilegedUsers(item);
        }
        return this;
    }

    public ServerChannel withPrivilegedUsers(Collection<? extends User> value) {
        for (final User item : value) {
            this.withPrivilegedUsers(item);
        }
        return this;
    }

    public ServerChannel withoutPrivilegedUsers(User value) {
        if (this.privilegedUsers != null && this.privilegedUsers.remove(value)) {
            this.firePropertyChange(PROPERTY_PRIVILEGED_USERS, value, null);
        }
        return this;
    }

    public ServerChannel withoutPrivilegedUsers(User... value) {
        for (final User item : value) {
            this.withoutPrivilegedUsers(item);
        }
        return this;
    }

    public ServerChannel withoutPrivilegedUsers(Collection<? extends User> value) {
        for (final User item : value) {
            this.withoutPrivilegedUsers(item);
        }
        return this;
    }

    public void removeYou() {
        this.withoutAudioMember(new ArrayList<>(this.getAudioMember()));
        this.withoutPrivilegedUsers(new ArrayList<>(this.getPrivilegedUsers()));
        this.withoutMessage(new ArrayList<>(this.getMessage()));
    }
}
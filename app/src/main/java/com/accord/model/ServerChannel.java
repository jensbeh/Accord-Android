package com.accord.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServerChannel extends Channel {
    public static final String PROPERTY_PRIVILEGE = "privilege";
    public static final String PROPERTY_TYPE = "type";
    public static final String PROPERTY_AUDIO_MEMBER = "audioMember";
    public static final String PROPERTY_PRIVILEGED_USERS = "privilegedUsers";

    private boolean privilege;
    private String type;
    private List<User> audioMember;
    private List<User> privilegedUsers;

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
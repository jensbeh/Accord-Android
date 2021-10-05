package com.accord.net.rest.responses;

import java.util.ArrayList;
import java.util.List;

public class ResponseWithJsonList {
    private String status = null;
    private String message = null;
    private ArrayList<Data> data = null;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public class Data {
        private String id;
        private String name;
        private String description;
        private String server;
        private List<ServerChannel> channels;

        private String type;
        private boolean privileged;
        private String category;
        private List<Member> members;
        private List<AudioMember> audioMembers;

        private String channel;
        private long timestamp;
        private String from;
        private String text;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getServer() {
            return server;
        }

        public List<ServerChannel> getChannels() {
            return channels;
        }

        public String getType() {
            return type;
        }

        public boolean isPrivileged() {
            return privileged;
        }

        public String getCategory() {
            return category;
        }

        public List<Member> getMembers() {
            return members;
        }

        public List<AudioMember> getAudioMembers() {
            return audioMembers;
        }

        public String getChannel() {
            return channel;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getFrom() {
            return from;
        }

        public String getText() {
            return text;
        }
    }

    public class ServerChannel {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class Member {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class AudioMember {
        private String id;

        public String getId() {
            return id;
        }
    }
}

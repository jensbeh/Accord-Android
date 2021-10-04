package com.accord.net.rest.responses;

import java.util.List;

public class ResponseWithJsonObject {
    private String status;
    private String message;
    private Data data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        private String userKey;
        private String id;
        private String name;
        private String owner;
        private List<String> categories;
        private List<Member> members;

        public String getUserKey() {
            return userKey;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getOwner() {
            return owner;
        }

        public List getCategories() {
            return categories;
        }

        public List<Member> getMembers() {
            return members;
        }
    }

    public class Member {
        private String id;
        private String name;
        private boolean online;
        private String description;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isOnline() {
            return online;
        }

        public String getDescription() {
            return description;
        }
    }
}

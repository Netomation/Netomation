package main.com.netomation.api;

import main.com.netomation.data.Globals;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public abstract class SocialNetwork {

    private ArrayList<SocialNetworkUser> activeUsersList = new ArrayList<>();

    public abstract Object expose();

    public abstract Object getOwnID();

    public abstract SocialNetworkUser getUser(Object id);

    public abstract ArrayList<SocialNetworkUser> getExpansionGroupByUser(Object id);

    public abstract boolean sendPrivateMessage(Object id, String msg);

    public abstract void setCredentials(String... credentials);

    public abstract boolean shouldContactUser(SocialNetworkUser user);

    public abstract boolean handleException(Exception exp);

    public abstract Object getListenerStream();

    public abstract Object getConfiguration();

    public abstract boolean canSendPrivateMessage(SocialNetworkUser user);

    public abstract SocialNetworkPrivateMessage mapPrivateMessage(Object privateMessage);

    public abstract boolean createFriendship(Object id);

    public ArrayList<SocialNetworkUser> getActiveUsersList() {
        return activeUsersList;
    }

    public boolean updateActiveUsersList() {
        if(activeUsersList.size() == 0) {
            for(Object id : Globals.START_GROUP_IDS)
                activeUsersList.add(getUser(id));
            return true;
        }
        boolean toReturn = false;
        ArrayList<SocialNetworkUser> toCombine = new ArrayList<>();
        for(SocialNetworkUser user : activeUsersList) {
            ArrayList<SocialNetworkUser> temp = getExpansionGroupByUser(user.id);
            if(temp == null || temp.size() <= 0)
                continue;
            toReturn = true;
            toCombine.addAll(temp);
        }
        activeUsersList.addAll(toCombine);
        return toReturn;
    }

    public boolean blockUser(Object id) {return false;}

    public boolean getConnection(SocialNetworkUser user1, SocialNetworkUser user2) {return false;}

    public boolean unblockUser(Object id) {return false;}

    public static abstract class SocialNetworkUser<T> {

        protected T _user;

        public SocialNetworkUser(T originalUserObject) {
            _user = originalUserObject;
            setFirstName(mapFirstName());
            setLastName(mapLastName());
            setId(mapId());
            setParentId(mapParentId());
            setDescription(mapDescription());
            setGeoLocation(mapGeoLocation());
            setLanguage(mapLanguage());
        }

        private String firstName = null;
        private String lastName = null;
        private Object id = null;
        private Object parentId = null; // from whom we arrived to this user
        private String description = null;
        private Object geoLocation = null;
        private Object language = null;
        private Globals.ConnectionType connectionType = Globals.ConnectionType.NONE;
        private long firstMeetTimestamp = new Date().getTime();

        public String getFirstName() { return firstName; }
        public String getFirstNameDatabaseKey() { return Globals.MONGO_DB_FIRST_NAME_KEY; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        protected abstract String mapFirstName();

        public String getLastName() { return lastName; }
        public String getLastNameDatabaseKey() { return Globals.MONGO_DB_LAST_NAME_KEY; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        protected abstract String mapLastName();

        public Object getId() { return id; }
        public String getIdDatabaseKey() { return Globals.MONGO_DB_USER_ID_KEY; }
        public void setId(Object id) { this.id = id; }
        protected abstract Object mapId();

        public Object getParentId() { return parentId; }
        public String getParentIdDatabaseKey() { return Globals.MONGO_DB_USER_PARENT_ID_KEY; }
        public void setParentId(Object parentId) { this.parentId = parentId; }
        protected abstract Object mapParentId();

        public String getDescription() { return description; }
        public String getDescriptionDatabaseKey() { return Globals.MONGO_DB_USER_DESCRIPTION_KEY; }
        public void setDescription(String description) { this.description = description; }
        protected abstract String mapDescription();

        public Object getGeoLocation() { return geoLocation; }
        public String getGeoLocationDatabaseKey() { return Globals.MONGO_DB_USER_GEO_LOCATION_KEY; }
        public void setGeoLocation(Object geoLocation) { this.geoLocation = geoLocation; }
        protected abstract Object mapGeoLocation();

        public Object getLanguage() { return language; }
        public String getLanguageDatabaseKey() { return Globals.MONGO_DB_USER_LANGUAGE_KEY; }
        public void setLanguage(Object language) { this.language = language; }
        protected abstract Object mapLanguage();

        public abstract ArrayList<SocialNetworkUser> getExpansionGroup();

        public T getOriginalUser() {
            return _user;
        }

        public long getFirstMeetTimestamp() { return firstMeetTimestamp; }
        public String getFirstMeetTimestampDatabaseKey() { return Globals.MONGO_DB_USER_FIRST_MEET_TIMESTAMP_KEY; }
        public void setFirstMeetTimestamp(long firstMeetTimestamp) { this.firstMeetTimestamp = firstMeetTimestamp; }

        public Globals.ConnectionType getConnectionType() { return connectionType; }
        public String getConnectionTypeDatabaseKey() { return Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY; }
        public void setConnectionType(Globals.ConnectionType connectionType) { this.connectionType = connectionType; }
    }

    public static class SocialNetworkPrivateMessage {
        private long timestamp = new Date().getTime();
        private String id = UUID.randomUUID().toString();
        private String content = null;
        private Object fromUserId = null;
        private Object toUserId = null;

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Object getFromUserId() {
            return fromUserId;
        }

        public void setFromUserId(Object fromUserId) {
            this.fromUserId = fromUserId;
        }

        public Object getToUserId() {
            return toUserId;
        }

        public void setToUserId(Object toUserId) {
            this.toUserId = toUserId;
        }
    }

}
package main.com.netomation.api;

import main.com.netomation.cache.MongoCache;
import main.com.netomation.data.Globals;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public abstract class SocialNetwork {

    private ArrayList<SocialNetworkUser> activeUsersList = new ArrayList<>();

    public abstract Object expose();

    public abstract Object getOwnID();

    public abstract String getOwnName();

    public abstract boolean isConnected();

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

    public int updateActiveUsersList(int i) {
        if (activeUsersList.size() == 0) {
            activeUsersList = convertIDsArrayToSocialNetworkUsers(MongoCache.getInstance().getQueueFromLastRunning());
            if(activeUsersList == null) {
                activeUsersList = new ArrayList<>();
                for (Object id : Globals.START_GROUP_IDS)
                    activeUsersList.add(getUser(id));
            }
            return activeUsersList.size();
        }
        ArrayList<SocialNetworkUser> toCombine = getExpansionGroupByUser(activeUsersList.get(i).id);
        if (toCombine == null || toCombine.size() <= 0) {
            if(i == activeUsersList.size()-1) {
                return -1;
            }
            return 0;
        }
        activeUsersList.addAll(toCombine);
        return toCombine.size();
    }

    public ArrayList<SocialNetworkUser> convertIDsArrayToSocialNetworkUsers(ArrayList<Object> ids) {
        if(ids == null) {
            return null;
        }
        ArrayList<SocialNetworkUser> toReturn = new ArrayList<>();
        for(Object id : ids) {
            toReturn.add(getUser(id));
        }
        return toReturn;
    }

    public boolean removeFriendship(Object id) { return false; }

    public boolean blockUser(Object id) { return false; }

    public boolean getConnection(SocialNetworkUser user1, SocialNetworkUser user2) { return false; }

    public boolean uploadStatus(String status) { return false; }

    public boolean updateStatus(Object statusId, String status) { return false; }

    public boolean deleteStatus(Object statusId) { return false; }

    public boolean updateProfile(Object... args) { return false; }

    public boolean unblockUser(Object id) { return false; }

    public boolean getPrivateMessage(Object id) { return false; }

    public boolean deletePrivateMessage(Object id) { return false; }

    public boolean reportSpam(Object id) { return false; }

    public boolean getTimeline() { return false; }

    public static abstract class SocialNetworkUser<T> {

        protected T _user;

        private String firstName = null;
        private String lastName = null;
        private Object id = null;
        private Object parentId = null; // from whom we arrived to this user
        private String description = null;
        private Object geoLocation = null;
        private Object language = null;
        private boolean clicked = false;
        private Date clickedTimestamp = null;
        private String connectionType = Globals.ConnectionType.NONE;
        private Date firstMeetTimestamp = new Date();

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

        protected abstract String mapFirstName();

        protected abstract String mapLastName();

        protected abstract Object mapId();

        protected abstract Object mapParentId();

        protected abstract String mapDescription();

        protected abstract Object mapGeoLocation();

        protected abstract Object mapLanguage();

        public abstract ArrayList<SocialNetworkUser> getExpansionGroup();

        public String getConnectionTypeDatabaseKey() {
            return Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY;
        }

        public String getFirstMeetTimestampDatabaseKey() {
            return Globals.MONGO_DB_USER_FIRST_MEET_TIMESTAMP_KEY;
        }

        public String getLanguageDatabaseKey() {
            return Globals.MONGO_DB_USER_LANGUAGE_KEY;
        }

        public String getGeoLocationDatabaseKey() {
            return Globals.MONGO_DB_USER_GEO_LOCATION_KEY;
        }

        public String getDescriptionDatabaseKey() {
            return Globals.MONGO_DB_USER_DESCRIPTION_KEY;
        }

        public String getParentIdDatabaseKey() {
            return Globals.MONGO_DB_USER_PARENT_ID_KEY;
        }

        public String getIdDatabaseKey() {
            return Globals.MONGO_DB_USER_ID_KEY;
        }

        public String getLastNameDatabaseKey() {
            return Globals.MONGO_DB_LAST_NAME_KEY;
        }

        public String getFirstNameDatabaseKey() {
            return Globals.MONGO_DB_FIRST_NAME_KEY;
        }

        public String getClickedDatabaseKey() {
            return Globals.MONGO_DB_USER_CLICKED_KEY;
        }

        public boolean getClicked() {
            return clicked;
        }

        public void setClicked(boolean clicked) {
            this.clicked = clicked;
        }

        public Date getClickedTimestamp() {
            return clickedTimestamp;
        }

        public void setClickedTimestamp(Date clickedTimestamp) {
            this.clickedTimestamp = clickedTimestamp;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Object getId() {
            return id;
        }

        public void setId(Object id) {
            this.id = id;
        }

        public Object getParentId() {
            return parentId;
        }

        public void setParentId(Object parentId) {
            this.parentId = parentId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Object getGeoLocation() {
            return geoLocation;
        }

        public void setGeoLocation(Object geoLocation) {
            this.geoLocation = geoLocation;
        }

        public Object getLanguage() {
            return language;
        }

        public void setLanguage(Object language) {
            this.language = language;
        }

        public T getOriginalUser() {
            return _user;
        }

        public Date getFirstMeetTimestamp() {
            return firstMeetTimestamp;
        }

        public void setFirstMeetTimestamp(Date firstMeetTimestamp) {
            this.firstMeetTimestamp = firstMeetTimestamp;
        }

        public String getConnectionType() {
            return connectionType;
        }

        public void setConnectionType(String connectionType) {
            this.connectionType = connectionType;
        }

        public class SocialNetworkUserCodec implements Codec<SocialNetworkUser> {

            @Override
            public SocialNetworkUser decode(BsonReader reader, DecoderContext decoderContext) {
                return null;
            }

            @Override
            public void encode(BsonWriter writer, SocialNetworkUser value, EncoderContext encoderContext) {
                writer.writeString(Globals.MONGO_DB_FIRST_NAME_KEY, value.getFirstName());
                writer.writeString(Globals.MONGO_DB_LAST_NAME_KEY, value.getLastName());
                writer.writeString(Globals.MONGO_DB_USER_ID_KEY, value.getId().toString());
                writer.writeString(Globals.MONGO_DB_USER_PARENT_ID_KEY, value.getParentId().toString());
                writer.writeString(Globals.MONGO_DB_USER_DESCRIPTION_KEY, value.getDescription());
                writer.writeString(Globals.MONGO_DB_USER_GEO_LOCATION_KEY, value.getGeoLocation().toString());
                writer.writeString(Globals.MONGO_DB_USER_LANGUAGE_KEY, value.getLanguage().toString());
                writer.writeString(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY, value.getConnectionType());
                writer.writeBoolean(Globals.MONGO_DB_USER_CLICKED_KEY, value.getClicked());
                writer.writeDateTime(Globals.MONGO_DB_USER_FIRST_MEET_TIMESTAMP_KEY, value.getFirstMeetTimestamp().getTime());
            }

            @Override
            public Class<SocialNetworkUser> getEncoderClass() {
                return SocialNetworkUser.class;
            }
        }

    }

    public static class SocialNetworkPrivateMessage {
        private Date timestamp = new Date();
        private String id = UUID.randomUUID().toString();
        private String content = null;
        private Object fromUserId = null;
        private Object toUserId = null;

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
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

        public static class SocialNetworkPrivateMessageCodec implements Codec<SocialNetworkPrivateMessage> {
            @Override
            public SocialNetworkPrivateMessage decode(BsonReader reader, DecoderContext decoderContext) {
                SocialNetworkPrivateMessage toReturn = new SocialNetworkPrivateMessage();
                toReturn.timestamp = new Date(reader.readDateTime(Globals.MONGO_DB_MESSAGE_TIMESTAMP_KEY));
                toReturn.id = reader.readString(Globals.MONGO_DB_MESSAGE_ID_KEY);
                toReturn.content = reader.readString(Globals.MONGO_DB_MESSAGE_CONTENT_KEY);
                toReturn.fromUserId = reader.readString(Globals.MONGO_DB_FROM_USER_ID_KEY);
                toReturn.toUserId = reader.readString(Globals.MONGO_DB_TO_USER_ID_KEY);
                return toReturn;
            }

            @Override
            public void encode(BsonWriter writer, SocialNetworkPrivateMessage value, EncoderContext encoderContext) {
                writer.writeString(value.id);
                writer.writeString(value.content);
                writer.writeString(value.fromUserId.toString());
                writer.writeString(value.toUserId.toString());
                writer.writeDateTime(value.timestamp.getTime());
            }

            @Override
            public Class<SocialNetworkPrivateMessage> getEncoderClass() {
                return SocialNetworkPrivateMessage.class;
            }
        }

        class SocialNetworkPrivateMessageCodecProvider implements CodecProvider {
            @Override
            public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
                if (clazz==SocialNetworkPrivateMessage.class) {
                    return (Codec<T>) new SocialNetworkPrivateMessageCodec();
                }
                return null;
            }
        }
    }

}
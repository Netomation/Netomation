package main.com.netomation.cache;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.xml.internal.ws.api.model.MEP;
import main.com.netomation.api.SocialNetwork;
import main.com.netomation.data.Globals;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class MongoCache {

    private static MongoCache instance = null;
    private MongoClient mongoClient = null;
    private MongoDatabase database = null;

    public synchronized static MongoCache getInstance() {
        if(instance == null) {
            instance = new MongoCache();
        }
        return instance;
    }

    public static boolean validConnection() {
        try {
            new MongoClient(Globals.MONGO_DB_ADDRESS, Globals.MONGO_DB_PORT).getAddress();
            return true;
        } catch (Exception exp) {return false;}
    }

    private MongoCache() {
        connect();
    }

    private void connect() {
        mongoClient = new MongoClient(Globals.MONGO_DB_ADDRESS, Globals.MONGO_DB_PORT);
        database = mongoClient.getDatabase(Globals.MONGO_DB_DATABASE_NAME);
    }

    private synchronized void putToUsersTable(HashMap<String, Object> values) {
        MongoCollection<Document> collection = database.getCollection(Globals.MONGO_DB_USERS_COLLECTION_NAME);
        BasicDBObject query = new BasicDBObject();
        query.put(Globals.MONGO_DB_USER_ID_KEY, values.get(Globals.MONGO_DB_USER_ID_KEY));
        FindIterable<Document> result = collection.find(query);
        if(result.first() == null) { // new entry
            Document document = new Document();
            document.put(Globals.MONGO_DB_ENTRY_CREATION_TIME_KEY, new Date());
            for (String key : values.keySet()) {
                Object value = values.get(key);
                if(value == null) {
                    document.put(key, null);
                } else if (value.getClass().isEnum()) {
                    value = value.toString();
                } else if(value.getClass().isArray()) {
                    Object[] array = (Object[])value;
                    ArrayList<Object> list = new ArrayList<>();
                    Collections.addAll(list, array);
                    value = list;
                }
                document.put(key, value);
            }
            collection.insertOne(document);
        } else { // edit entry
//            BasicDBObject updatedDocument = new BasicDBObject();
//            for (String key : values.keySet()) {
//                if(key.equals(Globals.MONGO_DB_USER_ID_KEY) && result.first().get(Globals.MONGO_DB_USER_ID_KEY) != null) {
//                    continue;
//                }
//                if(key.equals(Globals.MONGO_DB_ENTRY_CREATION_TIME_KEY) && result.first().get(Globals.MONGO_DB_ENTRY_CREATION_TIME_KEY) != null) {
//                    continue;
//                }
//                if(key.equals(Globals.MONGO_DB_USER_FIRST_MEET_TIMESTAMP_KEY) && result.first().get(Globals.MONGO_DB_USER_FIRST_MEET_TIMESTAMP_KEY) != null) {
//                    continue;
//                }
//                if(key.equals(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY) && result.first().get(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY) != null) {
//                    continue;
//                }
//                if(key.equals(Globals.MONGO_DB_USER_CLICKED_KEY) && result.first().get(Globals.MONGO_DB_USER_CLICKED_KEY) != null) {
//                    continue;
//                }
//                updatedDocument.put("$set", new BasicDBObject().append(key, values.get(key)));
//                updateCollection(collection, query, updatedDocument);
//            }
        }
    }

    public void putToUsersTable(SocialNetwork.SocialNetworkUser user) {
        HashMap<String, Object> values = new HashMap<>();
        Method[] methods = user.getClass().getMethods();
        for(Method method : methods) {
            if(method.getName().endsWith("DatabaseKey")) {
                try {
                    String key = method.invoke(user).toString();
                    Object value = user.getClass().getMethod(method.getName().split("DatabaseKey")[0]).invoke(user);
                    values.put(key, value);
                }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
        putToUsersTable(values);
    }

    // Dont call this method without making sure the DB has entry with the same ID
    public synchronized void addMessageToUser(Object userID, SocialNetwork.SocialNetworkPrivateMessage message) {
        MongoCollection<Document> collection = database.getCollection(Globals.MONGO_DB_USERS_COLLECTION_NAME);
        BasicDBObject query = new BasicDBObject();
        query.put(Globals.MONGO_DB_USER_ID_KEY, userID);
        Document user = collection.find(query).first();
        if(user == null)
            return;
        else if(user.containsKey(Globals.MONGO_DB_USER_PRIVATE_MESSAGES_KEY)) {
            BasicDBObject updateQuery = new BasicDBObject("$push", new BasicDBObject().append(Globals.MONGO_DB_USER_PRIVATE_MESSAGES_KEY, parseMessage(message)));
            updateCollection(collection, query, updateQuery);
        } else {
            ArrayList<HashMap<String ,Object>> toAdd = new ArrayList<>();
            toAdd.add(parseMessage(message));
            BasicDBObject updatedDocument = new BasicDBObject();
            updatedDocument.put("$set", new BasicDBObject().append(Globals.MONGO_DB_USER_PRIVATE_MESSAGES_KEY, toAdd));
            updateCollection(collection, query, updatedDocument);
        }
    }

    private HashMap<String ,Object> parseMessage(SocialNetwork.SocialNetworkPrivateMessage message) {
        HashMap<String ,Object> toReturn = new HashMap<>();
        toReturn.put(Globals.MONGO_DB_MESSAGE_CONTENT_KEY, message.getContent());
        toReturn.put(Globals.MONGO_DB_MESSAGE_ID_KEY, message.getId());
        toReturn.put(Globals.MONGO_DB_MESSAGE_TIMESTAMP_KEY, message.getTimestamp());
        toReturn.put(Globals.MONGO_DB_FROM_USER_ID_KEY, message.getFromUserId());
        toReturn.put(Globals.MONGO_DB_TO_USER_ID_KEY, message.getToUserId());
        return toReturn;
    }

    public void deleteAllDataFromDatabase() {
        MongoCollection<Document> collection = database.getCollection(Globals.MONGO_DB_USERS_COLLECTION_NAME);
        collection.drop();
    }

    public void followingUser(SocialNetwork.SocialNetworkUser user) {
        if(!userExistInDB(user.getId())) {
            this.putToUsersTable(user);
        }
        MongoCollection<Document> collection = database.getCollection(Globals.MONGO_DB_USERS_COLLECTION_NAME);
        BasicDBObject query = new BasicDBObject();
        query.put(Globals.MONGO_DB_USER_ID_KEY, user.getId());
        Document result = collection.find(query).first();
        String connectionType = result.get(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY).toString();
        BasicDBObject updatedDocument = new BasicDBObject();
        if(connectionType.equals(Globals.ConnectionType.NONE)) {
            updatedDocument.put("$set", new BasicDBObject().append(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY,Globals.ConnectionType.FROM_ME));
        } else if(connectionType.equals(Globals.ConnectionType.BOTH)) {
            updatedDocument.put("$set", new BasicDBObject().append(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY,Globals.ConnectionType.BOTH));
        } else if(connectionType.equals(Globals.ConnectionType.FROM_HIM)) {
            updatedDocument.put("$set", new BasicDBObject().append(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY,Globals.ConnectionType.BOTH));
        } else if(connectionType.equals(Globals.ConnectionType.FROM_ME)) {
            updatedDocument.put("$set", new BasicDBObject().append(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY,Globals.ConnectionType.FROM_ME));
        }
        updateCollection(collection, query, updatedDocument);
    }

    public void userFollowUs(SocialNetwork.SocialNetworkUser user) {
        if(!userExistInDB(user.getId())) {
            this.putToUsersTable(user);
        }
        MongoCollection<Document> collection = database.getCollection(Globals.MONGO_DB_USERS_COLLECTION_NAME);
        BasicDBObject query = new BasicDBObject();
        query.put(Globals.MONGO_DB_USER_ID_KEY, user.getId());
        Document result = collection.find(query).first();
        String connectionType = result.get(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY).toString();
        BasicDBObject updatedDocument = new BasicDBObject();
        if(connectionType.equals(Globals.ConnectionType.NONE)) {
            updatedDocument.put("$set", new BasicDBObject().append(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY,Globals.ConnectionType.FROM_HIM));
        } else if(connectionType.equals(Globals.ConnectionType.BOTH)) {
            updatedDocument.put("$set", new BasicDBObject().append(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY,Globals.ConnectionType.BOTH));
        } else if(connectionType.equals(Globals.ConnectionType.FROM_HIM)) {
            updatedDocument.put("$set", new BasicDBObject().append(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY,Globals.ConnectionType.FROM_HIM));
        } else if(connectionType.equals(Globals.ConnectionType.FROM_ME)) {
            updatedDocument.put("$set", new BasicDBObject().append(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY,Globals.ConnectionType.BOTH));
        }
        updateCollection(collection, query, updatedDocument);
    }

    public void userStoppedFollowUs(SocialNetwork.SocialNetworkUser user) {
        if(!userExistInDB(user.getId())) {
            this.putToUsersTable(user);
        }
        MongoCollection<Document> collection = database.getCollection(Globals.MONGO_DB_USERS_COLLECTION_NAME);
        BasicDBObject query = new BasicDBObject();
        query.put(Globals.MONGO_DB_USER_ID_KEY, user.getId());
        Document result = collection.find(query).first();
        String connectionType = result.get(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY).toString();
        BasicDBObject updatedDocument = new BasicDBObject();
        if(connectionType.equals(Globals.ConnectionType.NONE)) {
            updatedDocument.put("$set", new BasicDBObject().append(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY,Globals.ConnectionType.NONE));
        } else if(connectionType.equals(Globals.ConnectionType.BOTH)) {
            updatedDocument.put("$set", new BasicDBObject().append(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY,Globals.ConnectionType.FROM_ME));
        } else if(connectionType.equals(Globals.ConnectionType.FROM_HIM)) {
            updatedDocument.put("$set", new BasicDBObject().append(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY,Globals.ConnectionType.NONE));
        } else if(connectionType.equals(Globals.ConnectionType.FROM_ME)) {
            updatedDocument.put("$set", new BasicDBObject().append(Globals.MONGO_DB_USER_CONNECTION_TYPE_KEY,Globals.ConnectionType.FROM_ME));
        }
        updateCollection(collection, query, updatedDocument);
    }

    private synchronized void updateCollection(MongoCollection<Document> collection, Bson filter, Bson update) {
        collection.updateOne(filter, update);
    }

    public boolean userExistInDB(Object id) {
        MongoCollection<Document> collection = database.getCollection(Globals.MONGO_DB_USERS_COLLECTION_NAME);
        BasicDBObject query = new BasicDBObject();
        query.put(Globals.MONGO_DB_USER_ID_KEY, id);
        FindIterable<Document> result = collection.find(query);
        return result.first() != null;
    }

}

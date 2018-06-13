package main.com.netomation.cache;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import main.com.netomation.api.SocialNetwork;
import main.com.netomation.data.Globals;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.omg.PortableInterceptor.Interceptor;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class MongoCache {

    private static MongoCache instance = null;
    private MongoDatabase database = null;

    public synchronized static MongoCache getInstance() {
        if(instance == null) {
            instance = new MongoCache();
        }
        return instance;
    }

    public static boolean validConnection() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
        boolean toReturn = false;
        MongoCredential credential = MongoCredential.createCredential(Globals.MONGO_DB_CONNECTION_USERNAME, "admin", Globals.MONGO_DB_CONNECTION_PASSWORD.toCharArray());
        MongoClientOptions options = new MongoClientOptions.Builder().serverSelectionTimeout(1000).build();
        try {
            try{new MongoClient(new ServerAddress(Globals.MONGO_DB_ADDRESS, Globals.MONGO_DB_PORT),credential, options).getAddress();}
            catch (Exception ignore) {
                new MongoClient(new ServerAddress(Globals.MONGO_DB_ADDRESS, Globals.MONGO_DB_DEFAULT_PORT),credential, options).getAddress();
            }
            toReturn = true;
        } catch (Exception ignore) {}
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
        return toReturn;
    }

    public static int serverRunsOnPort() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
        MongoCredential credential = MongoCredential.createCredential(Globals.MONGO_DB_CONNECTION_USERNAME, "admin", Globals.MONGO_DB_CONNECTION_PASSWORD.toCharArray());
        MongoClientOptions options = new MongoClientOptions.Builder().serverSelectionTimeout(1000).build();
        int portToReturn = Globals.MONGO_DB_PORT;
        try {
            try{new MongoClient(new ServerAddress(Globals.MONGO_DB_ADDRESS, portToReturn),credential, options).getAddress();}
            catch (Exception ignore) {
                portToReturn = Globals.MONGO_DB_DEFAULT_PORT;
                new MongoClient(new ServerAddress(Globals.MONGO_DB_ADDRESS, portToReturn),credential, options).getAddress();
            }
        } catch (Exception ignore) {}
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
        return portToReturn;
    }

    private MongoCache() {
        connect();
    }

    private void connect() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
        MongoCredential credential = MongoCredential.createCredential(Globals.MONGO_DB_CONNECTION_USERNAME, "admin", Globals.MONGO_DB_CONNECTION_PASSWORD.toCharArray());
        MongoClientOptions options = new MongoClientOptions.Builder().serverSelectionTimeout(1000).build();
        MongoClient mongoClient = new MongoClient(new ServerAddress(Globals.MONGO_DB_ADDRESS, serverRunsOnPort()), credential, options);
        database = mongoClient.getDatabase(Globals.MONGO_DB_DATABASE_NAME);
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
    }

    private synchronized void putToUsersTable(HashMap<String, Object> values) {
        MongoCollection<Document> collection = database.getCollection(Globals.MONGO_DB_USERS_COLLECTION_NAME);
        BasicDBObject query = new BasicDBObject();
        query.put(Globals.MONGO_DB_USER_ID_KEY, values.get(Globals.MONGO_DB_USER_ID_KEY));
        FindIterable<Document> result = collection.find(query);
        if(result.first() == null) {
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
        }
    }

    public void putToUsersTable(SocialNetwork.SocialNetworkUser user) {
        putToUsersTable(parseSocialNetworkUserToHashMap(user));
    }

    private HashMap<String, Object> parseSocialNetworkUserToHashMap(SocialNetwork.SocialNetworkUser user) {
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
        return values;
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
        database.getCollection(Globals.MONGO_DB_USERS_COLLECTION_NAME).drop();
        database.getCollection(Globals.MONGO_DB_ACTIVE_USERS_COLLECTION_NAME).drop();
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
        return getUserDocumentById(id) != null;
    }

    private Document getUserDocumentById(Object id) {
        MongoCollection<Document> collection = database.getCollection(Globals.MONGO_DB_USERS_COLLECTION_NAME);
        BasicDBObject query = new BasicDBObject();
        query.put(Globals.MONGO_DB_USER_ID_KEY, id);
        FindIterable<Document> result = collection.find(query);
        return result.first();
    }

    public void updateActiveUsers(ArrayList<SocialNetwork.SocialNetworkUser> queue, int arrayIndex, int updateFromIndex) {
        database.getCollection(Globals.MONGO_DB_ACTIVE_USERS_COLLECTION_NAME).drop();
        Document document = new Document();
        document.put(Globals.MONGO_DB_ACTIVE_USERS_COLLECTION_NAME, Globals.MONGO_DB_ACTIVE_USERS_COLLECTION_NAME);
        document.put(Globals.MONGO_DB_ACTIVE_USERS_QUEUE, parseSocialNetworkUsersToIds(queue));
        document.put(Globals.MONGO_DB_ACTIVE_USERS_ARRAY_INDEX, arrayIndex);
        document.put(Globals.MONGO_DB_ACTIVE_USERS_UPDATE_FROM_INDEX, updateFromIndex);
        database.getCollection(Globals.MONGO_DB_ACTIVE_USERS_COLLECTION_NAME).insertOne(document);
    }

    public ArrayList<Object> parseSocialNetworkUsersToIds(ArrayList<SocialNetwork.SocialNetworkUser> queue) {
        ArrayList<Object> toReturn = new ArrayList<>();
        for(SocialNetwork.SocialNetworkUser user : queue)
            toReturn.add(user.getId());
        return toReturn;
    }

    public ArrayList<Object> getQueueFromLastRunning() {
        Document doc =  getQueueDocument();
        if(doc == null) {
            return null;
        }
        return (ArrayList<Object>)doc.get(Globals.MONGO_DB_ACTIVE_USERS_QUEUE);
    }

    public int getArrayIndexQueueFromLastRunning() {
        Document result = getQueueDocument();
        if(result == null) {
            return 0;
        }
        return result.getInteger(Globals.MONGO_DB_ACTIVE_USERS_ARRAY_INDEX);
    }

    public int getUpdateFromIndexQueueFromLastRunning() {
        Document result = getQueueDocument();
        if(result == null) {
            return 0;
        }
        return result.getInteger(Globals.MONGO_DB_ACTIVE_USERS_UPDATE_FROM_INDEX);
    }

    private Document getQueueDocument() {
        BasicDBObject query = new BasicDBObject();
        query.put(Globals.MONGO_DB_ACTIVE_USERS_COLLECTION_NAME, Globals.MONGO_DB_ACTIVE_USERS_COLLECTION_NAME);
        return database.getCollection(Globals.MONGO_DB_ACTIVE_USERS_COLLECTION_NAME).find(query).first();
    }

    public String getDbKeyByUserId(Object id) {
        Document doc = getUserDocumentById(id);
        if(doc == null) {
            return "";
        }
        return doc.get("_id").toString();
    }

}

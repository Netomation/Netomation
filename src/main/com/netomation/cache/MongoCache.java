package main.com.netomation.cache;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import main.com.netomation.api.SocialNetwork;
import main.com.netomation.data.Globals;
import org.bson.Document;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class MongoCache {

    private static MongoCache instance = null;
    private MongoClient mongoClient = null;
    private MongoDatabase database = null;

    public static MongoCache getInstance() {
        if(instance == null)
            instance = new MongoCache();
        return instance;
    }

    private MongoCache() {
        connect();
    }

    private void connect() {
        mongoClient = new MongoClient(Globals.MONGO_DB_ADDRESS, Globals.MONGO_DB_PORT);
        database = mongoClient.getDatabase(Globals.MONGO_DB_DATABASE_NAME);
    }

    public void putToUsersTable(HashMap<String, Object> values) {
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
            BasicDBObject updatedDocument = new BasicDBObject();
            for (String key : values.keySet()) {
                updatedDocument.put("$set", new BasicDBObject().append(key, values.get(key)));
                collection.updateOne(query, updatedDocument);
            }
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

    public void addMessageToUser(Object userID, SocialNetwork.SocialNetworkPrivateMessage message) {

    }

    public void deleteAllDataFromDatabase() {
        MongoCollection<Document> collection = database.getCollection(Globals.MONGO_DB_USERS_COLLECTION_NAME);
        collection.drop();
    }

}

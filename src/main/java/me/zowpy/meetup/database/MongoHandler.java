package me.zowpy.meetup.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

import java.io.Closeable;

@Getter
public class MongoHandler implements Closeable {

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private final MongoCollection<Document> profiles;

    public MongoHandler(String uri) {
        mongoClient = new MongoClient(new MongoClientURI(uri));
        database = mongoClient.getDatabase("zMeetup");

        profiles = database.getCollection("profiles");
    }

    @Override
    public void close() {
        if (mongoClient != null)
            mongoClient.close();
    }
}

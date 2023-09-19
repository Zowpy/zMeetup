package me.zowpy.meetup.leaderboard;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.leaderboard.enums.LeaderboardType;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
@RequiredArgsConstructor
public class LeaderboardHandler {

    private final MeetupPlugin plugin;

    @Setter
    private long lastUpdate;

    private final ConcurrentMap<LeaderboardType, List<LeaderboardEntry>> leaderboardEntries = new ConcurrentHashMap<>();

    public void update() {

        if (Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("You can't update leaderboards on main thread.");
        }

        FindIterable<Document> documents = plugin.getMongoHandler().getProfiles().find();

        for (LeaderboardType type : LeaderboardType.values()) {

            List<LeaderboardEntry> entries = new ArrayList<>();

            documents.sort(Filters.eq(type.getField(), -1)).limit(10).forEach((Block<? super Document>) document -> {

                entries.add(new LeaderboardEntry(
                        UUID.fromString(document.getString("_id")),
                        document.getString("name"),
                        document.getInteger(type.getField())
                ));

                leaderboardEntries.put(type, entries);
            });

        }

        lastUpdate = System.currentTimeMillis();
    }
}

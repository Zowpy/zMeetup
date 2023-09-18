package me.zowpy.meetup.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class ProfileHandler {

    private final MeetupPlugin plugin;

    private final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    private final UpdateOptions UPDATE_OPTIONS = new UpdateOptions().upsert(true);

    public void save(Profile profile) {
        plugin.getMongoHandler().getProfiles().updateOne(
                Filters.eq("_id", profile.getUuid().toString()),
                new Document("$set", Document.parse(GSON.toJson(profile))),
                UPDATE_OPTIONS
        );
    }

    public Profile findByUUID(UUID uuid) {
        Document document = plugin.getMongoHandler().getProfiles().find(Filters.eq("_id", uuid.toString())).first();

        if (document == null) return null;

        return GSON.fromJson(document.toJson(), Profile.class);
    }

    public Profile findOrDefault(Player player) {
        Profile profile = findByUUID(player.getUniqueId());

        if (profile == null) {
            return new Profile(player.getUniqueId(), player.getName());
        }

        return profile;
    }

    public void loss(Player player) {
        Profile profile = findOrDefault(player);
        profile.setLosses(profile.getLosses() + 1);

        save(profile);
    }

    public void win(Player player) {
        Profile profile = findOrDefault(player);
        profile.setWins(profile.getWins() + 1);

        save(profile);
    }

    public void gamePlayed(Player player) {
        Profile profile = findOrDefault(player);
        profile.setGamesPlayed(profile.getGamesPlayed() + 1);

        save(profile);
    }

    public void kill(Player player) {
        Profile profile = findOrDefault(player);
        profile.setKills(profile.getKills() + 1);

        save(profile);
    }

    public void death(Player player) {
        Profile profile = findOrDefault(player);
        profile.setDeaths(profile.getDeaths() + 1);

        save(profile);
    }
}

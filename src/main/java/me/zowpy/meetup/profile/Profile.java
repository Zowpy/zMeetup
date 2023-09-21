package me.zowpy.meetup.profile;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import me.zowpy.meetup.loadout.Loadout;

import java.util.UUID;

@Data
public class Profile {

    @SerializedName("_id")
    private final UUID uuid;

    private String name;

    private int gamesPlayed, wins, losses, kills, deaths;

    private Loadout loadout = new Loadout();

    private long lastAnnounce = -1L;

    public Profile(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
}

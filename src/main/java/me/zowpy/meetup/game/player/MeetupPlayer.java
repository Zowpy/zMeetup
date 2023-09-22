package me.zowpy.meetup.game.player;

import lombok.Data;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class MeetupPlayer {

    private final UUID uuid;
    private final String name;

    private boolean dead;
    private boolean spectating;

    private int kills = 0;

    public MeetupPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    public boolean isPlaying() {
        return !dead && !spectating;
    }
}

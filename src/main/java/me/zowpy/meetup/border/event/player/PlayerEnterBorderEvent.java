package me.zowpy.meetup.border.event.player;

import lombok.Getter;
import me.zowpy.meetup.border.Border;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public class PlayerEnterBorderEvent extends PlayerBorderEvent {

    private final Location from;
    private final Location to;

    public PlayerEnterBorderEvent(Border border, Player player, Location from, Location to) {
        super(border, player);

        this.from = from;
        this.to = to;
    }
}

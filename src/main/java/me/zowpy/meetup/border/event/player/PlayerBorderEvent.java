package me.zowpy.meetup.border.event.player;

import lombok.Getter;
import lombok.Setter;
import me.zowpy.meetup.border.event.border.BorderEvent;
import me.zowpy.meetup.border.Border;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Getter
public class PlayerBorderEvent extends BorderEvent implements Cancellable {

    private final Player player;
    @Setter private boolean cancelled;

    public PlayerBorderEvent(Border border, Player player) {
        super(border);

        this.player = player;
    }

}

package me.zowpy.meetup.border.event.border;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.border.Border;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public class BorderEvent extends Event {

    @Getter private static final HandlerList handlerList = new HandlerList();

    @Getter private final Border border;

    public HandlerList getHandlers() {
        return handlerList;
    }
}


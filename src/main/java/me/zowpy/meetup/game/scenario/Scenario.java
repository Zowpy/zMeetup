package me.zowpy.meetup.game.scenario;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Listener;

@RequiredArgsConstructor @Getter
public abstract class Scenario implements Listener {

    private final String name;

    public abstract void enable();
    public abstract void disable();
}

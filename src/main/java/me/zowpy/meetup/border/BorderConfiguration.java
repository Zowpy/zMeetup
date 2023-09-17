package me.zowpy.meetup.border;

import me.zowpy.meetup.border.action.DefaultBorderActions;
import me.zowpy.meetup.border.event.border.BorderChangeEvent;
import me.zowpy.meetup.border.event.player.PlayerEnterBorderEvent;
import me.zowpy.meetup.border.event.player.PlayerExitBorderEvent;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
public class BorderConfiguration {

    public static final BorderConfiguration DEFAULT_CONFIGURATION = new BorderConfiguration();

    private final Set<Consumer<BorderChangeEvent>> defaultBorderChangeActions = new HashSet<>();
    private final Set<Consumer<PlayerEnterBorderEvent>> defaultBorderEnterActions = new HashSet<>();
    private final Set<Consumer<PlayerExitBorderEvent>> defaultBorderExitActions = new HashSet<>();

    public BorderConfiguration() {
        defaultBorderChangeActions.add(DefaultBorderActions.ENSURE_PLAYERS_IN_BORDER);
        defaultBorderExitActions.add(DefaultBorderActions.PUSHBACK_ON_EXIT);
        defaultBorderExitActions.add(DefaultBorderActions.CANCEL_EXIT);
    }

}

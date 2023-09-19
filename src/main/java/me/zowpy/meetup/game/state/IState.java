package me.zowpy.meetup.game.state;

import me.zowpy.meetup.game.enums.GameState;

public interface IState {

    void enable();
    void disable();

    GameState getGameState();
}

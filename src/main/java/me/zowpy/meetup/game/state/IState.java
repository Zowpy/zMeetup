package me.zowpy.meetup.game.state;

public interface IState {

    void enable();
    void disable();

    GameState getGameState();
}

package me.zowpy.meetup.leaderboard.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LeaderboardType {

    KILLS("kills"),
    DEATHS("deaths"),
    WINS("wins"),
    LOSSES("losses"),
    GAMES_PLAYED("gamesPlayed");

    private final String field;
}

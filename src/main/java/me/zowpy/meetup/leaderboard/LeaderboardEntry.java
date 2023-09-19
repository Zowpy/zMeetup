package me.zowpy.meetup.leaderboard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class LeaderboardEntry {

    private final UUID uuid;
    private final String name;

    private final int value;
}

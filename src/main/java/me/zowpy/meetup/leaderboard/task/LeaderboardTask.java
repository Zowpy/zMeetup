package me.zowpy.meetup.leaderboard.task;

import me.zowpy.meetup.MeetupPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaderboardTask extends BukkitRunnable {

    private final MeetupPlugin plugin;

    public LeaderboardTask(MeetupPlugin plugin) {
        this.plugin = plugin;

        this.runTaskTimerAsynchronously(plugin, 0L, 40L);
    }

    @Override
    public void run() {

        if (System.currentTimeMillis() - plugin.getLeaderboardHandler().getLastUpdate() <= plugin.getSettings().leaderboardInterval * 1000L) return;

        plugin.getLeaderboardHandler().update();
    }
}

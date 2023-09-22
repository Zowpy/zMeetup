package me.zowpy.meetup.game.task;

import me.zowpy.meetup.MeetupPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GameUpdateTask extends BukkitRunnable {

    private final MeetupPlugin plugin;

    public GameUpdateTask(MeetupPlugin plugin) {
        this.plugin = plugin;

        this.runTaskTimerAsynchronously(plugin, 20L, 60L);
    }

    @Override
    public void run() {
        plugin.getRedisHandler().saveGameData(plugin.getGameHandler(), plugin.getGameHandler().getGameState().getGameState());
    }
}

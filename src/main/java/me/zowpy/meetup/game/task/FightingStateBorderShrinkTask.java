package me.zowpy.meetup.game.task;

import lombok.Getter;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.border.Border;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;

public class FightingStateBorderShrinkTask extends BukkitRunnable {

    @Getter
    private long lastShrink;

    private int nextBorderSize;

    public static final int SECONDS_PER_UPDATE = 120;

    private final MeetupPlugin plugin;
    private final World world;

    public FightingStateBorderShrinkTask(MeetupPlugin plugin, World world) {
        this.plugin = plugin;
        this.world = world;

        this.runTaskTimerAsynchronously(MeetupPlugin.getInstance(), 0L, 20L);
        lastShrink = System.currentTimeMillis();

        nextBorderSize = plugin.getSettings().borderSizes.stream()
                .max(Comparator.comparingInt(Integer::intValue))
                .orElse(-1);

        Bukkit.broadcastMessage(plugin.getMessages().borderShrink.replace("<time>", "2")
                .replace("<unit>", "minutes")
                .replace("<blocks>", nextBorderSize + ""));
    }

    @Override
    public void run() {

        if (nextBorderSize == -1) {
            plugin.getGameHandler().getGameState().disable();
            return;
        }

        int secondsLeft = (int) (((lastShrink + (SECONDS_PER_UPDATE * 1000)) - System.currentTimeMillis()) / 1000);

        switch (secondsLeft) {
            case 60: {
                Bukkit.broadcastMessage(plugin.getMessages().borderShrink.replace("<time>", "1")
                        .replace("<unit>", "minute")
                        .replace("<blocks>", nextBorderSize + ""));
                break;
            }

            case 30:
            case 15:
            case 10:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1: {
                Bukkit.broadcastMessage(plugin.getMessages().borderShrink.replace("<time>", secondsLeft + "")
                        .replace("<unit>", secondsLeft == 1 ? "second" : "seconds")
                        .replace("<blocks>", nextBorderSize + ""));
            }

        }

        if (secondsLeft <= 0) {
            //plugin.getBorderManager().setCurrentRadius(nextBorderSize);
            Border border = plugin.getBorderHandler().getBorderForWorld(world);

            border.contract(border.getSize() - nextBorderSize);
            border.fill();

            int size = (int) world.getWorldBorder().getSize();

            int currentSize = nextBorderSize;

            world.getWorldBorder().setSize(size * 2.0);

            lastShrink = System.currentTimeMillis();
            nextBorderSize = plugin.getSettings().borderSizes.stream()
                    .filter(integer -> integer < currentSize)
                    .max(Comparator.comparingInt(Integer::intValue))
                    .orElse(-1);

            if (nextBorderSize != -1) {
                Bukkit.broadcastMessage(plugin.getMessages().borderShrink.replace("<time>", "2")
                        .replace("<unit>", "minutes")
                        .replace("<blocks>", nextBorderSize + ""));
            }
        }
    }
}

package me.zowpy.meetup.utils.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class MenuUpdateTask extends BukkitRunnable {

    public MenuUpdateTask(Plugin plugin) {
        this.runTaskTimerAsynchronously(plugin, 2L, 2L);
    }

    public void run() {

        for (Map.Entry<UUID, Menu> entry : Menu.getCurrentlyOpenedMenus().entrySet()) {
            UUID key = entry.getKey();
            Menu value = entry.getValue();

            if (!value.isAutoUpdate()) continue;

            Player player = Bukkit.getPlayer(key);

            if (player != null) {
                value.openMenu(player);
            }
        }
    }
}
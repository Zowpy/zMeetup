package me.zowpy.meetup.border;

import lombok.Getter;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.border.listener.BorderListener;
import me.zowpy.meetup.border.listener.InternalBorderListener;
import me.zowpy.meetup.border.runnable.EnsureInsideRunnable;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class BorderHandler {

    @Getter private final Map<World,Border> borderMap = new HashMap<>();

    public BorderHandler(MeetupPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new BorderListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new InternalBorderListener(), plugin);

        new EnsureInsideRunnable().runTaskTimer(plugin, 5L, 5L);
    }

    public Border getBorderForWorld(World world) {
        return this.borderMap.get(world);
    }

    public void addBorder(Border border) {
        this.borderMap.put(border.getOrigin().getWorld(), border);
    }

}

package me.zowpy.meetup.game.prevention;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class PreventionListener implements Listener {

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (event.getEntity().getType().isAlive() && event.getEntity().getType() != EntityType.PLAYER) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }

}

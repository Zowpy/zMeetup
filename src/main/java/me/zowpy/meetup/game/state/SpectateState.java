package me.zowpy.meetup.game.state;

import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.menu.SpectatingMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;

public abstract class SpectateState implements Listener {

    @EventHandler
    public void on(BlockPlaceEvent event) {
        if (spectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (spectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(BlockDamageEvent event) {
        if (spectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        if (spectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerPickupItemEvent event) {
        if (spectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerBucketEmptyEvent event) {
        if (spectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerBucketFillEvent event) {
        if (spectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (spectating(event.getPlayer())) {

            if (event.hasItem()) {
                if (event.getAction().name().contains("RIGHT")) {
                    if (event.getItem().isSimilar(MeetupPlugin.getInstance().getGameHandler().getSpectatorMenuItem())) {
                        new SpectatingMenu().openMenu(event.getPlayer());
                    }
                }
            }

            event.setCancelled(true);
        }
    }
    

    @EventHandler
    public void on(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (spectating((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (spectating((Player) event.getDamager())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PlayerInteractAtEntityEvent event) {
        if (spectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerInteractEntityEvent event) {
        if (spectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(InventoryClickEvent event) {
        if (spectating((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(InventoryDragEvent event) {
        if (spectating((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    private boolean spectating(Player player) {
        return MeetupPlugin.getInstance().getGameHandler().getPlayer(player).isSpectating();
    }
}

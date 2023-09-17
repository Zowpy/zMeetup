package me.zowpy.meetup.border.listener;

import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.border.action.DefaultBorderActions;
import me.zowpy.meetup.border.event.player.PlayerBorderEvent;
import me.zowpy.meetup.border.event.player.PlayerEnterBorderEvent;
import me.zowpy.meetup.border.event.player.PlayerExitBorderEvent;
import me.zowpy.meetup.utils.Cuboid;
import me.zowpy.meetup.border.Border;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class BorderListener implements Listener {

    private final MeetupPlugin plugin;
    
    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        final Location fromLoc = event.getFrom();
        final Location toLoc = event.getTo();
        final Border border = plugin.getBorderHandler().getBorderForWorld(fromLoc.getWorld());

        if (border == null) {
            return;
        }

        final boolean from = border.contains(fromLoc.getBlockX(), fromLoc.getBlockZ());
        final boolean to = border.contains(toLoc.getBlockX(), toLoc.getBlockZ());
        final boolean movedBlock = event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ();

        if (!movedBlock) {
            return;
        }

        PlayerBorderEvent playerBorderEvent = null;

        if (from && !to) {
            playerBorderEvent = new PlayerExitBorderEvent(border, event.getPlayer(), fromLoc, toLoc);
        } else if (!from && to) {
            playerBorderEvent = new PlayerEnterBorderEvent(border, event.getPlayer(), fromLoc, toLoc);
        }

        if (playerBorderEvent != null) {

            plugin.getServer().getPluginManager().callEvent(playerBorderEvent);

            if (playerBorderEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent event) {

        if (!(event.getVehicle().getPassenger() instanceof Player)) {
            return;
        }

        final Border border = plugin.getBorderHandler().getBorderForWorld(event.getVehicle().getWorld());

        if (border == null) {
            return;
        }

        final Player player = (Player) event.getVehicle().getPassenger();
        final Vehicle vehicle = event.getVehicle();

        if (border.contains(vehicle.getLocation()) && !(vehicle instanceof Horse)) {
            return;
        }

        final Location location = vehicle.getLocation();
        final Cuboid cuboid = border.getPhysicalBounds();

        double validX = location.getX();
        double validZ = location.getZ();

        if (location.getBlockX() + 2 > cuboid.getUpperX()) {
            validX = cuboid.getUpperX() - 3;
        } else if (location.getBlockX() - 2 < cuboid.getLowerX()) {
            validX = cuboid.getLowerX() + 4;
        }

        if (location.getBlockZ() + 2 > cuboid.getUpperZ()) {
            validZ = cuboid.getUpperZ() - 3;
        } else if (location.getBlockZ() - 2 < cuboid.getLowerZ()) {
            validZ = cuboid.getLowerZ() + 4;
        }

        final Location validLoc = new Location(location.getWorld(), validX, location.getY(), validZ);
        final Vector velocity = validLoc.toVector().subtract(location.toVector()).multiply(2);

        vehicle.setVelocity(velocity);

        if (!DefaultBorderActions.getLastMessaged().containsKey(player.getUniqueId()) || System.currentTimeMillis() - DefaultBorderActions.getLastMessaged().get(player.getUniqueId()) > TimeUnit.SECONDS.toMillis(1L)) {
            player.sendMessage(plugin.getMessages().borderExit);

            DefaultBorderActions.getLastMessaged().put(player.getUniqueId(), System.currentTimeMillis());
        }

    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {

        final Border border = plugin.getBorderHandler().getBorderForWorld(event.getTo().getWorld());

        if (border == null) {
            return;
        }

        final Location location = event.useTravelAgent() ? event.getPortalTravelAgent().findOrCreate(event.getTo()) : event.getTo();
        final Cuboid cuboid = border.getPhysicalBounds();

        double validX = location.getX();
        double validZ = location.getZ();

        final int buffer = 30;

        if (location.getBlockX() + 2 > cuboid.getUpperX()) {
            validX = cuboid.getUpperX() - buffer;
        } else if (location.getBlockX() - 2 < cuboid.getLowerX()) {
            validX = cuboid.getLowerX() + buffer + 1;
        }

        if (location.getBlockZ() + 2 > cuboid.getUpperZ()) {
            validZ = cuboid.getUpperZ() - buffer;
        } else if (location.getBlockZ() - 2 < cuboid.getLowerZ()) {
            validZ = cuboid.getLowerZ() + buffer + 1;
        }

        final Location validLoc = new Location(location.getWorld(), validX, location.getY(), validZ);

        event.setTo(validLoc);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {

        final Location toLoc = event.getTo();
        final Border border = plugin.getBorderHandler().getBorderForWorld(toLoc.getWorld());

        if (border == null) {
            return;
        }

        final boolean to = border.contains(toLoc.getBlockX(), toLoc.getBlockZ());

        if (!to && event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
            event.setTo(event.getFrom());
        }
    }

}

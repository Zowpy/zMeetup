package me.zowpy.meetup.border.action;

import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.border.event.border.BorderChangeEvent;
import me.zowpy.meetup.border.event.player.PlayerExitBorderEvent;
import me.zowpy.meetup.utils.Cuboid;
import com.google.common.collect.Maps;

import me.zowpy.meetup.border.Border;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DefaultBorderActions {

    @Getter private static final Map<UUID, Long> lastMessaged = Maps.newHashMap();

    public static final Consumer<PlayerExitBorderEvent> CANCEL_EXIT = (event) -> {

        final Player player = event.getPlayer();

        event.setCancelled(true);

        if (!lastMessaged.containsKey(player.getUniqueId()) || System.currentTimeMillis() - lastMessaged.get(player.getUniqueId()) > TimeUnit.SECONDS.toMillis(1L)) {
            player.sendMessage(MeetupPlugin.getInstance().getMessages().borderExit);
            lastMessaged.put(player.getUniqueId(), System.currentTimeMillis());
        }

    };

    public static final Consumer<PlayerExitBorderEvent> PUSHBACK_ON_EXIT = (event) -> {
        event.getPlayer().setMetadata("Border-Pushback", new FixedMetadataValue(MeetupPlugin.getInstance(), System.currentTimeMillis()));
        new BukkitRunnable() {

            @Override
            public void run() {

                final Border border = event.getBorder();
                final Player player = event.getPlayer();
                final Location location = event.getTo();

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
                final Vector velocity = validLoc.toVector().subtract(event.getTo().toVector()).multiply(0.18D);

                if (player.getVehicle() != null) {
                    player.getVehicle().setVelocity(velocity);
                } else {
                    player.setVelocity(velocity);
                }

                if (!DefaultBorderActions.lastMessaged.containsKey(player.getUniqueId()) || System.currentTimeMillis() - DefaultBorderActions.lastMessaged.get(player.getUniqueId()) > TimeUnit.SECONDS.toMillis(1L)) {
                    player.sendMessage(MeetupPlugin.getInstance().getMessages().borderExit);

                    DefaultBorderActions.lastMessaged.put(player.getUniqueId(), System.currentTimeMillis());
                }

            }
        }.runTask(MeetupPlugin.getInstance());
    };

    public static final Consumer<BorderChangeEvent> ENSURE_PLAYERS_IN_BORDER = (event) -> {

        final Border border = event.getBorder();

        for (Player loopPlayer : MeetupPlugin.getInstance().getServer().getOnlinePlayers()) {

            if (loopPlayer.getWorld() == border.getOrigin().getWorld() && !border.contains(loopPlayer.getLocation().getBlockX(),loopPlayer.getLocation().getBlockZ())) {

                final Location location = border.correctLocation(loopPlayer.getLocation());

                if (loopPlayer.getVehicle() == null) {
                    loopPlayer.teleport(location);
                } else {

                    final Entity vehicle = loopPlayer.getVehicle();

                    loopPlayer.leaveVehicle();

                    vehicle.teleport(location);
                    loopPlayer.teleport(location);
                    vehicle.setPassenger(loopPlayer);
                }

                loopPlayer.sendMessage(MeetupPlugin.getInstance().getMessages().teleportInsideBorder);
            }
        }
    };

}

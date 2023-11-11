package me.zowpy.meetup.game.scenario.impl;

import lombok.Getter;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.scenario.Scenario;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NoCleanScenario extends Scenario {

    @Getter
    private final ConcurrentMap<UUID, Long> noClean = new ConcurrentHashMap<>();

    private BukkitTask noCleanTask;

    private final MeetupPlugin plugin;

    public NoCleanScenario(MeetupPlugin plugin) {
        super("NoClean");

        this.plugin = plugin;
        enable();
    }

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, MeetupPlugin.getInstance());

        noCleanTask = new BukkitRunnable() {
            @Override
            public void run() {

                List<UUID> toRemove = new ArrayList<>();

                for (Map.Entry<UUID, Long> entry : noClean.entrySet()) {
                    int secondsLeft = (int) (((entry.getValue() + (15 * 1000)) - System.currentTimeMillis()) / 1000);

                    Player player = Bukkit.getPlayer(entry.getKey());

                    if (player == null) {
                        toRemove.add(entry.getKey());
                        continue;
                    }

                    switch (secondsLeft) {
                        case 10:
                        case 5:
                        case 4:
                        case 3:
                        case 2: {
                            player.sendMessage(plugin.getMessages().noCleanExpiring.replace("<seconds>", secondsLeft + "").replace("<unit>", "seconds"));
                            break;
                        }

                        case 1: {
                            player.sendMessage(plugin.getMessages().noCleanExpiring.replace("<seconds>", "1").replace("<unit>", "second"));
                            break;
                        }
                    }

                    if (secondsLeft == 0) {
                        toRemove.add(player.getUniqueId());
                        player.sendMessage(plugin.getMessages().noCleanExpired);
                    }
                }

                toRemove.forEach(noClean::remove);
                toRemove.clear();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L);
    }

    @Override
    public void disable() {
        if (noCleanTask != null)
            noCleanTask.cancel();

        noClean.clear();

        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        if (killer == null) return;

        killer.sendMessage(plugin.getMessages().noCleanStart);
        noClean.put(killer.getUniqueId(), System.currentTimeMillis());

        killer.sendMessage(plugin.getMessages().noCleanExpiring.replace("<seconds>", "15").replace("<unit>", "seconds"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && (event.getDamager() instanceof Player || event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player)) {

            Player entity = (Player) event.getEntity();

            Player damager;

            if (event.getDamager() instanceof Projectile) {
                damager = (Player) ((Projectile) event.getDamager()).getShooter();
            } else {
                damager = (Player) event.getDamager();
            }

            if (noClean.containsKey(entity.getUniqueId())) {
                int secondsLeft = (int) (((noClean.get(entity.getUniqueId()) + (15 * 1000)) - System.currentTimeMillis()) / 1000);

                damager.sendMessage(plugin.getMessages().noCleanHit.replace("<player>", entity.getName())
                        .replace("<seconds>", secondsLeft + "").replace("<unit>", secondsLeft == 1 ? "second" : "seconds"));

                event.setCancelled(true);
                return;
            }

            if (noClean.containsKey(damager.getUniqueId())) {
                noClean.remove(damager.getUniqueId());
                damager.sendMessage(plugin.getMessages().noCleanRemoved);
            }
        }
    }
}

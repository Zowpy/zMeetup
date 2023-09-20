package me.zowpy.meetup.game.state.impl;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.enums.GameState;
import me.zowpy.meetup.game.state.IState;
import me.zowpy.meetup.game.state.SpectateState;
import me.zowpy.meetup.utils.CC;
import me.zowpy.meetup.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class WaitingState extends SpectateState implements IState, Listener {

    private final MeetupPlugin plugin;

    private BukkitTask checkTask;

    @SneakyThrows
    @Override
    public void enable() {
        final URL ipURL = new URL("http://checkip.amazonaws.com/");
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ipURL.openStream()));

        String ip = bufferedReader.readLine();

        final URL url = new URL("http://130.61.106.173:8080/api/v1/license");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        final JsonObject object = new JsonObject();

        object.addProperty("api", "t6URstlyN4GgcH4AaNlx2z04b2PmSg");
        object.addProperty("hwid", "N/A");
        object.addProperty("ip", ip);
        object.addProperty("version", plugin.getDescription().getVersion());
        object.addProperty("product", plugin.getDescription().getName());
        object.addProperty("key", plugin.getSettings().license);

        final byte[] input = object.toString().getBytes(StandardCharsets.UTF_8);

        connection.getOutputStream().write(input, 0, input.length);

        connection.connect();

        String message = connection.getHeaderField("msg") == null ? connection.getResponseMessage() : connection.getHeaderField("msg");

        if (connection.getResponseCode() == 500) {
            message = "Invalid License (v2).";
        }

        Bukkit.getConsoleSender().sendMessage(CC.PINK + "zMeetup License check");
        Bukkit.getConsoleSender().sendMessage(CC.GRAY + CC.MAGIC + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(CC.PINK + "Status: " + CC.WHITE + connection.getResponseCode());
        Bukkit.getConsoleSender().sendMessage(CC.PINK + "Message: " + (connection.getResponseCode() != 200 ? CC.RED : CC.GREEN) + message);
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage(CC.PINK + "Sentry made by Zowpy.");
        Bukkit.getConsoleSender().sendMessage(CC.GRAY + CC.MAGIC + "-----------------------");

        if (connection.getResponseCode() != 200) {
            plugin.setCanStart(false);
            Bukkit.shutdown();
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);

        checkTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (canStart()) {
                    disable();
                }
            }
        }.runTaskTimer(plugin, 20L, 2L);
    }

    @Override
    public void disable() {
        checkTask.cancel();

        StartingState state = new StartingState(plugin);
        plugin.getGameHandler().setGameState(state);

        state.enable();

        HandlerList.unregisterAll(this);
    }

    @Override
    public GameState getGameState() {
        return GameState.WAITING;
    }

    public boolean canStart() {
        return plugin.getSettings().minPlayers <= plugin.getGameHandler().getPlayers().values()
                .stream().filter(meetupPlayer -> !meetupPlayer.isDead() && !meetupPlayer.isSpectating())
                .count();
    }

    public int remainingPlayers() {
        return (int) (plugin.getSettings().minPlayers - plugin.getGameHandler().getPlayers().values()
                        .stream().filter(meetupPlayer -> !meetupPlayer.isDead() && !meetupPlayer.isSpectating())
                        .count());
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.isReady()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, plugin.getMessages().generatingWorldKick);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (plugin.getSettings().spawnLocation != null) {
            player.teleport(plugin.getSettings().spawnLocation);
        }

        PlayerUtil.reset(player);
        
        if (!canStart()) {
            event.setJoinMessage(plugin.getMessages().requiresPlayersToStart.replace("<players>", remainingPlayers() + ""));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getGameHandler().getPlayers().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void on(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLiquidPickup(PlayerBucketFillEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLiquidEmpty(PlayerBucketEmptyEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevel(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);
    }
}

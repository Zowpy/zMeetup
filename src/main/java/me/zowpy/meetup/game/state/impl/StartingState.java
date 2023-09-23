package me.zowpy.meetup.game.state.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.border.Border;
import me.zowpy.meetup.game.enums.GameState;
import me.zowpy.meetup.game.state.IState;
import me.zowpy.meetup.game.state.SpectateState;
import me.zowpy.meetup.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class StartingState extends SpectateState implements IState, Listener {

    private final MeetupPlugin plugin;

    private BukkitTask startTask;

    @Getter
    private long started;

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        started = System.currentTimeMillis();

        for (Player player : Bukkit.getOnlinePlayers()) {
            teleport(player);

            if (plugin.getGameHandler().isPlaying(player)) {
                PlayerUtil.reset(player);
                PlayerUtil.sit(player);

                CompletableFuture.runAsync(() -> {
                    plugin.getLoadoutHandler().giveRandom(player, plugin.getProfileHandler().findOrDefault(player).getLoadout());
                });
            }
        }

        startTask = new BukkitRunnable() {

            @Override
            public void run() {
                int secondsLeft = (int) (((started + (plugin.getSettings().seconds * 1000)) - System.currentTimeMillis()) / 1000);

                if (plugin.getMessages().secondsBroadcast.contains(secondsLeft)) {
                    Bukkit.broadcastMessage(plugin.getMessages().starting.replace("<seconds>", secondsLeft + ""));
                }

                if (plugin.getSettings().titles && plugin.getSettings().titleSeconds.contains(secondsLeft)) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        PlayerUtil.sendTitleBar(
                                player,
                                plugin.getMessages().startingTitle.replace("<seconds>", secondsLeft + ""),
                                plugin.getMessages().startingSubTitle,
                                0,
                                80,
                                10
                        );
                    });
                }

                if (secondsLeft <= 0) {
                    if (plugin.getSettings().minPlayers > Bukkit.getOnlinePlayers().size()) {
                        Bukkit.broadcastMessage(plugin.getMessages().restarting);
                        started = System.currentTimeMillis();
                    } else {

                        FightingState fightingState = new FightingState(plugin);
                        fightingState.enable();

                        plugin.getGameHandler().setGameState(fightingState);
                        disable();
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L);
    }

    @Override
    public void disable() {
        if (startTask != null)
            startTask.cancel();

        HandlerList.unregisterAll(this);
    }

    @Override
    public GameState getGameState() {
        return GameState.STARTING;
    }

    private void teleport(Player player) {
        World world = Bukkit.getWorld(plugin.getSettings().worldName);
        Border border = plugin.getBorderHandler().getBorderForWorld(world);

        int x = ThreadLocalRandom.current().nextInt(border.getSize() - 2);
        int z = ThreadLocalRandom.current().nextInt(border.getSize() - 2);

        Block block = world.getHighestBlockAt(x, z);
        Location loc = block.getLocation();

        player.teleport(loc);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        teleport(player);
        PlayerUtil.sit(player);

        if (empty(player)) {
            PlayerUtil.reset(player);

            CompletableFuture.runAsync(() -> {
                plugin.getLoadoutHandler().giveRandom(player, plugin.getProfileHandler().findOrDefault(player).getLoadout());
            });
        }else {

            ItemStack[] content = player.getInventory().getContents();
            ItemStack[] armor = player.getInventory().getArmorContents();

            PlayerUtil.reset(player);

            player.getInventory().setContents(content);
            player.getInventory().setArmorContents(armor);

            player.updateInventory();
        }
    }

    private boolean empty(Player player) {
        return Arrays.stream(player.getInventory().getContents())
                .allMatch(itemStack -> itemStack == null || itemStack.getType() == Material.AIR);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerUtil.unsit(event.getPlayer());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
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
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevel(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}

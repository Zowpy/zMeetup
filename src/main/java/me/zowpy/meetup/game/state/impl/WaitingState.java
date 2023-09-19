package me.zowpy.meetup.game.state.impl;

import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.enums.GameState;
import me.zowpy.meetup.game.player.MeetupPlayer;
import me.zowpy.meetup.game.state.IState;
import me.zowpy.meetup.game.state.SpectateState;
import me.zowpy.meetup.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

@RequiredArgsConstructor
public class WaitingState extends SpectateState implements IState, Listener {

    private final MeetupPlugin plugin;
    
    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
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

        MeetupPlayer meetupPlayer = new MeetupPlayer(player);
        plugin.getGameHandler().getPlayers().put(player.getUniqueId(), meetupPlayer);
        
        if (!canStart()) {
            event.setJoinMessage(plugin.getMessages().requiresPlayersToStart.replace("<players>", remainingPlayers() + ""));
        }else {
            disable();
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
    public void onInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);
    }
}

package me.zowpy.meetup.game.state.impl;

import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.state.GameState;
import me.zowpy.meetup.game.state.IState;
import me.zowpy.meetup.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

public class WaitingState implements IState, Listener {

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, MeetupPlugin.getInstance());
    }

    @Override
    public void disable() {
        StartingState state = new StartingState(MeetupPlugin.getInstance());
        MeetupPlugin.getInstance().getGameHandler().setGameState(state);

        state.enable();

        HandlerList.unregisterAll(this);
    }

    @Override
    public GameState getGameState() {
        return GameState.WAITING;
    }

    public boolean canStart() {
        return MeetupPlugin.getInstance().getSettings().minPlayers <= Bukkit.getOnlinePlayers().size();
    }

    public int remainingPlayers() {
        return MeetupPlugin.getInstance().getSettings().minPlayers - Bukkit.getOnlinePlayers().size();
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (!MeetupPlugin.getInstance().isReady()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, MeetupPlugin.getInstance().getMessages().generatingWorldKick);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (MeetupPlugin.getInstance().getSettings().spawnLocation != null) {
            player.teleport(MeetupPlugin.getInstance().getSettings().spawnLocation);
        }

        PlayerUtil.reset(player);

        if (!canStart()) {
            event.setJoinMessage(MeetupPlugin.getInstance().getMessages().requiresPlayersToStart.replace("<players>", remainingPlayers() + ""));
        }else {
            disable();
        }
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

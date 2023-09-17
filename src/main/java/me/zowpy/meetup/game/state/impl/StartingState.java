package me.zowpy.meetup.game.state.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.border.Border;
import me.zowpy.meetup.game.state.IState;
import me.zowpy.meetup.loadout.Loadout;
import me.zowpy.meetup.utils.PlayerUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class StartingState implements IState, Listener {

    private final MeetupPlugin plugin;

    @Getter
    private long started;

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        started = System.currentTimeMillis();

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerUtil.reset(player);

            teleport(player);
            sit(player);

            plugin.getLoadoutHandler().giveRandom(player, new Loadout());
        }

        new BukkitRunnable() {

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

                        cancel();
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    private void sit(Player player) {
        Location location = player.getLocation().clone();

        EntityBat bat = new EntityBat(((CraftWorld) location.getWorld()).getHandle());
        bat.setLocation(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
        bat.setInvisible(true);

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        PacketPlayOutSpawnEntityLiving entity = new PacketPlayOutSpawnEntityLiving(bat);

        entityPlayer.playerConnection.sendPacket(entity);

        player.setMetadata("sit", new FixedMetadataValue(plugin, bat.getId()));

        PacketPlayOutAttachEntity attachEntity = new PacketPlayOutAttachEntity(0, entityPlayer, bat);
        entityPlayer.playerConnection.sendPacket(attachEntity);

    }

    private void unsit(Player player) {

        if (player.hasMetadata("sit")) {

            int entityId = player.getMetadata("sit").get(0).asInt();

            PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entityId);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
        }
    }

    private void teleport(Player player) {
        World world = Bukkit.getWorld(plugin.getSettings().worldName);
        Border border = plugin.getBorderHandler().getBorderForWorld(world);

        int offsetX = ThreadLocalRandom.current().nextInt(border.getSize() - 2);
        int offsetZ = ThreadLocalRandom.current().nextInt(border.getSize() - 2);

        Block block = world.getHighestBlockAt(offsetX, offsetZ);
        Location loc = block.getLocation();

        player.teleport(loc);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerUtil.reset(player);

        teleport(player);
        sit(player);

        plugin.getLoadoutHandler().giveRandom(player, new Loadout());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        unsit(event.getPlayer());
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
}

package me.zowpy.meetup.game.state.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.player.MeetupPlayer;
import me.zowpy.meetup.game.state.IState;
import me.zowpy.meetup.game.state.SpectateState;
import me.zowpy.meetup.game.task.FightingStateBorderShrinkTask;
import me.zowpy.meetup.loadout.LoadoutHandler;
import me.zowpy.meetup.utils.PlayerUtil;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FightingState extends SpectateState implements IState, Listener {

    @Getter
    private FightingStateBorderShrinkTask shrinkTask;

    private final MeetupPlugin plugin;

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        for (Player player : Bukkit.getOnlinePlayers()) {
            unsit(player);

            if (plugin.getSettings().titles) {
                PlayerUtil.sendTitleBar(
                        player,
                        plugin.getMessages().startedTitle,
                        plugin.getMessages().startedSubtitle,
                        0,
                        60,
                        10
                );
            }

            MeetupPlayer meetupPlayer = new MeetupPlayer(player);
            plugin.getGameHandler().getPlayers().put(player.getUniqueId(), meetupPlayer);
        }

        Bukkit.broadcastMessage(plugin.getMessages().started);
        shrinkTask = new FightingStateBorderShrinkTask(plugin, Bukkit.getWorld(plugin.getSettings().worldName));
    }

    @Override
    public void disable() {
        List<MeetupPlayer> winners = plugin.getGameHandler().getPlayers().values()
                .stream().filter(meetupPlayer1 -> !meetupPlayer1.isDead() && !meetupPlayer1.isSpectating())
                .collect(Collectors.toList());

        EndingState endingState;

        if (winners.size() != 1) {
            endingState = new EndingState(null);
        } else {
            endingState = new EndingState(winners.get(0));
        }

        endingState.enable();

        shrinkTask.cancel();
        HandlerList.unregisterAll(this);
    }

    private void unsit(Player player) {

        if (player.hasMetadata("sit")) {

            int entityId = player.getMetadata("sit").get(0).asInt();

            PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entityId);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
        }
    }

    private void deathChest(Player player, List<ItemStack> drops) {
        Location loc = player.getLocation().clone();
        Location loc2 = loc.getBlock().getRelative(BlockFace.NORTH).getLocation();

        Block block1 = loc.getBlock();
        Block block2 = loc2.getBlock();

        block1.setType(Material.CHEST);
        block2.setType(Material.CHEST);

        Chest chest = (Chest) block1.getState();

        InventoryHolder holder = chest.getInventory().getHolder();

        if (holder instanceof DoubleChest) {
            DoubleChest doubleChest = ((DoubleChest) holder);

            drops.forEach(itemStack -> doubleChest.getInventory().addItem(itemStack));
        } else {
            drops.forEach(itemStack -> chest.getBlockInventory().addItem(itemStack));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        MeetupPlayer meetupPlayer = plugin.getGameHandler().getPlayers().remove(event.getPlayer().getUniqueId());

        if (!meetupPlayer.isDead() && !meetupPlayer.isSpectating()) {
            Player player = event.getPlayer();

            List<ItemStack> drops = new ArrayList<>();

            drops.addAll(Arrays.asList(player.getInventory().getArmorContents()));
            drops.addAll(Arrays.asList(player.getInventory().getContents()));

            deathChest(player, drops);
        }

        if (remainingPlayers() <= 1) {
            disable();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MeetupPlayer meetupPlayer = new MeetupPlayer(player);

        plugin.getGameHandler().getPlayers().put(player.getUniqueId(), meetupPlayer);
        plugin.getGameHandler().handleSpectator(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        deathChest(player, event.getDrops());
        event.getDrops().clear();

        event.setDeathMessage(null);

        MeetupPlayer meetupPlayer = plugin.getGameHandler().getPlayer(player);
        meetupPlayer.setDead(true);

        String playerFormat = plugin.getMessages().playerFormat.replace("<player>", player.getName());

        if (hasPlaceholderAPI()) {
            playerFormat = PlaceholderAPI.setPlaceholders(player, playerFormat);
        }

        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            MeetupPlayer killerProfile = plugin.getGameHandler().getPlayer(killer);

            String killerFormat = plugin.getMessages().playerFormat.replace("<player>", killer.getName());

            if (hasPlaceholderAPI()) {
                killerFormat = PlaceholderAPI.setPlaceholders(player.getKiller(), killerFormat);
            }

            killerProfile.setKills(killerProfile.getKills() + 1);

            Material material = killer.getItemInHand().getType();

            Bukkit.broadcastMessage(plugin.getMessages().killMessage.replace("<player>", playerFormat)
                    .replace("<player_kills>", meetupPlayer.getKills() + "")
                    .replace("<killer>", killerFormat)
                    .replace("<killer_kills>", killerProfile.getKills() + "")
                    .replace("<item>", prettyName(material)));
        } else {

            switch (player.getLastDamageCause().getCause()) {
                case FALL: {
                    Bukkit.broadcastMessage(plugin.getMessages().fallDeath.replace("<player>", playerFormat)
                            .replace("<kills>", meetupPlayer.getKills() + "")
                    );
                    break;
                }

                case LAVA: {
                    Bukkit.broadcastMessage(plugin.getMessages().lavaDeath.replace("<player>", playerFormat)
                            .replace("<kills>", meetupPlayer.getKills() + "")
                    );
                    break;
                }

                default: {
                    Bukkit.broadcastMessage(plugin.getMessages().otherDeath.replace("<player>", playerFormat)
                            .replace("<kills>", meetupPlayer.getKills() + "")
                    );
                    break;
                }
            }

        }

        player.spigot().respawn();

        if (remainingPlayers() <= 1) {
            disable();
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        player.setAllowFlight(true);
        player.setFlying(true);

        event.setRespawnLocation(player.getLocation().clone().add(0, 15, 0));

        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getGameHandler().handleSpectator(player), 4L);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (LoadoutHandler.GOLDEN_HEAD.isSimilar(event.getItem())) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1), true);
        }
    }

    public int remainingPlayers() {
        return (int) plugin.getGameHandler().getPlayers().values()
                .stream().filter(meetupPlayer1 -> !meetupPlayer1.isDead() && !meetupPlayer1.isSpectating())
                .count();
    }

    private boolean hasPlaceholderAPI() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    private String prettyName(Material material) {
        String pretty = material.name().toLowerCase().replace("_", " ");
        String[] split = pretty.split("\\s+");

        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            char c = s.charAt(0);

            s = s.replaceFirst(String.valueOf(c), Character.toUpperCase(c) + "");

            split[i] = s;
        }

        return StringUtils.join(split, " ");
    }
}
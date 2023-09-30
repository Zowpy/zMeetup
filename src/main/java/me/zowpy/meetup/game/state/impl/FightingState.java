package me.zowpy.meetup.game.state.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import me.clip.placeholderapi.PlaceholderAPI;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.enums.GameState;
import me.zowpy.meetup.game.enums.SpectateReason;
import me.zowpy.meetup.game.player.MeetupPlayer;
import me.zowpy.meetup.game.state.IState;
import me.zowpy.meetup.game.state.SpectateState;
import me.zowpy.meetup.game.task.FightingStateBorderShrinkTask;
import me.zowpy.meetup.loadout.LoadoutHandler;
import me.zowpy.meetup.utils.PlayerUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FightingState extends SpectateState implements IState, Listener {

    @Getter @Setter
    private FightingStateBorderShrinkTask shrinkTask;

    private BukkitTask checkTask;

    private final MeetupPlugin plugin;

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerUtil.unsit(player);

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
        }

        CompletableFuture.runAsync(() -> {
            Bukkit.getOnlinePlayers().stream().filter(player -> plugin.getGameHandler().isPlaying(player)).forEach(player -> plugin.getProfileHandler().gamePlayed(player));
        });

        Bukkit.broadcastMessage(plugin.getMessages().started);
        shrinkTask = new FightingStateBorderShrinkTask(plugin, Bukkit.getWorld(plugin.getSettings().worldName));

        checkTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (remainingPlayers() <= 1) {
                    disable();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @Override
    public void disable() {
        if (shrinkTask != null)
            shrinkTask.cancel();

        shrinkTask = null;

        checkTask.cancel();

        List<MeetupPlayer> winners = plugin.getGameHandler().getPlayers().values()
                .stream().filter(meetupPlayer1 -> !meetupPlayer1.isDead() && !meetupPlayer1.isSpectating())
                .collect(Collectors.toList());

        EndingState endingState;

        if (winners.size() != 1) {
            endingState = new EndingState(plugin, null);
        } else {
            endingState = new EndingState(plugin, winners.get(0));
        }

        endingState.enable();
        HandlerList.unregisterAll(this);
    }

    @Override
    public GameState getGameState() {
        return GameState.FIGHTING;
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

            drops.stream().filter(itemStack -> itemStack != null && itemStack.getType() != Material.AIR).forEach(itemStack -> doubleChest.getInventory().addItem(itemStack));
        } else {
            drops.stream().filter(itemStack -> itemStack != null && itemStack.getType() != Material.AIR).forEach(itemStack -> chest.getBlockInventory().addItem(itemStack));
        }

        if (plugin.getScenarioHandler().isEnabled("timebomb")) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    holder.getInventory().clear();

                    block1.setType(Material.AIR);
                    block2.setType(Material.AIR);

                    World world = loc.getWorld();

                    world.createExplosion(loc, (float) plugin.getSettings().timeBombExplosionPower, false);

                    if (hasPlaceholderAPI()) {
                        Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().timeBombExplode.replace("<player>", player.getName())));
                    }else {
                        Bukkit.broadcastMessage(plugin.getMessages().timeBombExplode.replace("<player>", player.getName()));
                    }
                }
            }.runTaskLater(plugin, plugin.getSettings().timeBombSeconds * 20L);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        MeetupPlayer meetupPlayer = plugin.getGameHandler().getPlayers().get(event.getPlayer().getUniqueId());

        if (!meetupPlayer.isDead() && !meetupPlayer.isSpectating()) {
            Player player = event.getPlayer();

            List<ItemStack> drops = new ArrayList<>();

            drops.addAll(Arrays.asList(player.getInventory().getArmorContents()));
            drops.addAll(Arrays.asList(player.getInventory().getContents()));

            deathChest(player, drops);

            String playerFormat = plugin.getMessages().playerFormat.replace("<player>", player.getName());

            if (hasPlaceholderAPI()) {
                playerFormat = PlaceholderAPI.setPlaceholders(player, playerFormat);
            }

            Bukkit.broadcastMessage(plugin.getMessages().disconnect.replace("<player>", playerFormat));

            CompletableFuture.runAsync(() -> {
                plugin.getProfileHandler().loss(player);
                plugin.getProfileHandler().death(player);
            });
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MeetupPlayer meetupPlayer = new MeetupPlayer(player);

        plugin.getGameHandler().getPlayers().put(player.getUniqueId(), meetupPlayer);
        plugin.getGameHandler().handleSpectator(player, SpectateReason.JOINED_TOO_LATE);
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

            CompletableFuture.runAsync(() -> plugin.getProfileHandler().kill(killer));

            String killerFormat = plugin.getMessages().playerFormat.replace("<player>", killer.getName());

            if (hasPlaceholderAPI()) {
                killerFormat = PlaceholderAPI.setPlaceholders(player.getKiller(), killerFormat);
            }

            killerProfile.setKills(killerProfile.getKills() + 1);

            Bukkit.broadcastMessage(plugin.getMessages().killMessage.replace("<player>", playerFormat)
                    .replace("<player_kills>", meetupPlayer.getKills() + "")
                    .replace("<killer>", killerFormat)
                    .replace("<killer_kills>", killerProfile.getKills() + "")
                    .replace("<item>", prettyName(killer.getItemInHand())));
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

        CompletableFuture.runAsync(() -> {
            plugin.getProfileHandler().death(player);
            plugin.getProfileHandler().loss(player);
        });

        player.spigot().respawn();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        player.setAllowFlight(true);
        player.setFlying(true);

        event.setRespawnLocation(player.getLocation().clone().add(0, 15, 0));

        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getGameHandler().handleSpectator(player, SpectateReason.DIED), 4L);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (LoadoutHandler.GOLDEN_HEAD.isSimilar(event.getItem())) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1), true);
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.ENCHANTING) return;

        Inventory inventory = event.getInventory();
        inventory.setItem(1, new ItemStack(Material.INK_SACK, 64, (short) 4));
    }

    @EventHandler
    @SneakyThrows
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.ENCHANTING) {
            if (event.getSlot() == 1) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.ENCHANTING) {

            Player player = (Player) event.getPlayer();

            event.getInventory().setItem(1, new ItemStack(Material.AIR));

            if (player.getItemOnCursor() != null && player.getItemOnCursor().getType() == Material.INK_SACK && player.getItemOnCursor().getDurability() == 4) {
                player.setItemOnCursor(null);
            }
        }
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {

            Player entity = (Player) event.getEntity();
            Player damager = (Player) ((Arrow) event.getDamager()).getShooter();
            double health = Math.ceil(entity.getHealth() - event.getFinalDamage()) / 2.0D;

            if (health > 0.0) {
                damager.sendMessage(plugin.getMessages().arrowHit.replace("<player>", entity.getName())
                        .replace("<hearts>", health + ""));
            }
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

    private String prettyName(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return "their fist";
        }

        String pretty = item.getType().name().toLowerCase().replace("_", " ");
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

package me.zowpy.meetup.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.player.MeetupPlayer;
import me.zowpy.meetup.game.state.IState;
import me.zowpy.meetup.game.state.impl.WaitingState;
import me.zowpy.meetup.utils.ItemBuilder;
import me.zowpy.meetup.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
@RequiredArgsConstructor
public class GameHandler {

    private final ConcurrentMap<UUID, MeetupPlayer> players = new ConcurrentHashMap<>();

    private final MeetupPlugin plugin;

    @Setter
    private IState gameState = new WaitingState();

    public void handleLeave(Player player) {
        players.remove(player.getUniqueId());
    }

    public void handleSpectator(Player player) {

        PlayerUtil.reset(player);

        MeetupPlayer meetupPlayer = getPlayer(player);
        meetupPlayer.setSpectating(true);

        player.setGameMode(GameMode.CREATIVE);
        //player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));

        player.setAllowFlight(true);
        player.setFlying(true);

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.getUniqueId().equals(player.getUniqueId())) continue;

            other.hidePlayer(player);
        }

        player.getInventory().setItem(plugin.getHotbarConfig().spectateMenuSlot, getSpectatorMenuItem());

        if (plugin.getHotbarConfig().enableNavigation) {
            player.getInventory().setItem(plugin.getHotbarConfig().navigationSlot,
                    new ItemBuilder(plugin.getHotbarConfig().navigation)
                            .name(plugin.getHotbarConfig().navigationName)
                            .lore(plugin.getHotbarConfig().navigationLore)
                            .build()
            );
        }
    }

    public ItemStack getSpectatorMenuItem() {
        return new ItemBuilder(plugin.getHotbarConfig().spectateMenuMaterial)
                .name(plugin.getHotbarConfig().spectateMenuName)
                .lore(plugin.getHotbarConfig().spectateMenuLore)
                .build();
    }

    public MeetupPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public MeetupPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }
}

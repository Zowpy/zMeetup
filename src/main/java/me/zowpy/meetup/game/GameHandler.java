package me.zowpy.meetup.game;

import lombok.Getter;
import lombok.Setter;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.game.enums.SpectateReason;
import me.zowpy.meetup.game.player.MeetupPlayer;
import me.zowpy.meetup.game.state.IState;
import me.zowpy.meetup.game.state.impl.WaitingState;
import me.zowpy.meetup.utils.CC;
import me.zowpy.meetup.utils.ItemBuilder;
import me.zowpy.meetup.utils.PlayerUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
public class GameHandler {

    private final ConcurrentMap<UUID, MeetupPlayer> players = new ConcurrentHashMap<>();

    private final MeetupPlugin plugin;

    @Setter
    private IState gameState;

    public GameHandler(MeetupPlugin plugin) {
        this.plugin = plugin;

        gameState = new WaitingState(plugin);
    }

    public void handleSpectator(Player player, SpectateReason reason) {

        PlayerUtil.unsit(player);
        PlayerUtil.reset(player);

        MeetupPlayer meetupPlayer = getPlayer(player);
        meetupPlayer.setSpectating(true);

        player.setGameMode(GameMode.CREATIVE);

        player.setAllowFlight(true);
        player.setFlying(true);

        player.setPlayerListName(CC.GRAY + player.getDisplayName());

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        //entityPlayer.listName = new ChatComponentText(CC.GRAY + player.getDisplayName());

        PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.getUniqueId().equals(player.getUniqueId())) continue;

            other.hidePlayer(player);
            ((CraftPlayer) other).getHandle().playerConnection.sendPacket(info);
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

        if (plugin.getSettings().titles) {
            PlayerUtil.sendTitleBar(player, plugin.getMessages().spectateTitle, plugin.getMessages().spectateSubtitle, 0, 80, 0);
        }

        player.sendMessage(plugin.getMessages().spectateReasonMessage.replace("<reason>", reason.getName()));
    }

    public void removeSpectator(Player player) {
        PlayerUtil.reset(player);

        MeetupPlayer meetupPlayer = getPlayer(player);
        meetupPlayer.setSpectating(false);

        player.setGameMode(GameMode.SURVIVAL);
        //player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));

        player.setPlayerListName(player.getDisplayName());

        player.setFlying(false);
        player.setAllowFlight(false);

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.getUniqueId().equals(player.getUniqueId())) continue;

            other.showPlayer(player);
        }
    }

    public boolean isPlaying(Player player) {
        return isPlaying(player.getUniqueId());
    }

    public boolean isPlaying(UUID uuid) {
        MeetupPlayer meetupPlayer = getPlayer(uuid);

        return !meetupPlayer.isDead() && !meetupPlayer.isSpectating();
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

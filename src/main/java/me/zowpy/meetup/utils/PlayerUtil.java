package me.zowpy.meetup.utils;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.spigotmc.AsyncCatcher;

public class PlayerUtil {

    public static void reset(Player player) {
        AsyncCatcher.enabled = false;

        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setSaturation(12.8F);
        player.setMaximumNoDamageTicks(20);
        player.setFireTicks(0);
        player.setFallDistance(0.0F);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setWalkSpeed(0.2F);
        player.getInventory().setHeldItemSlot(0);
        player.setAllowFlight(false);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.closeInventory();

        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);

        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte)0);

        player.updateInventory();
    }

    public static void sendTitleBar(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        resetTitleBar(player);

        PacketPlayOutTitle times = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
        entityPlayer.playerConnection.sendPacket(times);

        if (title != null) {
            PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(title));
            entityPlayer.playerConnection.sendPacket(packetTitle);
        }

        if (subtitle != null && !subtitle.isEmpty()) {
            PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatComponentText(subtitle));
            entityPlayer.playerConnection.sendPacket(packetSubtitle);
        }
    }

    public static void resetTitleBar(Player player) {
        player.resetTitle();
    }
}
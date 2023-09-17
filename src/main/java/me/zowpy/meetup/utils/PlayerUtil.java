package me.zowpy.meetup.utils;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.AsyncCatcher;

public class PlayerUtil {

    public static void reset(Player player) {
        AsyncCatcher.enabled = false;

        player.getActivePotionEffects().clear();
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0f);
        player.setFireTicks(0);
        player.setMaximumNoDamageTicks(20);
        player.setNoDamageTicks(20);
        player.setSaturation(20);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setContents(new ItemStack[36]);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.updateInventory();
    }

    public static void sendTitleBar(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        PacketPlayOutTitle reset = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.RESET, null);
        entityPlayer.playerConnection.sendPacket(reset);

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
}
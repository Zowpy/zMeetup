package me.zowpy.meetup.utils.menu.buttons.impl;

import me.zowpy.meetup.utils.menu.buttons.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Glass extends Button {

    public ItemStack getButtonItem(Player player) {
        Material mat = Material.STAINED_GLASS_PANE;

        ItemStack itemStack = new ItemStack(mat, 1, (short) 7);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(" ");

        itemStack.setItemMeta(meta);

        return itemStack;
    }
}

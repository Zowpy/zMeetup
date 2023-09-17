package me.zowpy.meetup.utils.menu.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Button {

    public static Button placeholder(final Material material, final byte data, String... title) {
        return (new Button() {
            public ItemStack getButtonItem(Player player) {
                ItemStack it = new ItemStack(material, 1, data);

                ItemMeta meta = it.getItemMeta();

                meta.setDisplayName(String.join("", title));
                it.setItemMeta(meta);

                return it;
            }
        });
    }

    public static void playFail(Player player) {

    }

    public static void playSuccess(Player player) {
    }

    public static void playNeutral(Player player) {
    }

    public abstract ItemStack getButtonItem(Player player);

    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton){
    }

    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return (true);
    }

    public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
        return (false);
    }
}

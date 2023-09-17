package me.zowpy.meetup.utils.menu.buttons.impl;

import lombok.AllArgsConstructor;
import me.zowpy.meetup.utils.menu.TypeCallback;
import me.zowpy.meetup.utils.menu.buttons.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@AllArgsConstructor
public class ConfirmationButton extends Button {

    private boolean confirm;
    private TypeCallback<Boolean> callback;
    private boolean closeAfterResponse;

    @Override
    public ItemStack getButtonItem(Player player) {
        Material mat = Material.WOOL;

        ItemStack itemStack = new ItemStack(mat, 1, (short) (confirm ? 5 : 14));
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.confirm ? "&aConfirm" : "&cCancel"));

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hb) {

        if (this.closeAfterResponse) {
            player.closeInventory();
        }

        this.callback.callback(this.confirm);
    }
}

package me.zowpy.meetup.utils.menu.pagination.button;

import lombok.AllArgsConstructor;
import me.zowpy.meetup.utils.ItemBuilder;
import me.zowpy.meetup.utils.menu.pagination.PaginatedMenu;
import me.zowpy.meetup.utils.menu.buttons.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PageButton extends Button {

    private final int mod;
    private final PaginatedMenu menu;

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemBuilder item = new ItemBuilder(Material.LEVER);

        if (this.hasNext(player)) {
            if (mod > 0) {
                item.durability((byte) 13);
            } else {
                item.durability((byte) 14);
            }
            item.name(ChatColor.translateAlternateColorCodes('&', this.mod > 0 ? "&aNext page" : "&cPrevious page"));
        } else {
            item.durability((byte) 7);
            item.name(ChatColor.translateAlternateColorCodes('&', (this.mod > 0 ? "&7Last page" : "&7First page")));
        }

        return item.build();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hb) {
        if (hasNext(player)) {
            this.menu.modPage(player, this.mod);
            Button.playNeutral(player);
        } else {
            Button.playFail(player);
        }
    }

    private boolean hasNext(Player player) {
        int pg = this.menu.getPage() + this.mod;
        return pg > 0 && this.menu.getPages(player) >= pg;
    }
}

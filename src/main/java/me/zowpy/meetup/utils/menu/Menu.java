package me.zowpy.meetup.utils.menu;

import lombok.Getter;
import lombok.Setter;
import me.zowpy.meetup.utils.menu.buttons.Button;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.IntStream;

@Getter
@Setter
public abstract class Menu {

    @Getter
    public static Map<UUID, Menu> currentlyOpenedMenus = new HashMap<>();

    @Getter
    public static List<UUID> transitionalMenus = new ArrayList<>();

    private Map<Integer, Button> buttons = new HashMap<>();

    private Inventory inventory;
    private boolean autoUpdate = false;
    private boolean updateAfterClick = true;
    private boolean closedByMenu = false;
    private boolean placeholder = false;

    private boolean noncancellingInventory = false;

    private ItemStack createItemStack(Player player, Button button) {
        ItemStack item = button.getButtonItem(player);

        Material mat = Material.SKULL_ITEM;

        if (item.getType() != mat) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName() && !isNoncancellingInventory()) {
                meta.setDisplayName(meta.getDisplayName() + "§b§c§d§e");
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public void fill(Map buttons, ItemStack itemStack) {
        IntStream.range(0, getSize()).filter((slot) -> buttons.get(slot) == null).forEach((slot) -> {
            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return itemStack;
                }
            });
        });
    }

    public void openMenu(Player player) {
        this.buttons = this.getButtons(player);

        Menu previousMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());

        inventory = null;

        int size = this.getSize() == -1 ? this.size(this.buttons) : this.getSize();

        boolean update = false;
        String title = this.getTitle(player);

        if (title.length() > 32) {
            title = title.substring(0, 32);
        }

        if (player.getOpenInventory() != null) {
            if (previousMenu == null) {
                player.closeInventory();
            } else {
                int previousSize = player.getOpenInventory().getTopInventory().getSize();

                if (previousSize == size && player.getOpenInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', title))) {
                    inventory = player.getOpenInventory().getTopInventory();
                    update = true;
                } else {
                    previousMenu.setClosedByMenu(true);
                    previousMenu.onClose(player);

                    transitionalMenus.add(player.getUniqueId());
                }
            }
        }

        if (inventory == null) {
            inventory = Bukkit.createInventory(player, size, ChatColor.translateAlternateColorCodes('&', title));
        }

        inventory.setContents(new ItemStack[inventory.getSize()]);

        if (Menu.currentlyOpenedMenus.containsKey(player.getUniqueId())) {
            Menu.currentlyOpenedMenus.replace(player.getUniqueId(), this);
        }else {
            Menu.currentlyOpenedMenus.put(player.getUniqueId(), this);
        }

        for (Map.Entry<Integer, Button> buttonEntry : this.buttons.entrySet()) {
            inventory.setItem(buttonEntry.getKey(), createItemStack(player, buttonEntry.getValue()));
        }

        if (update) {
            player.updateInventory();
        } else {
            player.openInventory(inventory);
        }

        this.onOpen(player);
        this.setClosedByMenu(false);
    }

    public int size(Map<Integer, Button> buttons) {
        int highest = 0;

        for (int buttonValue : buttons.keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }

        return (int) (Math.ceil((highest + 1) / 9D) * 9D);
    }

    public int getSlot(int x, int y) {
        return ((9 * y) + x);
    }

    public int getSize() {
        return -1;
    }

    public abstract String getTitle(Player player);

    public abstract Map<Integer, Button> getButtons(Player player);

    public void onOpen(Player player) {
    }

    public void onClose(Player player) {
    }

    protected void surroundButtons(boolean full, Map buttons, ItemStack itemStack) {
        IntStream.range(0, getSize()).filter(slot -> buttons.get(slot) == null).forEach(slot -> {
            if (slot < 9 || slot > getSize() - 10 || full && (slot % 9 == 0 || (slot + 1) % 9 == 0)) {
                buttons.put(slot, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return itemStack;
                    }
                });
            }
        });
    }
}

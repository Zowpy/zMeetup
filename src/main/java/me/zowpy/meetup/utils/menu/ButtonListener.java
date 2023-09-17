package me.zowpy.meetup.utils.menu;

import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.utils.menu.buttons.Button;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class ButtonListener implements Listener {

    private final Plugin plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());

        if (openMenu != null) {
            if (event.getAction().name().contains("DROP")) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                return;
            }

            if (event.getAction().name().contains("HOTBAR")) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                return;
            }

            if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                return;
            }

            System.out.println(event.getAction().name());

            if (event.getSlot() != event.getRawSlot()) {

                if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);

                    if (/*openMenu.isNoncancellingInventory() && */event.getCurrentItem() != null) {
                        player.getOpenInventory().getTopInventory().addItem(event.getCurrentItem());
                        event.setCurrentItem(null);

                        event.setCancelled(true);
                        event.setResult(Event.Result.DENY);
                    }
                }

                if (!openMenu.isNoncancellingInventory()) {
                    event.setCancelled(true);
                    event.setResult(Event.Result.DENY);
                }

                if (event.getAction().name().contains("PLACE") && event.getClickedInventory().getType() == InventoryType.PLAYER) {
                    event.setCancelled(true);
                    event.setResult(Event.Result.DENY);
                }

                return;
            }

            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                return;
            }

            if (openMenu.getButtons().containsKey(event.getSlot())) {
                Button button = openMenu.getButtons().get(event.getSlot());
                boolean cancel = button.shouldCancel(player, event.getSlot(), event.getClick());

                if (!cancel &&
                        (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {

                    if (!openMenu.isNoncancellingInventory()) {
                        event.setCancelled(true);

                        if (event.getCurrentItem() != null) {
                            player.getInventory().addItem(event.getCurrentItem());
                        }
                    }
                } else {
                    event.setCancelled(cancel);
                }

                button.clicked(player, event.getSlot(), event.getClick(), event.getHotbarButton());

                if (Menu.currentlyOpenedMenus.containsKey(player.getUniqueId())) {
                    Menu newMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());

                    if (newMenu == openMenu) {
                        boolean buttonUpdate = button.shouldUpdate(player, event.getSlot(), event.getClick());

                        if (buttonUpdate) {
                            openMenu.setClosedByMenu(true);
                            newMenu.openMenu(player);
                        }
                    }
                } else if (button.shouldUpdate(player, event.getSlot(), event.getClick())) {
                    openMenu.setClosedByMenu(true);
                    openMenu.openMenu(player);
                }

                if (event.isCancelled()) {
                    Bukkit.getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
                }
            } else {
                if (!openMenu.isNoncancellingInventory()) {
                    event.setCancelled(true);
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();

        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());

        if (openMenu != null && !openMenu.isNoncancellingInventory()) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }

        if (openMenu != null) {
            event.setCancelled(event.getRawSlots().stream().anyMatch(slot -> slot > event.getView().getTopInventory().getSize()));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());

        if (openMenu != null) {

            if (player.getItemOnCursor() != null) {
                event.getInventory().addItem(player.getItemOnCursor());
                player.setItemOnCursor(null);
            }

            if (Menu.transitionalMenus.contains(player.getUniqueId())) {
                Menu.transitionalMenus.remove(player.getUniqueId());
                return;
            }

            openMenu.onClose(player);

            Menu.currentlyOpenedMenus.remove(player.getUniqueId());
        }
    }

    /*@EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata("scanglitch")) {
            player.removeMetadata("scanglitch", plugin);

            for (ItemStack it : player.getInventory().getContents()) {
                if (it != null) {
                    ItemMeta meta = it.getItemMeta();
                    if (meta != null && meta.hasDisplayName()) {

                        if (meta.getDisplayName().contains("§b§c§d§e")) {
                            player.getInventory().remove(it);
                        }
                    }
                }
            }
        }
    }*/
}

package me.zowpy.meetup.menu;

import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.loadout.LoadoutItem;
import me.zowpy.meetup.profile.Profile;
import me.zowpy.meetup.utils.CC;
import me.zowpy.meetup.utils.menu.Menu;
import me.zowpy.meetup.utils.menu.buttons.Button;
import me.zowpy.meetup.utils.menu.buttons.impl.DisplayButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class LoadoutMenu extends Menu {

    private final Profile profile;

    @Override
    public String getTitle(Player player) {
        return "Loadout Editor";
    }

    @Override
    public int getSize() {
        return 36;
    }

    @Override
    public boolean isNoncancellingInventory() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> toReturn = new HashMap<>();

        for (LoadoutItem item : LoadoutItem.values()) {
            if (item.isArmor()) continue;

            toReturn.put(inventoryOrder(profile.getLoadout().getItems().get(item)), new DisplayButton(item.getDisplayItem(), false));
        }

        return toReturn;
    }

    @Override
    public void onClose(Player player) {

        Inventory inventory = getInventory();

        boolean water = false;
        boolean lava = false;

        for (int i = 0; i < 36; i++) {
            ItemStack item = inventory.getItem(i);

            if (item == null || item.getType() == Material.AIR) continue;

            for (LoadoutItem loadoutItem : LoadoutItem.values()) {
                if (!loadoutItem.compatibleMaterial(item, item.getType())) continue;

                if (loadoutItem == LoadoutItem.LAVA_1 && lava) {
                    loadoutItem = LoadoutItem.LAVA_2;
                }

                if (loadoutItem == LoadoutItem.LAVA_1) {
                    lava = true;
                }

                if (loadoutItem == LoadoutItem.WATER_1 && water) {
                    loadoutItem = LoadoutItem.WATER_2;
                }

                if (loadoutItem == LoadoutItem.WATER_1) {
                    water = true;
                }

                int slot = reverseOrder(i);
                profile.getLoadout().getItems().put(loadoutItem, slot);
            }
        }

        CompletableFuture.runAsync(() -> {
            MeetupPlugin.getInstance().getProfileHandler().save(profile);
        });

        MeetupPlugin.getInstance().getMessages().modifiedLoadout.forEach(s -> {
            player.sendMessage(CC.translate(s));
        });
    }

    private int inventoryOrder(int slot) {
        return slot >= 9 && slot <= 35 ? slot - 9 : slot >= 0 && slot <= 8 ? slot + 27 : slot <= 17 ? slot - 9 : slot;
    }

    private int reverseOrder(int slot) {
        return slot >= 27 && slot <= 35 ? slot - 27 : slot >= 1 && slot <= 26 ? slot + 9 : slot == 0 ? 9 : slot;
    }
}

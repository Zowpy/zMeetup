package me.zowpy.meetup.loadout;

import lombok.RequiredArgsConstructor;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.utils.CC;
import me.zowpy.meetup.utils.ConfigFile;
import me.zowpy.meetup.utils.ItemBuilder;
import me.zowpy.meetup.utils.ItemHelper;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LoadoutHandler {

    private final MeetupPlugin plugin;

    public static final ItemStack GOLDEN_HEAD = new ItemBuilder(Material.GOLDEN_APPLE)
            .name(CC.GOLD + "Golden Head")
            .build();

    public void giveRandom(Player player, Loadout loadout) {

        List<ItemStack> itemStacks = new ArrayList<>();
        List<ItemStack> armor = new ArrayList<>();

        for (LoadoutItem item : LoadoutItem.values()) {

            if (item == LoadoutItem.WATER_2) {
                item = LoadoutItem.WATER_1;
            }

            if (item == LoadoutItem.LAVA_2) {
                item = LoadoutItem.LAVA_1;
            }

            List<ItemStack> possibleItems = getItems(item);
            ItemStack itemStack = possibleItems.get(ThreadLocalRandom.current().nextInt(possibleItems.size()));

            if (item.isArmor()) {
                armor.add(itemStack);
                continue;
            }

            itemStacks.add(itemStack);
        }

        Collections.reverse(armor);

        player.getInventory().setArmorContents(armor.toArray(new ItemStack[0]));
        give(player, loadout, itemStacks);

        player.updateInventory();
    }

    public List<ItemStack> getItems(LoadoutItem item) {
        ConfigFile configFile = plugin.getLoadoutsFile();
        ConfigurationSection section = configFile.getConfigurationSection(item.name());

        return section.getStringList("items").stream().map(ItemHelper::deserialize)
                .collect(Collectors.toList());
    }

    public void give(Player player, Loadout loadout, List<ItemStack> contents) {

        Map<LoadoutItem, Integer> items = loadout.getItems();

        boolean water = false;
        boolean lava = false;

        for (ItemStack itemStack : contents) {

            boolean set = false;

            for (Map.Entry<LoadoutItem, Integer> entry : items.entrySet()) {

                LoadoutItem loadoutItem = entry.getKey();

                if (loadoutItem.compatibleMaterial(itemStack, itemStack.getType())) {

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

                    Integer slot = entry.getValue();

                    if (entry.getKey() != loadoutItem) {
                        LoadoutItem finalLoadoutItem = loadoutItem;
                        Map.Entry<LoadoutItem, Integer> newEntry = items.entrySet().stream().filter(integerLoadoutItemEntry -> integerLoadoutItemEntry.getKey() == finalLoadoutItem)
                                .findFirst().orElse(null);

                        if (newEntry != null) {
                            slot = newEntry.getValue();
                        }
                    }

                    if (loadoutItem == LoadoutItem.GOLDEN_APPLE) {
                        System.out.println(slot);
                        System.out.println(itemStack.getType());
                        System.out.println(itemStack.hasItemMeta());
                    }

                    player.getInventory().setItem(slot, itemStack);
                    set = true;
                }
            }

            if (!set) {
                player.getInventory().addItem(itemStack);
            }
        }
    }
}

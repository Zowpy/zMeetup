package me.zowpy.meetup.menu;

import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.config.MenusConfig;
import me.zowpy.meetup.game.player.MeetupPlayer;
import me.zowpy.meetup.utils.CC;
import me.zowpy.meetup.utils.ItemBuilder;
import me.zowpy.meetup.utils.menu.Menu;
import me.zowpy.meetup.utils.menu.buttons.Button;
import me.zowpy.meetup.utils.menu.buttons.impl.DisplayButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SpectatingMenu extends Menu {

    private final MenusConfig.SpectatingMenu config = MeetupPlugin.getInstance().getMenusConfig().spectating;

    @Override
    public String getTitle(Player player) {
        return CC.translate(config.title);
    }

    @Override
    public int getSize() {
        return config.initialSize;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        Map<Integer, Button> toReturn = new HashMap<>();

        Button placeholderItem = new DisplayButton(
                new ItemBuilder(config.placeholderItem)
                        .durability(config.placeholderItemDurability)
                        .name(config.placeholderItemName)
                        .lore(config.placeholderItemLore)
                        .build(), true
        );

        config.placeholderSlots.forEach(integer -> toReturn.put(integer, placeholderItem));

        int i = config.playerItemStartingSlot;

        for (MeetupPlayer meetupPlayer : MeetupPlugin.getInstance().getGameHandler().getPlayers().values()) {
            if (meetupPlayer.isSpectating() || meetupPlayer.isDead()) continue;

            toReturn.put(i, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return createPlayerItemStack(meetupPlayer.getName());
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    player.teleport(Bukkit.getPlayer(meetupPlayer.getUuid()));
                }
            });

            i++;
        }

        return toReturn;
    }

    private ItemStack createPlayerItemStack(String owner) {

        ItemStack itemStack = new ItemBuilder(config.playerItem)
                .name(config.playerItemName.replace("<player>", owner))
                .lore(config.playerItemLore.stream().map(s -> s.replace("<player>", owner)).collect(Collectors.toList()))
                .durability(config.playerItemDurability)
                .build();

        if (itemStack.getType() == Material.SKULL_ITEM) {

            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            meta.setOwner(owner);

            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }
}

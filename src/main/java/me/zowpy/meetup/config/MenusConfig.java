package me.zowpy.meetup.config;

import org.bukkit.Material;
import xyz.mkotb.configapi.Coloured;
import xyz.mkotb.configapi.comment.Comment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MenusConfig {

    public SpectatingMenu spectating = new SpectatingMenu();

    public static class SpectatingMenu {

        public String title = "Remaining Players";

        @Comment("This is the initial size of the spectating menu, the size will scale up depending how many players are playing")
        public int initialSize = 18;

        public Material placeholderItem = Material.STAINED_GLASS_PANE;
        public int placeholderItemDurability = 7;
        public String placeholderItemName = " ";
        public List<String> placeholderItemLore = Collections.emptyList();

        public List<Integer> placeholderSlots = Arrays.asList(
                0, 1, 2, 3, 4, 5, 6, 7, 8
        );

        public Material playerItem = Material.SKULL_ITEM;
        public int playerItemDurability = 3;

        @Coloured
        public String playerItemName = "&d<player>";

        public List<String> playerItemLore = Arrays.asList(
                "&eClick to teleport to &d<player>&e."
        );

        public int playerItemStartingSlot = 9;
    }
}

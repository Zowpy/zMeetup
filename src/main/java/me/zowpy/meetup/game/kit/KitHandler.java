package me.zowpy.meetup.game.kit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KitHandler {

    public static List<ItemStack> getKit() {

        ItemStack bow = new ItemStack(Material.BOW);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemStack arrow = new ItemStack(Material.ARROW, 32);

        return Arrays.asList(bow, sword, arrow);
    }
}

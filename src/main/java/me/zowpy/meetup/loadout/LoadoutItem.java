package me.zowpy.meetup.loadout;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
public enum LoadoutItem {

    SWORD(0, false, new ItemStack(Material.DIAMOND_SWORD)),
    ROD(1, false, new ItemStack(Material.FISHING_ROD)),
    BOW(2, false, new ItemStack(Material.BOW)),
    FOOD(3, false, new ItemStack(Material.COOKED_BEEF)),
    GOLDEN_APPLE(4, false, new ItemStack(Material.GOLDEN_APPLE)),
    GOLDEN_HEAD(5, false, LoadoutHandler.GOLDEN_HEAD),
    AXE(6, false, new ItemStack(Material.DIAMOND_AXE)),
    FLINT_STEEL(7, false, new ItemStack(Material.FLINT_AND_STEEL)),
    BLOCK(8, false, new ItemStack(Material.COBBLESTONE)),
    ARROW(9, false, new ItemStack(Material.ARROW)),
    LAVA_1(10, false, new ItemStack(Material.LAVA_BUCKET)),
    LAVA_2(11, false, new ItemStack(Material.LAVA_BUCKET)),
    WATER_1(12, false, new ItemStack(Material.WATER_BUCKET)),
    WATER_2(13, false, new ItemStack(Material.WATER_BUCKET)),
    PICKAXE(14, false, new ItemStack(Material.DIAMOND_PICKAXE)),
    ENCHANT_TABLE(15, false, new ItemStack(Material.ENCHANTMENT_TABLE)),
    ANVIL(16, false, new ItemStack(Material.ANVIL)),
    EXP_BOTTLE(17, false, new ItemStack(Material.EXP_BOTTLE)),

    HELMET(999, true, null),
    CHESTPLATE(999, true, null),
    LEGGINGS(999, true, null),
    BOOTS(999, true, null);

    private final int slot;
    private final boolean armor;
    private final ItemStack displayItem;

    public boolean compatibleMaterial(ItemStack itemStack, Material material) {
        switch (this) {
            case SWORD: {
                return material.name().contains("SWORD");
            }

            case ROD: {
                return material == Material.FISHING_ROD;
            }

            case BOW: {
                return material == Material.BOW;
            }

            case FOOD: {
                return material == Material.COOKED_BEEF;
            }

            case GOLDEN_HEAD: {
                return LoadoutHandler.GOLDEN_HEAD.isSimilar(itemStack);
            }

            case GOLDEN_APPLE: {
                return material == Material.GOLDEN_APPLE && !LoadoutHandler.GOLDEN_HEAD.isSimilar(itemStack);
            }

            case AXE: {
                return material.name().contains("AXE") && !material.name().contains("PICKAXE");
            }

            case FLINT_STEEL: {
                return material == Material.FLINT_AND_STEEL;
            }

            case BLOCK: {
                return material == Material.COBBLESTONE || material == Material.WOOD;
            }

            case ARROW: {
                return material == Material.ARROW;
            }

            case LAVA_2:
            case LAVA_1: {
                return material == Material.LAVA_BUCKET;
            }

            case WATER_2:
            case WATER_1: {
                return material == Material.WATER_BUCKET;
            }

            case PICKAXE: {
                return material.name().contains("PICKAXE");
            }

            case ENCHANT_TABLE: {
                return material == Material.ENCHANTMENT_TABLE;
            }

            case ANVIL: {
                return material == Material.ANVIL;
            }

            case EXP_BOTTLE: {
                return material == Material.EXP_BOTTLE;
            }
        }

        if (armor) {
            return material.name().contains(name());
        }

        return false;
    }
}

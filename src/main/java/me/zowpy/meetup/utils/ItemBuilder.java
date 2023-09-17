package me.zowpy.meetup.utils;

import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ItemBuilder {

    private final ItemStack is;

    public ItemBuilder(Material mat) {
        this.is = new ItemStack(mat);
    }

    public ItemBuilder(ItemStack is) {
        this.is = is;
    }

    public ItemBuilder amount(int amount) {
        this.is.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = this.is.getItemMeta();
        meta.setDisplayName(CC.translate(name));

        this.is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder lore(String name) {
        if (name == null || name.isEmpty()) {
            return this;
        }

        ItemMeta meta = this.is.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(name);
        meta.setLore(lore);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        if (lore == null || lore.isEmpty()) {
            return this;
        }

        List<String> toSet = new ArrayList<>();
        ItemMeta meta = this.is.getItemMeta();

        for (String string : lore) {
            toSet.add(CC.translate(string));
        }

        meta.setLore(toSet);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder loreWithoutOverride(List<String> lore) {
        if (lore == null || lore.isEmpty()) {
            return this;
        }

        ItemMeta meta = this.is.getItemMeta();
        List<String> toSet = meta.getLore() == null ? new ArrayList<>() : meta.getLore();

        for (String string : lore) {
            toSet.add(CC.translate(string));
        }

        meta.setLore(toSet);
        this.is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder durability(int durability) {
        this.is.setDurability((short)durability);
        return this;
    }

    /** @deprecated */
    @Deprecated
    public ItemBuilder data(int data) {
        this.is.setData(new MaterialData(this.is.getType(), (byte)data));
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        this.is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment) {
        if (enchantment == null) return this;

        this.is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder type(Material material) {
        this.is.setType(material);
        return this;
    }

    public ItemBuilder clearLore() {
        ItemMeta meta = this.is.getItemMeta();
        meta.setLore(new ArrayList<>());
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearEnchantments() {

        for (Enchantment e : this.is.getEnchantments().keySet()) {
            this.is.removeEnchantment(e);
        }

        return this;
    }

    public ItemBuilder nameWithoutOverride(String name) {
        if (!is.getItemMeta().hasDisplayName()) {
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(name);
            is.setItemMeta(meta);
        }else {
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(CC.translate(meta.getDisplayName()));

            is.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder color(Color color) {
        if (this.is.getType() == Material.LEATHER_BOOTS || this.is.getType() == Material.LEATHER_CHESTPLATE || this.is.getType() == Material.LEATHER_HELMET || this.is.getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) this.is.getItemMeta();
            meta.setColor(color);
            this.is.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        ItemMeta meta = is.getItemMeta();
        meta.addItemFlags(flags);

        is.setItemMeta(meta);

        return this;
    }

    public ItemStack build() {
        return this.is;
    }
}

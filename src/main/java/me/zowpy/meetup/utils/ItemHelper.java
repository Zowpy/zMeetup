package me.zowpy.meetup.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;

public class ItemHelper {

    public static String serialize(ItemStack itemStack) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemStack.getType().name()).append(":");

        if (itemStack.getAmount() > 1) {
            stringBuilder.append("amount=").append(itemStack.getAmount()).append(":");
        }

        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            stringBuilder.append("name=").append(itemStack.getItemMeta().getDisplayName()).append(":");
        }

        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
            stringBuilder.append(entry.getKey().getName()).append("=").append(entry.getValue()).append(":");
        }

        String s = stringBuilder.toString();

        if (s.endsWith(":")) {
            s = s.substring(0, s.length() - 1);
        }

        return s;
    }

    public static ItemStack deserialize(String s) {
        if (s == null) return null;

        String[] split = s.split(":");

        if (split.length <= 0) return null;

        Material material;

        try {
            material = Material.valueOf(split[0].toUpperCase());
        }catch (Exception e) {
            System.out.println("Expected material here but found: " + split[0]);
            return null;
        }

        ItemStack itemStack = new ItemStack(material);

        if (split.length == 1) {
            return itemStack;
        }

        for (String data : Arrays.asList(split).subList(1, split.length)) {

            if (data.startsWith("amount=")) {

                int amount;

                try {
                    amount = Integer.parseInt(data.split("=")[1]);
                }catch (Exception e) {

                    if (data.split("=").length < 2) {
                        System.out.println("Expected an integer after amount=. Skipping");
                        continue;
                    }

                    System.out.println("Expected an integer but found: " + data.split("=")[1]);
                    continue;
                }

                itemStack.setAmount(amount);
                continue;
            }

            if (data.startsWith("name=")) {
                String name = data.replaceFirst("name=", "");

                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(name);

                itemStack.setItemMeta(meta);
                continue;
            }

            String[] dataSplit = data.split("=");

            if (dataSplit.length != 2) {
                System.out.println("Unexpected argument: " + data);
                continue;
            }

            Enchantment enchantment;

            try {
                enchantment = Enchantment.getByName(dataSplit[0]);
            }catch (Exception e) {
                System.out.println("Failed to find enchantment with the name '" + dataSplit[0] + "'");
                continue;
            }

            int level;

            try {
                level = Integer.parseInt(dataSplit[1]);
            }catch (Exception e) {
                System.out.println("Expected an integer but found: " + dataSplit[1]);
                continue;
            }

            itemStack.addUnsafeEnchantment(enchantment, level);
        }

        return itemStack;
    }
}

package me.zowpy.meetup.loadout;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Loadout {

    private final Map<LoadoutItem, Integer> items = new HashMap<>();

    public Loadout() {

        for (LoadoutItem item : LoadoutItem.values()) {
            if (item.isArmor()) continue;

            items.put(item, item.getSlot());
        }
    }
}

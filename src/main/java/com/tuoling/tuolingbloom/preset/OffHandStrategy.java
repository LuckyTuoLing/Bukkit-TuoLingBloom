package com.tuoling.tuolingbloom.preset;

import com.tuoling.tuolingbloom.utils.GetMMName;
import com.tuoling.tuolingbloom.utils.GetTexturePaths;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OffHandStrategy implements BloomStrategy {




    @Override
    public String getModeName() {
        return "off-hand";
    }

    @Override
    public List<String> getTexturePaths(Player player, String preset) {
        ItemStack item = player.getInventory().getItemInOffHand();
        List<ItemStack> items = new ArrayList<>();
        if (item != null) {
            items.add(item);
        }
        return GetTexturePaths.byItem(items);
    }

    @Override
    public List<String> getMMNames(Player player, String preset) {
        ItemStack item = player.getInventory().getItemInOffHand();
        List<ItemStack> items = new ArrayList<>();
        if (item != null) {
            items.add(item);
        }
        return GetMMName.byItem(items);
    }
}

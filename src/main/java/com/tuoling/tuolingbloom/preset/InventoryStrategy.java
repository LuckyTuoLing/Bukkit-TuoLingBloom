package com.tuoling.tuolingbloom.preset;

import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingbloom.utils.GetMMName;
import com.tuoling.tuolingbloom.utils.GetTexturePaths;
import com.tuoling.tuolingbloom.utils.PresetUtils;
import com.tuoling.tuolingcore.utils.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryStrategy implements BloomStrategy {

    @Override
    public String getModeName() {
        return "inventory";
    }

    @Override
    public List<String> getTexturePaths(Player player, String preset) {
        PresetUtils pu = new PresetUtils(preset);

        List<ItemStack> items = new ArrayList<>();
        if (!pu.hasValue()) {
            sendError(player, preset);
            return new ArrayList<>();
        }
        for (String strInt : pu.getValueList()) {
            int index = parseIntSafely(strInt, player, preset);
            if (index < 0 || index >= 36) continue;

            ItemStack item = player.getInventory().getItem(index);
            if (ItemUtil.itemIsNull(item)) {
                continue;
            }
            items.add(item);
        }
        return GetTexturePaths.byItem(items);
    }

    @Override
    public List<String> getMMNames(Player player, String preset) {
        PresetUtils pu = new PresetUtils(preset);
        List<ItemStack> items = new ArrayList<>();
        if (!pu.hasValue()) {
            sendError(player, preset);
            return new ArrayList<>();
        }
        for (String strInt : pu.getValueList()) {
            int index = parseIntSafely(strInt, player, preset);
            if (index < 0 || index >= 36) continue;
            ItemStack item = player.getInventory().getItem(index);
            if (ItemUtil.itemIsNull(item)) {
                continue;
            }
            items.add(item);
        }
        return GetMMName.byItem(items);
    }

    private int parseIntSafely(String strInt, Player player, String preset) {
        try {
            return Integer.parseInt(strInt);
        } catch (NumberFormatException e) {
            Message.sendPlayer(player, "config-error-value-not-number", "%value%", strInt);
            return -1;
        }
    }

    private void sendError(Player player, String preset) {
        Message.sendPlayer(player, "config-error-slot");
        Message.sendPlayer(player, "config-error-slot-detail", "%preset%", preset);
    }
}

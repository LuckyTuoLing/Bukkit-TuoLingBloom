package com.tuoling.tuolingbloom.preset;

import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingbloom.utils.GetMMName;
import com.tuoling.tuolingbloom.utils.GetTexturePaths;
import com.tuoling.tuolingbloom.utils.PresetUtils;
import com.tuoling.tuolingcore.utils.ItemUtil;
import eos.moe.dragoncore.api.SlotAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DragonSlotStrategy implements BloomStrategy {

    @Override
    public String getModeName() {
        return "dragon-slot";
    }

    @Override
    public List<String> getTexturePaths(Player player, String preset) {
        PresetUtils pu = new PresetUtils(preset);

        List<ItemStack> items = new ArrayList<>();
        if (!pu.hasValue()) {
            sendError(player, preset);
            return new ArrayList<>();
        }
        for (String slotName : pu.getValueList()) {
            if (slotName == null || slotName.isEmpty()) {
                sendError(player, preset);
                return new ArrayList<>();
            }
            ItemStack item = SlotAPI.getCacheSlotItem(player, slotName);
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
        for (String slotName : pu.getValueList()) {
            if (slotName == null || slotName.isEmpty()) {
                sendError(player, preset);
                return new ArrayList<>();
            }
            ItemStack item = SlotAPI.getCacheSlotItem(player, slotName);
            if (ItemUtil.itemIsNull(item)) {
                continue;
            }
            items.add(item);
        }
        return GetMMName.byItem(items);
    }

    private void sendError(Player player, String preset) {
        Message.sendPlayer(player, "config-error-slot");
        Message.sendPlayer(player, "config-error-slot-detail", "%preset%", preset);
    }

}

package com.tuoling.tuolingbloom.utils;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.config.data.IdentifyModel;
import com.tuoling.tuolingbloom.config.holder.DefaultConfig;
import com.tuoling.tuolingcore.utils.ItemUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GetMMName {

    /**
     * 根据物品识别 mm-name（单个）
     */
    public static String byItem(ItemStack item) {
        if (ItemUtil.itemIsNull(item)) {
            return null;
        }
        DefaultConfig defaultConfig = TuoLingBloom.inst().defaultConfig();
        if (defaultConfig == null) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        for (IdentifyModel model : defaultConfig.getIdentifyModels().values()) {
            String mmName = model.getMmName();
            String checkName = model.getCheckName();
            String checkLore = model.getCheckLore();

            if (checkLore == null || checkLore.trim().isEmpty()) {
                if (checkName == null || checkName.trim().isEmpty()) {
                    continue;
                }
                if (!ItemUtil.itemHasName(item)) {
                    continue;
                }
                String name = meta.getDisplayName();
                if (name != null && name.contains(checkName)) {
                    return mmName;
                }
            } else if (checkName == null || checkName.trim().isEmpty()) {
                if (!ItemUtil.itemHasLore(item)) {
                    continue;
                }
                List<String> lore = meta.getLore();
                if (lore == null) continue;
                for (String l : lore) {
                    if (l.contains(checkLore)) {
                        return mmName;
                    }
                }
            } else {
                if (!ItemUtil.itemHasLore(item)) {
                    continue;
                }
                if (!ItemUtil.itemHasName(item)) {
                    continue;
                }
                String name = meta.getDisplayName();
                List<String> lore = meta.getLore();
                if (name == null || lore == null) continue;
                if (!name.contains(checkName)) {
                    continue;
                }
                for (String l : lore) {
                    if (l.contains(checkLore)) {
                        return mmName;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据多个物品识别 mm-name 列表
     */
    public static List<String> byItem(List<ItemStack> items) {
        List<String> names = new LinkedList<>();
        for (ItemStack item : items) {
            String name = byItem(item);
            if (name != null) {
                names.add(name);
            }
        }
        return names;
    }

    /**
     * 根据 identify-model 下的名字列表查表获取 mm-name 列表
     */
    public static List<String> byName(List<String> modelNames) {
        List<String> names = new LinkedList<>();
        DefaultConfig defaultConfig = TuoLingBloom.inst().defaultConfig();
        if (defaultConfig == null) return names;

        for (String modelName : modelNames) {
            IdentifyModel model = defaultConfig.getIdentifyModels().get(modelName);
            if (model != null) {
                names.add(model.getMmName());
            }
        }
        return names;
    }
}

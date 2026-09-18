package com.tuoling.tuolingbloom.utils;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.config.data.IdentifyTexture;
import com.tuoling.tuolingbloom.config.holder.DefaultConfig;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingcore.utils.ItemUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GetTexturePaths {

    public static List<String> byItem(List<ItemStack> items) {
        List<String> paths = new LinkedList<>();
        DefaultConfig defaultConfig = TuoLingBloom.inst().defaultConfig();
        if (defaultConfig == null) return paths;

        for (ItemStack item : items) {
            if (ItemUtil.itemIsNull(item)) {
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            for (IdentifyTexture texture : defaultConfig.getIdentifyTextures().values()) {
                String path = texture.getPath();
                String checkName = texture.getCheckName();
                String checkLore = texture.getCheckLore();

                if (checkLore == null || checkLore.trim().isEmpty()) {
                    if (checkName == null || checkName.trim().isEmpty()) {
                        continue;
                    }
                    if (!ItemUtil.itemHasName(item)) {
                        continue;
                    }
                    String name = meta.getDisplayName();
                    if (name != null && name.contains(checkName)) {
                        paths.add(path);
                    }
                } else if (checkName == null || checkName.trim().isEmpty()) {
                    if (!ItemUtil.itemHasLore(item)) {
                        continue;
                    }
                    List<String> lore = meta.getLore();
                    if (lore == null) continue;
                    for (String l : lore) {
                        if (l.contains(checkLore)) {
                            paths.add(path);
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
                            paths.add(path);
                        }
                    }
                }
            }
        }
        return paths;
    }

    public static List<String> byName(List<String> textureNames) {
        List<String> paths = new ArrayList<>();
        DefaultConfig defaultConfig = TuoLingBloom.inst().defaultConfig();
        if (defaultConfig == null) return paths;

        for (String textureName : textureNames) {
            IdentifyTexture texture = defaultConfig.getIdentifyTextures().get(textureName);
            if (texture == null) {
                Message.sendConsole("identify-not-found", "%name%", textureName);
                continue;
            }
            paths.add(texture.getPath());
        }
        return paths;
    }
}

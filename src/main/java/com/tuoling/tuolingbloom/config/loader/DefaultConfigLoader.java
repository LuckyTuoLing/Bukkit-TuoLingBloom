package com.tuoling.tuolingbloom.config.loader;

import com.tuoling.tuolingbloom.config.data.*;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingcore.framework.config.ConfigHolder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultConfigLoader {

    public DefaultConfigData load(ConfigHolder holder) {
        if (holder.configIsNull()) {
            Message.sendConsole("config-empty", "%type%", "DefaultConfig");
            return new DefaultConfigData(
                    true,
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    Collections.emptyList()
            );
        }

        boolean opNotCooldown = holder.getBoolean("op-not-cooldown", true);
        Map<String, IdentifyTexture> identifyTextures = loadIdentifyTextures(holder);
        Map<String, IdentifyModel> identifyModels = loadIdentifyModels(holder);
        Map<String, BloomPresetEntry> bloomPresets = loadBloomPresets(holder);
        List<BloomGroup> bloomGroups = loadBloomGroups(holder);

        return new DefaultConfigData(opNotCooldown, identifyTextures, identifyModels, bloomPresets, bloomGroups);
    }

    private Map<String, IdentifyTexture> loadIdentifyTextures(ConfigHolder holder) {
        Map<String, IdentifyTexture> map = new LinkedHashMap<>();
        ConfigurationSection section = holder.getConfigurationSection("identify-texture");
        if (section == null) return map;
        for (String name : section.getKeys(false)) {
            ConfigurationSection s = section.getConfigurationSection(name);
            if (s == null) continue;
            map.put(name, new IdentifyTexture(
                    s.getString("check-lore", null),
                    s.getString("check-name", null),
                    s.getString("path", null)
            ));
        }
        return map;
    }

    private Map<String, IdentifyModel> loadIdentifyModels(ConfigHolder holder) {
        Map<String, IdentifyModel> map = new LinkedHashMap<>();
        ConfigurationSection section = holder.getConfigurationSection("identify-model");
        if (section == null) return map;
        for (String name : section.getKeys(false)) {
            ConfigurationSection s = section.getConfigurationSection(name);
            if (s == null) continue;
            map.put(name, new IdentifyModel(
                    s.getString("check-lore", null),
                    s.getString("check-name", null),
                    s.getString("mm-name", null)
            ));
        }
        return map;
    }

    private Map<String, BloomPresetEntry> loadBloomPresets(ConfigHolder holder) {
        Map<String, BloomPresetEntry> map = new LinkedHashMap<>();
        ConfigurationSection section = holder.getConfigurationSection("preset");
        if (section == null) return map;
        collectPresets(section, map);
        return map;
    }

    private void collectPresets(ConfigurationSection section, Map<String, BloomPresetEntry> map) {
        for (String key : section.getKeys(false)) {
            ConfigurationSection s = section.getConfigurationSection(key);
            if (s == null) continue;
            if (s.contains("mode")) {
                String mode = s.getString("mode", "main-hand");
                String value = s.getString("value", null);
                String view = s.getString("view", null);
                String group = s.getString("group");
                long duration = s.getLong("duration", 0);
                List<String> valueList = parseValueList(value);
                map.put(key, new BloomPresetEntry(key, mode, value, view, group, duration, valueList));
            } else {
                collectPresets(s, map);
            }
        }
    }

    private List<BloomGroup> loadBloomGroups(ConfigHolder holder) {
        List<BloomGroup> list = new ArrayList<>();
        ConfigurationSection section = holder.getConfigurationSection("bloom-group");
        if (section == null) return list;
        for (String groupName : section.getKeys(false)) {
            ConfigurationSection groupSection = section.getConfigurationSection(groupName);
            if (groupSection == null) continue;
            long cooldown = groupSection.getLong("cooldown");
            list.add(new BloomGroup(groupName, cooldown));
        }
        return list;
    }

    private static List<String> parseValueList(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (String part : value.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }
}

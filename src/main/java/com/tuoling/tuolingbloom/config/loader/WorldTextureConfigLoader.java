package com.tuoling.tuolingbloom.config.loader;

import com.tuoling.tuolingbloom.config.data.WorldTextureEntry;
import com.tuoling.tuolingbloom.config.data.WorldPosition;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingcore.framework.config.ConfigHolder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class WorldTextureConfigLoader {

    public Map<String, WorldTextureEntry> load(ConfigHolder holder) {
        if (holder.configIsNull()) {
            Message.sendConsole("config-empty", "%type%", "WorldTextureConfig");
            return Collections.emptyMap();
        }
        Map<String, WorldTextureEntry> map = new LinkedHashMap<>();
        for (String name : holder.getValues(false).keySet()) {
            ConfigurationSection section = holder.getConfigurationSection(name);
            if (section == null) continue;
            String world = section.getString("world", "");
            String preset = section.getString("preset", "");
            WorldPosition location = loadLocation(section.getConfigurationSection("location"));
            map.put(name, new WorldTextureEntry(world, location, preset));
        }
        return map;
    }

    private WorldPosition loadLocation(ConfigurationSection section) {
        if (section == null) return null;
        return new WorldPosition(
                section.getDouble("x", 0),
                section.getDouble("y", 0),
                section.getDouble("z", 0)
        );
    }
}

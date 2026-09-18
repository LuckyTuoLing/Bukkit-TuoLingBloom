package com.tuoling.tuolingbloom.config.loader;

import com.tuoling.tuolingbloom.config.data.ModelData;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingcore.framework.config.ConfigHolder;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModelConfigLoader {

    public Map<Integer, ModelData> load(ConfigHolder holder) {
        if (holder.configIsNull()) {
            Message.sendConsole("config-empty", "%type%", "ModelConfig");
            return Collections.emptyMap();
        }
        Map<Integer, ModelData> map = new LinkedHashMap<>();
        ConfigurationSection settingSection = holder.getConfigurationSection("setting");
        if (settingSection == null) return map;
        for (String key : settingSection.getKeys(false)) {
            int index;
            try {
                index = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                Message.sendConsole("config-parse-error", "%type%", "ModelConfig", "%key%", key);
                continue;
            }
            ConfigurationSection s = settingSection.getConfigurationSection(key);
            if (s == null) continue;
            map.put(index, loadModelData(s));
        }
        return map;
    }

    private ModelData loadModelData(ConfigurationSection s) {
        int delay = s.getInt("delay", 0);
        ConfigurationSection locSection = s.getConfigurationSection("location");
        if (locSection == null) {
            return new ModelData(delay, new Location(null, 0, 0, 0));
        }
        Location loc = new Location(
                null,
                locSection.getDouble("x", 0),
                locSection.getDouble("y", 0),
                locSection.getDouble("z", 0)
        );
        return new ModelData(delay, loc);
    }
}

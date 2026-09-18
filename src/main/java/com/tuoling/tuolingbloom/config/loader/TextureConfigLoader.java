package com.tuoling.tuolingbloom.config.loader;

import com.tuoling.tuolingbloom.config.data.AnimationConfig;
import com.tuoling.tuolingbloom.config.data.LayerSetting;
import com.tuoling.tuolingbloom.config.data.RotateAnimConfig;
import com.tuoling.tuolingbloom.config.data.ScaleAnimConfig;
import com.tuoling.tuolingbloom.config.data.TranslateAnimConfig;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingcore.framework.config.ConfigHolder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TextureConfigLoader {

    public Map<Integer, LayerSetting> load(ConfigHolder holder) {
        if (holder.configIsNull()) {
            Message.sendConsole("config-empty", "%type%", "TextureConfig");
            return Collections.emptyMap();
        }
        Map<Integer, LayerSetting> map = new LinkedHashMap<>();
        ConfigurationSection settingSection = holder.getConfigurationSection("setting");
        if (settingSection == null) return map;
        for (String key : settingSection.getKeys(false)) {
            int index;
            try {
                index = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                Message.sendConsole("config-parse-error", "%type%", "TextureConfig", "%key%", key);
                continue;
            }
            ConfigurationSection layerSection = settingSection.getConfigurationSection(key);
            if (layerSection == null) continue;
            map.put(index, loadLayerSetting(layerSection));
        }
        return map;
    }

    private LayerSetting loadLayerSetting(ConfigurationSection section) {
        int delay = section.getInt("delay", 0);

        LayerSetting.TextureFields texture = null;
        if (section.isConfigurationSection("texture")) {
            texture = loadTextureFields(section.getConfigurationSection("texture"));
        }

        List<AnimationConfig> animations = new ArrayList<>();
        if (section.isConfigurationSection("animations") && texture != null) {
            ConfigurationSection animSection = section.getConfigurationSection("animations");
            if (animSection.isConfigurationSection("translate-animation")) {
                TranslateAnimConfig c = loadTranslateAnim(animSection.getConfigurationSection("translate-animation"));
                if (c != null) animations.add(c);
            }
            if (animSection.isConfigurationSection("scale-animation")) {
                ScaleAnimConfig c = loadScaleAnim(animSection.getConfigurationSection("scale-animation"));
                if (c != null) animations.add(c);
            }
            if (animSection.isConfigurationSection("rotate-animation")) {
                RotateAnimConfig c = loadRotateAnim(animSection.getConfigurationSection("rotate-animation"));
                if (c != null) animations.add(c);
            }
        }
        return new LayerSetting(delay, texture, animations);
    }

    private LayerSetting.TextureFields loadTextureFields(ConfigurationSection section) {
        if (section == null) return null;
        return new LayerSetting.TextureFields(
                section.getDouble("translate-x", 0),
                section.getDouble("translate-y", 0),
                section.getDouble("translate-z", 0),
                (float) section.getDouble("rotate-x", 0),
                (float) section.getDouble("rotate-y", 0),
                (float) section.getDouble("rotate-z", 0),
                section.getBoolean("follow-player-eyes", false)
        );
    }

    private TranslateAnimConfig loadTranslateAnim(ConfigurationSection section) {
        if (section == null) return null;
        return new TranslateAnimConfig(
                section.getString("direction", "z"),
                section.getInt("delay", 0),
                (float) section.getDouble("distance", 0),
                section.getInt("duration", 0),
                section.getInt("cycle-count", 1),
                section.getBoolean("fixed", true)
        );
    }

    private ScaleAnimConfig loadScaleAnim(ConfigurationSection section) {
        if (section == null) return null;
        return new ScaleAnimConfig(
                section.getInt("delay", 0),
                (float) section.getDouble("from-scale", 1),
                (float) section.getDouble("to-scale", 1),
                section.getInt("duration", 0),
                section.getInt("cycle-count", 1),
                section.getBoolean("fixed", true),
                section.getInt("resetTime", 0)
        );
    }

    private RotateAnimConfig loadRotateAnim(ConfigurationSection section) {
        if (section == null) return null;
        return new RotateAnimConfig(
                section.getString("direction", "z"),
                section.getInt("delay", 0),
                (float) section.getDouble("angle", 0),
                section.getInt("duration", 0),
                section.getInt("cycle-count", -1),
                section.getBoolean("fixed", false),
                section.getInt("resetTime", 0)
        );
    }
}

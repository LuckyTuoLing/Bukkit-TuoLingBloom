package com.tuoling.tuolingbloom.config.holder;

import com.tuoling.tuolingbloom.config.data.LayerSetting;
import com.tuoling.tuolingbloom.config.loader.TextureConfigLoader;
import com.tuoling.tuolingcore.framework.config.ConfigHolder;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class TextureConfig extends ConfigHolder {

    private final Map<Integer, LayerSetting> settings;

    public TextureConfig(File configFile) {
        super(configFile);
        TextureConfigLoader loader = new TextureConfigLoader();
        this.settings = loader.load(this);
    }

    public Map<Integer, LayerSetting> getSettings() {
        return new LinkedHashMap<>(settings);
    }
}

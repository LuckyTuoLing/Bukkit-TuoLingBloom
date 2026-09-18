package com.tuoling.tuolingbloom.config.holder;

import com.tuoling.tuolingbloom.config.data.WorldTextureEntry;
import com.tuoling.tuolingbloom.config.loader.WorldTextureConfigLoader;
import com.tuoling.tuolingcore.framework.config.ConfigHolder;

import java.io.File;
import java.util.Map;

public class WorldTextureConfig extends ConfigHolder {

    private final Map<String, WorldTextureEntry> entries;

    public WorldTextureConfig(File configFile) {
        super(configFile);
        WorldTextureConfigLoader loader = new WorldTextureConfigLoader();
        this.entries = loader.load(this);
    }

    public Map<String, WorldTextureEntry> getEntries() {
        return new java.util.LinkedHashMap<>(entries);
    }
}

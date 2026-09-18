package com.tuoling.tuolingbloom.config.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 主配置的加载产物，纯数据载体。
 * 由 DefaultConfigLoader 从 yml 解析产出，DefaultConfig 直接持有。
 * 集合 getter 返回浅拷贝副本，元素自身不可变，无需深拷贝。
 */
public class DefaultConfigData {

    private final boolean opNotCooldown;
    private final Map<String, IdentifyTexture> identifyTextures;
    private final Map<String, IdentifyModel> identifyModels;
    private final Map<String, BloomPresetEntry> bloomPresets;
    private final List<BloomGroup> bloomGroups;

    public DefaultConfigData(boolean opNotCooldown,
                             Map<String, IdentifyTexture> identifyTextures,
                             Map<String, IdentifyModel> identifyModels,
                             Map<String, BloomPresetEntry> bloomPresets,
                             List<BloomGroup> bloomGroups) {
        this.opNotCooldown = opNotCooldown;
        this.identifyTextures = identifyTextures;
        this.identifyModels = identifyModels;
        this.bloomPresets = bloomPresets;
        this.bloomGroups = bloomGroups;
    }

    public boolean isOpNotCooldown() {
        return opNotCooldown;
    }

    public Map<String, IdentifyTexture> getIdentifyTextures() {
        return new LinkedHashMap<>(identifyTextures);
    }

    public Map<String, IdentifyModel> getIdentifyModels() {
        return new LinkedHashMap<>(identifyModels);
    }

    public Map<String, BloomPresetEntry> getBloomPresets() {
        return new LinkedHashMap<>(bloomPresets);
    }

    public List<BloomGroup> getBloomGroups() {
        return new ArrayList<>(bloomGroups);
    }
}
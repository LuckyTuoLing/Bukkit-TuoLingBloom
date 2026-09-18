package com.tuoling.tuolingbloom.config.holder;

import com.tuoling.tuolingbloom.config.data.BloomGroup;
import com.tuoling.tuolingbloom.config.data.BloomPresetEntry;
import com.tuoling.tuolingbloom.config.data.DefaultConfigData;
import com.tuoling.tuolingbloom.config.data.IdentifyModel;
import com.tuoling.tuolingbloom.config.data.IdentifyTexture;
import com.tuoling.tuolingbloom.config.loader.DefaultConfigLoader;
import com.tuoling.tuolingcore.framework.config.ConfigHolder;
import lombok.Getter;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 主配置持有者。
 * - 构造时调 Loader 拿到 DefaultConfigData，直接持有
 * - 集合 getter 委托给 data 的浅拷贝
 * - 业务查询方法（getCooldown / isInBloomGroup）在本体
 */
public class DefaultConfig extends ConfigHolder {

    private final DefaultConfigData data;

    public DefaultConfig(File configFile) {
        super(configFile);
        this.data = new DefaultConfigLoader().load(this);
    }

    public boolean isOpNotCooldown() {
        return data.isOpNotCooldown();
    }

    public Map<String, IdentifyTexture> getIdentifyTextures() {
        return data.getIdentifyTextures();
    }

    public Map<String, IdentifyModel> getIdentifyModels() {
        return data.getIdentifyModels();
    }

    public Map<String, BloomPresetEntry> getBloomPresets() {
        return data.getBloomPresets();
    }

    public List<BloomGroup> getBloomGroups() {
        return data.getBloomGroups();
    }

    public long getCooldown(String preset) {
        BloomPresetEntry entry = data.getBloomPresets().get(preset);
        if (entry == null || entry.getGroup() == null) {
            return 0;
        }
        for (BloomGroup group : data.getBloomGroups()) {
            if (group.getGroupName().equals(entry.getGroup())) {
                return group.getCooldown();
            }
        }
        return 0;
    }

    /**
     * 判断传入的 bloom 名是否全部落在同一个 group 内（互斥冲突检查）。
     * 通过每个 preset 自带的 group 字段判断，而非旧版的 group-names 列表。
     */
    public boolean isInBloomGroup(List<String> presets) {
        if (presets == null || presets.size() < 2) {
            return false;
        }
        String firstGroup = resolveGroup(presets.get(0));
        if (firstGroup == null) {
            return false;
        }
        for (int i = 1; i < presets.size(); i++) {
            String g = resolveGroup(presets.get(i));
            if (g == null || !g.equals(firstGroup)) {
                return false;
            }
        }
        return true;
    }

    private String resolveGroup(String preset) {
        BloomPresetEntry entry = data.getBloomPresets().get(preset);
        return entry == null ? null : entry.getGroup();
    }
}

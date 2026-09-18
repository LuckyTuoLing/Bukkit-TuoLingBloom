package com.tuoling.tuolingbloom.utils;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.config.data.BloomPresetEntry;
import com.tuoling.tuolingbloom.config.data.LayerSetting;
import com.tuoling.tuolingbloom.config.holder.DefaultConfig;
import com.tuoling.tuolingbloom.config.holder.ModelConfig;
import com.tuoling.tuolingbloom.config.holder.TextureConfig;
import com.tuoling.tuolingbloom.utils.Message;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 预设工具类，构造时传入 preset 名称，后续直接调用方法获取数据。
 * 消除 TuoLingBloom.inst().defaultConfig().getBloomPresets().get(preset).xxx() 死亡链。
 */
@Getter
public class PresetUtils {

    private final String preset;
    private final BloomPresetEntry entry;

    public PresetUtils(String preset) {
        this.preset = preset;
        DefaultConfig config = TuoLingBloom.inst().defaultConfig();
        if (config == null) {
            this.entry = null;
            return;
        }
        this.entry = config.getBloomPresets().get(preset);
    }

    /**
     * 预设是否存在
     */

    public boolean exists() {
        return entry != null;
    }

    /**
     * 获取动画配置文件名（对应 config.yml 中的 view 字段）
     */
    public String getView() {
        if (entry == null) return null;
        return entry.getView();
    }

    /**
     * 获取策略模式（main-hand / off-hand / dragon-slot / inventory / direct-bloom）
     */
    public String getMode() {
        if (entry == null) return null;
        return entry.getMode();
    }

    /**
     * 获取 value 原始值（对应 config.yml 中的 value 字段）
     */
    public String getValue() {
        if (entry == null) return null;
        return entry.getValue();
    }

    /**
     * 获取 value 分割后的列表（按英文逗号分割）
     */
    public List<String> getValueList() {
        if (entry == null) return Collections.emptyList();
        return entry.getValueList();
    }

    /**
     * value 是否为空（null 或空字符串）
     */
    public boolean hasValue() {
        if (entry == null) return false;
        String value = entry.getValue();
        return value != null && !value.isEmpty();
    }

    /**
     * 获取绽放组名（对应 config.yml 中的 group 字段）
     */
    public String getGroup() {
        if (entry == null) return null;
        return entry.getGroup();
    }

    /**
     * 获取动画持续时间（秒，-1 表示不自动删除）
     */
    public long getDuration() {
        if (entry == null) return -1;
        return entry.getDuration();
    }

    /**
     * 获取贴图层级设置（texture 文件夹下对应动画的 layers）
     */
    public Map<Integer, LayerSetting> getTextureLayers() {
        if (entry == null) return null;
        TextureConfig textureConfig = TuoLingBloom.inst().textureConfig(entry.getView());
        if (textureConfig == null) {
            Message.sendConsole("config-not-found", "%type%", "texture", "%name%", entry.getView(), "%preset%", preset);
            return null;
        }
        return textureConfig.getSettings();
    }

    /**
     * 获取模型配置（model 文件夹下对应动画的 ModelConfig）
     */
    public ModelConfig getModelConfig() {
        if (entry == null) return null;
        ModelConfig modelConfig = TuoLingBloom.inst().modelConfig(entry.getView());
        if (modelConfig == null) {
            Message.sendConsole("config-not-found", "%type%", "model", "%name%", entry.getView(), "%preset%", preset);
        }
        return modelConfig;
    }

    /**
     * 获取绽放类型（"model" 或 "texture"），用于路由到对应的 Bloom 实现
     */
    public String getBloomType() {
        if (entry == null) return null;
        return TuoLingBloom.inst().getBloomRouter().router(entry.getView());
    }

    /**
     * 获取所有预设名称（用于 Tab 补全）
     */
    public static Set<String> getAllPresetNames() {
        DefaultConfig config = TuoLingBloom.inst().defaultConfig();
        if (config == null) return Collections.emptySet();
        return config.getBloomPresets().keySet();
    }
}

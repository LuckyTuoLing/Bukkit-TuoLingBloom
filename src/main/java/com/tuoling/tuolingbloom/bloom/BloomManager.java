package com.tuoling.tuolingbloom.bloom;

import com.tuoling.tuolingcore.framework.manager.KeyValueManager;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingbloom.utils.PresetUtils;
import org.bukkit.entity.LivingEntity;

public class BloomManager extends KeyValueManager<String, Bloom> {

    public BloomManager() {
        register("model", new BloomModel());
        register("texture", new BloomTexture());
    }

    public void bloom(LivingEntity entity, String preset) {
        PresetUtils pu = new PresetUtils(preset);
        if (!pu.exists()) {
            Message.sendConsole("preset-not-exist", "%preset%", preset);
            return;
        }
        String bloomType = pu.getBloomType();
        Bloom bloom = get(bloomType);
        if (bloom == null) {
            Message.sendConsole("bloom-type-not-found", "%type%", String.valueOf(bloomType), "%preset%", preset);
            return;
        }
        bloom.bloom(entity, preset);
    }
}

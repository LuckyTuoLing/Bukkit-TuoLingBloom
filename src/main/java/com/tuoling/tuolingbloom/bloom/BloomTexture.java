package com.tuoling.tuolingbloom.bloom;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.service.BloomTextureService;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class BloomTexture implements Bloom {
    @Override
    public String getName() {
        return "texture";
    }

    @Override
    public void bloom(LivingEntity entity, String preset) {
        // direct-bloom 策略不需要 player 参数，传 null 即可
        Player player = entity instanceof Player ? (Player) entity : null;
        List<String> paths = TuoLingBloom.inst().getBloomStrategyManager().getPaths(player, preset);
        BloomTextureService bloomTextureService = new BloomTextureService(entity, preset);
        bloomTextureService.texPathToBloom(paths);
    }
}

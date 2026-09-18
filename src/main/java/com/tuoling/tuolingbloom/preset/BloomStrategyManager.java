package com.tuoling.tuolingbloom.preset;


import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingbloom.utils.PresetUtils;
import com.tuoling.tuolingcore.framework.manager.AbstractKeyedManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;



//底层自动创建HashMap
public class BloomStrategyManager extends AbstractKeyedManager<String, BloomStrategy> {

    public BloomStrategyManager() {
        List<BloomStrategy> bloomStrategies = new ArrayList<>();
        bloomStrategies.add(new DirectBloomStrategy());
        bloomStrategies.add(new DragonSlotStrategy());
        bloomStrategies.add(new MainHandStrategy());
        bloomStrategies.add(new OffHandStrategy());
        bloomStrategies.add(new InventoryStrategy());
        registerAll(bloomStrategies);

    }

    public List<String> getPaths(Player player, String preset){
        PresetUtils pu = new PresetUtils(preset);
        if (!pu.exists()) {
            Message.sendPlayer(player, "preset-not-exist", "%preset%", preset);
            return new ArrayList<>();
        }
        String mode = pu.getMode();
        BloomStrategy strategy = get(mode);
        if (strategy == null) {
            Message.sendConsole("strategy-not-found", "%mode%", String.valueOf(mode), "%preset%", preset);
            return new ArrayList<>();
        }
        return strategy.getTexturePaths(player, preset);
    }

    public List<String> getMmNames(Player player, String preset){
        PresetUtils pu = new PresetUtils(preset);
        if (!pu.exists()) {
            Message.sendPlayer(player, "preset-not-exist", "%preset%", preset);
            return new ArrayList<>();
        }
        String mode = pu.getMode();
        BloomStrategy strategy = get(mode);
        if (strategy == null) {
            Message.sendConsole("strategy-not-found", "%mode%", String.valueOf(mode), "%preset%", preset);
            return new ArrayList<>();
        }
        return strategy.getMMNames(player, preset);
    }


    @Override
    public String extractKey(BloomStrategy bloomStrategy) {
        return bloomStrategy.getModeName();
    }

}

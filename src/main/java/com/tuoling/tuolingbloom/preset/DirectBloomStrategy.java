package com.tuoling.tuolingbloom.preset;

import com.tuoling.tuolingbloom.utils.GetMMName;
import com.tuoling.tuolingbloom.utils.GetTexturePaths;
import com.tuoling.tuolingbloom.utils.PresetUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class DirectBloomStrategy implements BloomStrategy {

    @Override
    public String getModeName() {
        return "direct-bloom";
    }

    @Override
    public List<String> getTexturePaths(Player player, String preset) {
        PresetUtils pu = new PresetUtils(preset);
        return GetTexturePaths.byName(pu.getValueList());
    }

    @Override
    public List<String> getMMNames(Player player, String preset) {
        PresetUtils pu = new PresetUtils(preset);
        return GetMMName.byName(pu.getValueList());
    }

}

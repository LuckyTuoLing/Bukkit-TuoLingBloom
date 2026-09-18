package com.tuoling.tuolingbloom.config.holder;

import com.tuoling.tuolingbloom.config.data.ModelData;
import com.tuoling.tuolingbloom.config.loader.ModelConfigLoader;
import com.tuoling.tuolingcore.framework.config.ConfigHolder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModelConfig extends ConfigHolder {

    private final Map<Integer, ModelData> settings;

    public ModelConfig(File configFile) {
        super(configFile);
        this.settings = new ModelConfigLoader().load(this);
    }

    public Map<Integer, ModelData> getSettings() {
        return new LinkedHashMap<>(settings);
    }

    public Location parsePlayer(int index, Player player) {
        ModelData data = settings.get(index);
        if (data == null) return null;
        Location base = data.getLocation();
        base.setWorld(player.getWorld());
        base.setX(base.getX() + player.getLocation().getX());
        base.setY(base.getY() + player.getLocation().getY());
        base.setZ(base.getZ() + player.getLocation().getZ());
        return base;
    }
}

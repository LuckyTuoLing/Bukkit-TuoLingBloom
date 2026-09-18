package com.tuoling.tuolingbloom.config.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

@Getter
@AllArgsConstructor
public class ModelData {
    private final int delay;
    private final Location location;

    /**
     * 解析基于绽放者位置的最终落点。
     * 实体位置 = 绽放者位置 + 配置偏移；朝向跟随绽放者
     */
    public Location resolveLocation(LivingEntity entity) {
        if (location == null || entity == null) return null;
        Location entityLoc = entity.getLocation();
        return new Location(
                entityLoc.getWorld(),
                entityLoc.getX() + location.getX(),
                entityLoc.getY() + location.getY(),
                entityLoc.getZ() + location.getZ(),
                entityLoc.getYaw(),
                entityLoc.getPitch()
        );
    }

    public Location getLocation() {
        return location == null ? null : location.clone();
    }
}

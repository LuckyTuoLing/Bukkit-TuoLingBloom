package com.tuoling.tuolingbloom.bloom;

import org.bukkit.entity.LivingEntity;

public interface Bloom {

    String getName();
    void bloom(LivingEntity entity, String preset);
}

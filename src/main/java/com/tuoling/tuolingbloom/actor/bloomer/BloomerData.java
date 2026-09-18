package com.tuoling.tuolingbloom.actor.bloomer;

import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

@Getter
public class BloomerData {
    private final LivingEntity entity;
    private final String preset;
    private final List<BukkitTask> tasks;
    private final List<String> textureIds;
    private final List<String> paths;
    private final List<LivingEntity> bloomEntities;
    public BloomerData(LivingEntity entity, String preset, List<BukkitTask> tasks,
                       List<String> textureIds, List<String> paths, List<LivingEntity> bloomEntities) {
        this.entity = entity;
        this.preset = preset;
        this.tasks = tasks;
        this.textureIds = textureIds;
        this.paths = paths;
        this.bloomEntities = bloomEntities;
    }
}

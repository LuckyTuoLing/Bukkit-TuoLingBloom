package com.tuoling.tuolingbloom.actor.looker;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;


import java.util.List;
@Getter
public class LookerData {
    private final Player looker;
    private final String preset;
    private final List<String> textureIds;
    private final List<BukkitTask> tasks;


    public LookerData(Player looker, String preset, List<String> textureIds, List<BukkitTask> tasks) {
        this.looker = looker;
        this.preset = preset;
        this.textureIds = textureIds;
        this.tasks = tasks;
    }

}

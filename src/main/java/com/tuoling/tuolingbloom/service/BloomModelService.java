package com.tuoling.tuolingbloom.service;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.config.data.ModelData;
import com.tuoling.tuolingbloom.actor.bloomer.Bloomer;
import com.tuoling.tuolingbloom.actor.bloomer.BloomerData;
import com.tuoling.tuolingbloom.config.holder.ModelConfig;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingbloom.utils.BloomEntityUtil;
import com.tuoling.tuolingbloom.utils.PresetUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BloomModelService {

    private final LivingEntity bloomer;
    private final String preset;
    private final TuoLingBloom plugin;
    private final Map<Integer, ModelData> settings;

    public BloomModelService(LivingEntity bloomer, String preset) {
        this.bloomer = bloomer;
        this.preset = preset;
        this.plugin = TuoLingBloom.inst();
        ModelConfig modelConfig = new PresetUtils(preset).getModelConfig();
        this.settings = (modelConfig != null) ? modelConfig.getSettings() : null;
    }

    public void mmNameToBloom(List<String> mmNames) {
        if (mmNames == null || mmNames.isEmpty()) return;
        if (settings == null || settings.isEmpty()) {
            Message.sendConsole("settings-empty", "%preset%", preset);
            return;
        }
        if (amountError(mmNames)) return;

        List<BukkitTask> tasks = new ArrayList<>();
        List<LivingEntity> activeMobs = new ArrayList<>();

        // duration task 只创建一次，preset 级别
        long duration = new PresetUtils(preset).getDuration();
        if (duration != -1) {
            BukkitTask durationTask = new BukkitRunnable() {
                @Override
                public void run() {
                    Bloomer b = new Bloomer(bloomer);
                    if (b.isBloomPreset(preset)) {
                        b.cancelPreset(preset);
                    }
                }
            }.runTaskLater(plugin, 20L * duration);
            tasks.add(durationTask);
        }

        // per-setting spawn task
        long cumulativeDelay = 0;
        int i = 0;
        for (Map.Entry<Integer, ModelData> entry : settings.entrySet()) {
            if (i >= mmNames.size()) break;
            ModelData data = entry.getValue();
            cumulativeDelay += data.getDelay();
            String mmName = mmNames.get(i);

            final String finalMmName = mmName;
            final ModelData finalData = data;
            final long delayTicks = cumulativeDelay * 20L;

            BukkitTask spawnTask = new BukkitRunnable() {
                @Override
                public void run() {
                    // 双重检查：绽放者可能在 delay 期间已经取消绽放
                    if (!new Bloomer(bloomer).isBloomPreset(preset)) return;

                    LivingEntity entity = BloomEntityUtil.spawn(bloomer, finalMmName, finalData);
                    if (entity != null) {
                        activeMobs.add(entity);
                        if (plugin.getDragonEntityCompatible() != null) {
                            plugin.getDragonEntityCompatible().setEntityModel(entity.getUniqueId());
                        }
                    }
                }
            }.runTaskLater(plugin, delayTicks);
            tasks.add(spawnTask);

            i++;
        }

        addBloomerCache(tasks, activeMobs);
    }

    private void addBloomerCache(List<BukkitTask> tasks, List<LivingEntity> activeMobs) {
        new Bloomer(bloomer)
                .addToCache(preset, new BloomerData(bloomer, preset, tasks, null, null, activeMobs));
    }

    private boolean amountError(List<String> mmNames) {
        if (settings.size() < mmNames.size()) {
            if (bloomer instanceof Player) {
                Message.sendPlayer((Player) bloomer, "config-error-amount",
                        "%actual%", String.valueOf(mmNames.size()),
                        "%require%", String.valueOf(settings.size()));
            }
            return true;
        }
        return false;
    }
}
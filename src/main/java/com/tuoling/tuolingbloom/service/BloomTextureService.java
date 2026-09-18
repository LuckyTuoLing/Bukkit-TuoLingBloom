package com.tuoling.tuolingbloom.service;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.config.data.LayerSetting;
import com.tuoling.tuolingbloom.actor.bloomer.Bloomer;
import com.tuoling.tuolingbloom.actor.looker.Looker;
import com.tuoling.tuolingbloom.actor.bloomer.BloomerData;
import com.tuoling.tuolingbloom.actor.looker.LookerData;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingbloom.utils.PresetUtils;
import eos.moe.dragoncore.api.CoreAPI;
import eos.moe.dragoncore.api.worldtexture.WorldTexture;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BloomTextureService {

    private final LivingEntity bloomer;
    private final List<Player> lookers;
    private final String preset;
    private final Map<Integer, LayerSetting> layers;

    public BloomTextureService(LivingEntity bloomer, String preset) {
        this.bloomer = bloomer;
        this.lookers = new ArrayList<>(Bukkit.getOnlinePlayers());
        this.preset = preset;
        this.layers = new PresetUtils(preset).getTextureLayers();
    }

    public void texPathToBloom(List<String> texturePaths) {
        if (texturePaths == null || texturePaths.isEmpty()) return;
        if (layers == null || layers.isEmpty()) {
            Message.sendConsole("layers-empty", "%preset%", preset);
            return;
        }
        if (amountError(texturePaths)) return;
        if (bloomer instanceof Player) {
            Message.sendPlayer((Player) bloomer, "bloom-success");

        }
        List<BukkitTask> tasks = new ArrayList<>();
        List<String> bloomerTexIds = new ArrayList<>();
        Map<UUID, List<String>> lookerTexIds = new HashMap<>();
        for (Player player : lookers) {
            lookerTexIds.put(player.getUniqueId(), new ArrayList<>());
        }

        // duration task 只创建一次,preset 级别而非 layer 级别
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
            }.runTaskLater(TuoLingBloom.inst(), 20L * duration);
            tasks.add(durationTask);
        }

        // per-layer 渲染 task
        int cumulativeDelay = 0;
        for (int i = 1; i <= texturePaths.size(); i++) {
            LayerSetting layerSetting = layers.get(i);
            if (layerSetting == null) {
                Message.sendConsole("layer-not-exist", "%index%", String.valueOf(i), "%preset%", preset);
                continue;
            }
            cumulativeDelay += layerSetting.getDelay();
            WorldTexture worldTexture = layerSetting.getTexture();
            if (worldTexture == null) {
                Message.sendConsole("layer-texture-null", "%index%", String.valueOf(i), "%preset%", preset);
                continue;
            }
            worldTexture.path = texturePaths.get(i - 1);

            String id = preset + "_" + i + "_" + bloomer.getUniqueId();
            final int delay = cumulativeDelay;
            final String texId = id;
            final WorldTexture texture = worldTexture;

            BukkitTask renderTask = new BukkitRunnable() {
                @Override
                public void run() {
                    texture.entity = bloomer.getUniqueId();

                    // 只有 Player 才给自己发包显示贴图
                    if (bloomer instanceof Player) {
                        CoreAPI.setPlayerWorldTextureItem((Player) bloomer, texId, texture);
                        bloomerTexIds.add(texId);
                    }

                    // 给所有其他在线玩家发包
                    for (Player looker : lookers) {
                        if (bloomer.getUniqueId().equals(looker.getUniqueId())) continue;
                        String lookerId = texId + looker.getUniqueId();
                        CoreAPI.setPlayerWorldTextureItem(looker, lookerId, texture);
                        lookerTexIds.get(looker.getUniqueId()).add(lookerId);
                    }
                }
            }.runTaskLater(TuoLingBloom.inst(), 20L * delay);

            tasks.add(renderTask);
        }

        addLookerCache(lookerTexIds);
        addBloomerCache(tasks, bloomerTexIds, texturePaths);
    }

    private void addLookerCache(Map<UUID, List<String>> lookerTexIds) {
        for (Player looker : lookers) {
            if (bloomer.getUniqueId().equals(looker.getUniqueId())) continue;
            Looker lookerActor = new Looker(looker);
            LookerData lookerData = new LookerData(looker, preset, lookerTexIds.get(looker.getUniqueId()), new ArrayList<>());
            lookerActor.addToCache(bloomer, preset, lookerData);
        }
    }

    private void addBloomerCache(List<BukkitTask> tasks, List<String> bloomerTexIds, List<String> texturePaths) {
        new Bloomer(bloomer)
                .addToCache(preset, new BloomerData(bloomer, preset, tasks, bloomerTexIds, texturePaths, null));
    }

    private boolean amountError(List<String> texturePaths) {
        if (layers.size() < texturePaths.size()) {
            if (bloomer instanceof Player) {
                Message.sendPlayer((Player) bloomer, "config-error-amount",
                        "%actual%", String.valueOf(texturePaths.size()),
                        "%require%", String.valueOf(layers.size()));
            }
            return true;
        }
        return false;
    }
}

package com.tuoling.tuolingbloom.service;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.config.data.LayerSetting;
import com.tuoling.tuolingbloom.actor.looker.Looker;
import com.tuoling.tuolingbloom.actor.bloomer.BloomerData;
import com.tuoling.tuolingbloom.actor.looker.LookerData;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingbloom.utils.PresetUtils;
import eos.moe.dragoncore.api.CoreAPI;
import eos.moe.dragoncore.api.worldtexture.WorldTexture;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReissuePlayerTextureService {
    public static void reissue(Player looker, BloomerData bloomerData) {
        TuoLingBloom plugin = TuoLingBloom.inst();
        String preset = bloomerData.getPreset();
        LivingEntity bloomer = bloomerData.getEntity();
        List<String> paths = bloomerData.getPaths();

        Map<Integer, LayerSetting> layers = new PresetUtils(preset).getTextureLayers();
        if (layers == null || layers.isEmpty()) {
            Message.sendConsole("layers-empty", "%preset%", preset);
            return;
        }

        List<BukkitTask> tasks = new ArrayList<>();
        List<String> textureIds = new ArrayList<>();

        int cumulativeDelay = 0;
        for (int i = 1; i <= paths.size(); i++) {
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
            worldTexture.path = paths.get(i - 1);

            String idNew = preset + "_" + i + "_" + bloomer.getUniqueId() + "_" + looker.getUniqueId();

            final String finalId = idNew;
            final WorldTexture finalWt = worldTexture;
            final LivingEntity finalBloomer = bloomer;

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    finalWt.entity = finalBloomer.getUniqueId();
                    CoreAPI.setPlayerWorldTextureItem(looker, finalId, finalWt);

                }
            }.runTaskLater(plugin, 20L * cumulativeDelay);

            tasks.add(task);
            textureIds.add(idNew);
        }

        Looker lookerActor = new Looker(looker);
        LookerData lookerData = new LookerData(looker, preset, textureIds, tasks);
        lookerActor.addToCache(bloomer, preset, lookerData);
    }
}

package com.tuoling.tuolingbloom.service;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.cache.WorldTextureCache;
import com.tuoling.tuolingbloom.config.data.LayerSetting;
import com.tuoling.tuolingbloom.config.data.WorldPosition;
import com.tuoling.tuolingbloom.config.data.WorldTextureEntry;
import com.tuoling.tuolingbloom.config.holder.WorldTextureConfig;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingbloom.utils.GetTexturePaths;
import com.tuoling.tuolingbloom.utils.PresetUtils;
import eos.moe.dragoncore.api.CoreAPI;
import eos.moe.dragoncore.api.worldtexture.WorldTexture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReissueWorldTextureService {

    public static void reissue(Player looker) {
        TuoLingBloom plugin = TuoLingBloom.inst();
        WorldTextureConfig config = plugin.worldTextureConfig();
        if (config == null) return;

        List<String> allTexIds = new ArrayList<>();

        for (Map.Entry<String, WorldTextureEntry> entry : config.getEntries().entrySet()) {
            String entryName = entry.getKey();
            WorldTextureEntry wtEntry = entry.getValue();
            String preset = wtEntry.getPreset();

            PresetUtils pu = new PresetUtils(preset);
            if (!pu.exists()) {
                Message.sendConsole("world-preset-not-exist", "%entry%", entryName, "%preset%", preset);
                continue;
            }

            List<String> paths = GetTexturePaths.byName(pu.getValueList());
            Map<Integer, LayerSetting> layers = pu.getTextureLayers();

            if (paths == null || paths.isEmpty()) {
                Message.sendConsole("world-paths-empty", "%entry%", entryName);
                continue;
            }
            if (layers == null || layers.isEmpty()) {
                Message.sendConsole("world-layers-empty", "%entry%", entryName);
                continue;
            }
            if (layers.size() < paths.size()) {
                Message.sendConsole("world-amount-error", "%entry%", entryName,
                        "%actual%", String.valueOf(paths.size()),
                        "%view%", pu.getView(),
                        "%require%", String.valueOf(layers.size()));
                continue;
            }

            WorldPosition location = wtEntry.getLocation();
            if (location == null) {
                Message.sendConsole("world-location-missing", "%entry%", entryName);
                continue;
            }

            for (int i = 1; i <= paths.size(); i++) {
                LayerSetting layerSetting = layers.get(i);
                if (layerSetting == null) {
                    Message.sendConsole("world-layer-not-exist", "%entry%", entryName, "%index%", String.valueOf(i));
                    continue;
                }
                WorldTexture worldTexture = layerSetting.getTexture();
                if (worldTexture == null) {
                    Message.sendConsole("world-layer-texture-null", "%entry%", entryName, "%index%", String.valueOf(i));
                    continue;
                }
                worldTexture.path = paths.get(i - 1);

                worldTexture.world = wtEntry.getWorld();
                worldTexture.translateX = location.getX();
                worldTexture.translateY = location.getY();
                worldTexture.translateZ = location.getZ();

                String id = entryName + "_" + i + "_" + looker.getUniqueId();
                CoreAPI.setPlayerWorldTextureItem(looker, id, worldTexture);
                allTexIds.add(id);
            }
        }

        WorldTextureCache cache = plugin.getCacheManager().getWorldTextureCache();
        if (cache != null) {
            cache.put(looker.getUniqueId(), allTexIds);
        }
    }

    public static void clear(Player looker) {
        WorldTextureCache cache = TuoLingBloom.inst().getCacheManager().getWorldTextureCache();
        if (cache == null) return;
        List<String> texIds = cache.remove(looker.getUniqueId());
        if (texIds == null) return;
        for (String id : texIds) {
            CoreAPI.removePlayerWorldTexture(looker, id);
        }
    }

    public static void clearAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            clear(p);
        }
    }
}

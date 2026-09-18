package com.tuoling.tuolingbloom.actor.looker;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.cache.LookerCache;
import com.tuoling.tuolingbloom.actor.bloomer.BloomerData;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.tuoling.tuolingbloom.service.ReissuePlayerTextureService;
import com.tuoling.tuolingbloom.utils.PresetUtils;
import eos.moe.dragoncore.api.CoreAPI;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
public class Looker {

    private final Player looker;
    private final Table<UUID,String,LookerData> lookerCaches;

    public Looker(Player looker) {
        this.looker = looker;
        TuoLingBloom plugin = TuoLingBloom.inst();
        LookerCache lookerCache = plugin.getCacheManager().getLookerCache();

        // 缓存可能不存在(如 /reload 后在线玩家未触发 initCache),自动创建空表并放入缓存
        Table<UUID, String, LookerData> table = lookerCache.getLookerCache().get(looker.getUniqueId());
        if (table == null) {
            table = HashBasedTable.create();
            lookerCache.getLookerCache().put(looker.getUniqueId(), table);
        }
        this.lookerCaches = table;
    }

    public boolean isLooker() {
        return !lookerCaches.isEmpty();
    }
    public boolean isLookEntityPreset(LivingEntity entity,String preset) {
        return lookerCaches.contains(entity.getUniqueId(), preset);
    }

    public void reissueBloom(BloomerData bloomData) {
        // 模型路线暂不支持补发(ReissueTexture 仅处理贴图)
        if ("model".equals(new PresetUtils(bloomData.getPreset()).getBloomType())){
            return;
        }
        //已经存在无需重新补发
        if (isLookEntityPreset(bloomData.getEntity(), bloomData.getPreset())) {
            return;
        }
        ReissuePlayerTextureService.reissue(looker, bloomData);
    }

    public void addToCache(LivingEntity bloomer,String preset,LookerData lookerData) {
        lookerCaches.put(bloomer.getUniqueId() ,preset, lookerData);
    }

    public void removePreset(UUID bloomerUUID, String preset) {
        LookerData lookerData = lookerCaches.get(bloomerUUID, preset);
        if (lookerData == null) return;
        if (!lookerData.getTasks().isEmpty()) {
            for (BukkitTask task : lookerData.getTasks()) {
                task.cancel();
            }
        }
        for (String ids : lookerData.getTextureIds()) {
            CoreAPI.removePlayerWorldTexture(looker, ids);
        }
        lookerCaches.remove(bloomerUUID, preset);
    }
    public void removeAll() {
        // 收集到列表再遍历,避免遍历 cellSet 时 removePreset 修改底层表导致 CME
        List<Table.Cell<UUID, String, LookerData>> cells = new ArrayList<>(lookerCaches.cellSet());
        for (Table.Cell<UUID, String, LookerData> cell : cells) {
            removePreset(cell.getRowKey(), cell.getColumnKey());
        }
        lookerCaches.clear();
    }

}

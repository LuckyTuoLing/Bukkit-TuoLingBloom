package com.tuoling.tuolingbloom.actor.bloomer;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.cache.CooldownCache;
import com.tuoling.tuolingbloom.config.holder.DefaultConfig;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingbloom.actor.looker.Looker;
import eos.moe.dragoncore.api.CoreAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Bloomer {

    @Getter
    private final LivingEntity bloomer;
    private final Map<String, BloomerData> textureData;

    public Bloomer(LivingEntity bloomer) {
        this.bloomer = bloomer;
        this.textureData = TuoLingBloom.inst().getCacheManager().getBloomerCache().row(bloomer.getUniqueId());
    }

    public Set<String> getPresets() {
        return new HashSet<>(textureData.keySet());
    }

    public List<BloomerData> getAllBloomData() {
        return new ArrayList<>(textureData.values());
    }

    public BloomerData getBloomData(String preset) {
        if (textureData.containsKey(preset)) {
            return textureData.get(preset);
        } else {
            Message.sendBloomer(bloomer, "not-bloomed", "%preset%", preset);
            return null;
        }
    }

    public void bloom(String preset) {
        if (isBloomPreset(preset)) {
            cancelPreset(preset);
            return;
        }
        if (isConflict(preset)) {
            return;
        }
        if (isCooldown(preset)) {
            return;
        }
        TuoLingBloom.inst().getBloomManager().bloom(bloomer, preset);
    }

    public boolean isBloomPreset(String preset) {
        return textureData.containsKey(preset);
    }

    public boolean isBloomer() {
        return !textureData.isEmpty();
    }

    public void cancelPreset(String preset) {
        BloomerData data = textureData.get(preset);
        if (data == null) return;

        for (BukkitTask bukkitTask : data.getTasks()) {
            bukkitTask.cancel();
        }

        // 只有 Player 才给自己移除贴图
        if (bloomer instanceof Player) {
            List<String> textureIds = data.getTextureIds();
            if (textureIds != null) {
                for (String ids : textureIds) {
                    CoreAPI.removePlayerWorldTexture((Player) bloomer, ids);
                }
            }
        }

        // 清理所有其他在线玩家的 looker 缓存
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(bloomer.getUniqueId())) continue;
            Looker looker = new Looker(p);
            looker.removePreset(bloomer.getUniqueId(), preset);
        }

        // 移除实体
        List<LivingEntity> entities = data.getBloomEntities();
        if (entities != null) {
            for (LivingEntity entity : entities) {
                if (entity == null) continue;
                if (entity.isDead() || !entity.isValid()) continue;
                entity.remove();
            }
            entities.clear();
        }

        textureData.remove(preset);
    }

    public void cancelAllPreset() {
        if (isBloomer()) {
            for (BloomerData bloomerData : getAllBloomData()) {
                cancelPreset(bloomerData.getPreset());
            }
        }
    }

    // ==================== 检查方法 ====================

    public boolean isConflict(String preset) {
        TuoLingBloom plugin = TuoLingBloom.inst();
        DefaultConfig defaultConfig = plugin.defaultConfig();
        if (defaultConfig == null) return false;

        List<String> bloomGroups = new ArrayList<>();
        bloomGroups.add(preset);

        for (BloomerData bloomerData : textureData.values()) {
            bloomGroups.add(bloomerData.getPreset());
        }

        if (defaultConfig.isInBloomGroup(bloomGroups)) {
            bloomGroups.remove(preset);
            Message.sendBloomer(bloomer, "conflict", "%current%", String.join(",", bloomGroups), "%preset%", preset);
            return true;
        }
        return false;
    }

    public boolean isCooldown(String preset) {
        // mob 不走冷却
        if (!(bloomer instanceof Player)) return false;
        Player player = (Player) bloomer;

        TuoLingBloom plugin = TuoLingBloom.inst();
        CooldownCache cooldownCache = plugin.getCacheManager().getCooldownCache();

        if (cooldownCache.isCooldown(player, preset)) {
            int remaining = cooldownCache.getCooldown(player, preset);
            Message.sendBloomer(bloomer, "cooldown", "%cooldown%", String.valueOf(remaining));
            return true;
        } else {
            cooldownCache.removeCooldown(player, preset);
            cooldownCache.addNow(player, preset);
        }
        return false;
    }
    public int getCooldown(String preset) {
        if (!(bloomer instanceof Player)){
            return 0;
        }
        return TuoLingBloom.inst().getCacheManager().getCooldownCache().getCooldown((Player) bloomer, preset);
    }

    public void addToCache(String preset, BloomerData bloomerData) {
        textureData.put(preset, bloomerData);
    }
}

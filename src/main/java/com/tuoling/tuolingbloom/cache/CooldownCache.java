package com.tuoling.tuolingbloom.cache;

import com.google.common.collect.ForwardingTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.config.holder.DefaultConfig;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CooldownCache extends ForwardingTable<UUID, String, Long> {

    private final Table<UUID, String, Long> cooldownCache = HashBasedTable.create();

    @Override
    protected Table<UUID, String, Long> delegate() {
        return cooldownCache;
    }

    // ================== 业务方法 ==================

    /**
     * 记录玩家的某个技能/动作的冷却开始时间
     */
    public void addNow(Player player, String preset) {
        UUID uuid = player.getUniqueId();
        // Table 会自动处理 null 问题，不需要手动判空和创建 Map
        cooldownCache.put(uuid, preset, System.currentTimeMillis());
    }

    /**
     * 判断玩家是否在冷却中
     */
    public boolean isCooldown(Player player, String preset) {
        return getCooldown(player, preset) > 0;
    }

    /**
     * 获取剩余冷却秒数(0 表示无冷却/已结束)
     */
    public int getCooldown(Player player, String preset) {
        UUID uuid = player.getUniqueId();
        DefaultConfig defaultConfig = TuoLingBloom.inst().defaultConfig();
        if (defaultConfig == null) return 0;

        // OP 免冷却
        if (player.isOp() && defaultConfig.isOpNotCooldown()) {
            return 0;
        }

        Long startTime = cooldownCache.get(uuid, preset);
        if (startTime == null) return 0;

        long totalSeconds = defaultConfig.getCooldown(preset);
        long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
        long remaining = totalSeconds - elapsedSeconds;

        return remaining > 0 ? (int) remaining : 0;
    }

    /**
     * 移除玩家的某个技能冷却
     */
    public void removeCooldown(Player player, String preset) {
        UUID uuid = player.getUniqueId();
        cooldownCache.remove(uuid, preset);
    }

    /**
     * 清空某个玩家的所有冷却
     */
    public void clearPlayerCooldowns(Player player) {
        UUID uuid = player.getUniqueId();
        // row() 返回一个 Map<String, Long>，clear() 会清空该行所有列
        if (cooldownCache.containsRow(uuid)) {
            cooldownCache.row(uuid).clear();
        }
    }
}
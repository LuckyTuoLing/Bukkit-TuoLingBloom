package com.tuoling.tuolingbloom.utils;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.actor.bloomer.Bloomer;
import com.tuoling.tuolingbloom.actor.bloomer.BloomerData;
import com.tuoling.tuolingbloom.actor.looker.Looker;
import com.tuoling.tuolingbloom.config.data.ModelData;
import com.tuoling.tuolingbloom.utils.Message;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * 绽放实体工具类：
 * 1. spawn MythicMob
 * 2. 关闭 AI / 关闭重力，让实体悬停
 * 3. 启动循环 task，每 tick 按 resolveLocation 更新实体位置（跟随绽放者移动）
 * 跟随 task 的生命周期：实体死亡/绽放者下线时自动 cancel
 */
public class BloomEntityUtil {

    public static LivingEntity spawn(LivingEntity source, String mmName, ModelData data) {
        Location loc = data.resolveLocation(source);
        if (loc == null) return null;

        Entity entity;
        try {
            entity = MythicMobs.inst().getAPIHelper().spawnMythicMob(mmName, loc);
        } catch (InvalidMobTypeException e) {
            Message.sendConsole("mm-spawn-failed", "%name%", mmName);
            return null;
        }
        if (!(entity instanceof LivingEntity)) return null;

        LivingEntity living = (LivingEntity) entity;
        living.setAI(false);
        living.setGravity(false);
        living.setCollidable(false);
        living.setInvulnerable(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (living.isDead() || !living.isValid()) {
                    cancel();
                    return;
                }
                if (!source.isValid()) {
                    living.remove();
                    cancel();
                    return;
                }
                Location target = data.resolveLocation(source);
                if (target == null) return;
                Location cur = living.getLocation();
                Location sourceLoc = source.getLocation();

                // 位置：每 tick 线性插值，系数 0.2
                double nextX = cur.getX() + (target.getX() - cur.getX()) * 0.2;
                double nextY = cur.getY() + (target.getY() - cur.getY()) * 0.2;
                double nextZ = cur.getZ() + (target.getZ() - cur.getZ()) * 0.2;

                // 朝向：计算从实体指向绽放者的方向向量
                Vector dir = new Vector(
                        sourceLoc.getX() - nextX,
                        sourceLoc.getY() + 1.0 - nextY,
                        sourceLoc.getZ() - nextZ
                );
                Location look = new Location(target.getWorld(), nextX, nextY, nextZ);
                look.setDirection(dir);

                living.teleport(look);
            }
        }.runTaskTimer(TuoLingBloom.inst(), 1L, 1L);

        return living;
    }

    /**
     * 遍历所有活跃的 MythicMob,对已绽放的 mob 向进服玩家补发贴图。
     * MythicMobs API 依赖隔离在此方法内,未安装时不会被调用。
     */
    public static void reissueMobs(Player looker) {
        for (ActiveMob mob : MythicMobs.inst().getMobManager().getActiveMobs()) {
            if (mob == null || mob.getEntity() == null) continue;
            Entity mobEntity = mob.getEntity().getBukkitEntity();
            if (mobEntity instanceof LivingEntity) {
                Bloomer bloomer = new Bloomer((LivingEntity) mobEntity);
                if (bloomer.isBloomer()) {
                    Looker lookerActor = new Looker(looker);
                    for (BloomerData bloomerData : bloomer.getAllBloomData()) {
                        lookerActor.reissueBloom(bloomerData);
                    }
                }
            }
        }
    }

    /**
     * 插件卸载时清理所有已绽放的 MythicMob 实体。
     * MythicMobs API 依赖隔离在此方法内,未安装时不会被调用。
     */
    public static void cancelAllMobBloom() {
        for (ActiveMob mob : MythicMobs.inst().getMobManager().getActiveMobs()) {
            if (mob == null || mob.getEntity() == null) continue;
            Entity mobEntity = mob.getEntity().getBukkitEntity();
            if (mobEntity instanceof LivingEntity) {
                Bloomer bloomer = new Bloomer((LivingEntity) mobEntity);
                if (bloomer.isBloomer()) {
                    bloomer.cancelAllPreset();
                }
            }
        }
    }
}

package com.tuoling.tuolingbloom.listener;

import com.tuoling.tuolingbloom.actor.bloomer.Bloomer;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class MobSpawn implements Listener {
    @EventHandler
    public void onMobSpawn(MythicMobSpawnEvent e) {
        if (e.getMobType() == null || e.getMobType().getConfig() == null) return;
        List<String> mobPresets = e.getMobType().getConfig().getStringList("TuoLingBloom");
        if (mobPresets.isEmpty()) return;
        if (e.getMob() == null || e.getMob().getEntity() == null) return;
        Entity entity = e.getMob().getEntity().getBukkitEntity();
        if (entity instanceof LivingEntity) {
            Bloomer bloomer = new Bloomer((LivingEntity) entity);
            for (String mobPreset : mobPresets) {
                bloomer.bloom(mobPreset);
            }
        }
    }
}

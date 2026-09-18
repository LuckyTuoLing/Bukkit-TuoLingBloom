package com.tuoling.tuolingbloom.listener;

import com.tuoling.tuolingbloom.actor.bloomer.Bloomer;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MobDeath implements Listener {
    @EventHandler
    public void onMobDeath(MythicMobDeathEvent e) {
        if (e.getMob() == null || e.getMob().getEntity() == null) return;
        Entity entity = e.getMob().getEntity().getBukkitEntity();
        if (entity instanceof LivingEntity) {
            Bloomer bloomer = new Bloomer((LivingEntity) entity);
            if (bloomer.isBloomer()) {
                bloomer.cancelAllPreset();
            }
        }
    }
}

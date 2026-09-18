package com.tuoling.tuolingbloom.compatible;

import com.tuoling.tuolingbloom.TuoLingBloom;
import eos.moe.dragoncore.api.event.ConfigLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DragonEntityListener implements Listener {

    @EventHandler
    public void onDragonConfigLoad(ConfigLoadEvent event) {
        if (event.getFileName().startsWith("EntityModel")){
            DragonEntityCompatible compat = TuoLingBloom.inst().getDragonEntityCompatible();
            if (compat != null) {
                compat.upData();
            }
        }
    }
}

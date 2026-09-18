package com.tuoling.tuolingbloom.compatible;


import eos.moe.dragoncore.api.ModelAPI;
import eos.moe.dragoncore.config.Config;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class DragonEntityCompatible {
    private final List<DragonEntityData> data = new ArrayList<>();

    public DragonEntityCompatible() {
        upData();
    }

    public void upData(){
        data.clear();
        for(Map.Entry<String, YamlConfiguration> entry : Config.fileMap.entrySet()) {
            if (entry.getKey().startsWith("EntityModel")) {
                for (String key: entry.getValue().getKeys(false)){
                    ConfigurationSection section = entry.getValue().getConfigurationSection(key);
                    if (section == null) continue;
                    String entityName = section.getString("entity");
                    data.add(new DragonEntityData(entityName, key));
                }
            }
        }
    }

    public void setEntityModel(UUID uuid){
        MobManager mobManager = MythicMobs.inst().getMobManager();
        if (!mobManager.isActiveMob(uuid)) {
            return;
        }
        Optional<ActiveMob> mob = mobManager.getActiveMob(uuid);
        if (!mob.isPresent()) {
            return;
        }
        ActiveMob activeMob = mob.get();

        for (DragonEntityData entity : data) {
            String entityName = entity.getEntityName();
            if (entityName != null && activeMob.getDisplayName() != null && activeMob.getDisplayName().contains(entityName)) {
                ModelAPI.setEntityModel(uuid, entity.getIndex());
                return;
            }
        }
    }

}

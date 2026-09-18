package com.tuoling.tuolingbloom.compatible;

import lombok.Getter;

@Getter
public class DragonEntityData {
    private final String entityName;
    private final String index;

    public DragonEntityData(String entityName, String index) {
        this.entityName = entityName;
        this.index = index;
    }
}


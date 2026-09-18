package com.tuoling.tuolingbloom.config.data;

import lombok.Getter;

public class BloomGroup {

    @Getter private final String groupName;
    @Getter private final long cooldown;

    public BloomGroup(String groupName, long cooldown) {
        this.groupName = groupName;
        this.cooldown = cooldown;
    }
}

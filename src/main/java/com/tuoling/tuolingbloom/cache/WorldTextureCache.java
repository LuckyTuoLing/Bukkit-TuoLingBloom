package com.tuoling.tuolingbloom.cache;

import com.google.common.collect.ForwardingMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldTextureCache extends ForwardingMap<UUID, List<String>> {

    private final Map<UUID, List<String>> uuidToTexIds = new HashMap<>();
    @Override
    protected Map<UUID, List<String>> delegate() {
        return uuidToTexIds;
    }
}

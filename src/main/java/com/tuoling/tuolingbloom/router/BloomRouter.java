package com.tuoling.tuolingbloom.router;

import com.tuoling.tuolingbloom.utils.Message;

import java.util.HashMap;
import java.util.Map;

public class BloomRouter{
    private final Map<String,String> configToBloomNameMap = new HashMap<>();

    public void addRouter(String configKey, String bloomKey) {
        configToBloomNameMap.put(configKey, bloomKey);
    }

    public String router(String configKey) {
        if (!configToBloomNameMap.containsKey(configKey)) {
            Message.sendConsole("config-name-not-exist", "%name%", configKey);
            return null;
        }
        return configToBloomNameMap.get(configKey);
    }
    public void clear() {
        configToBloomNameMap.clear();
    }


}

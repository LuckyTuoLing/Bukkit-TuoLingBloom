package com.tuoling.tuolingbloom.cache;

import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingcore.framework.manager.InstanceManager;

public class CacheManager extends InstanceManager<Object> {
    public CacheManager() {
        register(BloomerCache.class, new BloomerCache());
        register(CooldownCache.class, new CooldownCache());
        register(LookerCache.class, new LookerCache());
        register(WorldTextureCache.class, new WorldTextureCache());
    }
    // ==================== 获取缓存（仅在空值时发送控制台错误） ====================

    public BloomerCache getBloomerCache() {
        if (containsKey(BloomerCache.class)) {
            return get(BloomerCache.class);
        }
        Message.sendConsole("cache-not-registered", "%name%", "BloomerCache");
        return null;
    }

    public CooldownCache getCooldownCache() {
        if (containsKey(CooldownCache.class)) {
            return get(CooldownCache.class);
        }
        Message.sendConsole("cache-not-registered", "%name%", "CooldownCache");
        return null;
    }

    public LookerCache getLookerCache() {
        if (containsKey(LookerCache.class)) {
            return get(LookerCache.class);
        }
        Message.sendConsole("cache-not-registered", "%name%", "LookerCache");
        return null;
    }
    public WorldTextureCache getWorldTextureCache() {
        if (containsKey(WorldTextureCache.class)) {
            return get(WorldTextureCache.class);
        }
        Message.sendConsole("cache-not-registered", "%name%", "WorldTextureCache");
        return null;
    }
}
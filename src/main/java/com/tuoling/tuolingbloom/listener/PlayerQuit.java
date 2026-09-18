package com.tuoling.tuolingbloom.listener;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.actor.bloomer.Bloomer;
import com.tuoling.tuolingbloom.actor.looker.Looker;
import com.tuoling.tuolingbloom.cache.BloomerCache;
import com.tuoling.tuolingbloom.cache.CacheManager;
import com.tuoling.tuolingbloom.cache.CooldownCache;
import com.tuoling.tuolingbloom.cache.LookerCache;

import com.tuoling.tuolingbloom.service.ReissueWorldTextureService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Bloomer bloomer = new Bloomer(player);

        if (bloomer.isBloomer()){
            bloomer.cancelAllPreset();
        }
        Looker looker = new Looker(player);
        looker.removeAll();

        ReissueWorldTextureService.clear(player);

        // 清理缓存行,防止内存泄漏
        TuoLingBloom plugin = TuoLingBloom.inst();
        CacheManager cacheManager = plugin.getCacheManager();
        cacheManager.getBloomerCache().rowMap().remove(player.getUniqueId());
        cacheManager.getLookerCache().getLookerCache().remove(player.getUniqueId());
        cacheManager.getCooldownCache().clearPlayerCooldowns(player);
    }

}

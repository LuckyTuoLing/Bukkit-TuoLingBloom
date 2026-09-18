package com.tuoling.tuolingbloom.listener;

import com.google.common.collect.HashBasedTable;
import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.cache.LookerCache;
import com.tuoling.tuolingbloom.actor.bloomer.Bloomer;
import com.tuoling.tuolingbloom.actor.looker.Looker;
import com.tuoling.tuolingbloom.actor.bloomer.BloomerData;
import com.tuoling.tuolingbloom.cache.WorldTextureCache;
import com.tuoling.tuolingbloom.service.ReissueWorldTextureService;
import com.tuoling.tuolingbloom.utils.BloomEntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;


public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player looker = event.getPlayer();

        // 延迟补发:玩家刚进服时客户端尚未完成实体加载,立即发包会导致 DragonCore 找不到目标实体
        new BukkitRunnable() {
            @Override
            public void run() {
                // 玩家可能在 40 tick 内已退出
                if (!looker.isOnline()) return;
                reissueForPlayers(looker);
                // MythicMobs 补发逻辑隔离到 BloomEntityUtil,未安装时不调用
                if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
                    BloomEntityUtil.reissueMobs(looker);
                }
                reissueWorldTexture(looker);
            }
        }.runTaskLater(TuoLingBloom.inst(), 40L);
    }

    private void reissueForPlayers(Player looker) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getUniqueId().equals(looker.getUniqueId())) {
                continue;
            }
            Bloomer bloomer = new Bloomer(onlinePlayer);
            if (bloomer.isBloomer()){
                Looker lookerActor = new Looker(looker);
                for (BloomerData bloomerData: bloomer.getAllBloomData()){
                    lookerActor.reissueBloom(bloomerData);
                }
            }
        }
    }

    private void reissueWorldTexture(Player looker){
        ReissueWorldTextureService.reissue(looker);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void initCache(PlayerJoinEvent event) {
        TuoLingBloom plugin = TuoLingBloom.inst();
        LookerCache lookerCache = plugin.getCacheManager().getLookerCache();
        lookerCache.put(event.getPlayer().getUniqueId(), HashBasedTable.create());

        WorldTextureCache worldTextureCache = plugin.getCacheManager().getWorldTextureCache();

        worldTextureCache.put(event.getPlayer().getUniqueId(),new ArrayList<>());
    }
}

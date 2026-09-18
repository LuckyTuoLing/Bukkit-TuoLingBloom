package com.tuoling.tuolingbloom.command.maincommand;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.actor.bloomer.Bloomer;
import com.tuoling.tuolingbloom.cache.BloomerCache;
import com.tuoling.tuolingbloom.service.ReissueWorldTextureService;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingcore.framework.command.CommandHandler;
import com.tuoling.tuolingcore.utils.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ReloadCommand implements CommandHandler {

    @Override
    public void execute(Command command, CommandSender sender, String[] args) {
        if (!sender.isOp()) {
            Message.sendSender(sender, "no-permission");
            return;
        }
        TuoLingBloom plugin = TuoLingBloom.inst();
        plugin.getConfigHolderManager().reloadAll();
        reloadBloom();
        Message.sendSender(sender, "reload-success");
    }

    /**
     * reload 后用新配置重新绽放所有正在绽放的实体（玩家+怪物）和世界贴图。
     * 两阶段：先记录所有 BloomerData 的 preset → cancelAllPreset 清理 → bloom 重新绽放。
     */
    private void reloadBloom() {
        TuoLingBloom plugin = TuoLingBloom.inst();
        BloomerCache cache = plugin.getCacheManager().getBloomerCache();
        if (cache != null) {
            // 快照 rowKeySet，清理后表会变空
            List<UUID> bloomerIds = new ArrayList<>(cache.rowKeySet());

            // 阶段1：记录 preset 列表 + cancelAllPreset 清理
            List<LivingEntity> rebloomEntities = new ArrayList<>();
            List<List<String>> rebloomPresetsList = new ArrayList<>();
            for (UUID bloomerId : bloomerIds) {
                Entity entity = Bukkit.getEntity(bloomerId);
                if (!(entity instanceof LivingEntity) || entity.isDead()) {
                    cache.row(bloomerId).clear();
                    continue;
                }
                LivingEntity living = (LivingEntity) entity;
                // 记录该实体当前所有 preset
                List<String> presets = new ArrayList<>(cache.row(bloomerId).keySet());
                new Bloomer(living).cancelAllPreset();
                rebloomEntities.add(living);
                rebloomPresetsList.add(presets);
            }

            // 阶段2：用新配置重新绽放
            for (int i = 0; i < rebloomEntities.size(); i++) {
                LivingEntity living = rebloomEntities.get(i);
                for (String preset : rebloomPresetsList.get(i)) {
                    plugin.getBloomManager().bloom(living, preset);
                }
            }
        }

        // 世界贴图：清理后用新配置重新渲染所有在线玩家
        ReissueWorldTextureService.clearAll();
        for (Player p : Bukkit.getOnlinePlayers()) {
            ReissueWorldTextureService.reissue(p);
        }
    }

    @Override
    public boolean isMatch(Command command, CommandSender sender, String[] args) {
        return new CommandUtil(command,sender,args).defaultIsMatch("reload","bloom","tuolingbloom");
    }

}

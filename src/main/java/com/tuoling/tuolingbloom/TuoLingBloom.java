package com.tuoling.tuolingbloom;

import com.tuoling.tuolingbloom.actor.bloomer.Bloomer;
import com.tuoling.tuolingbloom.actor.looker.Looker;
import com.tuoling.tuolingbloom.bloom.BloomManager;
import com.tuoling.tuolingbloom.cache.CacheManager;
import com.tuoling.tuolingbloom.command.CommandManager;
import com.tuoling.tuolingbloom.command.CommandTrigger;
import com.tuoling.tuolingbloom.compatible.DragonEntityCompatible;
import com.tuoling.tuolingbloom.compatible.DragonEntityListener;
import com.tuoling.tuolingbloom.config.ConfigHolderManager;
import com.tuoling.tuolingbloom.config.holder.DefaultConfig;
import com.tuoling.tuolingbloom.config.holder.WorldTextureConfig;
import com.tuoling.tuolingbloom.config.holder.ModelConfig;
import com.tuoling.tuolingbloom.config.holder.TextureConfig;
import com.tuoling.tuolingbloom.utils.BloomEntityUtil;
import com.tuoling.tuolingbloom.utils.EnableMessageUtil;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingbloom.listener.MobDeath;
import com.tuoling.tuolingbloom.listener.MobSpawn;
import com.tuoling.tuolingbloom.listener.PlayerJoin;

import com.tuoling.tuolingbloom.listener.PlayerQuit;
import com.tuoling.tuolingbloom.papi.BloomPlaceholderExpansion;
import com.tuoling.tuolingbloom.preset.BloomStrategyManager;
import com.tuoling.tuolingbloom.router.BloomRouter;
import com.tuoling.tuolingbloom.service.ReissueWorldTextureService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class TuoLingBloom extends JavaPlugin {
    private static TuoLingBloom inst;

    public static TuoLingBloom inst() {
        return inst;
    }

    private CommandManager commandManager;
    private ConfigHolderManager configHolderManager;
    private BloomStrategyManager bloomStrategyManager;
    private CacheManager cacheManager;
    private BloomManager bloomManager;
    private DragonEntityCompatible dragonEntityCompatible;
    private BloomRouter bloomRouter;

    @Override
    public void onEnable() {
        inst = this;
        bloomRouter = new BloomRouter();
        registerManager();
        registerListeners();
        registerCommands();
        registerPlaceholder();
        // MythicMobs 软依赖:未安装时不创建 DragonEntityCompatible
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            dragonEntityCompatible = new DragonEntityCompatible();
        }
        EnableMessageUtil.print(configHolderManager);
    }

    private void registerCommands() {
        CommandTrigger trigger = new CommandTrigger();
        Bukkit.getPluginCommand("bloom").setExecutor(trigger);
        Bukkit.getPluginCommand("tuolingbloom").setExecutor(trigger);
        Bukkit.getPluginCommand("bloom").setTabCompleter(trigger);
        Bukkit.getPluginCommand("tuolingbloom").setTabCompleter(trigger);
    }

    private void registerPlaceholder() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new BloomPlaceholderExpansion(this).register();
        }
    }

    @Override
    public void onDisable() {
        // 清理所有已绽放的 MythicMob 实体
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            BloomEntityUtil.cancelAllMobBloom();
        }
        // 清理所有在线玩家的绽放状态、looker 缓存和世界贴图
        for (Player player : Bukkit.getOnlinePlayers()) {
            Bloomer bloomer = new Bloomer(player);
            if (bloomer.isBloomer()) {
                bloomer.cancelAllPreset();
            }
            Looker looker = new Looker(player);
            if (looker.isLooker()) {
                looker.removeAll();
            }
            ReissueWorldTextureService.clear(player);
        }
    }
    private void  registerManager(){
        configHolderManager = new ConfigHolderManager();
        commandManager = new CommandManager();
        bloomStrategyManager = new BloomStrategyManager();
        cacheManager = new CacheManager();
        bloomManager = new BloomManager();


    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new DragonEntityListener(), this);
        // MythicMobs 软依赖:未安装时不注册相关监听器,避免 NoClassDefFoundError
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            Bukkit.getPluginManager().registerEvents(new MobSpawn(), this);
            Bukkit.getPluginManager().registerEvents(new MobDeath(), this);
        }
    }

    public DefaultConfig defaultConfig(){
        return configHolderManager.defaultConfig();
    }
    public WorldTextureConfig worldTextureConfig(){
        return configHolderManager.worldTextureConfig();
    }
    public TextureConfig textureConfig(String fileName){
        return configHolderManager.textureConfig(fileName);
    }
    public ModelConfig modelConfig(String fileName){
        return configHolderManager.modelConfig(fileName);
    }

}

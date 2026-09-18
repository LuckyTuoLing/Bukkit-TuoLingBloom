package com.tuoling.tuolingbloom.utils;

import com.tuoling.tuolingbloom.config.ConfigHolderManager;
import com.tuoling.tuolingbloom.config.holder.DefaultConfig;
import com.tuoling.tuolingbloom.config.holder.ModelConfig;
import com.tuoling.tuolingbloom.config.holder.TextureConfig;
import com.tuoling.tuolingbloom.config.holder.WorldTextureConfig;
import com.tuoling.tuolingcore.framework.config.ConfigHolder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制台启动信息打印工具。
 * 把原 TuoLingBloom#printEnableMsg() 的启动刷屏逻辑迁移到这里，
 * 主类只负责组件组装与生命周期，不再关心控制台展示细节。
 *
 * 用法:
 *   EnableMessageUtil.print(TuoLingBloom.inst().getConfigHolderManager());
 */
public final class EnableMessageUtil {

    private static final String LINE = "§f==================§6[§b拓灵绽放§6]§f==================";
    private static final String AUTHOR = "LuckyNine(QQ 2718479709)";

    private EnableMessageUtil() {
    }

    /**
     * 打印插件启用汇总信息(依赖状态 + 配置统计)。
     */
    public static void print(ConfigHolderManager configHolderManager) {
        send(LINE);
        send("§6插件已经成功加载");
        send("§6插件作者: " + AUTHOR);
        send("§d重要说明: 此插件§a永久免费开源");
        send("§6开源地址: Minebbs,Mcbbs,GitHub");
        printDependencies();
        printConfigStats(configHolderManager);
        send(LINE);
    }

    private static void printDependencies() {
        printDependency("DragonCore", "硬依赖", true);
        printDependency("TuoLingCore", "硬依赖", true);
        printDependency("MythicMobs", "软依赖", false);
        printDependency("PlaceholderAPI", "软依赖", false);
    }

    private static void printDependency(String name, String type, boolean hard) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        if (plugin != null) {
            send("§f" + type + ": §e" + name + " §dv" + plugin.getDescription().getVersion() + "§f(§a加载成功§f)");
        } else {
            send("§f" + type + ": §e" + name + "§f(§c" + (hard ? "加载失败" : "未安装") + "§f)");
        }
    }

    private static void printConfigStats(ConfigHolderManager configHolderManager) {
        List<String> textureNames = new ArrayList<>();
        List<String> modelNames = new ArrayList<>();
        List<String> worldTextureNames = new ArrayList<>();
        List<String> presetNames = new ArrayList<>();

        for (ConfigHolder holder : configHolderManager.values()) {
            if (holder instanceof TextureConfig) {
                textureNames.add(holder.getConfigName().replace(".yml", ""));
            } else if (holder instanceof ModelConfig) {
                modelNames.add(holder.getConfigName().replace(".yml", ""));
            } else if (holder instanceof WorldTextureConfig) {
                worldTextureNames.addAll(((WorldTextureConfig) holder).getEntries().keySet());
            }
        }
        DefaultConfig defaultConfig = configHolderManager.defaultConfig();
        if (defaultConfig != null) {
            presetNames.addAll(defaultConfig.getBloomPresets().keySet());
        }

        printNameList("§f成功加载Texture动画§7(" + textureNames.size() + ")§f:", textureNames);
        printNameList("§f成功加载Model§7(" + modelNames.size() + ")§f:", modelNames);
        printNameList("§f成功加载世界贴图§7(" + worldTextureNames.size() + ")§f:", worldTextureNames);
        printNameList("§f成功加载预设§7(" + presetNames.size() + ")§f:", presetNames);
    }

    private static void printNameList(String title, List<String> names) {
        send(title);
        for (String name : names) {
            send("  §f- §e" + name);
        }
    }

    private static void send(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }
}

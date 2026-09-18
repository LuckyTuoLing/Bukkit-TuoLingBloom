package com.tuoling.tuolingbloom.utils;

import com.tuoling.tuolingcore.utils.NormalUtil;
import org.bukkit.Bukkit;

/**
 * 日志工具,仅用于向控制台输出日志信息。
 * 玩家消息走 Message 类,BloomLogger 只负责后台日志。
 */
public final class BloomLogger {

    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage("§f[§bTuoLingBloom§f] §f" + NormalUtil.colorString(message));
    }

    public static void warn(String message) {
        Bukkit.getConsoleSender().sendMessage("§f[§bTuoLingBloom§f] §e" + NormalUtil.colorString(message));
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage("§f[§bTuoLingBloom§f] §c" + NormalUtil.colorString(message));
    }
}
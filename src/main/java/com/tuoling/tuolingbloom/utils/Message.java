package com.tuoling.tuolingbloom.utils;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.config.holder.MessageConfig;
import com.tuoling.tuolingcore.utils.NormalUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * 消息工具类，从 MessageConfig 读取消息并替换占位符后发送。
 * 消息文本自带前缀(如 §f[§c绽放§f])，直接发送，不经过 SendMessageUtil。
 *
 * 用法:
 *   Message.sendPlayer(player, "bloom-success")
 *   Message.sendPlayer(player, "preset-not-exist", "%preset%", preset)
 *   Message.sendConsole("preset-not-exist", "%preset%", preset)
 *   Message.sendBloomer(bloomer, "conflict", "%current%", cur, "%preset%", preset)
 */
public class Message {

    private static MessageConfig config() {
        return TuoLingBloom.inst().getConfigHolderManager().messageConfig();
    }

    private static String replace(String msg, String... placeholders) {
        if (placeholders == null || placeholders.length == 0) return msg;
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                msg = msg.replace(placeholders[i], placeholders[i + 1]);
            }
        }
        return msg;
    }

    private static String color(String msg) {
        return NormalUtil.colorString(msg);
    }

    // ==================== 玩家消息 ====================

    public static void sendPlayer(Player player, String key, String... placeholders) {
        MessageConfig cfg = config();
        if (cfg == null) return;
        String msg = cfg.getPlayerMessage(key);
        if (msg == null) return;
        player.sendMessage(color(replace(msg, placeholders)));
    }

    public static void sendConsole(String key, String... placeholders) {
        MessageConfig cfg = config();
        if (cfg == null) return;
        String msg = cfg.getConsoleMessage(key);
        if (msg == null) return;
        Bukkit.getConsoleSender().sendMessage(color(replace(msg, placeholders)));
    }

    /**
     * 按 sender 类型派发:Player 走 player 节点,其余走 console 节点
     */
    public static void sendSender(CommandSender sender, String key, String... placeholders) {
        if (sender instanceof Player) {
            sendPlayer((Player) sender, key, placeholders);
        } else {
            sendConsole(key, placeholders);
        }
    }

    /**
     * 按绽放者类型派发:Player 走 player 节点,mob 等非玩家走 console 节点
     */
    public static void sendBloomer(LivingEntity bloomer, String key, String... placeholders) {
        if (bloomer instanceof Player) {
            sendPlayer((Player) bloomer, key, placeholders);
        } else {
            sendConsole(key, placeholders);
        }
    }

    // ==================== 帮助消息 ====================

    public static void sendHelp(CommandSender sender) {
        MessageConfig cfg = config();
        if (cfg == null) return;
        List<String> help = cfg.getHelpList();
        for (String line : help) {
            sender.sendMessage(color(line));
        }
    }
}

package com.tuoling.tuolingbloom.config.loader;

import com.tuoling.tuolingbloom.config.data.MessageConfigData;
import com.tuoling.tuolingbloom.utils.BloomLogger;
import com.tuoling.tuolingcore.framework.config.ConfigHolder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息配置加载器。
 * 注意: MessageConfig 是第一个加载的配置,此时 Message 工具类尚不可用,
 *       因此错误信息直接通过 BloomLogger 输出,不走 Message.sendConsole。
 */
public class MessageConfigLoader {

    public MessageConfigData load(ConfigHolder holder) {
        if (holder.configIsNull()) {
            BloomLogger.error("message.yml 加载失败: 配置文件为空!");
            return new MessageConfigData(
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    Collections.emptyList()
            );
        }

        Map<String, String> playerMessages = loadSection(holder, "player");
        Map<String, String> consoleMessages = loadSection(holder, "console");
        List<String> help = loadHelp(holder);

        return new MessageConfigData(playerMessages, consoleMessages, help);
    }

    private Map<String, String> loadSection(ConfigHolder holder, String sectionKey) {
        Map<String, String> map = new LinkedHashMap<>();
        ConfigurationSection section = holder.getConfigurationSection(sectionKey);
        if (section == null) return map;
        for (String key : section.getKeys(false)) {
            String value = section.getString(key);
            if (value != null) {
                map.put(key, value);
            }
        }
        return map;
    }

    private List<String> loadHelp(ConfigHolder holder) {
        List<String> help = holder.getStringList("help");
        return help != null ? new ArrayList<>(help) : new ArrayList<>();
    }
}

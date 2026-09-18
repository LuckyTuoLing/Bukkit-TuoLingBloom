package com.tuoling.tuolingbloom.config.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息配置的加载产物，纯数据载体。
 * 由 MessageConfigLoader 从 message.yml 解析产出，MessageConfig 直接持有。
 * 集合 getter 返回浅拷贝副本。
 */
public class MessageConfigData {

    private final Map<String, String> playerMessages;
    private final Map<String, String> consoleMessages;
    private final List<String> help;

    public MessageConfigData(Map<String, String> playerMessages,
                             Map<String, String> consoleMessages,
                             List<String> help) {
        this.playerMessages = playerMessages;
        this.consoleMessages = consoleMessages;
        this.help = help;
    }

    public Map<String, String> getPlayerMessages() {
        return new LinkedHashMap<>(playerMessages);
    }

    public Map<String, String> getConsoleMessages() {
        return new LinkedHashMap<>(consoleMessages);
    }

    public List<String> getHelp() {
        return new ArrayList<>(help);
    }

    public String getPlayerMessage(String key) {
        return playerMessages.get(key);
    }

    public String getConsoleMessage(String key) {
        return consoleMessages.get(key);
    }
}

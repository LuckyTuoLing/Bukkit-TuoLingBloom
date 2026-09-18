package com.tuoling.tuolingbloom.config.holder;

import com.tuoling.tuolingbloom.config.data.MessageConfigData;
import com.tuoling.tuolingbloom.config.loader.MessageConfigLoader;
import com.tuoling.tuolingcore.framework.config.ConfigHolder;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 消息配置持有者。
 * 构造时调 Loader 拿到 MessageConfigData，直接持有。
 * 集合 getter 委托给 data 的浅拷贝。
 */
public class MessageConfig extends ConfigHolder {

    private final MessageConfigData data;

    public MessageConfig(File configFile) {
        super(configFile);
        this.data = new MessageConfigLoader().load(this);
    }

    public String getPlayerMessage(String key) {
        return data.getPlayerMessage(key);
    }

    public String getConsoleMessage(String key) {
        return data.getConsoleMessage(key);
    }

    public Map<String, String> getPlayerMessages() {
        return data.getPlayerMessages();
    }

    public Map<String, String> getConsoleMessages() {
        return data.getConsoleMessages();
    }

    public List<String> getHelpList() {
        return data.getHelp();
    }
}

package com.tuoling.tuolingbloom.config;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.config.holder.DefaultConfig;
import com.tuoling.tuolingbloom.config.holder.MessageConfig;
import com.tuoling.tuolingbloom.config.holder.WorldTextureConfig;
import com.tuoling.tuolingbloom.config.holder.ModelConfig;
import com.tuoling.tuolingbloom.config.holder.TextureConfig;
import com.tuoling.tuolingbloom.router.BloomRouter;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingcore.framework.config.ConfigHolder;
import com.tuoling.tuolingcore.framework.manager.AbstractKeyedManager;
import com.tuoling.tuolingcore.utils.ConfigLoadUtils;
import lombok.Getter;

import java.io.File;

import java.util.List;

@Getter
public class ConfigHolderManager extends AbstractKeyedManager<String, ConfigHolder> {


    public ConfigHolderManager() {
        reloadAll();
    }

    @Override
    public String extractKey(ConfigHolder configHolder) {
        return configHolder.getConfigName().replace(".yml", "");
    }

    public void reloadAll() {
        try {
            clear();
            BloomRouter bloomRouter = TuoLingBloom.inst().getBloomRouter();
            bloomRouter.clear();

            // message.yml 最先加载，确保后续错误消息可用
            MessageConfig messageConfig = new MessageConfig(ConfigLoadUtils.getOrCreateConfigFile(TuoLingBloom.inst(), "message"));
            register(messageConfig);

            List<File> modelFiles = ConfigLoadUtils.loadYmlFilesRecursively(TuoLingBloom.inst(), "model");
            for (File configFile : modelFiles) {
                ModelConfig cfg = new ModelConfig(configFile);
                register(cfg);
                bloomRouter.addRouter(cfg.getConfigName().replace(".yml", ""), "model");
            }

            List<File> texFiles = ConfigLoadUtils.loadYmlFilesRecursively(TuoLingBloom.inst(), "texture");
            for (File configFile : texFiles) {
                TextureConfig cfg = new TextureConfig(configFile);
                register(cfg);
                bloomRouter.addRouter(cfg.getConfigName().replace(".yml", ""), "texture");

            }

            WorldTextureConfig worldTextureConfig = new WorldTextureConfig(ConfigLoadUtils.getOrCreateConfigFile(TuoLingBloom.inst(), "world-texture"));
            register(worldTextureConfig);

            DefaultConfig defaultConfig = new DefaultConfig(ConfigLoadUtils.getOrCreateConfigFile(TuoLingBloom.inst(), "config"));
            register(defaultConfig);
        }
        catch (Exception e) {
            Message.sendConsole("reload-failed", "%error%", String.valueOf(e));
        }
    }

    public DefaultConfig defaultConfig() {
        if (get("config") instanceof DefaultConfig) {
            return (DefaultConfig) get("config");
        }
        return null;
    }

    public MessageConfig messageConfig() {
        if (get("message") instanceof MessageConfig) {
            return (MessageConfig) get("message");
        }
        return null;
    }

    public WorldTextureConfig worldTextureConfig() {
        if (get("world-texture") instanceof WorldTextureConfig) {
            return (WorldTextureConfig) get("world-texture");
        }
        return null;
    }

    public TextureConfig textureConfig(String fileName) {
        if (get(fileName) instanceof TextureConfig) {
            return (TextureConfig) get(fileName);
        }
        return null;
    }

    public ModelConfig modelConfig(String fileName) {
        if (get(fileName) instanceof ModelConfig) {
            return (ModelConfig) get(fileName);
        }
        return null;
    }
}

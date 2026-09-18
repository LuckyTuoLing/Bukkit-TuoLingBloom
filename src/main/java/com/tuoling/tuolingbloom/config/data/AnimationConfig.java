package com.tuoling.tuolingbloom.config.data;

import eos.moe.dragoncore.api.worldtexture.WorldTexture;

/**
 * 动画配置抽象基类。子类持有纯字段，applyTo 时现场创建 DragonCore 动画对象塞入 WorldTexture。
 */
public abstract class AnimationConfig {
    public abstract void applyTo(WorldTexture texture);
}
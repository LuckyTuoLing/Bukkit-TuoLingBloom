package com.tuoling.tuolingbloom.config.data;

import eos.moe.dragoncore.api.worldtexture.WorldTexture;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 一层绽放动画配置。
 * getTexture() 时现场 new 一个 WorldTexture 并把字段塞进去，返回的天然是独立副本。
 * path 由 BloomTextureService 从 preset.value 查表后塞入 WorldTexture。
 */
public class LayerSetting {

    @Getter
    private final int delay;
    private final TextureFields texture;       // nullable
    private final List<AnimationConfig> animations;

    public LayerSetting(int delay, TextureFields texture, List<AnimationConfig> animations) {
        this.delay = delay;
        this.texture = texture;
        this.animations = animations == null
                ? Collections.<AnimationConfig>emptyList()
                : Collections.unmodifiableList(new ArrayList<>(animations));
    }

    public boolean hasTexture() {
        return texture != null;
    }

    /**
     * 现场拼装一个 WorldTexture 实例并返回，每次调用都是新对象，调用方修改不会影响配置源。
     * path 由调用方塞入。
     */
    public WorldTexture getTexture() {
        if (texture == null) return null;
        WorldTexture t = new WorldTexture();
        texture.applyTo(t);
        for (AnimationConfig a : animations) {
            a.applyTo(t);
        }
        return t;
    }

    public List<AnimationConfig> getAnimations() {
        return new ArrayList<>(animations);
    }

    /**
     * texture 纯字段载体。applyTo 时把字段塞进 WorldTexture。
     * 字段全基本类型，天然不可变。
     */
    @Getter
    public static class TextureFields {
        private final double translateX;
        private final double translateY;
        private final double translateZ;
        private final float rotateX;
        private final float rotateY;
        private final float rotateZ;
        private final boolean followPlayerEyes;

        public TextureFields(double translateX, double translateY, double translateZ,
                             float rotateX, float rotateY, float rotateZ, boolean followPlayerEyes) {
            this.translateX = translateX;
            this.translateY = translateY;
            this.translateZ = translateZ;
            this.rotateX = rotateX;
            this.rotateY = rotateY;
            this.rotateZ = rotateZ;
            this.followPlayerEyes = followPlayerEyes;
        }

        public void applyTo(WorldTexture t) {
            t.translateX = translateX;
            t.translateY = translateY;
            t.translateZ = translateZ;
            t.rotateX = rotateX;
            t.rotateY = rotateY;
            t.rotateZ = rotateZ;
            t.followPlayerEyes = followPlayerEyes;
        }
    }
}

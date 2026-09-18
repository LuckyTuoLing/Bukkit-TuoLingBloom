package com.tuoling.tuolingbloom.config.data;

import eos.moe.dragoncore.api.worldtexture.WorldTexture;
import eos.moe.dragoncore.api.worldtexture.animation.TranslateAnimation;
import lombok.Getter;

@Getter
public class TranslateAnimConfig extends AnimationConfig {
    private final String direction;
    private final int delay;
    private final float distance;
    private final int duration;
    private final int cycleCount;
    private final boolean fixed;

    public TranslateAnimConfig(String direction, int delay, float distance, int duration, int cycleCount, boolean fixed) {
        this.direction = direction;
        this.delay = delay;
        this.distance = distance;
        this.duration = duration;
        this.cycleCount = cycleCount;
        this.fixed = fixed;
    }

    @Override
    public void applyTo(WorldTexture texture) {
        TranslateAnimation anim = new TranslateAnimation();
        anim.direction = direction;
        anim.delay = delay;
        anim.distance = distance;
        anim.duration = duration;
        anim.cycleCount = cycleCount;
        anim.fixed = fixed;
        texture.animationList.add(anim);
    }
}
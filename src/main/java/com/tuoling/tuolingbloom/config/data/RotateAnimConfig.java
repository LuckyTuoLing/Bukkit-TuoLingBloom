package com.tuoling.tuolingbloom.config.data;

import eos.moe.dragoncore.api.worldtexture.WorldTexture;
import eos.moe.dragoncore.api.worldtexture.animation.RotateAnimation;
import lombok.Getter;

@Getter
public class RotateAnimConfig extends AnimationConfig {
    private final String direction;
    private final int delay;
    private final float angle;
    private final int duration;
    private final int cycleCount;
    private final boolean fixed;
    private final int resetTime;

    public RotateAnimConfig(String direction, int delay, float angle, int duration, int cycleCount, boolean fixed, int resetTime) {
        this.direction = direction;
        this.delay = delay;
        this.angle = angle;
        this.duration = duration;
        this.cycleCount = cycleCount;
        this.fixed = fixed;
        this.resetTime = resetTime;
    }

    @Override
    public void applyTo(WorldTexture texture) {
        RotateAnimation anim = new RotateAnimation();
        anim.direction = direction;
        anim.delay = delay;
        anim.angle = angle;
        anim.duration = duration;
        anim.cycleCount = cycleCount;
        anim.fixed = fixed;
        anim.resetTime = resetTime;
        texture.animationList.add(anim);
    }
}
package com.tuoling.tuolingbloom.config.data;

import eos.moe.dragoncore.api.worldtexture.WorldTexture;
import eos.moe.dragoncore.api.worldtexture.animation.ScaleAnimation;
import lombok.Getter;

@Getter
public class ScaleAnimConfig extends AnimationConfig {
    private final int delay;
    private final float fromScale;
    private final float toScale;
    private final int duration;
    private final int cycleCount;
    private final boolean fixed;
    private final int resetTime;

    public ScaleAnimConfig(int delay, float fromScale, float toScale, int duration, int cycleCount, boolean fixed, int resetTime) {
        this.delay = delay;
        this.fromScale = fromScale;
        this.toScale = toScale;
        this.duration = duration;
        this.cycleCount = cycleCount;
        this.fixed = fixed;
        this.resetTime = resetTime;
    }

    @Override
    public void applyTo(WorldTexture texture) {
        ScaleAnimation anim = new ScaleAnimation();
        anim.delay = delay;
        anim.fromScale = fromScale;
        anim.toScale = toScale;
        anim.duration = duration;
        anim.cycleCount = cycleCount;
        anim.fixed = fixed;
        anim.resetTime = resetTime;
        texture.animationList.add(anim);
    }
}
package com.tuoling.tuolingbloom.papi;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.actor.bloomer.Bloomer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BloomPlaceholderExpansion extends PlaceholderExpansion {

    private final TuoLingBloom plugin;

    public BloomPlaceholderExpansion(TuoLingBloom plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "tuolingbloom";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "LuckyNine";
    }

    @NotNull
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Nullable
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return null;

        if (params.startsWith("cooldown_")) {
            String preset = params.substring("cooldown_".length());
            if (preset.isEmpty()) return "0";
            Bloomer bloomer = new Bloomer(player);
            return String.valueOf(bloomer.getCooldown(preset));
        }

        return null;
    }
}

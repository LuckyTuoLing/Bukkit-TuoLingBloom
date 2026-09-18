package com.tuoling.tuolingbloom.config.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorldTextureEntry {
    private final String world;
    private final WorldPosition location;
    private final String preset;
}
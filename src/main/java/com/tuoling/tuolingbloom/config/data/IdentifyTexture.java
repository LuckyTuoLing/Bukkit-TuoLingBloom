package com.tuoling.tuolingbloom.config.data;

import lombok.Getter;

@Getter
public class IdentifyTexture {
    private final String checkLore;
    private final String checkName;
    private final String path;

    public IdentifyTexture(String checkLore, String checkName, String path) {
        this.checkLore = checkLore;
        this.checkName = checkName;
        this.path = path;
    }
}
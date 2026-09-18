package com.tuoling.tuolingbloom.config.data;

import lombok.Getter;

@Getter
public class IdentifyModel {
    private final String checkLore;
    private final String checkName;
    private final String mmName;

    public IdentifyModel(String checkLore, String checkName, String mmName) {
        this.checkLore = checkLore;
        this.checkName = checkName;
        this.mmName = mmName;
    }
}

package com.tuoling.tuolingbloom.config.data;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BloomPresetEntry {

    @Getter private final String presetName;
    @Getter private final String mode;
    @Getter private final String value;
    @Getter private final String view;
    @Getter private final String group;
    @Getter private final long duration;
    private final List<String> valueList;

    public BloomPresetEntry(String presetName, String mode, String value, String view, String group, long duration, List<String> valueList) {
        this.presetName = presetName;
        this.mode = mode;
        this.value = value;
        this.view = view;
        this.group = group;
        this.duration = duration;
        this.valueList = valueList == null ? Collections.<String>emptyList() : Collections.unmodifiableList(new ArrayList<>(valueList));
    }

    public List<String> getValueList() {
        return new ArrayList<>(valueList);
    }
}

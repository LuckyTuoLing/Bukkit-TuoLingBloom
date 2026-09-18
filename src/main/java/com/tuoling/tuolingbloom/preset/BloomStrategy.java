package com.tuoling.tuolingbloom.preset;

import org.bukkit.entity.Player;

import java.util.List;

//绽放前数据整合接口
public interface BloomStrategy {
    //匹配用
    String getModeName();

    List<String> getTexturePaths(Player player, String preset);
    List<String> getMMNames(Player player, String preset);


}

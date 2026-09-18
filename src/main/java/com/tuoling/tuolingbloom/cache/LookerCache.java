package com.tuoling.tuolingbloom.cache;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Table;

import com.tuoling.tuolingbloom.actor.looker.LookerData;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


//省点代码,懒得封装~
@Getter
public class LookerCache extends ForwardingMap<UUID,Table<UUID,String,LookerData>> {

    //looker,bloomer 或 mob,preset,lookData
    private final Map<UUID,Table<UUID,String,LookerData> >lookerCache = new HashMap<>();

    @Override
    protected Map<UUID, Table<UUID, String, LookerData>> delegate() {
        return lookerCache;
    }
}

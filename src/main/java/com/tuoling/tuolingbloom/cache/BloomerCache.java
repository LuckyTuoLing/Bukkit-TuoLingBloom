package com.tuoling.tuolingbloom.cache;

import com.google.common.collect.ForwardingTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.tuoling.tuolingbloom.actor.bloomer.BloomerData;


import java.util.UUID;


public class BloomerCache extends ForwardingTable<UUID,String, BloomerData> {

    private final Table<UUID,String,BloomerData> bloomerTable = HashBasedTable.create();

    @Override
    protected Table<UUID, String, BloomerData> delegate() {
        return bloomerTable;
    }


}
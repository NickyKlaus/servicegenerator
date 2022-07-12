package com.home.origami.plugin.processing.registry.meta;

import com.home.origami.plugin.processing.registry.meta.json.NodeModule;
import com.home.origami.plugin.processing.registry.meta.model.MetaModel;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.mapper.JacksonMapper;
import org.dizitart.no2.objects.ObjectRepository;

public class Db {
    private static final Nitrite db;

    static {
        db = Nitrite.builder()
                .registerModule(new NodeModule())
                .nitriteMapper(new JacksonMapper())
                .compressed()
                .enableOffHeapStorage()
                .openOrCreate();
    }

    private Db() {
    }

    public static void commit() {
        db.commit();
    }

    public static void close() {
        db.close();
    }

    public static <T extends MetaModel> ObjectRepository<T> getRepository(String collectionName, Class<T> cls) {
        return db.getRepository(collectionName, cls);
    }
}

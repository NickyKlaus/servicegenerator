package com.home.origami.plugin.db;

import com.home.origami.plugin.db.json.NodeModule;
import com.home.origami.plugin.db.model.Model;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.mapper.JacksonMapper;
import org.dizitart.no2.objects.ObjectRepository;

public class DBClient implements AutoCloseable {
    public static final DBClient INSTANCE = new DBClient();
    private static final Nitrite _db =
            Nitrite.builder()
                    .registerModule(new NodeModule())
                    .nitriteMapper(new JacksonMapper())
                    .compressed()
                    .enableOffHeapStorage()
                    .openOrCreate();

    private DBClient() {
    }

    public static void commit() {
        _db.commit();
    }

    public void close() {
        _db.close();
    }

    public static <T extends Model> ObjectRepository<T> getRepository(String collectionName, Class<T> cls) {
        return _db.getRepository(collectionName, cls);
    }
}

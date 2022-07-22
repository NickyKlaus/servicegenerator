package com.github.origami.plugin.db;

import com.github.origami.plugin.db.model.Model;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.repository.ObjectRepository;
import org.dizitart.no2.store.memory.InMemoryStoreModule;

public class DBClient implements AutoCloseable {
    public static final DBClient INSTANCE = new DBClient();
    private static final Nitrite _db =
            Nitrite.builder()
                    .loadModule(
                            InMemoryStoreModule
                                    .withConfig()
                                    .build())
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
        return _db.getRepository(cls, collectionName);
    }
}

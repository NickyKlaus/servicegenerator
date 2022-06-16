package com.home.servicegenerator.plugin.processing.configuration.schemaprovider;

public enum ProcessingSchemaProvidingStrategy {
    /*PRELOADED {
        @Override
        ASTProcessingSchema get(Supplier<Class<ASTProcessingSchema>> schemaSupplier) {
            final Class<? extends ASTProcessingSchema> schemaClass;
            if (schemaSupplier == null || (schemaClass = schemaSupplier.get()) == null) {
                return null;
            }
            return schemaSupplier.get();
        }
    },
    URI_BASED {
        @Override
        ASTProcessingSchema get(Supplier<Class<ASTProcessingSchema>> schemaSupplier) {
            final Class<? extends ASTProcessingSchema> schemaClass;
            if (schemaSupplier == null || (schemaClass = schemaSupplier.get()) == null) {
                return null;
            }
            ASTProcessingSchema schema = null;
            try {
                schema = schemaClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return schema;
        }
    };

    abstract ASTProcessingSchema get(final Supplier<Class<ASTProcessingSchema>> schemaSupplier);*/
}

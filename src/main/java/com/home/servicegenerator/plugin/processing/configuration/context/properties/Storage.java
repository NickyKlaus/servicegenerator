package com.home.servicegenerator.plugin.processing.configuration.context.properties;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.home.servicegenerator.plugin.utils.NormalizerUtils.REPLACING_MODEL_TYPE_SYMBOL;

public class Storage {
    public enum DbType {
        mongo {
            private static final String SPRING_DATA_ENABLE_MONGO =
                    "org.springframework.data.mongodb.repository.config.EnableMongoRepositories";
            private static final String SPRING_BOOT_STARTER_DATA_MONGODB =
                    "org.springframework.boot:spring-boot-starter-data-mongodb:2.5.3";

            @Override
            public String getCrudRepositoryInterfaceName() {
                return "org.springframework.data.mongodb.repository.MongoRepository";
            }

            @Override
            public String dbRepositoryConfigAnnotationClass() {
                return SPRING_DATA_ENABLE_MONGO;
            }

            @Override
            public List<MethodDeclaration> getRepositoryImplementationMethodDeclarations() {
                String[] declarations = {
                        String.format("public %s save(%s entity) {}", REPLACING_MODEL_TYPE_SYMBOL, REPLACING_MODEL_TYPE_SYMBOL),
                        String.format("public List<%s> saveAll(Iterable<%s> entities) {}", REPLACING_MODEL_TYPE_SYMBOL, REPLACING_MODEL_TYPE_SYMBOL),
                        String.format("public Optional<%s> findById(Long id) {}", REPLACING_MODEL_TYPE_SYMBOL),
                        "public boolean existsById(Long id) {}",
                        String.format("public List<%s> findAll() {}", REPLACING_MODEL_TYPE_SYMBOL),
                        String.format("public Iterable<%s> findAllById(Iterable<Long> ids) {}", REPLACING_MODEL_TYPE_SYMBOL),
                        "public long count() {}",
                        "public void deleteById(Long id) {}",
                        String.format("public void delete(%s entity) {}", REPLACING_MODEL_TYPE_SYMBOL),
                        "public void deleteAllById(Iterable<Long> ids) {}",
                        String.format("public void deleteAll(Iterable<%s> entities) {}", REPLACING_MODEL_TYPE_SYMBOL),
                        "public void deleteAll() {}",
                        //"public org.springframework.data.domain.Page<T> findAll(org.springframework.data.domain.Pageable pageable) {}",
                        //"public java.util.List<T> findAll(org.springframework.data.domain.Sort sort) {}",
                        //"public <S extends T> S insert(S entity) {}",
                        //"public <S extends T> java.util.List<S> insert(Iterable<S> entities) {}",
                        //"public <S extends T> java.util.Optional<S> findOne(org.springframework.data.util.ProxyUtils.Example<S> example) {}",
                        //"public <S extends T> java.util.List<S> findAll(org.springframework.data.util.ProxyUtils.Example<S> example) {}",
                        //"public <S extends T> java.util.List<S> findAll(org.springframework.data.util.ProxyUtils.Example<S> example, org.springframework.data.domain.Sort sort) {}",
                        //"public <S extends T> org.springframework.data.domain.Page<S> findAll(org.springframework.data.util.ProxyUtils.Example<S> example, org.springframework.data.domain.Pageable pageable) {}",
                        //"public <S extends T> long count(org.springframework.data.util.ProxyUtils.Example<S> example) {}",
                        //"public <S extends T> boolean exists(org.springframework.data.util.ProxyUtils.Example<S> example) {}",
                };

                return Arrays.stream(declarations)
                        .map(StaticJavaParser::parseMethodDeclaration)
                        .collect(Collectors.toUnmodifiableList());
            }

            @Override
            public String dependencyDescriptor() {
                return SPRING_BOOT_STARTER_DATA_MONGODB;
            }
        },
        cassandra {
            private static final String SPRING_DATA_ENABLE_CASSANDRA =
                    "org.springframework.data.cassandra.repository.config.EnableCassandraRepositories";
            private static final String SPRING_BOOT_STARTER_DATA_CASSANDRA =
                    "org.springframework.boot:spring-boot-starter-data-cassandra:2.5.3";

            @Override
            public String getCrudRepositoryInterfaceName() {
                return "null";
            }

            @Override
            public String dbRepositoryConfigAnnotationClass() {
                return SPRING_DATA_ENABLE_CASSANDRA;
            }

            @Override
            public List<MethodDeclaration> getRepositoryImplementationMethodDeclarations() {
                String[] declarations = {
                        /*"public <S extends T> S save(S entity) {}",
                        "public <S extends T> List<S> saveAll(Iterable<S> entities) {}",
                        "public Optional<T> findById(ID id) {}",
                        "public boolean existsById(ID id) {}",*/
                        String.format("public List<%s> findAll() {}", REPLACING_MODEL_TYPE_SYMBOL),
                        /*"public Iterable<T> findAllById(Iterable<ID> ids) {}",
                        "public long count() {}",
                        "public void deleteById(ID id) {}",
                        "public void delete(T entity) {}",
                        "public void deleteAllById(Iterable<? extends ID> ids) {}",
                        "public void deleteAll(Iterable<? extends T> entities) {}",
                        "public void deleteAll() {}",
                        "public Page<T> findAll(Pageable pageable) {}",
                        "public List<T> findAll(Sort sort) {}",
                        "public <S extends T> S insert(S entity) {}",
                        "public <S extends T> List<S> insert(Iterable<S> entities) {}",
                        "public <S extends T> Optional<S> findOne(Example<S> example) {}",
                        "public <S extends T> List<S> findAll(Example<S> example) {}",
                        "public <S extends T> List<S> findAll(Example<S> example, Sort sort) {}",
                        "public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {}",
                        "public <S extends T> long count(Example<S> example) {}",
                        "public <S extends T> boolean exists(Example<S> example) {}",*/
                };

                return Arrays.stream(declarations)
                        .map(StaticJavaParser::parseMethodDeclaration)
                        .collect(Collectors.toUnmodifiableList());
            }

            @Override
            public String dependencyDescriptor() {
                return SPRING_BOOT_STARTER_DATA_CASSANDRA;
            }
        },
        /*elasticsearch {
            @Override
            public String dbRepositoryConfigAnnotation() {
                return null;
            }

            var SPRING_BOOT_STARTER_DATA_ELASTICSEARCH = "org.springframework.boot:spring-boot-starter-data-elasticsearch:2.5.3";

        },*/;

        public abstract String getCrudRepositoryInterfaceName();

        public abstract String dbRepositoryConfigAnnotationClass();

        public abstract List<MethodDeclaration> getRepositoryImplementationMethodDeclarations();

        public abstract String dependencyDescriptor();

        public List<ImportDeclaration> getUsedImportDeclarations() {
            String[] declarations = {
                    "import java.util.List;",
                    "import java.util.Optional;",
            };

            return Arrays.stream(declarations)
                    .map(StaticJavaParser::parseImport)
                    .collect(Collectors.toUnmodifiableList());
        }
    }
}

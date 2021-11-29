package com.home.servicegenerator.plugin.context;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.home.servicegenerator.api.context.Property;
import com.home.servicegenerator.plugin.processing.MatchMethodStrategy;
import com.home.servicegenerator.plugin.utils.MethodNormalizer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ProcessingProperty implements Property {
    private final ProcessingProperty.Name name;
    private final Object value;

    public ProcessingProperty(ProcessingProperty.Name name, Object value) {
        Objects.requireNonNull(name, "name must be non null!");
        Objects.requireNonNull(value, "value must be non null!");
        this.name = name;
        this.value = value;
    }
    public ProcessingProperty(String name, Object value) {
        Objects.requireNonNull(name, "name must be non null!");
        Objects.requireNonNull(value, "value must be non null!");
        this.name = ProcessingProperty.Name.valueOf(name);
        this.value = value;
    }

    public static ProcessingProperty of(ProcessingProperty.Name name, Object value) {
        return new ProcessingProperty(name, value);
    }

    @Override
    public String getName() {
        return name.name();
    }

    public ProcessingProperty.Name getPropertyName() {
        return name;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ProcessingProperty) {
            return Objects.equals(((ProcessingProperty) other).getName(), getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

    @Override
    public String toString() {
        return "ProcessingProperty: [name= " + getName() + ", value= " + getValue().toString() + "]";
    }

    public enum Name {
        DB_TYPE,
        REPOSITORY_ID_CLASS_NAME,
        REPOSITORY_NAME,
        REPOSITORY_PACKAGE_NAME,
        ABSTRACT_SERVICE_NAME,
        ABSTRACT_SERVICE_PACKAGE_NAME,
        ABSTRACT_SERVICE_METHOD_DECLARATION,
        REPOSITORY_METHOD_DECLARATION,
    }

    public enum StorageType {
        mongo {
            private static final String SPRING_DATA_ENABLE_MONGO = "org.springframework.data.mongodb.repository.config.EnableMongoRepositories";

            @Override
            public String getCrudRepositoryInterfaceName() {
                return "org.springframework.data.mongodb.repository.MongoRepository";
            }

            @Override
            public AnnotationExpr prepareDbRepositoryConfigAnnotation(List<String> repositoriesBasePackageNames) {
                return prepareSpringDataDbConfigAnnotation(
                        SPRING_DATA_ENABLE_MONGO,
                        NodeList.nodeList(
                                new MemberValuePair(
                                        "basePackages",
                                        new ArrayInitializerExpr(
                                                repositoriesBasePackageNames
                                                        .stream()
                                                        .map(StringLiteralExpr::new)
                                                        .collect(NodeList.toNodeList())))));
            }



            @Override
            public List<MethodDeclaration> getRepositoryImplementationMethodDeclarations() {
                String[] declarations = {
                        "public $ModelType save($ModelType entity) {}",
                        "public List<$ModelType> saveAll(Iterable<$ModelType> entities) {}",
                        "public Optional<$ModelType> findById(Long id) {}",
                        "public boolean existsById(Long id) {}",
                        "public List<$ModelType> findAll() {}",
                        "public Iterable<$ModelType> findAllById(Iterable<Long> ids) {}",
                        "public long count() {}",
                        "public void deleteById(Long id) {}",
                        "public void delete($ModelType entity) {}",
                        "public void deleteAllById(Iterable<Long> ids) {}",
                        "public void deleteAll(Iterable<$ModelType> entities) {}",
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
        },
        cassandra {
            private static final String SPRING_DATA_ENABLE_CASSANDRA = "org.springframework.data.cassandra.repository.config.EnableCassandraRepositories";

            @Override
            public String getCrudRepositoryInterfaceName() {
                return null;
            }

            @Override
            public AnnotationExpr prepareDbRepositoryConfigAnnotation(List<String> repositoriesBasePackageNames) {
                return prepareSpringDataDbConfigAnnotation(
                        SPRING_DATA_ENABLE_CASSANDRA,
                        NodeList.nodeList(
                                new MemberValuePair(
                                        "basePackages",
                                        new ArrayInitializerExpr(
                                                repositoriesBasePackageNames
                                                        .stream()
                                                        .map(StringLiteralExpr::new)
                                                        .collect(NodeList.toNodeList())))));
            }

            @Override
            public List<MethodDeclaration> getRepositoryImplementationMethodDeclarations() {
                String[] declarations = {
                        /*"public <S extends T> S save(S entity) {}",
                        "public <S extends T> List<S> saveAll(Iterable<S> entities) {}",
                        "public Optional<T> findById(ID id) {}",
                        "public boolean existsById(ID id) {}",*/
                        "public List<$ModelType> findAll() {}",
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
        },
        /*elasticsearch {
            @Override
            public AnnotationExpr prepareDbRepositoryConfigAnnotation(List<String> repositoriesBasePackageNames) {
                return null;
            }
        },*/
        ;
        public static final String REPLACING_MODEL_TYPE_SYMBOL = "$ModelType";

        public abstract String getCrudRepositoryInterfaceName();
        public abstract AnnotationExpr prepareDbRepositoryConfigAnnotation(List<String> repositoriesBasePackageNames);
        public abstract List<MethodDeclaration> getRepositoryImplementationMethodDeclarations();
        public List<ImportDeclaration> getUsedImportDeclarations() {
            String[] declarations = {
                    "import java.util.List;",
                    "import java.util.Optional;",
            };

            return Arrays.stream(declarations)
                    .map(StaticJavaParser::parseImport)
                    .collect(Collectors.toUnmodifiableList());
        }

        public static Optional<MethodDeclaration> getMethodMatchedWithPipeline(
                final MethodDeclaration pipeline,
                final List<MethodDeclaration> checkedMethods,
                final com.github.javaparser.ast.expr.Name pipelineId,
                final MatchMethodStrategy checkingMethodStrategy
        ) {
            return checkedMethods
                    .stream()
                    .filter(checkedMethod -> checkingMethodStrategy
                            .matchMethods(pipelineId.getIdentifier())
                            .test(pipeline, checkedMethod))
                    .map(m -> MethodNormalizer.denormalize(m, pipelineId.getIdentifier(), REPLACING_MODEL_TYPE_SYMBOL))
                    .findFirst();
        }
    }

    private static AnnotationExpr prepareSpringDataDbConfigAnnotation(
            String enableAnnotationName, NodeList<MemberValuePair> annotationMembers
    ) {
        return new NormalAnnotationExpr(new com.github.javaparser.ast.expr.Name(enableAnnotationName), annotationMembers);
    }
}

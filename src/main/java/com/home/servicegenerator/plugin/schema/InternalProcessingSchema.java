package com.home.servicegenerator.plugin.schema;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.context.properties.Storage;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.home.servicegenerator.plugin.processing.configuration.context.properties.PropertyName.*;

public enum InternalProcessingSchema implements ASTProcessingSchema {

    CreateRepository {
        @Override
        public BiFunction<CompilationUnit, Context, CompilationUnit> preProcessCompilationUnit() {
            return (CompilationUnit n, Context context) -> {
                final Name model = context.get(PIPELINE_ID.name(), Name.class);
                final Storage.DbType dbType = context.get(DB_TYPE.name(), Storage.DbType.class);
                final String repositoryPackageName = context.get(REPOSITORY_PACKAGE_NAME.name(), String.class);
                final String repositoryName = model.getIdentifier() + "Repository";
                final String repositoryIdClass = context.get(REPOSITORY_ID_CLASS_NAME.name(), String.class);

                // Create public interface extended Spring Data CrudRepository and mark it as Spring Repository bean.
                n.setPackageDeclaration(repositoryPackageName)
                        .addImport(model.toString())
                        .addImport("org.springframework.stereotype.Repository")
                        .addInterface(repositoryName, Modifier.Keyword.PUBLIC)
                        .addMarkerAnnotation(SPRING_REPOSITORY)
                        .setExtendedTypes(
                                NodeList.nodeList(
                                        new ClassOrInterfaceType()
                                                .setName(dbType.getCrudRepositoryInterfaceName())
                                                .setTypeArguments(
                                                        NodeList.nodeList(
                                                                new ClassOrInterfaceType().setName(model.getIdentifier()),
                                                                new ClassOrInterfaceType().setName(repositoryIdClass)))));
                return n;
            };
        }
    },

    CreateAbstractService {
        @Override
        public BiFunction<CompilationUnit, Context, CompilationUnit> preProcessCompilationUnit() {
            return (CompilationUnit n, Context context) -> {
                final Name model = context.get(PIPELINE_ID.name(), Name.class);
                final String servicePackageName = context.get(ABSTRACT_SERVICE_PACKAGE_NAME.name(), String.class);
                final String serviceName = model.getIdentifier() + "Service";
                final Storage.DbType storageType = context.get(DB_TYPE.name(), Storage.DbType.class);

                ClassOrInterfaceDeclaration abstractServiceInterface =
                        new ClassOrInterfaceDeclaration()
                                .setInterface(true)
                                .setName(serviceName)
                                .setModifier(Modifier.Keyword.PUBLIC, true);
                n.setPackageDeclaration(servicePackageName)
                        .addImport(model.toString())
                        .addType(abstractServiceInterface);
                storageType.getUsedImportDeclarations().forEach(n::addImport);
                return n;
            };
        }
    },

    AddServiceAbstractMethod {
        @Override
        public BiFunction<ClassOrInterfaceDeclaration, Context, ClassOrInterfaceDeclaration> preProcessClassOrInterfaceDeclaration() {
            return (ClassOrInterfaceDeclaration n, Context context) -> {
                final MethodDeclaration methodDeclaration =
                        context.get(ABSTRACT_SERVICE_METHOD_DECLARATION.name(), MethodDeclaration.class);

                n.addMethod(methodDeclaration.getNameAsString(), Modifier.Keyword.PUBLIC, Modifier.Keyword.ABSTRACT)
                        .setType(methodDeclaration.getType())
                        .setBody(null)
                        .setParameters(methodDeclaration.getParameters());
                return n;
            };
        }
    },

    CreateServiceImplementation {
        @Override
        public BiFunction<CompilationUnit, Context, CompilationUnit> preProcessCompilationUnit() {
            return (CompilationUnit n, Context context) -> {
                final Name model = context.get(PIPELINE_ID.name(), Name.class);
                final String abstractServicePackageName = context.get(ABSTRACT_SERVICE_PACKAGE_NAME.name(), String.class);
                final String abstractServiceName = model.getIdentifier() + "Service";
                final String servicePackageName = abstractServicePackageName + ".impl";
                final String serviceName = abstractServiceName + "Impl";
                final String repositoryPackageName = context.get(REPOSITORY_PACKAGE_NAME.name(), String.class);
                final String repositoryName = model.getIdentifier() + "Repository";
                final Storage.DbType storageType = context.get(DB_TYPE.name(), Storage.DbType.class);
                final String fullyQualifiedRepositoryName = repositoryPackageName + "." + repositoryName;
                final String fullyQualifiedAbstractServiceName = abstractServicePackageName + "." + abstractServiceName;
                final String repositoryFieldName = repositoryName.toLowerCase();

                // Constructor body with injected repository
                final BlockStmt constructorBody = new BlockStmt()
                        .setStatements(NodeList.nodeList(
                                new ExpressionStmt()
                                        .setExpression(
                                                new AssignExpr(
                                                        new FieldAccessExpr()
                                                                .setScope(new ThisExpr())
                                                                .setName(repositoryFieldName),
                                                        new NameExpr(repositoryFieldName),
                                                        AssignExpr.Operator.ASSIGN))));

                // Add package, private final field for repository and public non-default constructor.
                // Mark class as Spring service bean.
                final ClassOrInterfaceDeclaration classOrInterfaceDeclaration =
                        n.setPackageDeclaration(servicePackageName)
                                .addImport(model.toString())
                                .addImport("org.springframework.stereotype.Service")
                                .addImport(fullyQualifiedAbstractServiceName)
                                .addImport(fullyQualifiedRepositoryName)
                                .addClass(serviceName, Modifier.Keyword.PUBLIC)
                                .setImplementedTypes(
                                        NodeList.nodeList(
                                                new ClassOrInterfaceType().setName(abstractServiceName)));
                storageType.getUsedImportDeclarations().forEach(n::addImport);

                classOrInterfaceDeclaration
                        .addMarkerAnnotation(SPRING_SERVICE)
                        .addField(
                                repositoryName,
                                repositoryFieldName,
                                Modifier.Keyword.PRIVATE,
                                Modifier.Keyword.FINAL);
                classOrInterfaceDeclaration
                        .addConstructor(Modifier.Keyword.PUBLIC)
                        .addParameter(repositoryName, repositoryFieldName)
                        .setBody(constructorBody);

                return n;
            };
        }
    },

    AddServiceMethodImplementation {
        @Override
        public BiFunction<TypeParameter, Context, TypeParameter> postProcessTypeParameter() {
            return (TypeParameter n, Context context) -> {
                final Name model = context.get(PIPELINE_ID.name(), Name.class);

                if (n.getNameAsString().length() == 1) {
                    n.setName(model.getIdentifier());
                }
                return n;
            };
        }

        @Override
        public BiFunction<ClassOrInterfaceDeclaration, Context, ClassOrInterfaceDeclaration> preProcessClassOrInterfaceDeclaration() {
            return (ClassOrInterfaceDeclaration n, Context context) -> {
                final Name model = context.get(PIPELINE_ID.name(), Name.class);
                final String repositoryName = model.getIdentifier() + "Repository";
                final MethodDeclaration abstractServiceMethod = context.get(ABSTRACT_SERVICE_METHOD_DECLARATION.name(), MethodDeclaration.class);
                final String repositoryFieldName = repositoryName.toLowerCase();

                final BlockStmt methodBody = new BlockStmt()
                        .setStatements(NodeList.nodeList(
                                new ReturnStmt()
                                        .setExpression(
                                                new MethodCallExpr()
                                                        .setScope(new NameExpr(repositoryFieldName))
                                                        .setName(abstractServiceMethod.getName())
                                                        .setArguments(
                                                                abstractServiceMethod
                                                                        .getParameters()
                                                                        .stream()
                                                                        .map(Parameter::getNameAsExpression)
                                                                        .collect(NodeList.toNodeList())))));

                final MethodDeclaration method = n.addMethod(abstractServiceMethod.getNameAsString(), Modifier.Keyword.PUBLIC)
                        .setParameters(abstractServiceMethod.getParameters())
                        .setBody(methodBody);
                method.setType(abstractServiceMethod.getType());
                return n;
            };
        }
    },

    InjectServiceIntoController {
        @Override
        public BiFunction<CompilationUnit, Context, CompilationUnit> preProcessCompilationUnit() {
            return (CompilationUnit n, Context context) -> {
                final Name model = context.get(PIPELINE_ID.name(), Name.class);
                final String abstractServicePackageName = context.get(ABSTRACT_SERVICE_PACKAGE_NAME.name(), String.class);
                final String abstractServiceName = model.getIdentifier() + "Service";
                final String fullyQualifiedAbstractServiceTypeName = abstractServicePackageName + "." + abstractServiceName;
                n.addImport(fullyQualifiedAbstractServiceTypeName);

                return n;
            };
        }

        @Override
        public BiFunction<ClassOrInterfaceDeclaration, Context, ClassOrInterfaceDeclaration> preProcessClassOrInterfaceDeclaration() {
            return (ClassOrInterfaceDeclaration n, Context context) -> {
                final Predicate<ConstructorDeclaration> isNotDefaultConstructor =
                        (ConstructorDeclaration constructor) -> constructor.getParameters().isNonEmpty();
                final Name model = context.get(PIPELINE_ID.name(), Name.class);
                final String abstractServicePackageName = context.get(ABSTRACT_SERVICE_PACKAGE_NAME.name(), String.class);
                final String abstractServiceName = model.getIdentifier() + "Service";
                final String fullyQualifiedAbstractServiceTypeName = abstractServicePackageName + "." + abstractServiceName;
                final String serviceFieldName = abstractServiceName.toLowerCase();

                // Add field for service
                n.addField(
                        abstractServiceName,
                        serviceFieldName,
                        Modifier.Keyword.PRIVATE,
                        Modifier.Keyword.FINAL);

                // Add service argument to all constructors excepting default one
                n.getConstructors()
                        .stream()
                        .filter(isNotDefaultConstructor)
                        .forEach(
                                constructorDeclaration -> {
                                    // Constructor injection
                                    constructorDeclaration
                                            .addParameter(
                                                    abstractServiceName,
                                                    serviceFieldName)
                                            .getBody()
                                            .addStatement(
                                                    new ExpressionStmt()
                                                            .setExpression(
                                                                    new AssignExpr(
                                                                            new FieldAccessExpr()
                                                                                    .setScope(new ThisExpr())
                                                                                    .setName(serviceFieldName),
                                                                            new NameExpr(serviceFieldName),
                                                                            AssignExpr.Operator.ASSIGN)));
                                });
                return n;
            };
        }
    },

    AddControllerMethodImplementation {
        @Override
        public BiFunction<CompilationUnit, Context, CompilationUnit> postProcessCompilationUnit() {
            return (CompilationUnit n, Context context) -> {
                final Name model = context.get(PIPELINE_ID.name(), Name.class);

                n.getImports().removeIf(importDeclaration -> importDeclaration.getName().getQualifier().isPresent() &&
                        importDeclaration.getName().getQualifier().get().toString().equals("java.util"));
                n.addImport("java.util", false, true);
                n.addImport("org.springframework.http.ResponseEntity", false, false);

                // Check and add import statement for the model being used
                if (n.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getName().equals(model))) {
                    n.addImport(model.toString());
                }

                return n;
            };
        }

        @Override
        public BiFunction<ClassOrInterfaceDeclaration, Context, ClassOrInterfaceDeclaration> preProcessClassOrInterfaceDeclaration() {
            return (ClassOrInterfaceDeclaration n, Context context) -> {
                final Name model = context.get(PIPELINE_ID.name(), Name.class);
                final String abstractServiceName = model.getIdentifier() + "Service";
                final String serviceFieldName = abstractServiceName.toLowerCase();
                final MethodDeclaration controllerMethodDeclaration = context.get(PIPELINE.name(), MethodDeclaration.class);
                final MethodDeclaration abstractServiceMethodDeclaration = context.get(ABSTRACT_SERVICE_METHOD_DECLARATION.name(), MethodDeclaration.class);

                // Controller method implementation (body)
                final BlockStmt methodBody = new BlockStmt()
                        .setStatements(NodeList.nodeList(
                                new ReturnStmt()
                                        .setExpression(
                                                new ObjectCreationExpr()
                                                        .setType(
                                                                controllerMethodDeclaration.getTypeAsString())
                                                        .setArguments(
                                                                NodeList.nodeList(
                                                                        new MethodCallExpr()
                                                                                .setScope(new NameExpr().setName(serviceFieldName))
                                                                                .setName(abstractServiceMethodDeclaration.getName())
                                                                                .setArguments(
                                                                                        controllerMethodDeclaration
                                                                                                .getParameters()
                                                                                                .stream()
                                                                                                .map(Parameter::getNameAsExpression)
                                                                                                .collect(NodeList.toNodeList())),
                                                                        new TypeExpr(
                                                                                new ClassOrInterfaceType()
                                                                                        .setName(SPRING_HTTP_STATUS_200)))))));

                // Controller method implementation (declaration)
                n.addMethod(controllerMethodDeclaration.getNameAsString(), Modifier.Keyword.PUBLIC)
                        .addMarkerAnnotation(OVERRIDE)
                        .setType(controllerMethodDeclaration.getType())
                        .setParameters(controllerMethodDeclaration.getParameters())
                        .setBody(methodBody);

                return n;
            };
        }
    },

    EditConfiguration {
        @Override
        public BiFunction<CompilationUnit, Context, CompilationUnit> preProcessCompilationUnit() {
            return (CompilationUnit n, Context context) -> {
                final Storage.DbType storageType = context.get(DB_TYPE.name(), Storage.DbType.class);

                n.addImport(storageType.dbRepositoryConfigAnnotationClass());
                return n;
            };
        }

        @Override
        public BiFunction<ClassOrInterfaceDeclaration, Context, ClassOrInterfaceDeclaration> preProcessClassOrInterfaceDeclaration() {
            return (ClassOrInterfaceDeclaration n, Context context) -> {
                final String SPRING_BOOT_APPLICATION_FULL = "org.springframework.boot.autoconfigure.SpringBootApplication";
                final String SPRING_BOOT_APPLICATION_SHORT = "SpringBootApplication";
                final String basePackage = context.get(BASE_PACKAGE.name(), String.class) + ".*";
                // Register repository into Spring application class
                final Storage.DbType storageType = context.get(DB_TYPE.name(), Storage.DbType.class);
                final String repositoryPackageName = context.get(REPOSITORY_PACKAGE_NAME.name(), String.class);

                if (n.getAnnotationByName(SPRING_BOOT_APPLICATION_FULL).isPresent() ||
                        n.getAnnotationByName(SPRING_BOOT_APPLICATION_SHORT).isPresent()) {
                    n.addAnnotation(prepareDbRepositoryConfigAnnotation(List.of(repositoryPackageName), storageType));
                    n.getAnnotationByName("ComponentScan")
                            .ifPresent(
                                    annotationExpr -> {
                                        var _basePackages =
                                                annotationExpr
                                                        .asNormalAnnotationExpr()
                                                        .getPairs()
                                                        .stream()
                                                        .filter(pair -> "basePackages".equals(pair.getName().getIdentifier()))
                                                        .findFirst();
                                        if (_basePackages.isPresent()) {
                                            annotationExpr
                                                    .asNormalAnnotationExpr()
                                                    .getPairs()
                                                    .removeIf(pair -> "basePackages".equals(pair.getName().getIdentifier()));
                                        }
                                        annotationExpr
                                                .asNormalAnnotationExpr()
                                                .addPair(
                                                        "basePackages",
                                                        new ArrayInitializerExpr(
                                                                NodeList.nodeList(new StringLiteralExpr(basePackage))));
                                    });
                }

                return n;
            };
        }
    },
    ;

    public static AnnotationExpr prepareDbRepositoryConfigAnnotation(
            List<String> repositoriesBasePackageNames, Storage.DbType dbType
    ) {
        final String[] _name = StringUtils.split(dbType.dbRepositoryConfigAnnotationClass(), ".");
        return prepareSpringDataDbConfigAnnotation(
                _name[_name.length-1],
                NodeList.nodeList(
                        new MemberValuePair(
                                "basePackages",
                                new ArrayInitializerExpr(
                                        repositoriesBasePackageNames
                                                .stream()
                                                .map(StringLiteralExpr::new)
                                                .collect(NodeList.toNodeList())))));
    }

    public static AnnotationExpr prepareSpringDataDbConfigAnnotation(
            String enableAnnotationName, NodeList<MemberValuePair> annotationMembers
    ) {
        return new NormalAnnotationExpr(new com.github.javaparser.ast.expr.Name(enableAnnotationName), annotationMembers);
    }

    private static final String SPRING_REPOSITORY = "Repository";
    private static final String SPRING_SERVICE = "Service";
    private static final String SPRING_HTTP_STATUS_200 = "org.springframework.http.HttpStatus.OK";
    private static final String OVERRIDE = "Override";
}

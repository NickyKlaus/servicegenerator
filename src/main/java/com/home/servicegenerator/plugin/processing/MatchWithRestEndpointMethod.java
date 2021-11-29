package com.home.servicegenerator.plugin.processing;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.home.servicegenerator.plugin.utils.MethodNormalizer;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

public class MatchWithRestEndpointMethod implements MatchMethodStrategy {
    private static final String SPRING_REQUEST_MAPPING_ANNOTATION_NAME_FULL = "org.springframework.web.bind.annotation.RequestMapping";
    private static final String SPRING_REQUEST_MAPPING_ANNOTATION_NAME_SHORT = "RequestMapping";
    private static final String REPLACING_MODEL_TYPE_SYMBOL = "$ModelType";

    //
    private Optional<MethodDeclaration> _getMatched(
            MethodDeclaration pipeline, MethodDeclaration checkedMethod, String pipelineId
    ) {
        var normalPipeline = MethodNormalizer.normalize(pipeline, pipelineId, REPLACING_MODEL_TYPE_SYMBOL);
        var normalCheckedMethod = MethodNormalizer.normalize(checkedMethod, pipelineId, REPLACING_MODEL_TYPE_SYMBOL);

        var pipelineReturnType = normalPipeline.getType();
        var pipelineParameters = normalPipeline.getParameters();
        var checkedMethodReturnType = normalCheckedMethod.getType();
        var checkedMethodParameters = normalCheckedMethod.getParameters();

        if (
                checkedMethodReturnType.equals(pipelineReturnType) &&
                        checkedMethodParameters.size() == pipelineParameters.size() &&
                        IntStream.range(0, checkedMethodParameters.size())
                                .mapToObj(i -> ImmutablePair.of(
                                        pipelineParameters.get(i).getType(), checkedMethodParameters.get(i).getType()))
                                .allMatch(pair -> pair.getLeft().equals(pair.getRight()))
        ) {
            return Optional.of(checkedMethod);
        }
        return Optional.empty();
    }

    @Override
    public BiPredicate<MethodDeclaration, MethodDeclaration> matchMethods(final String pipelineId) {
        return (MethodDeclaration pipeline, MethodDeclaration checkedMethod) -> {
            return _getMatched(pipeline, checkedMethod, pipelineId).isPresent();
            /*var checkingResult = _getMatched(pipeline, checkedMethod, pipelineId).isPresent();
            var pipelineReturnType = pipeline.getType();

            if (
                    pipeline.getType().isReferenceType() && pipeline.getType().isClassOrInterfaceType() &&
                    pipeline.getType().asClassOrInterfaceType().getNameAsString().equals("ResponseEntity") &&
                    pipeline.getType().asClassOrInterfaceType().getTypeArguments().isPresent()
            ) {
                pipelineReturnType = pipeline.getType().asClassOrInterfaceType().getTypeArguments().get().get(0);
            }

            var pipelineAnnotations = pipeline.getAnnotations();
            var pipelineParameters = pipeline.getParameters();
            var requestMappingAnnotation = pipelineAnnotations
                    .stream()
                    .filter(a -> a.getName().getIdentifier().equals(SPRING_REQUEST_MAPPING_ANNOTATION_NAME_FULL) ||
                            a.getName().getIdentifier().equals(SPRING_REQUEST_MAPPING_ANNOTATION_NAME_SHORT))
                    .findFirst();

            var checkedMethodReturnType = checkedMethod.getType();
            var checkedMethodParameters = checkedMethod.getParameters();*/

            //Check return types and method parameters of pipeline and checked method are the same
            /*if (!(checkedMethodReturnType.equals(pipelineReturnType) &&
                    checkedMethodParameters.size() == pipelineParameters.size() &&
                    IntStream.range(0, checkedMethodParameters.size())
                            .mapToObj(i -> ImmutablePair.of(
                                    pipelineParameters.get(i).getType(), checkedMethodParameters.get(i).getType()))
                            .allMatch(pair -> pair.getLeft().equals(pair.getRight())))
            ) {
                return false;
            }*/

            //Check metadata from annotation
            /*if (requestMappingAnnotation.isPresent()) {
                var httpMethodName = "GET";
                if (requestMappingAnnotation.get().isNormalAnnotationExpr()) {
                    var _httpMethod = requestMappingAnnotation
                            .get()
                            .asNormalAnnotationExpr()
                            .findFirst(FieldAccessExpr.class);
                    if (_httpMethod.isPresent()) {
                        httpMethodName = _httpMethod.get().getNameAsString();
                    }
                }

                switch (httpMethodName) {
                    case "GET": {
                        //Check metadata from return type
                        //"findById", "existsById", "findAll", "findAllById", "count"
                        if (
                                ResolvedPrimitiveType.LONG.name().equalsIgnoreCase(pipelineReturnType.toString())
                        ) {
                            if (pipelineParameters.isEmpty()) {
                                return true; //matched with: long count();
                            }
                        }
                        if (ResolvedPrimitiveType.BOOLEAN.name().equalsIgnoreCase(pipelineReturnType.toString())) {
                            if (pipelineParameters.size() == 1 &&
                                    (pipelineParameters.get(0).getNameAsString().equals(Long.class.getCanonicalName()) ||
                                            pipelineParameters.get(0).getNameAsString().equals(Long.class.getSimpleName()))) {
                                return true; //matched with: boolean existsById(ID id);
                            }
                        }
                        if (pipelineReturnType.isReferenceType() && pipelineReturnType.isClassOrInterfaceType()) {
                            if (pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(List.class.getCanonicalName()) ||
                                    pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(List.class.getSimpleName()) ||
                                    pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(Iterable.class.getCanonicalName()) ||
                                    pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(Iterable.class.getSimpleName())
                            ) {
                                if (pipelineParameters.isEmpty()) {
                                    return true; //matched with: Iterable<T> findAll();
                                } else {
                                    if (pipelineReturnType.isReferenceType() && pipelineReturnType.isClassOrInterfaceType() &&
                                            (pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(List.class.getSimpleName()) ||
                                                    pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(List.class.getCanonicalName()) ||
                                                    pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(Iterable.class.getCanonicalName()) ||
                                                    pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(Iterable.class.getSimpleName()))) {
                                        return true; //matched with: Iterable<T> findAllById(Iterable<ID> ids);
                                    }
                                }
                            }
                            if (pipelineReturnType.isReferenceType() && pipelineReturnType.isClassOrInterfaceType() &&
                                    (pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(Optional.class.getCanonicalName()) ||
                                            pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(Optional.class.getSimpleName()))
                            ) {
                                return true; //matched with: Optional<T> findById(ID id);
                            }
                        }
                        break;
                    }
                    case "HEAD": {
                        // not implemented yet
                        break;
                    }
                    case "POST": {
                        //"save", "saveAll"
                        //Check metadata from method parameters
                        if (pipelineReturnType.isReferenceType() && pipelineReturnType.isClassOrInterfaceType() &&
                                pipelineParameters.size() == 1 &&
                                pipelineParameters.get(0).getType().isReferenceType() && pipelineParameters.get(0).getType().isClassOrInterfaceType() &&
                                pipelineReturnType.equals(pipelineParameters.get(0).getType())
                        ) {
                            if ((pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(List.class.getSimpleName()) ||
                                    pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(List.class.getCanonicalName()) ||
                                    pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(Iterable.class.getCanonicalName()) ||
                                    pipelineReturnType.asClassOrInterfaceType().getNameAsString().equals(Iterable.class.getSimpleName()))

                            ) {
                                return true; //matched with: <S extends T> Iterable<S> saveAll(Iterable<S> entities);
                            }
                            return true; //matched with: <S extends T> S save(S entity);
                        }
                        break;
                    }
                    case "PUT": {
                        // not implemented yet
                        break;
                    }
                    case "PATCH": {
                        // not implemented yet
                        break;
                    }
                    case "DELETE": {
                        // not implemented yet
                        //"deleteById", "delete", "deleteAllById", "deleteAll", "deleteAll", "deleteAll"
                        *//*if (checkedMethodReturnType.isVoidType()) {
                            if (checkedMethodParameters.isEmpty()) {
                                //matched with: void deleteAll();
                                return true;
                            }
                            if (checkedMethodParameters.size() == 1 && checkedMethodParameters.getFirst().isPresent()) {
                                if (checkedMethodParameters.getFirst().get().getType().isReferenceType()) {
                                    if (checkedMethodParameters
                                            .getFirst()
                                            .get()
                                            .getType()
                                            .resolve()
                                            .isAssignableBy(
                                                    StaticJavaParser
                                                            .parseType(Iterable.class.getCanonicalName())
                                                            .resolve())) {
                                        //strategy method parameter is matched with [Iterable]<? extends ID> of repo class
                                        //matched with: void deleteAllById(Iterable<? extends ID> ids);

                                        //strategy method parameter is matched with [Iterable]<? extends T> of repo class
                                        //matched with: void deleteAll(Iterable<? extends T> entities);
                                    }
                                    if (checkedMethodParameters
                                            .getFirst()
                                            .get()
                                            .getType()
                                            .resolve()
                                            .isAssignableBy(
                                                    StaticJavaParser
                                                            .parseType()//S extends T
                                                            .resolve())) {
                                        //strategy method parameter type is matched with S extends T class
                                        //matched with: void delete(T entity);
                                    }
                                    if (checkedMethodParameters
                                            .getFirst()
                                            .get()
                                            .getType()
                                            .resolve()
                                            .isAssignableBy(
                                                    StaticJavaParser
                                                            .parseType()//ID
                                                            .resolve())) {
                                        //strategy method parameter type is matched with ID of repo class
                                        //matched with: void deleteById(ID id);
                                    }
                                }
                            }
                        }*//*
                        break;
                    }
                    case "OPTIONS": {
                        // not implemented yet
                        break;
                    }
                    case "TRACE": {
                        // not implemented yet
                        break;
                    }
                }
            }
            return checkingResult;*/
        };
    }
}

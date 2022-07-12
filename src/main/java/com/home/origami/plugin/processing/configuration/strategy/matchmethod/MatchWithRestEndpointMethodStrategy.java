package com.home.origami.plugin.processing.configuration.strategy.matchmethod;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import com.home.origami.plugin.utils.MethodNormalizer;
import com.home.origami.plugin.utils.NormalizerUtils;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

public class MatchWithRestEndpointMethodStrategy implements MatchingMethodStrategy {
    @Override
    public BiPredicate<MethodDeclaration, MethodDeclaration> matchMethods(final String pipelineId) {
        return (MethodDeclaration pipeline, MethodDeclaration checkedMethod) ->
            _getMatched(pipeline, checkedMethod, pipelineId).isPresent();
    }

    private Optional<MethodDeclaration> _getMatched(
            MethodDeclaration pipeline, MethodDeclaration checkedMethod, String pipelineId
    ) {
        final MethodDeclaration normalPipeline = MethodNormalizer.normalize(pipeline, pipelineId, NormalizerUtils.REPLACING_MODEL_TYPE_SYMBOL);
        final MethodDeclaration normalCheckedMethod = MethodNormalizer.normalize(checkedMethod, pipelineId, NormalizerUtils.REPLACING_MODEL_TYPE_SYMBOL);
        final Type pipelineReturnType = normalPipeline.getType();
        final NodeList<Parameter> pipelineParameters = normalPipeline.getParameters();
        final Type checkedMethodReturnType = normalCheckedMethod.getType();
        final NodeList<Parameter> checkedMethodParameters = normalCheckedMethod.getParameters();
        boolean isMatched = checkedMethodReturnType.equals(pipelineReturnType) &&
                checkedMethodParameters.size() == pipelineParameters.size() &&
                IntStream.range(0, checkedMethodParameters.size())
                        .mapToObj(i -> ImmutablePair.of(
                                pipelineParameters.get(i).getType(), checkedMethodParameters.get(i).getType()))
                        .allMatch(pair -> pair.getLeft().equals(pair.getRight()));
        return isMatched ? Optional.of(checkedMethod) : Optional.empty();
    }
}

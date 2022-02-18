package com.home.servicegenerator.plugin.processing.processor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.home.servicegenerator.plugin.utils.MethodNormalizer;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

import static com.home.servicegenerator.plugin.utils.NormalizerUtils.REPLACING_MODEL_TYPE_SYMBOL;

public class MatchWithRestEndpointMethodStrategy implements MatchingMethodStrategy {
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
        return (MethodDeclaration pipeline, MethodDeclaration checkedMethod) ->
            _getMatched(pipeline, checkedMethod, pipelineId).isPresent();
    }
}

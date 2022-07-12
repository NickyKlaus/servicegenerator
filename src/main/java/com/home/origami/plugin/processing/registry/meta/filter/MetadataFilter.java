package com.home.origami.plugin.processing.registry.meta.filter;

public class MetadataFilter implements Filter {
    private final StringFilterExpression filterExpression;

    public MetadataFilter(StringFilterExpression filterExpression) {
        this.filterExpression = filterExpression;
    }

    public MetadataFilter(String filterExpressionAsString) {
        this.filterExpression = StringFilterExpression.of(filterExpressionAsString);
    }

    @Override
    public StringFilterExpression getExpression() {
        return filterExpression;
    }

    public static MetadataFilter of(String filterExpressionAsString) {
        return new MetadataFilter(filterExpressionAsString);
    }
}

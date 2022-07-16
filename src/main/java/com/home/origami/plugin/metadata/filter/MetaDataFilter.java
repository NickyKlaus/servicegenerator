package com.home.origami.plugin.metadata.filter;

import com.home.origami.plugin.db.filter.Filter;
import com.home.origami.plugin.db.filter.StringFilterExpression;

public class MetaDataFilter implements Filter {
    private final StringFilterExpression filterExpression;

    public MetaDataFilter(StringFilterExpression filterExpression) {
        this.filterExpression = filterExpression;
    }

    public MetaDataFilter(String filterExpressionAsString) {
        this.filterExpression = StringFilterExpression.of(filterExpressionAsString);
    }

    @Override
    public StringFilterExpression getExpression() {
        return filterExpression;
    }

    public static MetaDataFilter of(String filterExpressionAsString) {
        return new MetaDataFilter(filterExpressionAsString);
    }
}

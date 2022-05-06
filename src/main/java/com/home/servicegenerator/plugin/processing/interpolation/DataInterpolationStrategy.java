package com.home.servicegenerator.plugin.processing.interpolation;

import com.home.servicegenerator.api.context.Context;

import java.util.Map;

public enum DataInterpolationStrategy {
    CONSTANT_SUBSTITUTION {
        @Override
        public Object interpolate(final String placeholder, final Context context) {
            return (placeholder == null || context == null) ?
                    null :
                    context.getPropertyByName(placeholder)
                            .orElseThrow(() -> new IllegalArgumentException("!!!"))
                            .getValue();
        }
    },

    INTERCONNECTION_LOOKUP {
        @Override
        public Object interpolate(final String placeholder, final Context context) {
            return (placeholder == null || context == null) ? null : context.getPropertyByName(placeholder);
        }
    },

    COMPILATION_UNIT_EVALUATION {
        @Override
        public Object interpolate(final String placeholder, final Context context) {
            return (placeholder == null || context == null) ? null : context.getPropertyByName(placeholder);
        }
    };

    public abstract Object interpolate(final String placeholder, final Context context);
}

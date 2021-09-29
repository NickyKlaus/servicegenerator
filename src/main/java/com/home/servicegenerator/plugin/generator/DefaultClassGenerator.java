package com.home.servicegenerator.plugin.generator;

import com.github.javaparser.ast.visitor.Visitable;
import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.Generator;
import com.home.servicegenerator.plugin.visitor.DefaultClassVisitor;

/**
 * Generates special representation of all Abstract Syntax Tree nodes of implementation class using the base class as
 * a template.
 *
 * This generator delegates processing AST-nodes of the base class to the internal visitor.
 *
 * @see Generator
 * @see DefaultClassVisitor
 * @see ASTProcessingSchema
 */
public final class DefaultClassGenerator implements Generator<Visitable> {
    private final DefaultClassVisitor visitor;

    private DefaultClassGenerator(final Builder builder) {
        super();
        this.visitor = builder.visitor;
    }

    /**
     * Generates target class using internal visitor.
     * @param baseUnit - a base class AST-node (usually root-node)
     * @param arg - an argument for every applied visitor's action
     * @return AST-node of the target class (usually root-node)
     */
    @Override
    public Visitable generate(final Visitable baseUnit, final Object arg) {
        return baseUnit.accept(visitor, arg);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final DefaultClassVisitor visitor;

        private Builder() {
            this.visitor = new DefaultClassVisitor();
        }

        /**
         * Sets a schema for AST processing.
         * @param processingSchema - a schema containing the actions for every base class' element that would be applied
         *                        to them to generate corresponding target class element.
         * @return Builder object
         */
        public synchronized Builder processingSchema(final ASTProcessingSchema processingSchema) {
            if (processingSchema == null) {
                throw new IllegalArgumentException("Tries to register invalid processing schema!");
            }
            this.visitor.registerProcessingSchema(processingSchema);
            return this;
        }

        public synchronized DefaultClassGenerator build() {
            return new DefaultClassGenerator(this);
        }
    }
}

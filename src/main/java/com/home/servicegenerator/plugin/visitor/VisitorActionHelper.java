package com.home.servicegenerator.plugin.visitor;

import com.home.servicegenerator.plugin.generator.DefaultClassGenerator;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiFunction;

/**
 * Contains basic abstract actions that may be applied when appropriate AST-node is being processed by
 * Abstract Syntax Tree visitor.
 *
 * @see DefaultClassGenerator
 * @see DefaultClassVisitor
 */
public final class VisitorActionHelper {
    private VisitorActionHelper() {
        //do nothing
    }

    /**
     * Represents action of the copying (cloning) AST node from base class without modification.
     *
     * @param <T> the node type
     * @param <U> the node argument
     * @return clone-node action as a {@code BiFunction<T, U, T>}
     */
    public static <T, U> BiFunction<T, U, T> cloneNode() {
        return (n, arg) -> n;
    }

    /**
     * Represents 'empty' action. Current AST node will be ignored (will not be cloned).
     *
     * @param <T> the node type
     * @param <U> the node argument
     * @return ignore-node action as a {@code BiFunction<T, U, T>}
     */
    public static <T, U> BiFunction<T, U, T> noCloneNode(final Class<T> nodeClass) {
        try {
            final T nodeInstance = nodeClass.getConstructor().newInstance();
            return (n, arg) -> nodeInstance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

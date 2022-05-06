package com.home.servicegenerator.plugin.processing.context.properties;

import com.home.servicegenerator.api.context.Property;

import java.util.Objects;

public final class ProcessingProperty implements Property {
    private final PropertyName propertyName;
    private final Object value;

    public ProcessingProperty(PropertyName propertyName, Object value) {
        Objects.requireNonNull(propertyName, "name must be non null!");
        Objects.requireNonNull(value, "value must be non null!");
        this.propertyName = propertyName;
        this.value = value;
    }
    public ProcessingProperty(String name, Object value) {
        Objects.requireNonNull(name, "name must be non null!");
        Objects.requireNonNull(value, "value must be non null!");
        this.propertyName = PropertyName.valueOf(name);
        this.value = value;
    }

    public static ProcessingProperty of(PropertyName propertyName, Object value) {
        return new ProcessingProperty(propertyName, value);
    }

    @Override
    public String getName() {
        return propertyName.name();
    }

    public PropertyName getPropertyName() {
        return propertyName;
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

}

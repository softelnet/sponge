package org.openksavi.sponge.core.engine;

import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.config.PropertyEntry;

/**
 * Property entry.
 */
public class GenericPropertyEntry implements PropertyEntry {

    private Object value;

    private boolean variable;

    private boolean system;

    public GenericPropertyEntry(Object value, boolean variable, boolean system) {
        this.value = value;
        this.variable = variable;
        this.system = system;

        if (system && (value == null || !(value instanceof String))) {
            throw new ConfigException("System property must be a not null String.");
        }
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean isVariable() {
        return variable;
    }

    public void setVariable(boolean variable) {
        this.variable = variable;
    }

    @Override
    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

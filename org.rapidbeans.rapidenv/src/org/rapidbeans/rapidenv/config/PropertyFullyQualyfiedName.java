package org.rapidbeans.rapidenv.config;

import org.rapidbeans.core.basic.PropertyString;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeProperty;

public class PropertyFullyQualyfiedName extends PropertyString {

    public PropertyFullyQualyfiedName(TypeProperty type, RapidBean parentBean) {
        super(type, parentBean);
    }

    public String getValue() {
        return ((Property) getBean()).getFullyQualifiedName();
    }
}

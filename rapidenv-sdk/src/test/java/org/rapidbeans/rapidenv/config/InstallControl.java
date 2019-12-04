/*
 * Completely generated code file: InstallControl.java
 * !!!Do not edit manually!!!
 *
 * Rapid Beans enum generator, Copyright Martin Bluemel, 2008
 *
 * generated Java implementation of Rapid Beans enum type
 * org.rapidbeans.rapidenv.config.InstallControl
 * 
 * model:    
 * template: 
 */

package org.rapidbeans.rapidenv.config;

import org.rapidbeans.core.basic.RapidEnum;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.type.TypeRapidEnum;


/**
 * Enum: InstallControl.
 */
public enum InstallControl implements RapidEnum {


    // ------------------------------------------------------------------------
    // enum elements
    // -----------------------------------------------------------------------

    /**
     * enum element normal.
     */
    normal,

    /**
     * enum element optional.
     */
    optional,

    /**
     * enum element discontinued.
     */
    discontinued;


    // ------------------------------------------------------------------------
    // fixed set of helper methods
    // -----------------------------------------------------------------------

    /**
     * Get the description from the model (meta information - not UI).
     *
     * @return this enumeration element's description.
     */
    public String getDescription() {
        return type.getDescription(this);
	}

	/**
     * @see org.rapidbeans.core.basic.Enum#toStringGui(org.rapidbeans.core.common.RapidBeansLocale)
     */
    public String toStringGui(final RapidBeansLocale locale) {
        return type.getStringGui(this, locale);
    }

    /**
     * @see org.rapidbeans.core.basic.Enum#toStringGuiShort(org.rapidbeans.core.common.RapidBeansLocale)
     */
    public String toStringGuiShort(final RapidBeansLocale locale) {
        return type.getStringGuiShort(this, locale);
    }

    /**
     * get the type object that describes the enum's metadata (like a Class object).
     *
     * @return the type object
     */
    public TypeRapidEnum getType() {
        return type;
    }

    /**
     * set the type object that describes the enum's metadata (like a Class object).
     * @param argType the type object
     */
    protected void setType(final TypeRapidEnum argType) {
        type = argType;
    }

    /**
     * internal static enum type with initialization.
     */
    private static TypeRapidEnum type = TypeRapidEnum.createInstance(InstallControl.class);
}

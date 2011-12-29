<?xml version="1.0" encoding="UTF-8"?>
<!--
 * Rapid Beans Framework: genQuantity.xsl
 *
 * Copyright (C) 2009 Martin Bluemel
 *
 * Creation Date: 07/01/2005
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copies of the GNU Lesser General Public License and the
 * GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
-->

<!--
    XSL stylesheet to generate a Java RapidBean quantity class out of a
    quantitytype XML model description.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="Java.xsl"/>
<xsl:import href="String.xsl"/>

<xsl:output method="text" version="1.0" encoding="iso-8859-1" indent="yes"/>

<xsl:param name="in"/>
<xsl:param name="out"/>
<xsl:param name="style"/>
<xsl:param name="codegen"/>
<xsl:param name="root"/>

<xsl:template match="//quantitytype">

<xsl:variable name="package">
	<xsl:call-template name="Java.extractPackage">
		<xsl:with-param name="fullname"><xsl:value-of select="@name"/></xsl:with-param>
	</xsl:call-template>
</xsl:variable>

<xsl:variable name="classname">
	<xsl:call-template name="Java.extractClassname">
		<xsl:with-param name="fullname"><xsl:value-of select="@name"/></xsl:with-param>
	</xsl:call-template>
</xsl:variable>

<xsl:call-template name="Java.fileHeader">
	<xsl:with-param name="classname"><xsl:value-of select="$classname"/></xsl:with-param>
	<xsl:with-param name="package"><xsl:value-of select="$package"/></xsl:with-param>
	<xsl:with-param name="in"><xsl:value-of select="$in"/></xsl:with-param>
	<xsl:with-param name="style"><xsl:value-of select="$style"/></xsl:with-param>
	<xsl:with-param name="incremental">false</xsl:with-param>
    <xsl:with-param name="root"><xsl:value-of select="$root"/></xsl:with-param>
	<xsl:with-param name="kindoftype">quantity</xsl:with-param>
</xsl:call-template>

<xsl:text>

package </xsl:text><xsl:value-of select="$package"/><xsl:text>;

import org.rapidbeans.core.basic.RapidEnum;
import org.rapidbeans.core.basic.RapidQuantity;
import org.rapidbeans.core.type.TypeRapidQuantity;

import java.math.BigDecimal;

<!-- ######################################################################
     # Class Header
     ###################################################################### -->
/**
 * EasyBiz quantity class: </xsl:text><xsl:value-of select="$classname"/><xsl:text>.
 * generated Java class
 * !!!Do not edit manually!!!
 **/
public</xsl:text>
<xsl:choose>
	<xsl:when test="@final = 'false'">
	</xsl:when>
	<xsl:otherwise>
		<xsl:text> final</xsl:text>
	</xsl:otherwise>
</xsl:choose>
<xsl:text> class </xsl:text><xsl:value-of select="$classname"/>
<xsl:text> extends RapidQuantity {

    /**
     * constructor out of a string.
     * @param s the string
     */
    public </xsl:text><xsl:value-of select="$classname"/><xsl:text>(final String s) {
        super(s);
    }

    /**
     * General constructor out of the magnitude as BigDecimal and a unit enum.
     * @param magnitude the number
     * @param unit the unit
     */
    public </xsl:text><xsl:value-of select="$classname"/><xsl:text>(final BigDecimal magnitude, final RapidEnum unit) {
        super(magnitude, (</xsl:text><xsl:value-of select="@unitenum"/><xsl:text>) unit);
    }

    /**
     * Special constructor out of the magnitude as BigDecimal and a </xsl:text><xsl:value-of select="@unitenum"/><xsl:text>.
     * @param magnitude the number
     * @param unit the unit
     */
    public </xsl:text><xsl:value-of select="$classname"/><xsl:text>(final BigDecimal magnitude, final </xsl:text><xsl:value-of select="@unitenum"/><xsl:text> unit) {
        super(magnitude, unit);
    }

    /**
     * the quantity's type (class variable).
     */
    private static TypeRapidQuantity type = TypeRapidQuantity.createInstance(</xsl:text><xsl:value-of select="$classname"/><xsl:text>.class);

    /**
     * @return the quantity's type
     */
    public TypeRapidQuantity getType() {
        return type;
    }
}
</xsl:text>
</xsl:template>

</xsl:stylesheet>

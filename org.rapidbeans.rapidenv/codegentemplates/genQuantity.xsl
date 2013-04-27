<?xml version="1.0" encoding="UTF-8"?>

<!--
 * Rapid Beans Framework: genQuantity.xsl
 *
 * Copyright (C) 2013 Martin Bluemel
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
<xsl:param name="indent"/>

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

<xsl:variable name="newline">
	<xsl:text>
</xsl:text>
</xsl:variable>

<xsl:variable name="indent1">
	<xsl:choose>
		<xsl:when test="$indent = ''">
			<xsl:text>	</xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$indent"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
<xsl:variable name="indent2">
	<xsl:choose>
		<xsl:when test="$indent = ''">
			<xsl:text>		</xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="concat($indent, $indent)"/>
		</xsl:otherwise>
	</xsl:choose>
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

<xsl:value-of select="$newline"/>
<xsl:text>package </xsl:text><xsl:value-of select="$package"/><xsl:text>;</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>
<xsl:text>import org.rapidbeans.core.basic.RapidEnum;</xsl:text><xsl:value-of select="$newline"/>
<xsl:text>import org.rapidbeans.core.basic.RapidQuantity;</xsl:text><xsl:value-of select="$newline"/>
<xsl:text>import org.rapidbeans.core.type.TypeRapidQuantity;</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>
<xsl:text>import java.math.BigDecimal;</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>

<!--
	######################################################################
	# Class Header
	######################################################################
-->
<xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
<xsl:text> * EasyBiz quantity class: </xsl:text><xsl:value-of select="$classname"/><xsl:text>.</xsl:text><xsl:value-of select="$newline"/>
<xsl:text> * generated Java class</xsl:text><xsl:value-of select="$newline"/>
<xsl:text> * !!!Do not edit manually!!!</xsl:text><xsl:value-of select="$newline"/>
<xsl:text> **/</xsl:text><xsl:value-of select="$newline"/>
<xsl:text>public</xsl:text>
<xsl:choose>
	<xsl:when test="@final = 'false'">
	</xsl:when>
	<xsl:otherwise>
		<xsl:text> final</xsl:text>
	</xsl:otherwise>
</xsl:choose>
<xsl:text> class </xsl:text><xsl:value-of select="$classname"/>
<xsl:text> extends RapidQuantity {</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>

<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * constructor out of a string.</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * @param s the string</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>public </xsl:text><xsl:value-of select="$classname"/><xsl:text>(final String s) {</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent2"/><xsl:text>super(s);</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>}</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>

<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * General constructor out of the magnitude as BigDecimal and a unit enum.</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * @param magnitude the number</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * @param unit the unit</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>public </xsl:text><xsl:value-of select="$classname"/><xsl:text>(final BigDecimal magnitude, final RapidEnum unit) {</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent2"/><xsl:text>super(magnitude, (</xsl:text><xsl:value-of select="@unitenum"/><xsl:text>) unit);</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>}</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>

<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * Special constructor out of the magnitude as BigDecimal and a </xsl:text><xsl:value-of select="@unitenum"/><xsl:text>.</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * @param magnitude the number</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * @param unit the unit</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>public </xsl:text><xsl:value-of select="$classname"/><xsl:text>(final BigDecimal magnitude, final </xsl:text><xsl:value-of select="@unitenum"/><xsl:text> unit) {</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent2"/><xsl:text>super(magnitude, unit);</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>}</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>

<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * the quantity's type (class variable).</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>private static TypeRapidQuantity type = TypeRapidQuantity.createInstance(</xsl:text><xsl:value-of select="$classname"/><xsl:text>.class);</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>

<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * @return the quantity's type</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>public TypeRapidQuantity getType() {</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent2"/><xsl:text>return type;</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>}</xsl:text><xsl:value-of select="$newline"/>
<xsl:text>}</xsl:text><xsl:value-of select="$newline"/>

</xsl:template>

</xsl:stylesheet>

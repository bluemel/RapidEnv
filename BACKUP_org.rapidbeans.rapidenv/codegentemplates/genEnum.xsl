<?xml version="1.0" encoding="UTF-8"?>

<!--
 * Rapid Beans Framework: genEnum.xsl
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
	XSL stylesheet to generate a Java RapidBean enum class out of an
	enumtype XML model description.

Grammar for a Rapid Beans XML Enum type definition

Element enumtype: (exactly one enclosing element mandatory)
		enclosing element for the whole type definition

	Attribute name: (mandatory) a string with the Enum's type name. 
		This is also the name of the generated class.

	Attribute generic: (optional) { 'true', 'false' } default 'false'
		generic enum types can exist without an associated
		(generated) Java class. Therefore there won't happen
		a code generation for these Enum types.

	Attribute genorderconstants: (optional) { 'true', 'false' } default 'true'
		switches the generation of integer order constants.

Element header: (optional) a header element defines the header of a 'value table'
		that means if in addition to an Enum element's name
		there are one ore more columns with values.
		Please note that the "value table feature" curently just
		works for code generated Enum types not for generic ones.

Element column: (at least one column is mandatory within header)
	Attribute name: the column's name
	Attribute type: the column's (Java) datatype.

Element enum:   (at least one enum is madatory within enumtype)
		most important to an Enum's type
		definition is the definition of the enumeration's elements 

	Attribute name:  (mandatory) each enumeration element has to have a name

	Attribute order: (optional) each enumeration element can have an explicit
		integer number as order. If no order is defined but
		the generation (genorderconstants = 'true').

	Element cell: (optional but one cell per defined column is mandatory if the
		header of a value table is defined)
	Attribute name:  (mandatory) each cells name must match the associated column's name.
	Attribute value: (mandatory) the value.

Example to demonstrate the grammar of a Rapid Beans XML Enum type definition
###############################################################################
<?xml version="1.0" encoding="ISO-8859-1" standalone="yes"?>

<enumtype name="Currency">
	<header>
		<column name="short2" type="String"/>
		<column name="short3" type="String"/>
	</header>
	<enum name="euro">
		<cell name="short2" value="EU"/>
		<cell name="short3" value="EUR"/>
	</enum>
	<enum name="deutschmark">
		<cell name="short2" value="DM"/>
		<cell name="short3" value="DEM"/>
	</enum>
</enumtype>
###############################################################################
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

<xsl:template match="//enumtype">

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
<xsl:variable name="indent3">
	<xsl:choose>
		<xsl:when test="$indent = ''">
			<xsl:text>			</xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="concat($indent, $indent, $indent)"/>
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
	<xsl:with-param name="kindoftype">enum</xsl:with-param>
</xsl:call-template>

<xsl:value-of select="$newline"/>
<xsl:text>package </xsl:text><xsl:value-of select="$package"/><xsl:text>;</xsl:text><xsl:value-of select="$newline"/>

<xsl:if test="$package !='org.rapidbeans.core.basic'">
	<xsl:text>import org.rapidbeans.core.basic.RapidEnum;</xsl:text><xsl:value-of select="$newline"/>
</xsl:if>
<xsl:if test="$package !='org.rapidbeans.core.common'">
	<xsl:text>import org.rapidbeans.core.common.RapidBeansLocale;</xsl:text><xsl:value-of select="$newline"/>
</xsl:if>
<xsl:if test="$package !='org.rapidbeans.core.type'">
	<xsl:text>import org.rapidbeans.core.type.TypeRapidEnum;</xsl:text><xsl:value-of select="$newline"/>
</xsl:if>

<!--
	######################################################################
	# Class Header
	######################################################################
-->
<xsl:value-of select="$newline"/>
<xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
<xsl:text> * Enum: </xsl:text><xsl:value-of select="$classname"/><xsl:text>.</xsl:text><xsl:value-of select="$newline"/>
<xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
<xsl:text>public</xsl:text>
<xsl:if test="final != 'false' and final != 'yes'">
	<xsl:text> final</xsl:text>
</xsl:if>
<xsl:text> enum </xsl:text><xsl:value-of select="$classname"/><xsl:text> implements RapidEnum {</xsl:text><xsl:value-of select="$newline"/>

<!--
	######################################################################
	# Enum Elements
	######################################################################
-->
<xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>// ------------------------------------------------------------------------</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>// enum elements</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>// -----------------------------------------------------------------------</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>

<xsl:for-each select="enum">

	<xsl:variable name="enumname">
		<xsl:value-of select="@name"/>
	</xsl:variable>

	<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text> * enum element </xsl:text><xsl:value-of select="@name"/><xsl:text>.</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:value-of select="@name"/>

<!--
	generate additional constructor argument values from cell values (for each column)
-->

	<xsl:if test="../header/column">
		<xsl:text>(</xsl:text>
		<xsl:for-each select="../header/column">
			<xsl:apply-templates select="//enumtype/enum">
				<xsl:with-param name="enumname"><xsl:value-of select="$enumname"/></xsl:with-param>
				<xsl:with-param name="columnname"><xsl:value-of select="@name"/></xsl:with-param>
				<xsl:with-param name="columntype"><xsl:value-of select="@type"/></xsl:with-param>
			</xsl:apply-templates>
			<xsl:if test="position() &lt; last()">
				<xsl:text>, </xsl:text>
			</xsl:if>
		</xsl:for-each>
		<xsl:text>)</xsl:text>
	</xsl:if>

	<xsl:choose>
		<xsl:when test="position() &lt; last()">
			<xsl:text>,</xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>;</xsl:text>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:value-of select="$newline"/>
	<xsl:value-of select="$newline"/>
</xsl:for-each>

<!--
	######################################################################
	# Enum Column Fields with getters
	######################################################################
-->

<xsl:if test = "count(header) > 0">
	<xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text>// ------------------------------------------------------------------------</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text>// column fields with getters</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text>// -----------------------------------------------------------------------</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$newline"/>
</xsl:if>

<xsl:for-each select="header/column">
	<xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text> * enum column </xsl:text><xsl:value-of select="@name"/><xsl:text>.</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text>private </xsl:text><xsl:value-of select="@type"/><xsl:text> </xsl:text><xsl:value-of select="@name"/><xsl:text>;</xsl:text><xsl:value-of select="$newline"/>

	<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text> * get enum column </xsl:text><xsl:value-of select="@name"/><xsl:text>.</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text> * @return enum column </xsl:text><xsl:value-of select="@name"/><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text>public </xsl:text><xsl:value-of select="@type"/><xsl:text> get</xsl:text>
	<xsl:value-of select="translate(substring(@name,1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
	<xsl:value-of select="substring(@name,2, string-length(@name) - 1)"/>
	<xsl:text>() {</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent2"/><xsl:text>return this.</xsl:text><xsl:value-of select="@name"/><xsl:text>;</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text>}</xsl:text><xsl:value-of select="$newline"/>
</xsl:for-each>


<!--
	######################################################################
	# Enum Element Constructor
	######################################################################
-->

<xsl:if test="header/column">
	<xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text> * The constructor for enum elements.</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text> * Since all enum elements are pre instantiated before the first use</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text> * of this enum class this constructor exclusively is used internally.</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text> *</xsl:text><xsl:value-of select="$newline"/>

	<xsl:for-each select="header/column">
		<xsl:value-of select="$indent1"/><xsl:text> * @param arg</xsl:text>
		<xsl:value-of select="translate(substring(@name,1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
		<xsl:value-of select="substring(@name,2, string-length(@name) - 1)"/>
		<xsl:text> the value for enum column </xsl:text><xsl:value-of select="@name"/><xsl:value-of select="$newline"/>
	</xsl:for-each>

	<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text>private </xsl:text>
	<xsl:value-of select="$classname"/>
	<xsl:text>(</xsl:text>
	<xsl:for-each select="header/column">
		<xsl:if test="position() > 1">
			<xsl:text>,</xsl:text><xsl:value-of select="$newline"/>
			<xsl:value-of select="$indent2"/>
		</xsl:if>
		<xsl:text>final </xsl:text>
		<xsl:value-of select="@type"/>
		<xsl:text> arg</xsl:text>
		<xsl:value-of select="translate(substring(@name,1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
		<xsl:value-of select="substring(@name,2, string-length(@name) - 1)"/>
	</xsl:for-each>
	<xsl:text>) {</xsl:text><xsl:value-of select="$newline"/>

	<xsl:for-each select="header/column">
		<xsl:value-of select="$indent2"/><xsl:text>this.</xsl:text><xsl:value-of select="@name"/><xsl:text> = </xsl:text>
		<xsl:text>arg</xsl:text>
		<xsl:value-of select="translate(substring(@name,1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
		<xsl:value-of select="substring(@name,2, string-length(@name) - 1)"/>
		<xsl:text>;</xsl:text><xsl:value-of select="$newline"/>
	</xsl:for-each>

	<xsl:value-of select="$newline"/>
	<xsl:value-of select="$indent1"/><xsl:text>}</xsl:text><xsl:value-of select="$newline"/>

</xsl:if>


<!--
	######################################################################
	# helper methods
	######################################################################
-->
<xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>// ------------------------------------------------------------------------</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>// fixed set of helper methods</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>// -----------------------------------------------------------------------</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>

<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * Get the description from the model (meta information - not UI).</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> *</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * @return this enumeration element's description.</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>public String getDescription() {</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent2"/><xsl:text>return type.getDescription(this);</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>}</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>

<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * @see org.rapidbeans.core.basic.Enum#toStringGui(org.rapidbeans.core.common.RapidBeansLocale)</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>public String toStringGui(final RapidBeansLocale locale) {</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent2"/><xsl:text>return type.getStringGui(this, locale);</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>}</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>

<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * @see org.rapidbeans.core.basic.Enum#toStringGuiShort(org.rapidbeans.core.common.RapidBeansLocale)</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>public String toStringGuiShort(final RapidBeansLocale locale) {</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent2"/><xsl:text>return type.getStringGuiShort(this, locale);</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>}</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>

<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * get the type object that describes the enum's metadata (like a Class object).</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> *</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * @return the type object</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>public TypeRapidEnum getType() {</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent2"/><xsl:text>return type;</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>}</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>

<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * set the type object that describes the enum's metadata (like a Class object).</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * @param argType the type object</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>protected void setType(final TypeRapidEnum argType) {</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent2"/><xsl:text>type = argType;</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>}</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>

<xsl:value-of select="$indent1"/><xsl:text>/**</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> * internal static enum type with initialization.</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text> */</xsl:text><xsl:value-of select="$newline"/>
<xsl:value-of select="$indent1"/><xsl:text>private static TypeRapidEnum type = TypeRapidEnum.createInstance(</xsl:text><xsl:value-of select="$classname"/><xsl:text>.class);</xsl:text><xsl:value-of select="$newline"/>

<!--
	######################################################################
	# Finish the class
	######################################################################
-->
<xsl:text>}</xsl:text><xsl:value-of select="$newline"/>

</xsl:template>

<!--
	######################################################################
	# Enum Element Constructor
	######################################################################
-->
<xsl:template match="//enumtype/enum">
	<xsl:param name="enumname"/>
	<xsl:param name="columnname"/>
	<xsl:param name="columntype"/>
	<xsl:if test="@name = $enumname">
		<xsl:for-each select="cell">
			<xsl:if test="@name = $columnname">
				<xsl:if test="$columntype = 'String'">
					<xsl:text>"</xsl:text>
				</xsl:if>
				<xsl:value-of select="@value"/>
				<xsl:if test="$columntype = 'String'">
					<xsl:text>"</xsl:text>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:if>
</xsl:template>

</xsl:stylesheet>

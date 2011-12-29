<?xml version="1.0" encoding="UTF-8"?>
<!--
 * Rapid Beans Framework: genBean.xsl
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
	XSL stylesheet to generate a Java RapidBean class out of a
	beantype XML model description.

	Parameters:

		in: the path of the file with the beantype XML model description.

		out: the path of the file with the generated Java class

		style: the path of this style sheet

		codegen: the code generation mode:
			{ <not defined>,  | 'split' | 'join' }

			<not defined>: generates a simple RapidBean class with type safe
				property (association) getters and setters.

			split: generates an abstract RapidBean base class named
				RapidBeanBase<class name> you simply derive the RapidBean
				class carrying the self implemented operations from.
				The derived class must implement all 3 rapid bean constructors
				and the static type field with a getter. 

			joint: generates a concrete RapidBean class.
				Self implemented operations have to be placed within protected
				regions. The usage of joint implies the usagage of the
				RapidBeans ant task xxslt. This task implements a merge mechanism
				that transfers the content of the protected regions to a newly
				generated version.

		All parameters are optional.
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

<xsl:template match="//beantype">

<xsl:variable name="package">
	<xsl:call-template name="Java.extractPackage">
		<xsl:with-param name="fullname"><xsl:value-of select="@name"/></xsl:with-param>
	</xsl:call-template>
</xsl:variable>

<xsl:variable name="classname">
    <xsl:if test="$codegen = 'split'">
        <xsl:text>RapidBeanBase</xsl:text>
    </xsl:if>
	<xsl:call-template name="Java.extractClassname">
		<xsl:with-param name="fullname"><xsl:value-of select="@name"/></xsl:with-param>
	</xsl:call-template>
</xsl:variable>

<xsl:call-template name="Java.fileHeader">
	<xsl:with-param name="classname"><xsl:value-of select="$classname"/></xsl:with-param>
	<xsl:with-param name="package"><xsl:value-of select="$package"/></xsl:with-param>
	<xsl:with-param name="in"><xsl:value-of select="$in"/></xsl:with-param>
	<xsl:with-param name="style"><xsl:value-of select="$style"/></xsl:with-param>
	<xsl:with-param name="codegen"><xsl:value-of select="$codegen"/></xsl:with-param>
    <xsl:with-param name="root"><xsl:value-of select="$root"/></xsl:with-param>
	<xsl:with-param name="kindoftype">bean</xsl:with-param>
</xsl:call-template>

<xsl:text>

package </xsl:text><xsl:value-of select="$package"/><xsl:text>;

</xsl:text>

<xsl:choose>
	<xsl:when test="@extends">
	</xsl:when>
	<xsl:otherwise>
		<xsl:if test="(not ($codegen)) or ($codegen != 'joint')">
		<xsl:text>
import org.rapidbeans.core.basic.RapidBean;</xsl:text>
		</xsl:if>
	</xsl:otherwise>
</xsl:choose>

<xsl:if test="(not ($codegen)) or ($codegen != 'joint')">
<xsl:text>
import org.rapidbeans.core.type.TypeRapidBean;</xsl:text>
</xsl:if>

<xsl:if test="property[(@type = 'association' or @type = 'associationend') and @maxmult = '1']">
<xsl:if test="(not ($codegen)) or ($codegen != 'joint')">
<xsl:text>
import org.rapidbeans.core.basic.Link;
import org.rapidbeans.core.basic.LinkFrozen;
import org.rapidbeans.core.exception.UnresolvedLinkException;
</xsl:text>
</xsl:if>
</xsl:if>

<xsl:if test="$codegen = 'joint'">
<xsl:text>

// BEGIN manual code section
// </xsl:text><xsl:value-of select="$classname"/>
<xsl:text>.import
// END manual code section</xsl:text>
</xsl:if>

<xsl:text>

<!-- ######################################################################
     # Class Header
     ###################################################################### -->
/**
 * Rapid Bean class: </xsl:text><xsl:value-of select="$classname"/><xsl:text>.
 * </xsl:text>
<xsl:choose>
	<xsl:when test="$codegen = 'joint'">
		<xsl:text>Partially </xsl:text>
	</xsl:when>
	<xsl:otherwise>
		<xsl:text>Completely </xsl:text>
	</xsl:otherwise>
</xsl:choose>
<xsl:text>generated Java class</xsl:text>
<xsl:choose>
	<xsl:when test="$codegen = 'joint'">
		<xsl:text>
 * !!!Do only edit manually in marked sections!!!</xsl:text>		
	</xsl:when>
	<xsl:otherwise>
		<xsl:text>
 * !!!Do not edit manually!!!</xsl:text>
	</xsl:otherwise>
</xsl:choose>

<xsl:text>
 **/
public</xsl:text>
<xsl:choose>
	<xsl:when test="@abstract = 'true' or $codegen = 'split'">
		<xsl:text> abstract</xsl:text>
	</xsl:when>
	<xsl:otherwise>
	</xsl:otherwise>
</xsl:choose>
<xsl:text> class </xsl:text>
<xsl:value-of select="$classname"/>
<xsl:text> extends </xsl:text>
<xsl:choose>
	<xsl:when test="@extends">
		<xsl:value-of select="@extends"/>
	</xsl:when>
	<xsl:otherwise>
		<xsl:text>RapidBean</xsl:text>
	</xsl:otherwise>
</xsl:choose>
<xsl:text> {</xsl:text>

<xsl:if test="$codegen = 'joint'">
<xsl:text>

    // BEGIN manual code section
    // </xsl:text><xsl:value-of select="$classname"/><xsl:text>.classBody
    // END manual code section</xsl:text>
</xsl:if>

<!-- ######################################################################
     # definintion of property references
     ###################################################################### -->
<xsl:text>
</xsl:text>

<xsl:for-each select="property">
<xsl:choose>
<xsl:when test="@depends">
</xsl:when>
<xsl:otherwise>
	<xsl:text>

    /**
     * property "</xsl:text><xsl:value-of select="@name"/><xsl:text>".
     */
    private org.rapidbeans.core.basic.Property</xsl:text>
	<xsl:choose>
		<xsl:when test="count(@type) = 0">
			<xsl:text>String</xsl:text>
		</xsl:when>
        <xsl:when test="@type = 'association' or @type = 'associationend'">
            <xsl:text>Associationend</xsl:text>
        </xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="translate(substring(@type,1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
			<xsl:value-of select="substring(@type,2, string-length(@type) - 1)"/>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:text> </xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>;</xsl:text>
</xsl:otherwise>
</xsl:choose>
</xsl:for-each>

<!-- ######################################################################
     # init method initProperties()
     ###################################################################### -->
<xsl:text>

    /**
     * property references initialization.
     */
    protected void initProperties() {</xsl:text>
<xsl:if test="@extends">
<xsl:text>
        super.initProperties();</xsl:text>
</xsl:if>
<xsl:for-each select="property">
<xsl:choose>
<xsl:when test="@depends">
</xsl:when>
<xsl:otherwise>
	<xsl:text>
        this.</xsl:text><xsl:value-of select="@name"/>
	<xsl:text> = (org.rapidbeans.core.basic.Property</xsl:text>
	<xsl:choose>
		<xsl:when test="count(@type) = 0">
			<xsl:text>String</xsl:text>
		</xsl:when>
		<xsl:when test="(@type = 'association' or @type = 'associationend')">
			<xsl:text>Associationend</xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="translate(substring(@type,1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
			<xsl:value-of select="substring(@type,2, string-length(@type) - 1)"/>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:text>)
            this.getProperty("</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>");</xsl:text>
</xsl:otherwise>
</xsl:choose>
</xsl:for-each>
<xsl:text>
    }</xsl:text>


<!-- ######################################################################
     # definintion of internal change event handler methods
     ###################################################################### -->
<xsl:text>
</xsl:text>

<xsl:for-each select="property">
<xsl:if test="@changeeventhandlerpre = 'true'">
<xsl:text>

    /**
     * pre change event handler for property "</xsl:text><xsl:value-of select="@name"/><xsl:text>".
     *
     * @param e the property change event.
     */
    private void </xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>BeforeChange(final PropertyertyChangeEvent e) {
        // BEGIN manual code section
        // </xsl:text>
	<xsl:value-of select="$classname"/>
	<xsl:text>.</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>BeforeChange()
        // END manual code section
    }</xsl:text>
</xsl:if>
</xsl:for-each>

<xsl:text>
</xsl:text>

<xsl:for-each select="property">
<xsl:if test="@changeeventhandlerpost = 'true'">
<xsl:text>

    /**
     * post change event handler for property "</xsl:text><xsl:value-of select="@name"/><xsl:text>".
     *
     * @param e the property change event.
     */
    private void </xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>Changed(final PropertyertyChangeEvent e) {
        // BEGIN manual code section
        // </xsl:text>
	<xsl:value-of select="$classname"/>
	<xsl:text>.</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>Changed()
        // END manual code section
    }</xsl:text>
</xsl:if>
</xsl:for-each>

<!-- ######################################################################
     # constructors
     ###################################################################### -->
<xsl:text>

    /**
     * default constructor.
     */
    public </xsl:text><xsl:value-of select="$classname"/><xsl:text>() {
        super();</xsl:text>

<xsl:if test="$codegen = 'joint'">
<xsl:text>
        // BEGIN manual code section
        // </xsl:text><xsl:value-of select="$classname"/>
<xsl:text>.</xsl:text><xsl:value-of select="$classname"/><xsl:text>()
        // END manual code section</xsl:text>
</xsl:if>

<xsl:text>

    }

    /**
     * constructor out of a string.
     * @param s the string
     */
    public </xsl:text><xsl:value-of select="$classname"/><xsl:text>(final String s) {
        super(s);</xsl:text>

<xsl:if test="$codegen = 'joint'">
<xsl:text>
        // BEGIN manual code section
        // </xsl:text><xsl:value-of select="$classname"/>
<xsl:text>.</xsl:text><xsl:value-of select="$classname"/><xsl:text>(String)
        // END manual code section</xsl:text>
</xsl:if>

<xsl:text>

    }

    /**
     * constructor out of a string array.
     * @param sa the string array
     */
    public </xsl:text><xsl:value-of select="$classname"/><xsl:text>(final String[] sa) {
        super(sa);</xsl:text>

<xsl:if test="$codegen = 'joint'">
<xsl:text>
        // BEGIN manual code section
        // </xsl:text><xsl:value-of select="$classname"/>
<xsl:text>.</xsl:text><xsl:value-of select="$classname"/><xsl:text>(String[])
        // END manual code section</xsl:text>
</xsl:if>

<xsl:text>
    }</xsl:text>

<!-- ######################################################################
     # Bean type defininition
     ###################################################################### -->

<xsl:if test="$codegen != 'split'">
	<xsl:text>

    /**
     * the bean's type (class variable).
     */</xsl:text>

	<xsl:if test="@abstract = 'true'">
		<xsl:text>
    @SuppressWarnings("unused")</xsl:text>
	</xsl:if>

	<xsl:text>
    private static TypeRapidBean type = TypeRapidBean.createInstance(</xsl:text>
	<xsl:value-of select="$classname"/>
	<xsl:text>.class);
	</xsl:text>
</xsl:if>

<xsl:text>

    /**
     * @return the Biz Bean's type
     */
    public </xsl:text>
<xsl:if test="@final = 'true' and (@abstract = 'true' or $codegen = 'split')">
	<xsl:message terminate="yes">ERROR a bean type can't be both: abstract and final</xsl:message>
</xsl:if>
<xsl:if test="@abstract = 'true' or $codegen = 'split'">
	<xsl:text>abstract </xsl:text>
</xsl:if>
<xsl:if test="@final = 'true'">
	<xsl:text>final </xsl:text>
</xsl:if>
<xsl:text>TypeRapidBean getType()</xsl:text>
<xsl:choose>
	<xsl:when test="@abstract = 'true' or $codegen = 'split'">
		<xsl:text>;</xsl:text>
	</xsl:when>
	<xsl:otherwise>
		<xsl:text> {
        return type;
    }</xsl:text>
	</xsl:otherwise>
</xsl:choose>

<!-- ######################################################################
     # Properties: getters and setters
     ###################################################################### -->
<xsl:for-each select="property">
	
	<!--
	########################################################
	# generate Getter
	########################################################
	-->
	<xsl:text>

    /**
     * @return value of Property '</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>'
     */</xsl:text>

	<xsl:if test="(@type = 'choice' and @multiple = 'true') or @type = 'association' or @type = 'associationend'">
		<xsl:text>
    @SuppressWarnings("unchecked")</xsl:text>
	</xsl:if>

	<xsl:text>
    public </xsl:text>

	<xsl:if test="@depends">
	<xsl:choose>			
	<xsl:when test="$codegen = 'split'">
		<xsl:text>abstract </xsl:text>
	</xsl:when>
	</xsl:choose>
	</xsl:if>

	<xsl:choose>

		<xsl:when test="@type = 'boolean'">
			<xsl:text>boolean</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'integer'">
			<xsl:text>int</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'string'">
			<xsl:text>String</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'url'">
			<xsl:text>java.net.URL</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'version'">
			<xsl:text>org.rapidbeans.core.util.Version</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'date'">
			<xsl:text>java.util.Date</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'file'">
			<xsl:text>java.io.File</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'quantity'">
			<xsl:choose>
				<xsl:when test="@quantity">
					<xsl:value-of select="@quantity"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>org.rapidbeans.core.basic.BBQuantity</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>

		<xsl:when test="@type = 'choice'">
			<xsl:choose>
				<xsl:when test="@multiple = 'true'">
					<xsl:text>java.util.List&lt;</xsl:text>
					<xsl:value-of select="@enum"/>
					<xsl:text>&gt;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@enum"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when> <!-- @type = 'choice' -->

		<xsl:when test="@type = 'association' or @type = 'associationend'">
			<xsl:choose>
				<xsl:when test="@maxmult = 1">
					<xsl:value-of select="@targettype"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>org.rapidbeans.core.common.ReadonlyListCollection&lt;</xsl:text>
					<xsl:value-of select="@targettype"/>
					<xsl:text>&gt;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when> <!-- @type = 'association(end)' -->

		<xsl:when test="@type">
			<xsl:text>ERROR!!! unknown type '</xsl:text>
			<xsl:value-of select="@type"/>
			<xsl:text>'</xsl:text>
		</xsl:when>

		<xsl:otherwise>
			<!-- if @type is not defined take default property type: "string" -->
			<xsl:text>String</xsl:text>
		</xsl:otherwise>

	</xsl:choose>

	<xsl:text> get</xsl:text>
	<xsl:choose>
		<xsl:when test="((@type = 'association' or @type = 'associationend') and @maxmult = '1' and @singular) or (@type = 'choice' and @multiple = 'false')">
			<xsl:call-template name="singularPropName"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="String.upperFirstCharacter">
				<xsl:with-param name="string"><xsl:value-of select="@name"/></xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>

	<xsl:text>()</xsl:text>

	<xsl:choose>			
	<xsl:when test="@depends and $codegen = 'split'">
		<xsl:text>;</xsl:text>
	</xsl:when>
	<xsl:otherwise>
		<xsl:text> {</xsl:text>		
	</xsl:otherwise>
	</xsl:choose>

	<xsl:choose>
	<xsl:when test="@depends">
		<xsl:choose>			
		<xsl:when test="$codegen = 'joint'">
		<xsl:text>
        // BEGIN manual code section
        // </xsl:text><xsl:value-of select="$classname"/>
		<xsl:text>.</xsl:text>
		<xsl:text> get</xsl:text>
		<xsl:choose>
		<xsl:when test="((@type = 'association' or @type = 'associationend') and @maxmult = '1' and @singular) or (@type = 'choice' and @multiple = 'false')">
			<xsl:call-template name="singularPropName"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="String.upperFirstCharacter">
				<xsl:with-param name="string"><xsl:value-of select="@name"/></xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
		</xsl:choose>
		<xsl:text>()
        // END manual code section
	}</xsl:text>
	</xsl:when>
	</xsl:choose>

	</xsl:when>

	<xsl:otherwise>
	<xsl:text>
        try {
            </xsl:text>

	<xsl:choose>

		<xsl:when test="@type = 'boolean'">
			<xsl:text>return ((org.rapidbeans.core.basic.PropertyBoolean) this.</xsl:text>
			<xsl:value-of select="@name"/>
			<xsl:text>).getValueBoolean();</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'integer'">
 			<xsl:text>return ((org.rapidbeans.core.basic.PropertyInteger) this.</xsl:text>
			<xsl:value-of select="@name"/>
			<xsl:text>).getValueInt();</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'string'">
			<xsl:text>return (String) this.</xsl:text>
			<xsl:value-of select="@name"/>
			<xsl:text>.getValue();</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'url'">
			<xsl:text>return (java.net.URL) this.</xsl:text>
			<xsl:value-of select="@name"/>
			<xsl:text>.getValue();</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'version'">
			<xsl:text>return (org.rapidbeans.core.util.Version) this.</xsl:text>
			<xsl:value-of select="@name"/>
			<xsl:text>.getValue();</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'date'">
			<xsl:text>return (java.util.Date) this.</xsl:text>
			<xsl:value-of select="@name"/>
			<xsl:text>.getValue();</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'file'">
			<xsl:text>return (java.io.File) this.</xsl:text>
			<xsl:value-of select="@name"/>
			<xsl:text>.getValue();</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'quantity'">
			<xsl:text>return (</xsl:text>
			<xsl:choose>
				<xsl:when test="@quantity">
					<xsl:value-of select="@quantity"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>org.rapidbeans.core.basic.BBQuantity</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>) this.</xsl:text>
			<xsl:value-of select="@name"/>
			<xsl:text>.getValue();</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'choice'">

			<xsl:choose>
				<xsl:when test="@multiple = 'true'">
					<xsl:text>return (java.util.List&lt;</xsl:text>
					<xsl:value-of select="@enum"/>
					<xsl:text>&gt;</xsl:text>
					<xsl:text>) </xsl:text>
					<xsl:text>this.</xsl:text>
					<xsl:value-of select="@name"/>
					<xsl:text>.getValue();</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>java.util.List&lt;?&gt; enumList = (java.util.List&lt;?&gt;) this.</xsl:text>
					<xsl:value-of select="@name"/>
					<xsl:text>.getValue();
            if (enumList == null || enumList.size() == 0) {
                return null;
            } else {
                return (</xsl:text>
					<xsl:value-of select="@enum"/>
					<xsl:text>) enumList.get(0);
            }</xsl:text>
				</xsl:otherwise>
			</xsl:choose>

		</xsl:when> <!-- @type = 'choice' -->

		<xsl:when test="@type = 'association' or @type = 'associationend'">
			<xsl:choose>

				<xsl:when test="@maxmult = 1">
					<xsl:text>org.rapidbeans.core.common.ReadonlyListCollection&lt;</xsl:text>
					<xsl:value-of select="@targettype"/>
					<xsl:text>&gt; col
                = (org.rapidbeans.core.common.ReadonlyListCollection&lt;</xsl:text>
					<xsl:value-of select="@targettype"/>
					<xsl:text>&gt;) this.</xsl:text>
					<xsl:value-of select="@name"/>
					<xsl:text>.getValue();
            if (col == null || col.size() == 0) {
                return null;
            } else {
                Link link = (Link) col.iterator().next();
                if (link instanceof LinkFrozen) {
                    throw new UnresolvedLinkException("unresolved link to \""
                            + "</xsl:text>
					<xsl:value-of select="@targettype"/>
					<xsl:text>"
                            + "\" \"" + link.getIdString() + "\"");
                } else {
                    return (</xsl:text>
					<xsl:value-of select="@targettype"/>
					<xsl:text>) col.iterator().next();
                }
            }</xsl:text>
				</xsl:when>

				<xsl:otherwise>
					<xsl:text>return (org.rapidbeans.core.common.ReadonlyListCollection&lt;</xsl:text>
					<xsl:value-of select="@targettype"/>
					<xsl:text>&gt;</xsl:text>
					<xsl:text>)
            this.</xsl:text>
					<xsl:value-of select="@name"/>
					<xsl:text>.getValue();</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when> <!-- @type = 'association(end)' -->

		<xsl:when test="@type">
			<xsl:text>ERROR!!! unknown type '</xsl:text>
			<xsl:value-of select="@type"/>
			<xsl:text>'</xsl:text>
		</xsl:when>

		<xsl:otherwise>
			<!-- if @type is not defined take default property type: "string" -->
			<xsl:text>return (String) this.</xsl:text>
			<xsl:value-of select="@name"/>
			<xsl:text>.getValue();</xsl:text>
		</xsl:otherwise>

	</xsl:choose>

	<xsl:text>
        } catch (NullPointerException e) {
            throw new org.rapidbeans.core.exception.PropNotInitializedException("</xsl:text><xsl:value-of select="@name"/><xsl:text>");
        }
    }</xsl:text>

	</xsl:otherwise>
	</xsl:choose>

	<!--
	########################################################
	# generate Setter
	########################################################
	-->

	<xsl:choose>
	<xsl:when test="@depends">
	</xsl:when>
	<xsl:otherwise>
	<xsl:text>

    /**
     * setter for Property '</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>'.
     * @param argValue value of Property '</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>' to set
     */</xsl:text>
	<xsl:if test="@type = 'choice' or @type = 'association' or @type = 'associationend'">
	</xsl:if>
	<xsl:text>
    public void set</xsl:text>
	<xsl:value-of select="translate(substring(@name,1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
	<xsl:value-of select="substring(@name,2, string-length(@name) - 1)"/>
	<xsl:text>(
        final </xsl:text>

	<xsl:choose>

		<xsl:when test="@type = 'boolean'">
			<xsl:text>boolean</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'integer'">
			<xsl:text>int</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'string'">
			<xsl:text>String</xsl:text>
		</xsl:when>
		
		<xsl:when test="@type = 'url'">
			<xsl:text>java.net.URL</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'version'">
			<xsl:text>org.rapidbeans.core.util.Version</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'date'">
			<xsl:text>java.util.Date</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'file'">
			<xsl:text>java.io.File</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'quantity'">
			<xsl:choose>
				<xsl:when test="@quantity">
					<xsl:value-of select="@quantity"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>org.rapidbeans.core.basic.BBQuantity</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>

		<xsl:when test="@type = 'choice'">
			<xsl:choose>
				<xsl:when test="@multiple = 'true'">
					<xsl:text>java.util.List&lt;</xsl:text>
					<xsl:value-of select="@enum"/>
					<xsl:text>&gt;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@enum"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when> <!-- @type = 'choice' -->

		<xsl:when test="@type = 'association' or @type = 'associationend'">
			<xsl:choose>
				<xsl:when test="@maxmult = 1">
					<xsl:value-of select="@targettype"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>java.util.Collection&lt;</xsl:text>
					<xsl:value-of select="@targettype"/>
					<xsl:text>&gt;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when> <!-- @type = 'association(end)' -->

		<xsl:when test="@type">
			<xsl:text>ERROR!!! unknown type '</xsl:text>
			<xsl:value-of select="@type"/>
			<xsl:text>'</xsl:text>
		</xsl:when>

		<xsl:otherwise>
			<!-- if @type is not defined take default property type: "string" -->
			<xsl:text>String</xsl:text>
		</xsl:otherwise>

	</xsl:choose>

	<xsl:text> argValue) {
        </xsl:text>

	<xsl:choose>

		<xsl:when test="@type = 'boolean'">
			<xsl:text>this.</xsl:text>
			<xsl:value-of select="@name"/>
			<xsl:text>.setValue(new Boolean(argValue));</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'integer'">
			<xsl:text>this.</xsl:text>
			<xsl:value-of select="@name"/>
			<xsl:text>.setValue(new Integer(argValue));</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'string' or @type = 'date' or @type = 'quantity' or @type = 'file' or @type = 'url' or @type = 'version'">
			<xsl:text>this.</xsl:text>
			<xsl:value-of select="@name"/>
			<xsl:text>.setValue(argValue);</xsl:text>
		</xsl:when>

		<xsl:when test="@type = 'choice'">
			<xsl:choose>
				<xsl:when test="@multiple = 'true'">
					<xsl:text>this.</xsl:text>
					<xsl:value-of select="@name"/>
					<xsl:text>.setValue(argValue);</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>java.util.List&lt;</xsl:text>
				<xsl:value-of select="@enum"/>
				<xsl:text>&gt; list =
            new java.util.ArrayList&lt;</xsl:text>
				<xsl:value-of select="@enum"/>
				<xsl:text>&gt;();
        list.add(argValue);
        this.</xsl:text>
					<xsl:value-of select="@name"/>
					<xsl:text>.setValue(list);</xsl:text>
					
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when> <!-- type = 'choice' -->

		<xsl:when test="@type = 'association' or @type = 'associationend'">

			<xsl:choose>
				<xsl:when test="@maxmult = 1">
					<xsl:text>this.</xsl:text>
					<xsl:value-of select="@name"/>
					<xsl:text>.setValue(argValue);</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>this.</xsl:text>
					<xsl:value-of select="@name"/>
					<xsl:text>.setValue(argValue);</xsl:text>
				</xsl:otherwise>
			</xsl:choose>

		</xsl:when> <!-- type = 'collection' -->

		<xsl:when test="@type">
			<xsl:text>ERROR!!! unknown type '</xsl:text>
			<xsl:value-of select="@type"/>
			<xsl:text>'</xsl:text>
		</xsl:when>

		<xsl:otherwise>
			<!-- if @type is not defined take default property type: "string" -->
			<xsl:text>this.</xsl:text>
			<xsl:value-of select="@name"/>
			<xsl:text>.setValue(argValue);</xsl:text>
		</xsl:otherwise>
	</xsl:choose>

<xsl:text>
    }</xsl:text>

	</xsl:otherwise>
	</xsl:choose>

	<!--
	########################################################
	# generate add method for collection properties with
	# multiplicity > 1
	########################################################
	-->
<xsl:if test="(@type = 'association' or @type = 'associationend') and ((count(@maxmult) = 0) or (@maxmult > 1))">

	<xsl:text>

    /**
     * add method for Property '</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>'.
     * @param bean the bean to add
     */</xsl:text>
	<xsl:text>
    public void add</xsl:text>
	<xsl:call-template name="singularPropName"/>
	<xsl:text>(final </xsl:text>
	<xsl:value-of select="@targettype"/>
	<xsl:text> bean) {
        </xsl:text>
	<xsl:text>((org.rapidbeans.core.basic.PropertyCollection) this.</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>).addLink(bean);</xsl:text>
	<xsl:text>
    }</xsl:text>
</xsl:if>

	<!--
	########################################################
	# generate remove method for collection properties with
	# multiplicity > 1
	########################################################
	-->
<xsl:if test="(@type = 'association' or @type = 'associationend') and ((count(@maxmult) = 0) or (@maxmult > 1))">

	<xsl:text>

    /**
     * remove method for Property '</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>'.
     * @param bean the bean to add
     */</xsl:text>
	<xsl:text>
    public void remove</xsl:text>
	<xsl:call-template name="singularPropName"/>
	<xsl:text>(final </xsl:text>
	<xsl:value-of select="@targettype"/>
	<xsl:text> bean) {
        </xsl:text>
	<xsl:text>((org.rapidbeans.core.basic.PropertyCollection) this.</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>).removeLink(bean);</xsl:text>
	<xsl:text>
    }</xsl:text>
</xsl:if>

</xsl:for-each> <!-- select="property" -->

<xsl:text>
}
</xsl:text>

</xsl:template>

<xsl:template name="singularPropName">
	<xsl:choose>
		<xsl:when test="@singular">
			<xsl:value-of select="translate(substring(@singular,1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
			<xsl:value-of select="substring(@singular, 2, string-length(@singular) - 1)"/>				
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="translate(substring(@name,1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
			<xsl:choose>
				<xsl:when test="substring(@name, string-length(@name), 1) = 's'">
					<xsl:value-of select="substring(@name, 2, string-length(@name) - 2)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="substring(@name, 2, string-length(@name) - 1)"/>				
				</xsl:otherwise>
			</xsl:choose>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>

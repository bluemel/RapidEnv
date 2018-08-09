<?xml version="1.0" encoding="UTF-8"?>

<!--
 * Rapid Beans Framework: genBean.xsl
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
	XSL stylesheet to generate a Java RapidBean class out of a
	beantype XML model description.

	Parameters:

		in: the path of the file with the beantype XML model description.

		out: the path of the file with the generated Java class

		style: the path of this style sheet

		codegen: the code generation mode:
			{ 'node' | 'simple' | 'split' | 'joint' }

			If codegen is given from outside it will overwrite
			the settings in the model or the default.

			none: no code generation. This template will stop processing in this case

			simple: generates a simple RapidBean class with type safe
				property (association) getters and setters.

			split: generates an abstract RapidBean base class named
				RapidBeanBase<class name> you simply derive the RapidBean
				class carrying the self implemented operations from.
				The derived class must implement all 3 rapid bean constructors
				and the static type field with a getter. 

			joint: generates a concrete RapidBean class.
				Self implemented operations have to be placed within protected
				regions. The usage of joint implies the usage of the
				RapidBeans ant task xxslt. This task implements a merge mechanism
				that transfers the content of the protected regions to a newly
				generated version.

		implementation: the RapidBean implementation { 'simple' | 'strict' }

			If implementation is given from outside it will overwrite
			the settings in the model or the default.

		indent: the indentation string.
			If not given TAB is taken as default.

		All parameters are optional.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="Java.xsl" />
	<xsl:import href="String.xsl" />

	<xsl:output method="text" version="1.0" encoding="iso-8859-1" indent="yes" />

	<xsl:param name="in" />
	<xsl:param name="out" />
	<xsl:param name="style" />
	<xsl:param name="root" />
	<xsl:param name="codegen" />
	<xsl:param name="implementation" />
	<xsl:param name="indent" />

	<xsl:template match="//beantype">

		<xsl:variable name="codegenmode">
			<xsl:choose>
				<xsl:when test="$codegen = ''">
					<xsl:choose>
						<xsl:when test="codegen/@mode">
					<!-- Read codegenmode from model -->
							<xsl:value-of select="codegen/@mode" />
						</xsl:when>
						<xsl:otherwise>
					<!--  take the default -->
							<xsl:value-of select="'simple'" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
			<!-- take the given parameter -->
					<xsl:value-of select="$codegen" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="$codegenmode = 'none'">
			<xsl:message terminate="yes">No code generation for RapidBean<xsl:value-of select="@name" />!</xsl:message>
		</xsl:if>

		<xsl:variable name="codegenimpl">
			<xsl:choose>
				<xsl:when test="$implementation = ''">
					<xsl:choose>
						<xsl:when test="codegen/@implementation">
					<!-- Read codegenimpl from model -->
							<xsl:call-template name="String.upperFirstCharacter">
								<xsl:with-param name="string">
									<xsl:value-of select="codegen/@impementation" />
								</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<!--  take the default -->
							<xsl:value-of select="'Simple'" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<!-- take the given parameter -->
					<xsl:call-template name="String.upperFirstCharacter">
						<xsl:with-param name="string">
							<xsl:value-of select="$implementation" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="package">
			<xsl:call-template name="Java.extractPackage">
				<xsl:with-param name="fullname">
					<xsl:value-of select="@name" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="classname">
			<xsl:if test="$codegenmode = 'split'">
				<xsl:text>RapidBeanBase</xsl:text>
			</xsl:if>
			<xsl:call-template name="Java.extractClassname">
				<xsl:with-param name="fullname">
					<xsl:value-of select="@name" />
				</xsl:with-param>
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
					<xsl:value-of select="$indent" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="indent2">
			<xsl:choose>
				<xsl:when test="$indent = ''">
					<xsl:text>		</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($indent, $indent)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="indent3">
			<xsl:choose>
				<xsl:when test="$indent = ''">
					<xsl:text>			</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($indent, $indent, $indent)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="indent4">
			<xsl:choose>
				<xsl:when test="$indent = ''">
					<xsl:text>				</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($indent, $indent, $indent, $indent)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="indent5">
			<xsl:choose>
				<xsl:when test="$indent = ''">
					<xsl:text>					</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($indent, $indent, $indent, $indent, $indent)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="indent6">
			<xsl:choose>
				<xsl:when test="$indent = ''">
					<xsl:text>						</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($indent, $indent, $indent, $indent, $indent, $indent)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="indent7">
			<xsl:choose>
				<xsl:when test="$indent = ''">
					<xsl:text>							</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($indent, $indent, $indent, $indent, $indent, $indent, $indent)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="Java.fileHeader">
			<xsl:with-param name="classname">
				<xsl:value-of select="$classname" />
			</xsl:with-param>
			<xsl:with-param name="package">
				<xsl:value-of select="$package" />
			</xsl:with-param>
			<xsl:with-param name="in">
				<xsl:value-of select="$in" />
			</xsl:with-param>
			<xsl:with-param name="style">
				<xsl:value-of select="$style" />
			</xsl:with-param>
			<xsl:with-param name="codegen">
				<xsl:value-of select="$codegenmode" />
			</xsl:with-param>
			<xsl:with-param name="root">
				<xsl:value-of select="$root" />
			</xsl:with-param>
			<xsl:with-param name="kindoftype">
				bean
			</xsl:with-param>
		</xsl:call-template>

		<xsl:value-of select="$newline" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text>;</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$newline" />


		<xsl:choose>
			<xsl:when test="@extends">
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="(not ($codegenmode)) or ($codegenmode != 'joint')">
					<xsl:text>import org.rapidbeans.core.basic.RapidBeanImpl</xsl:text>
					<xsl:value-of select="$codegenimpl" />
					<xsl:text>;</xsl:text>
					<xsl:value-of select="$newline" />
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>

		<xsl:if test="(not ($codegenmode)) or ($codegenmode != 'joint')">
			<xsl:text>import org.rapidbeans.core.type.TypeRapidBean;</xsl:text>
			<xsl:value-of select="$newline" />
		</xsl:if>

		<xsl:if test="$codegenimpl = 'Simple'">
			<xsl:if test="count(property) > 0">
				<xsl:text>import org.rapidbeans.core.basic.Property;</xsl:text>
				<xsl:value-of select="$newline" />
			</xsl:if>
			<xsl:choose>
				<xsl:when test="property[@type = 'choice' and @multiple = 'true']">
					<xsl:text>import org.rapidbeans.core.common.ReadonlyListCollection;</xsl:text>
					<xsl:value-of select="$newline" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="property[@type = 'association' or @type = 'associationend']">
						<xsl:choose>
							<xsl:when test="@maxmult = '1'">
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>import org.rapidbeans.core.common.ReadonlyListCollection;</xsl:text>
								<xsl:value-of select="$newline" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>

		<xsl:if test="$codegenimpl = 'Strict' and property[(@type = 'association' or @type = 'associationend') and @maxmult = '1']">
			<xsl:if test="(not ($codegenmode)) or ($codegenmode != 'joint')">
				<xsl:text>import org.rapidbeans.core.basic.Link;</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:text>import org.rapidbeans.core.basic.LinkFrozen;</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:text>import org.rapidbeans.core.exception.UnresolvedLinkException;</xsl:text>
				<xsl:value-of select="$newline" />
			</xsl:if>
		</xsl:if>

		<xsl:if test="$codegenmode = 'joint'">
			<xsl:value-of select="$newline" />
			<xsl:text>// BEGIN manual code section</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:text>// </xsl:text>
			<xsl:value-of select="$classname" />
			<xsl:text>.import</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:text>// END manual code section</xsl:text>
			<xsl:value-of select="$newline" />
		</xsl:if>

<!--
	######################################################################
	# Class Header
	######################################################################
-->
		<xsl:value-of select="$newline" />
		<xsl:text>/**</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:text> * Rapid Bean class: </xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text>.</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:text> * </xsl:text>

		<xsl:choose>
			<xsl:when test="$codegenmode = 'joint'">
				<xsl:text>Partially </xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>Completely </xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>generated Java class</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:choose>
			<xsl:when test="$codegenmode = 'joint'">
				<xsl:text> * !!!Do only edit manually in marked sections!!!</xsl:text>
				<xsl:value-of select="$newline" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:text> * !!!Do not edit manually!!!</xsl:text>
				<xsl:value-of select="$newline" />
			</xsl:otherwise>
		</xsl:choose>

		<xsl:text> **/</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:text>public</xsl:text>
		<xsl:choose>
			<xsl:when test="@abstract = 'true' or $codegenmode = 'split'">
				<xsl:text> abstract</xsl:text>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text> class </xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text> extends </xsl:text>
		<xsl:choose>
			<xsl:when test="@extends">
				<xsl:value-of select="@extends" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>RapidBeanImpl</xsl:text>
				<xsl:value-of select="$codegenimpl" />
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text> {</xsl:text>
		<xsl:value-of select="$newline" />


		<xsl:if test="$codegenmode = 'joint'">
			<xsl:value-of select="$indent1" />
			<xsl:text>// BEGIN manual code section</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent1" />
			<xsl:text>// </xsl:text>
			<xsl:value-of select="$classname" />
			<xsl:text>.classBody</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent1" />
			<xsl:text>// END manual code section</xsl:text>
			<xsl:value-of select="$newline" />
		</xsl:if>

<!--
	######################################################################
	# definintion of properties
	######################################################################
-->

		<xsl:for-each select="property">
			<xsl:choose>
				<xsl:when test="@depends">
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$newline" />
					<xsl:value-of select="$indent1" />
					<xsl:text>/**</xsl:text>
					<xsl:value-of select="$newline" />
					<xsl:value-of select="$indent1" />
					<xsl:text> * property "</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>".</xsl:text>
					<xsl:value-of select="$newline" />
					<xsl:value-of select="$indent1" />
					<xsl:text> */</xsl:text>
					<xsl:value-of select="$newline" />
					<xsl:choose>
						<xsl:when test="$codegenimpl = 'Simple'">
							<xsl:value-of select="$indent1" />
							<xsl:text>private </xsl:text>
							<xsl:call-template name="javaType">
								<xsl:with-param name="mode">
									<xsl:value-of select="'prop'" />
								</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise> <!-- $imp = Strict -->
							<xsl:value-of select="$indent1" />
							<xsl:text>private org.rapidbeans.core.basic.Property</xsl:text>
							<xsl:choose>
								<xsl:when test="count(@type) = 0">
									<xsl:text>String</xsl:text>
								</xsl:when>
								<xsl:when test="@type = 'association' or @type = 'associationend'">
									<xsl:text>Associationend</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="String.upperFirstCharacter">
										<xsl:with-param name="string">
											<xsl:value-of select="@type" />
										</xsl:with-param>
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:text> </xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>;</xsl:text>
					<xsl:value-of select="$newline" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>

<!--
	######################################################################
	# init method initProperties()
	######################################################################
-->
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text>/**</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> * property references initialization.</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> */</xsl:text>
		<xsl:value-of select="$newline" />

		<xsl:if test="($codegenimpl = 'Simple') and (property[@type = 'choice' and @multiple = 'true'] or property[@type = 'association' or @type = 'associationend'])">
			<xsl:value-of select="$indent1" />
			<xsl:text>@SuppressWarnings("unchecked")</xsl:text>
			<xsl:value-of select="$newline" />
		</xsl:if>

		<xsl:value-of select="$indent1" />
		<xsl:text>public void initProperties() {</xsl:text>
		<xsl:value-of select="$newline" />

		<xsl:if test="@extends">
			<xsl:value-of select="$indent2" />
			<xsl:text>super.initProperties();</xsl:text>
			<xsl:value-of select="$newline" />
		</xsl:if>
		<xsl:for-each select="property">
			<xsl:choose>
				<xsl:when test="@depends">
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$indent2" />
					<xsl:text>this.</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text> = </xsl:text>
					<xsl:choose>
						<xsl:when test="$codegenimpl = 'Simple'">
							<xsl:choose>
								<xsl:when test="@type = 'boolean'">
							<!-- this.xxx = (Boolean) getType().getPropertyType("xxx").getDefaultValue(); -->
									<xsl:text>(Boolean) getType().getPropertyType("</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>").getDefaultValue();</xsl:text>
								</xsl:when>
								<xsl:when test="@type = 'integer'">
							<!-- this.xxx = (Integer) getType().getPropertyType("xxx").getDefaultValue(); -->
									<xsl:text>(Integer) getType().getPropertyType("</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>").getDefaultValue();</xsl:text>
								</xsl:when>
								<xsl:when test="@type = 'choice' and @multiple = 'true'">
							<!-- this.xxx = (Date) getType().getPropertyType("xxx").getDefaultValue(); -->
									<xsl:text>(</xsl:text>
									<xsl:call-template name="javaType">
										<xsl:with-param name="mode">
											<xsl:value-of select="'prop'" />
										</xsl:with-param>
									</xsl:call-template>
									<xsl:text>) getType().getPropertyType("</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>").getDefaultValue();</xsl:text>
								</xsl:when>
								<xsl:when test="@type = 'choice'">
							<!--
								this.xxx = (java.util.List<Lang>)
									((java.util.List<?>) getType().getPropertyType("xxx").getDefaultValue()).get(0);
								this.xxx = getType().getPropertyType("osfamily").getDefaultValue() == null ? null :
									(org.rapidbeans.core.util.OperatingSystemFamily)
		              ((java.util.List<?>) getType().getPropertyType("osfamily").getDefaultValue()).get(0);
							 -->
							 		<xsl:text>getType().getPropertyType("</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>").getDefaultValue() == null ? null :</xsl:text>
									<xsl:value-of select="$newline" />
									<xsl:value-of select="$indent4" />
									<xsl:text>(</xsl:text>
									<xsl:call-template name="javaType">
										<xsl:with-param name="mode">
											<xsl:value-of select="'prop'" />
										</xsl:with-param>
									</xsl:call-template>
									<xsl:text>)</xsl:text>
									<xsl:value-of select="$newline" />
									<xsl:value-of select="$indent4" />
									<xsl:text>((java.util.List&lt;?&gt;) getType().getPropertyType("</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>").getDefaultValue()).get(0);</xsl:text>
								</xsl:when>
								<xsl:otherwise>
							<!-- this.xxx = (Date) getType().getPropertyType("xxx").getDefaultValue(); -->
									<xsl:text>(</xsl:text>
									<xsl:call-template name="javaType">
										<xsl:with-param name="mode">
											<xsl:value-of select="'prop'" />
										</xsl:with-param>
									</xsl:call-template>
									<xsl:text>) getType().getPropertyType("</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>").getDefaultValue();</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>(org.rapidbeans.core.basic.Property</xsl:text>
							<xsl:choose>
								<xsl:when test="count(@type) = 0">
									<xsl:text>String</xsl:text>
								</xsl:when>
								<xsl:when test="(@type = 'association' or @type = 'associationend')">
									<xsl:text>Associationend</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="translate(substring(@type,1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
									<xsl:value-of select="substring(@type,2, string-length(@type) - 1)" />
								</xsl:otherwise>
							</xsl:choose>
							<xsl:text>)</xsl:text>
							<xsl:value-of select="$newline" />
							<xsl:value-of select="$indent3" />
							<xsl:text>this.getProperty("</xsl:text>
							<xsl:value-of select="@name" />
							<xsl:text>");</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:value-of select="$newline" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>

		<xsl:value-of select="$indent1" />
		<xsl:text>}</xsl:text>
		<xsl:value-of select="$newline" />


<!--
	######################################################################
	# definintion of internal change event handler methods
	######################################################################
-->

		<xsl:for-each select="property">
			<xsl:if test="@changeeventhandlerpre = 'true'">
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text>/**</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> * pre change event handler for property "</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>".</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> *</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> * @param e the property change event.</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> */</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text>private void </xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>BeforeChange(final PropertyertyChangeEvent e) {</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent2" />
				<xsl:text>// BEGIN manual code section</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent2" />
				<xsl:text>// </xsl:text>
				<xsl:value-of select="$classname" />
				<xsl:text>.</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>BeforeChange()</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent2" />
				<xsl:text>// END manual code section</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text>}</xsl:text>
				<xsl:value-of select="$newline" />
			</xsl:if>
		</xsl:for-each>

		<xsl:for-each select="property">
			<xsl:if test="@changeeventhandlerpost = 'true'">
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text>/**</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> * post change event handler for property "</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>".</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> *</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> * @param e the property change event.</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> */</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text>private void </xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>Changed(final PropertyertyChangeEvent e) {</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent2" />
				<xsl:text>// BEGIN manual code section</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent2" />
				<xsl:text>// </xsl:text>
				<xsl:value-of select="$classname" />
				<xsl:text>.</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>Changed()</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent2" />
				<xsl:text>// END manual code section</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text>}</xsl:text>
				<xsl:value-of select="$newline" />
			</xsl:if>
		</xsl:for-each>

<!--
	######################################################################
	# constructors
	######################################################################
-->
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text>/**</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> * default constructor.</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> */</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text>public </xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text>() {</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent2" />
		<xsl:text>super();</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:if test="$codegenmode = 'joint'">
			<xsl:value-of select="$indent2" />
			<xsl:text>// BEGIN manual code section</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent2" />
			<xsl:text>// </xsl:text>
			<xsl:value-of select="$classname" />
			<xsl:text>.</xsl:text>
			<xsl:value-of select="$classname" />
			<xsl:text>()</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent2" />
			<xsl:text>// END manual code section</xsl:text>
			<xsl:value-of select="$newline" />
		</xsl:if>
		<xsl:value-of select="$indent1" />
		<xsl:text>}</xsl:text>
		<xsl:value-of select="$newline" />

		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text>/**</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> * constructor out of a string.</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> * @param s</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> *            the string</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> */</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text>public </xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text>(final String s) {</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent2" />
		<xsl:text>super(s);</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:if test="$codegenmode = 'joint'">
			<xsl:value-of select="$indent2" />
			<xsl:text>// BEGIN manual code section</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent2" />
			<xsl:text>// </xsl:text>
			<xsl:value-of select="$classname" />
			<xsl:text>.</xsl:text>
			<xsl:value-of select="$classname" />
			<xsl:text>(String)</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent2" />
			<xsl:text>// END manual code section</xsl:text>
			<xsl:value-of select="$newline" />
		</xsl:if>
		<xsl:value-of select="$indent1" />
		<xsl:text>}</xsl:text>
		<xsl:value-of select="$newline" />

		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text>/**</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> * constructor out of a string array.</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> * @param sa</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> *            the string array</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> */</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text>public </xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text>(final String[] sa) {</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent2" />
		<xsl:text>super(sa);</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:if test="$codegenmode = 'joint'">
			<xsl:value-of select="$indent2" />
			<xsl:text>// BEGIN manual code section</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent2" />
			<xsl:text>// </xsl:text>
			<xsl:value-of select="$classname" />
			<xsl:text>.</xsl:text>
			<xsl:value-of select="$classname" />
			<xsl:text>(String[])</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent2" />
			<xsl:text>// END manual code section</xsl:text>
			<xsl:value-of select="$newline" />
		</xsl:if>
		<xsl:value-of select="$indent1" />
		<xsl:text>}</xsl:text>
		<xsl:value-of select="$newline" />

<!--
	######################################################################
	# Bean type defininition
	######################################################################
-->

		<xsl:if test="$codegenmode != 'split'">
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent1" />
			<xsl:text>/**</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent1" />
			<xsl:text> * the bean's type (class variable).</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent1" />
			<xsl:text> */</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:if test="@abstract = 'true'">
				<xsl:value-of select="$indent1" />
				<xsl:text>@SuppressWarnings("unused")</xsl:text>
				<xsl:value-of select="$newline" />
			</xsl:if>
			<xsl:value-of select="$indent1" />
			<xsl:text>private static TypeRapidBean type = TypeRapidBean.createInstance(</xsl:text>
			<xsl:value-of select="$classname" />
			<xsl:text>.class);</xsl:text>
			<xsl:value-of select="$newline" />
		</xsl:if>

		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text>/**</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> * @return the Biz Bean's type</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text> */</xsl:text>
		<xsl:value-of select="$newline" />
		<xsl:value-of select="$indent1" />
		<xsl:text>public </xsl:text>
		<xsl:if test="@final = 'true' and (@abstract = 'true' or $codegenmode = 'split')">
			<xsl:message terminate="yes">ERROR a bean type can't be both: abstract and final</xsl:message>
		</xsl:if>
		<xsl:if test="@abstract = 'true' or $codegenmode = 'split'">
			<xsl:text>abstract </xsl:text>
		</xsl:if>
		<xsl:if test="@final = 'true'">
			<xsl:text>final </xsl:text>
		</xsl:if>
		<xsl:text>TypeRapidBean getType()</xsl:text>
		<xsl:choose>
			<xsl:when test="@abstract = 'true' or $codegenmode = 'split'">
				<xsl:text>;</xsl:text>
				<xsl:value-of select="$newline" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:text> {</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent2" />
				<xsl:text>return type;</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text>}</xsl:text>
				<xsl:value-of select="$newline" />
			</xsl:otherwise>
		</xsl:choose>

<!--
	######################################################################
	# Properties: getters and setters
	######################################################################
-->
		<xsl:for-each select="property">

<!--
	########################################################
	# generate Getter
	########################################################
-->
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent1" />
			<xsl:text>/**</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent1" />
			<xsl:text> * @return value of Property '</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>'</xsl:text>
			<xsl:value-of select="$newline" />
			<xsl:value-of select="$indent1" />
			<xsl:text> */</xsl:text>
			<xsl:value-of select="$newline" />

			<xsl:if test="(($codegenimpl = 'Strict') and ((@type = 'choice' and @multiple = 'true') or @type = 'association' or @type = 'associationend')) or (($codegenimpl = 'Simple') and (@type = 'association' or @type = 'associationend') and (count(@maxmult) = 0 or @maxmult > 1))">
				<xsl:value-of select="$indent1" />
				<xsl:text>@SuppressWarnings("unchecked")</xsl:text>
				<xsl:value-of select="$newline" />
			</xsl:if>

			<xsl:value-of select="$indent1" />
			<xsl:text>public </xsl:text>
			<xsl:if test="@depends">
				<xsl:choose>
					<xsl:when test="$codegenmode = 'split'">
						<xsl:text>abstract </xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:if>

			<xsl:call-template name="javaType">
				<xsl:with-param name="mode">
					<xsl:value-of select="'get'" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text> get</xsl:text>

			<xsl:choose>
				<xsl:when test="((@type = 'association' or @type = 'associationend') and @maxmult = '1' and @singular) or (@type = 'choice' and @multiple = 'false')">
					<xsl:call-template name="singularPropName" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="String.upperFirstCharacter">
						<xsl:with-param name="string">
							<xsl:value-of select="@name" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>

			<xsl:text>()</xsl:text>

			<xsl:choose>
				<xsl:when test="@depends and $codegenmode = 'split'">
					<xsl:text>;</xsl:text>
					<xsl:value-of select="$newline" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text> {</xsl:text>
					<xsl:value-of select="$newline" />
				</xsl:otherwise>
			</xsl:choose>

			<xsl:choose>

				<xsl:when test="@depends">

					<xsl:choose>
						<xsl:when test="$codegenmode = 'joint'">
							<xsl:value-of select="$indent2" />
							<xsl:text>// BEGIN manual code section</xsl:text>
							<xsl:value-of select="$newline" />
							<xsl:value-of select="$indent2" />
							<xsl:text>// </xsl:text>
							<xsl:value-of select="$classname" />
							<xsl:text>.</xsl:text>
							<xsl:text> get</xsl:text>
							<xsl:choose>
								<xsl:when
									test="((@type = 'association' or @type = 'associationend') and @maxmult = '1' and @singular) or (@type = 'choice' and @multiple = 'false')">
									<xsl:call-template name="singularPropName" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="String.upperFirstCharacter">
										<xsl:with-param name="string">
											<xsl:value-of select="@name" />
										</xsl:with-param>
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:text>()</xsl:text>
							<xsl:value-of select="$newline" />
							<xsl:value-of select="$indent2" />
							<xsl:text>// END manual code section</xsl:text>
							<xsl:value-of select="$newline" />
							<xsl:value-of select="$indent1" />
							<xsl:text>}</xsl:text>
							<xsl:value-of select="$newline" />
						</xsl:when> <!-- $codegenmode = 'joint' -->
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="$codegenimpl = 'Simple'">

							<xsl:choose> <!-- switch over types -->

								<xsl:when test="@type = 'boolean'">
									<xsl:value-of select="$indent2" />
									<xsl:text>if (this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text> == null) {</xsl:text>
									<xsl:value-of select="$newline" />

									<xsl:value-of select="$indent3" />
									<xsl:text>throw new org.rapidbeans.core.exception.RapidBeansRuntimeException(</xsl:text>
									<xsl:value-of select="$newline" />

									<xsl:value-of select="$indent5" />
									<xsl:text>"value for property \&quot;</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>\&quot; not defined");</xsl:text>

									<xsl:value-of select="$indent2" />
									<xsl:text>}</xsl:text>
									<xsl:value-of select="$newline" />

									<xsl:value-of select="$indent2" />
									<xsl:text>return this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.booleanValue();</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

								<xsl:when test="@type = 'integer'">
									<xsl:value-of select="$indent2" />
									<xsl:text>if (this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text> == null) {</xsl:text>
									<xsl:value-of select="$newline" />

									<xsl:value-of select="$indent3" />
									<xsl:text>throw new org.rapidbeans.core.exception.RapidBeansRuntimeException(</xsl:text>
									<xsl:value-of select="$newline" />

									<xsl:value-of select="$indent5" />
									<xsl:text>"value for property \&quot;</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>\&quot; not defined");</xsl:text>

									<xsl:value-of select="$indent2" />
									<xsl:text>}</xsl:text>
									<xsl:value-of select="$newline" />

									<xsl:value-of select="$indent2" />
									<xsl:text>return this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.intValue();</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

								<xsl:when test="@type = 'string' or @type = 'integer' or @type = 'file' or @type = 'quantity' or @type = 'url' or @type = 'version'">
									<xsl:value-of select="$indent2" />
									<xsl:text>return this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>;</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

<!--
								<xsl:when test="@type = 'url'">
									<xsl:message terminate="yes">code generation getter for url property not yet implemented</xsl:message>
								</xsl:when>

								<xsl:when test="@type = 'version'">
									<xsl:message terminate="yes">code generation getter for version property not yet implemented</xsl:message>
								</xsl:when>
-->

								<xsl:when test="@type = 'date'">
									<xsl:value-of select="$indent2" />
									<xsl:text>if (this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text> == null) {</xsl:text>
									<xsl:value-of select="$newline" />
									<xsl:value-of select="$indent3" />
									<xsl:text>return null;</xsl:text>
									<xsl:value-of select="$newline" />
									<xsl:value-of select="$indent2" />
									<xsl:text>} else {</xsl:text>
									<xsl:value-of select="$newline" />
									<xsl:value-of select="$indent3" />
									<xsl:text>return new java.util.Date(this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.getTime());</xsl:text>
									<xsl:value-of select="$newline" />
									<xsl:value-of select="$indent2" />
									<xsl:text>}</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

<!--
								<xsl:when test="@type = 'quantity'">
									<xsl:message terminate="yes">code generation getter for quantity property not yet implemented</xsl:message>
								</xsl:when>
-->

								<xsl:when test="@type = 'choice'">
									<xsl:choose>
										<xsl:when test="@multiple = 'true'">
											<xsl:value-of select="$indent2" />
											<xsl:text>if (this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text> == null) {</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent3" />
											<xsl:text>return null;</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent2" />
											<xsl:text>} else {</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent3" />
											<xsl:text>return new ReadonlyListCollection&lt;</xsl:text>
											<xsl:value-of select="@enum"/>
											<xsl:text>&gt;(this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>, getType().getPropertyType("</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>"));</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent2" />
											<xsl:text>}</xsl:text>
											<xsl:value-of select="$newline" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="$indent2" />
											<xsl:text>return this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>;</xsl:text>
											<xsl:value-of select="$newline" />
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when> <!-- @type = 'choice' -->

								<xsl:when test="@type = 'association' or @type = 'associationend'">
									<xsl:choose>
										<xsl:when test="@maxmult = '1'">
											<xsl:value-of select="$indent2" />
											<xsl:text>return (</xsl:text>
											<xsl:value-of select="@targettype" />
											<xsl:text>)this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>;</xsl:text>
											<xsl:value-of select="$newline" />
										</xsl:when> <!-- maxmult = '1' -->
										<xsl:otherwise>
											<xsl:value-of select="$indent2" />
											<xsl:text>if (this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text> == null) {</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent3" />
											<xsl:text>return null;</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent2" />
											<xsl:text>} else {</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent3" />
											<xsl:text>return new ReadonlyListCollection&lt;</xsl:text>
											<xsl:value-of select="@targettype"/>
											<xsl:text>&gt;((java.util.Collection&lt;</xsl:text>
											<xsl:value-of select="@targettype"/>
											<xsl:text>&gt;) ((Object) this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>)</xsl:text>
											<xsl:text>, getType().getPropertyType("</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>"));</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent2" />
											<xsl:text>}</xsl:text>
											<xsl:value-of select="$newline" />
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when> <!-- @type = 'association(end)' -->

								<xsl:when test="@type">
									<xsl:message terminate="yes">ERROR: unknown type '<xsl:value-of select="@type" />'</xsl:message>
									<!--
										<xsl:text>ERROR!!! unknown type '</xsl:text>
										<xsl:value-of select="@type" />
										<xsl:text>'</xsl:text>
									-->
								</xsl:when>

								<xsl:otherwise>
									<xsl:value-of select="$indent2" />
									<xsl:text>return this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>;</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:otherwise>

							</xsl:choose> <!-- switch over types -->

							<xsl:value-of select="$indent1" />
							<xsl:text>}</xsl:text>
							<xsl:value-of select="$newline" />

						</xsl:when> <!-- Simple implementation -->

						<xsl:otherwise> <!-- Strict implementation -->

							<xsl:value-of select="$indent2" />
							<xsl:text>try {</xsl:text>
							<xsl:value-of select="$newline" />
							<xsl:value-of select="$indent3" />

							<xsl:choose>

								<xsl:when test="@type = 'boolean'">
									<xsl:text>return ((org.rapidbeans.core.basic.PropertyBoolean) this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>).getValueBoolean();</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

								<xsl:when test="@type = 'integer'">
									<xsl:text>return ((org.rapidbeans.core.basic.PropertyInteger) this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>).getValueInt();</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

								<xsl:when test="@type = 'string'">
									<xsl:text>return (String) this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.getValue();</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

								<xsl:when test="@type = 'url'">
									<xsl:text>return (java.net.URL) this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.getValue();</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

								<xsl:when test="@type = 'version'">
									<xsl:text>return (org.rapidbeans.core.util.Version) this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.getValue();</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

								<xsl:when test="@type = 'date'">
									<xsl:text>return (java.util.Date) this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.getValue();</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

								<xsl:when test="@type = 'file'">
									<xsl:text>return (java.io.File) this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.getValue();</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

								<xsl:when test="@type = 'quantity'">
									<xsl:text>return (</xsl:text>
									<xsl:choose>
										<xsl:when test="@quantity">
											<xsl:value-of select="@quantity" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:text>org.rapidbeans.core.basic.BBQuantity</xsl:text>
										</xsl:otherwise>
									</xsl:choose>
									<xsl:text>) this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.getValue();</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

								<xsl:when test="@type = 'choice'">

									<xsl:choose>
										<xsl:when test="@multiple = 'true'">
											<xsl:text>return (java.util.List&lt;</xsl:text>
											<xsl:value-of select="@enum" />
											<xsl:text>&gt;</xsl:text>
											<xsl:text>) </xsl:text>
											<xsl:text>this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>.getValue();</xsl:text>
											<xsl:value-of select="$newline" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:text>java.util.List&lt;?&gt; enumList = (java.util.List&lt;?&gt;) this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>.getValue();</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent3" />
											<xsl:text>if (enumList == null || enumList.size() == 0) {</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent4" />
											<xsl:text>return null;</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent3" />
											<xsl:text>} else {</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent4" />
											<xsl:text>return (</xsl:text>
											<xsl:value-of select="@enum" />
											<xsl:text>) enumList.get(0);</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent3" />
											<xsl:text>}</xsl:text>
											<xsl:value-of select="$newline" />
										</xsl:otherwise>
									</xsl:choose>

								</xsl:when> <!-- @type = 'choice' -->

								<xsl:when test="@type = 'association' or @type = 'associationend'">
									<xsl:choose>
										<xsl:when test="@maxmult = 1">
											<xsl:text>org.rapidbeans.core.common.ReadonlyListCollection&lt;</xsl:text>
											<xsl:value-of select="@targettype" />
											<xsl:text>&gt; col</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent4" />
											<xsl:text>= (org.rapidbeans.core.common.ReadonlyListCollection&lt;</xsl:text>
											<xsl:value-of select="@targettype" />
											<xsl:text>&gt;) this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>.getValue();</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent3" />
											<xsl:text>if (col == null || col.size() == 0) {</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent4" />
											<xsl:text>return null;</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent3" />
											<xsl:text>} else {</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent4" />
											<xsl:text>Link link = (Link) col.iterator().next();</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent4" />
											<xsl:text>if (link instanceof LinkFrozen) {</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent5" />
											<xsl:text>throw new UnresolvedLinkException("unresolved link to \""</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent7" />
											<xsl:text>+ "</xsl:text>
											<xsl:value-of select="@targettype" />
											<xsl:text>"</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent7" />
											<xsl:text>+ "\" \"" + link.getIdString() + "\"");</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent4" />
											<xsl:text>} else {</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent5" />
											<xsl:text>return (</xsl:text>
											<xsl:value-of select="@targettype" />
											<xsl:text>) col.iterator().next();</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent4" />
											<xsl:text>}</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent3" />
											<xsl:text>}</xsl:text>
											<xsl:value-of select="$newline" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:text>return (org.rapidbeans.core.common.ReadonlyListCollection&lt;</xsl:text>
											<xsl:value-of select="@targettype" />
											<xsl:text>&gt;</xsl:text>
											<xsl:text>)</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent3" />
											<xsl:text>this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>.getValue();</xsl:text>
											<xsl:value-of select="$newline" />
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when> <!-- @type = 'association(end)' -->

								<xsl:when test="@type">
									<xsl:message terminate="yes">ERROR: unknown type '<xsl:value-of select="@type" />'</xsl:message>
									<!--
										<xsl:text>ERROR!!! unknown type '</xsl:text>
										<xsl:value-of select="@type" />
										<xsl:text>'</xsl:text>
									-->
								</xsl:when>

								<xsl:otherwise>
									<!-- if @type is not defined take default property type: "string" -->
									<xsl:text>return (String) this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.getValue();</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:otherwise>

							</xsl:choose>

							<xsl:value-of select="$indent2" />
							<xsl:text>} catch (NullPointerException e) {</xsl:text>
							<xsl:value-of select="$newline" />
							<xsl:value-of select="$indent3" />
							<xsl:text>throw new org.rapidbeans.core.exception.PropNotInitializedException("</xsl:text>
							<xsl:value-of select="@name" />
							<xsl:text>");</xsl:text>
							<xsl:value-of select="$newline" />
							<xsl:value-of select="$indent2" />
							<xsl:text>}</xsl:text>
							<xsl:value-of select="$newline" />
							<xsl:value-of select="$indent1" />
							<xsl:text>}</xsl:text>
							<xsl:value-of select="$newline" />

						</xsl:otherwise> <!-- Strict implementation -->

					</xsl:choose>

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

					<xsl:value-of select="$newline" />
					<xsl:value-of select="$indent1" />
					<xsl:text>/**</xsl:text>
					<xsl:value-of select="$newline" />
					<xsl:value-of select="$indent1" />
					<xsl:text> * setter for Property '</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>'.</xsl:text>
					<xsl:value-of select="$newline" />
					<xsl:value-of select="$indent1" />
					<xsl:text> * @param argValue</xsl:text>
					<xsl:value-of select="$newline" />
					<xsl:value-of select="$indent1" />
					<xsl:text> *            value of Property '</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>' to set</xsl:text>
					<xsl:value-of select="$newline" />
					<xsl:value-of select="$indent1" />
					<xsl:text> */</xsl:text>
					<xsl:value-of select="$newline" />
					<xsl:value-of select="$indent1" />
					<xsl:text>public void set</xsl:text>
					<xsl:value-of select="translate(substring(@name,1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
					<xsl:value-of select="substring(@name,2, string-length(@name) - 1)" />
					<xsl:text>(</xsl:text>
					<xsl:text>final </xsl:text>
					<xsl:call-template name="javaType">
						<xsl:with-param name="mode">
							<xsl:value-of select="'set'" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:text> argValue) {</xsl:text>
					<xsl:value-of select="$newline" />

					<xsl:choose>

						<xsl:when test="$codegenimpl = 'Simple'"> <!-- implementation = Simple -->
							<xsl:value-of select="$indent2" />

							<xsl:choose> <!-- switch over types -->

								<xsl:when test="@type = 'boolean' or @type = 'integer' or @type = 'string' or @type = 'date' or @type = 'quantity' or @type = 'file' or @type = 'url' or @type = 'version' or @type = 'choice' or @type = 'association' or @type = 'associationend'">
									<xsl:text>Property.createInstance(getType().getPropertyType("</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>"), this).setValue(argValue);</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>
								<xsl:when test="@type">
									<xsl:message terminate="yes">ERROR: unknown type '<xsl:value-of select="@type" />'</xsl:message>
									<!--
										<xsl:text>ERROR!!! unknown type '</xsl:text>
										<xsl:value-of select="@type" />
										<xsl:text>'</xsl:text>
									-->
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>Property.createInstance(getType().getPropertyType("</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>"), this).setValue(argValue);</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:otherwise>

							</xsl:choose> <!-- switch over types -->

						</xsl:when> <!-- implementation = Simple -->

						<xsl:otherwise> <!-- implementation = Strict -->
							<xsl:value-of select="$indent2" />

							<xsl:choose> <!-- switch over types -->

								<xsl:when test="@type = 'boolean'">
									<xsl:text>this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.setValue(new Boolean(argValue));</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

								<xsl:when test="@type = 'integer'">
									<xsl:text>this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.setValue(new Integer(argValue));</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

								<xsl:when test="@type = 'string' or @type = 'date' or @type = 'quantity' or @type = 'file' or @type = 'url' or @type = 'version'">
									<xsl:text>this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.setValue(argValue);</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:when>

								<xsl:when test="@type = 'choice'">
									<xsl:choose>
										<xsl:when test="@multiple = 'true'">
											<xsl:text>this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>.setValue(argValue);</xsl:text>
											<xsl:value-of select="$newline" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:text>java.util.List&lt;</xsl:text>
											<xsl:value-of select="@enum" />
											<xsl:text>&gt; list =</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent3" />
											<xsl:text>new java.util.ArrayList&lt;</xsl:text>
											<xsl:value-of select="@enum" />
											<xsl:text>&gt;();</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent2" />
											<xsl:text>list.add(argValue);</xsl:text>
											<xsl:value-of select="$newline" />
											<xsl:value-of select="$indent2" />
											<xsl:text>this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>.setValue(list);</xsl:text>
											<xsl:value-of select="$newline" />
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when> <!-- type = 'choice' -->

								<xsl:when test="@type = 'association' or @type = 'associationend'">

									<xsl:choose>
										<xsl:when test="@maxmult = 1">
											<xsl:text>this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>.setValue(argValue);</xsl:text>
											<xsl:value-of select="$newline" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:text>this.</xsl:text>
											<xsl:value-of select="@name" />
											<xsl:text>.setValue(argValue);</xsl:text>
											<xsl:value-of select="$newline" />
										</xsl:otherwise>
									</xsl:choose>

								</xsl:when> <!-- type = 'collection' -->

								<xsl:when test="@type">
									<xsl:message terminate="yes">ERROR: unknown type '<xsl:value-of select="@type" />'</xsl:message>
									<!--
										<xsl:text>ERROR!!! unknown type '</xsl:text>
										<xsl:value-of select="@type" />
										<xsl:text>'</xsl:text>
									-->
								</xsl:when>

								<xsl:otherwise>
						<!-- if @type is not defined take default property type: "string" -->
									<xsl:text>this.</xsl:text>
									<xsl:value-of select="@name" />
									<xsl:text>.setValue(argValue);</xsl:text>
									<xsl:value-of select="$newline" />
								</xsl:otherwise>

							</xsl:choose> <!-- switch over types -->

						</xsl:otherwise> <!-- implementation = Strict -->

					</xsl:choose>

					<xsl:value-of select="$indent1" />
					<xsl:text>}</xsl:text>
					<xsl:value-of select="$newline" />

				</xsl:otherwise>

			</xsl:choose>

<!--
	########################################################
	# generate add method for collection properties with
	# multiplicity > 1
	########################################################
-->
			<xsl:if test="(@type = 'association' or @type = 'associationend') and ((count(@maxmult) = 0) or (@maxmult > 1))">
				<xsl:value-of select="$indent1" />
				<xsl:text>/**</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> * add method for Property '</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>'.</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> * @param bean</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> *            the bean to add</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> */</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text>public void add</xsl:text>
				<xsl:call-template name="singularPropName" />
				<xsl:text>(final </xsl:text>
				<xsl:value-of select="@targettype" />
				<xsl:text> bean) {</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent2" />

				<xsl:choose>
					<xsl:when test="$codegenimpl = 'Simple'"> <!-- implementation = Simple -->
						<xsl:text>((org.rapidbeans.core.basic.PropertyCollection) Property.createInstance(getType().getPropertyType("</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text>"), this)).addLink(bean);</xsl:text>
						<xsl:value-of select="$newline" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>((org.rapidbeans.core.basic.PropertyCollection) this.</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text>).addLink(bean);</xsl:text>
						<xsl:value-of select="$newline" />
					</xsl:otherwise>
				</xsl:choose>

				<xsl:value-of select="$indent1" />
				<xsl:text>}</xsl:text>
				<xsl:value-of select="$newline" />

			</xsl:if>

<!--
	########################################################
	# generate remove method for collection properties with
	# multiplicity > 1
	########################################################
-->
			<xsl:if test="(@type = 'association' or @type = 'associationend') and ((count(@maxmult) = 0) or (@maxmult > 1))">
				<xsl:value-of select="$indent1" />
				<xsl:text>/**</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> * remove method for Property '</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>'.</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> * @param bean</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> *            the bean to remove</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text> */</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent1" />
				<xsl:text>public void remove</xsl:text>
				<xsl:call-template name="singularPropName" />
				<xsl:text>(final </xsl:text>
				<xsl:value-of select="@targettype" />
				<xsl:text> bean) {</xsl:text>
				<xsl:value-of select="$newline" />
				<xsl:value-of select="$indent2" />

				<xsl:choose>
					<xsl:when test="$codegenimpl = 'Simple'"> <!-- implementation = Simple -->
						<xsl:text>((org.rapidbeans.core.basic.PropertyCollection) Property.createInstance(getType().getPropertyType("</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text>"), this)).removeLink(bean);</xsl:text>
						<xsl:value-of select="$newline" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>((org.rapidbeans.core.basic.PropertyCollection) this.</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text>).removeLink(bean);</xsl:text>
						<xsl:value-of select="$newline" />
					</xsl:otherwise>
				</xsl:choose>

				<xsl:value-of select="$indent1" />
				<xsl:text>}</xsl:text>
				<xsl:value-of select="$newline" />
			</xsl:if>

		</xsl:for-each> <!-- select="property" -->

		<xsl:text>}</xsl:text>
		<xsl:value-of select="$newline" />

	</xsl:template>

	<xsl:template name="singularPropName">
		<xsl:choose>
			<xsl:when test="@singular">
				<xsl:value-of select="translate(substring(@singular,1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
				<xsl:value-of select="substring(@singular, 2, string-length(@singular) - 1)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="translate(substring(@name,1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
				<xsl:choose>
					<xsl:when test="substring(@name, string-length(@name), 1) = 's'">
						<xsl:value-of select="substring(@name, 2, string-length(@name) - 2)" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="substring(@name, 2, string-length(@name) - 1)" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="javaType">
		<xsl:param name="mode" />

		<xsl:choose>

			<xsl:when test="@type = 'boolean'">
				<xsl:choose>
					<xsl:when test="$mode = 'prop'">
						<xsl:text>Boolean</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>boolean</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>

			<xsl:when test="@type = 'integer'">
				<xsl:choose>
					<xsl:when test="$mode = 'prop'">
						<xsl:text>Integer</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>int</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
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
						<xsl:value-of select="@quantity" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>org.rapidbeans.core.basic.RapidQuantity</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>

			<xsl:when test="@type = 'choice'">
				<xsl:choose>
					<xsl:when test="@multiple = 'true'">
						<xsl:text>java.util.List&lt;</xsl:text>
						<xsl:value-of select="@enum" />
						<xsl:text>&gt;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@enum" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when> <!-- @type = 'choice' -->

			<xsl:when test="@type = 'association' or @type = 'associationend'">
				<xsl:choose>
					<xsl:when test="@maxmult = 1">
						<xsl:choose>
							<xsl:when test="$mode = 'prop'">
								<xsl:text>org.rapidbeans.core.basic.Link</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@targettype" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$mode = 'set'">
								<xsl:text>java.util.Collection&lt;</xsl:text>
							</xsl:when>
							<xsl:when test="$mode = 'prop'">
								<xsl:choose>
									<xsl:when test="@flavor = 'bag'">
										<xsl:choose>
											<xsl:when test="@collectionclass">
												<xsl:value-of select="@collectionclass"/>
												<xsl:text>&lt;</xsl:text>
											</xsl:when>
											<xsl:otherwise>
												<xsl:text>java.util.ArrayList&lt;</xsl:text>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise> <!-- flavor is Set per default -->
										<xsl:choose>
											<xsl:when test="@collectionclass">
												<xsl:value-of select="@collectionclass"/>
												<xsl:text>&lt;</xsl:text>
											</xsl:when>
											<xsl:otherwise>
												<xsl:text>java.util.LinkedHashSet&lt;</xsl:text>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>org.rapidbeans.core.common.ReadonlyListCollection&lt;</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:choose>
							<xsl:when test="$mode = 'prop'">
								<xsl:text>org.rapidbeans.core.basic.Link</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@targettype" />
							</xsl:otherwise>
						</xsl:choose>
						<xsl:text>&gt;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when> <!-- @type = 'association(end)' -->

			<xsl:when test="@type">
				<xsl:message terminate="yes">ERROR: unknown type '<xsl:value-of select="@type" />'</xsl:message>
				<!--
					<xsl:text>ERROR!!! unknown type '</xsl:text>
					<xsl:value-of select="@type" />
					<xsl:text>'</xsl:text>
				-->
			</xsl:when>

			<xsl:otherwise>
				<!-- if @type is not defined take default property type: "string" -->
				<xsl:text>String</xsl:text>
			</xsl:otherwise>

		</xsl:choose>

	</xsl:template>

</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>
<!--
 * RapidEnv: RapidEnvReferenceManual.xsl
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 10/26/2005
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
    XSL stylesheet to generate the RapidEnv reference manual.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="../codegentemplates/String.xsl"/>
<xsl:import href="../codegentemplates/Java.xsl"/>

<xsl:output method="text" version="1.0" encoding="iso-8859-1" indent="yes"/>
<xsl:param name="projecthome"/>

<!--
	print the docbook reference manual frame
-->
<xsl:template match="//folder">

<!--
	<xsl:variable name="project">
		<xsl:value-of select="concat($projecthome, '/', 'model/org/rapidbeans/rapidenv/config/Project.xml')"/>
	</xsl:variable>
-->
	<xsl:variable name="commands">
		<xsl:value-of select="concat($projecthome, '/', 'model/org/rapidbeans/rapidenv/CmdRenvCommand.xml')"/>
	</xsl:variable>
	<xsl:variable name="options">
		<xsl:value-of select="concat($projecthome, '/', 'model/org/rapidbeans/rapidenv/CmdRenvOption.xml')"/>
	</xsl:variable>

	<xsl:text><![CDATA[<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE book SYSTEM "docbook.dtd">

<book>
	<title>
		RapidEnv Reference Manual
		<author>
			<personname>Martin Bl&#252;mel</personname>
		</author>
	</title>
	<preface>
		<para>
			The comprehensive reference describing the configuration language,
			the expression language, and the commandline interface.
		</para>
<!--
		<para>
			Please note that this document is under construction!
			<mediaobject>
				<imageobject>
					<imagedata format="GIF" fileref="RapidEnvReferenceManual-files/image001_underConstruction.gif"/>
				</imageobject>
			</mediaobject>
		</para>
-->
	</preface>
	<chapter>
		<title>RapidEnv Configuration Language Reference</title>
]]></xsl:text>

	<xsl:apply-templates
		select="document(concat($projecthome, '/doc/RapidEnvReferenceManualCldescr.xml'))/grammar">
	</xsl:apply-templates>

<xsl:text><![CDATA[	</chapter>
	<chapter>
		<title>RapidEnv Expression Language Reference</title>
		<sect1>
			<title>String Literal</title>
			<para>
				Syntax: '&lt;literal&gt;'
			</para>
		</sect1>
		<sect1>
			<title>Property Value Expansion</title>
			<para>
				Syntax: ${&lt;property name&gt;}
			</para>
		</sect1>
		<sect1>
			<title>Functions</title>
			<para>
				Syntax: &lt;function name&gt;(&lt;argument value 1&gt;, &lt;argument value 1&gt;, ...)
			</para>
			<para>]]></xsl:text>

	<xsl:for-each select="folder/folder/folder/folder/folder[@path = 'model/org/rapidbeans/rapidenv/config/expr']">
		<xsl:for-each select="file[contains(@name, 'ConfigExprFunction')]">
			<xsl:choose>
				<xsl:when test="@name = 'ConfigExprFunction.xml'">
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="file">
						<xsl:value-of select="concat($projecthome, '/', ../@path, '/', @name)"/>
					</xsl:variable>
					<xsl:apply-templates select="document($file)/beantype"
						mode="function"
						/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:for-each>

<xsl:text><![CDATA[
			</para>
		</sect1>
	</chapter>
	<chapter>
		<title>RapidEnv Command Line Reference</title>
		<sect1>
			<title>Usage</title>
			<para>renv [-&lt;option&gt; ...] [&lt;command&gt;] [&lt;install unit&gt; ...]</para>
		</sect1>
		<sect1>
			<title>Commands</title>
			<itemizedlist>
]]></xsl:text>
	<xsl:apply-templates select="document($commands)/enumtype"
		 mode="commandsoroptions"/>
	<xsl:text><![CDATA[
			</itemizedlist>
		</sect1>
]]></xsl:text>
	<xsl:text><![CDATA[
		<sect1>
			<title>Options</title>
			<itemizedlist>
]]></xsl:text>
	<xsl:apply-templates select="document($options)/enumtype"
		 mode="commandsoroptions"/>
<xsl:text><![CDATA[
			</itemizedlist>
		</sect1>
	</chapter>
</book>
]]></xsl:text>

</xsl:template>

<!--
	print documentation for a function
-->
<xsl:template match="/beantype" mode="function">

	<xsl:variable name="classname">
		<xsl:call-template name="Java.extractClassname">
			<xsl:with-param name="fullname">
				<xsl:value-of select="@name"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>

	<xsl:variable name="funcname1">
		<xsl:value-of select="substring-after($classname, 'ConfigExprFunction')"/>
	</xsl:variable>

	<xsl:variable name="funcname">
		<xsl:call-template name="String.lowerFirstCharacter">
			<xsl:with-param name="string">
				<xsl:value-of select="$funcname1"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>
	
	<xsl:text><![CDATA[
				<section>
					<title>]]></xsl:text>
	<xsl:value-of select="$funcname"/>
	<xsl:text><![CDATA[</title>
					<para>]]></xsl:text>
	<xsl:value-of select="description"/>
	<xsl:text><![CDATA[
					</para>
					<para>
						arguments:
					</para>
]]></xsl:text>
	<xsl:text><![CDATA[
					<itemizedlist>
]]></xsl:text>

	<xsl:for-each select="property[@name != 'returnval']">
		<xsl:text><![CDATA[
					<listitem>
						<para>
]]></xsl:text>
		<xsl:text>						</xsl:text>
		<xsl:text><![CDATA[<emphasis role="bold">]]></xsl:text>
		<xsl:value-of select="@name"/>
		<xsl:choose>
			<xsl:when test="@mandatory = 'true'">
			</xsl:when>
			<xsl:otherwise>
				<xsl:text> (optional)</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>: </xsl:text>
		<xsl:text><![CDATA[</emphasis>]]></xsl:text>
		<xsl:value-of select="description"/>
		<xsl:text><![CDATA[
						</para>
					</listitem>
]]></xsl:text>
	</xsl:for-each>

	<xsl:for-each select="property[@name = 'returnval']">
		<xsl:text><![CDATA[
					<listitem>
						<para>
]]></xsl:text>
		<xsl:text><![CDATA[
						<emphasis role="bold">return: </emphasis>]]></xsl:text>
		<xsl:value-of select="description"/>
		<xsl:text><![CDATA[
						</para>
					</listitem>
]]></xsl:text>
	</xsl:for-each>

	<xsl:text><![CDATA[
					</itemizedlist>
]]></xsl:text>
	<xsl:text><![CDATA[
					</section>
]]></xsl:text>

</xsl:template>

<!--
	print documentation for a bean type
-->
<xsl:template match="/grammar">

	<xsl:for-each select="element">
	
	<xsl:text><![CDATA[
			<section>
				<title>]]></xsl:text><xsl:value-of select="@name"/><xsl:text><![CDATA[</title>
]]></xsl:text>

<!--
	<xsl:text><![CDATA[				<para>
					class: ]]></xsl:text>
	<xsl:value-of select="@beantype"/>
	<xsl:text><![CDATA[				</para>
]]></xsl:text>
-->

	<xsl:text><![CDATA[
				<para><![CDATA[]]></xsl:text>
	<xsl:value-of select="description"/>
	<xsl:text>]]</xsl:text>
	<xsl:text><![CDATA[>
				</para>
]]></xsl:text>

	<xsl:text><![CDATA[
					<itemizedlist>
]]></xsl:text>

	<xsl:for-each select="attribute">

		<xsl:text><![CDATA[
				<!-- attribute: ]]></xsl:text>
		<xsl:value-of select="../@name"/>
		<xsl:text>.</xsl:text>
		<xsl:value-of select="@name"/>
		<xsl:text><![CDATA[-->]]>
</xsl:text>

		<xsl:choose>
			<xsl:when test="@transient = 'true'">
			</xsl:when>
			<xsl:otherwise>

		<xsl:text><![CDATA[
				<listitem>
					<para>
]]></xsl:text>
		<xsl:text><![CDATA[<emphasis role="bold">]]></xsl:text>
		<xsl:value-of select="@name"/>
		<xsl:text>: </xsl:text>
		<xsl:text><![CDATA[</emphasis>]]></xsl:text>
		<xsl:choose>
			<xsl:when test="@type = 'bool'">
				<xsl:text>boolean (single choice of { 'true' | 'false' })</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'choice'">
				<xsl:for-each select="enumtype">
					<xsl:choose>
						<xsl:when test="@multiple = 'true'">
							<xsl:text>multiple</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>single</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:text> choice of {</xsl:text>
						<xsl:for-each select="enum">
							<xsl:text> </xsl:text>
							<xsl:if test="position() > 1">
								<xsl:text>| </xsl:text>
							</xsl:if>
							<xsl:value-of select="@name"/>
						</xsl:for-each>
					<xsl:text> }</xsl:text>
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="@type = 'collection' and @composition = 'true'">
				<xsl:text>child element</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'collection'">
				<xsl:text>comma separated list of</xsl:text>
			</xsl:when>
			<xsl:when test="@type = 'file'">
				<xsl:text>path to local file</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@type"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="@type = 'collection'">
<!--
				<xsl:if test="@composition = 'true'">
					<xsl:text>, composition</xsl:text>
				</xsl:if>
-->
				<xsl:text> [</xsl:text>
				<xsl:choose>
					<xsl:when test="@minmult">
						<xsl:value-of select="@minmult"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>0</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>..</xsl:text>
				<xsl:choose>
					<xsl:when test="@maxmult">
						<xsl:value-of select="@maxmult"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>*</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>]</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="@mandatory = 'true'">
						<xsl:text><![CDATA[, <emphasis>mandatory</emphasis>]]></xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text><![CDATA[, <emphasis>optional</emphasis>]]></xsl:text>
						<xsl:choose>
							<xsl:when test="@default = '@@@undefined@@@'">
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>, default = &quot;</xsl:text>
								<xsl:value-of select="@default"/>
								<xsl:text>&quot;</xsl:text>
							</xsl:otherwise>							
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text><![CDATA[
					</para>
]]></xsl:text>


		<xsl:for-each select="description">
			<xsl:text><![CDATA[
					<para><![CDATA[]]></xsl:text>
			<xsl:value-of select="."/>
			<xsl:text>]]</xsl:text>
			<xsl:text><![CDATA[>]]></xsl:text>
			<xsl:text><![CDATA[
					</para>]]></xsl:text>
		</xsl:for-each>


		<xsl:if test="@type = 'choice'">
			<xsl:for-each select="enumtype">
		<xsl:text><![CDATA[
					<para>
]]></xsl:text>
				<xsl:choose>
					<xsl:when test="../description">
					</xsl:when>
					<xsl:otherwise>
						<xsl:for-each select="description">
							<xsl:value-of select="."/>
						</xsl:for-each>		
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text><![CDATA[
							<itemizedlist>
]]></xsl:text>
				<xsl:for-each select="enum">

					<xsl:text><![CDATA[
								<listitem>
									<para>
]]></xsl:text>
					<xsl:text><![CDATA[<emphasis role="bold">]]></xsl:text>
					<xsl:value-of select="@name"/>
					<xsl:text>:</xsl:text>
					<xsl:text><![CDATA[</emphasis>
]]></xsl:text>
					<xsl:for-each select="description">
						<xsl:value-of select="."/>
					</xsl:for-each>
					<xsl:text><![CDATA[
									</para>
								</listitem>
]]></xsl:text>
				</xsl:for-each>
				<xsl:text><![CDATA[
							</itemizedlist>
]]></xsl:text>
		<xsl:text><![CDATA[
					</para>
]]></xsl:text>
			</xsl:for-each>
		</xsl:if>

		<xsl:text><![CDATA[
				</listitem>
]]></xsl:text>

		</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each>
	
		<xsl:text><![CDATA[
			</itemizedlist>
]]></xsl:text>

		<xsl:text><![CDATA[
			</section>
]]></xsl:text>


	</xsl:for-each>

</xsl:template>


<!--
	print a list of document commands or options
-->
<xsl:template match="/enumtype" mode="commandsoroptions">
	<xsl:for-each select="enum">
		<xsl:text><![CDATA[
				<listitem>
					<para>
]]></xsl:text>
		<xsl:text><![CDATA[<emphasis role="bold">]]></xsl:text>
		<xsl:value-of select="@name"/>
		<xsl:for-each select="cell[@name = 'short1']">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="@value"/>
		</xsl:for-each>
		<xsl:text><![CDATA[: </emphasis>
]]></xsl:text>
				<xsl:value-of select="description"/>
		<xsl:text><![CDATA[
					</para>
				</listitem>
]]></xsl:text>
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>

<?xml version="1.0" encoding="iso-8859-1"?>
<!--
 * Rapid Beans Framework: Java.xsl
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
	Helper templates to support Java code generation.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template name="Java.extractPackage">
	<xsl:param name="fullname"/>
	<xsl:call-template name="String.splitAfterLastOccurrenceBefore">
		<xsl:with-param name="string"><xsl:value-of select="$fullname"/></xsl:with-param>
		<xsl:with-param name="sepchar">.</xsl:with-param>
		<xsl:with-param name="include">false</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template name="Java.extractClassname">
	<xsl:param name="fullname"/>
	<xsl:call-template name="String.splitAfterLastOccurrenceAfter">
		<xsl:with-param name="string"><xsl:value-of select="$fullname"/></xsl:with-param>
		<xsl:with-param name="sepchar">.</xsl:with-param>
		<xsl:with-param name="include">false</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template name="Java.fileHeader">
	<xsl:param name="classname"/>
	<xsl:param name="package"/>
	<xsl:param name="in"/>
	<xsl:param name="out"/>
	<xsl:param name="codegen"/>
    <xsl:param name="root"/>
	<xsl:param name="kindoftype"/>
	<xsl:param name="style"/>

	<xsl:text>/*
 * </xsl:text>
<xsl:choose>
	<xsl:when test="$codegen = 'joint'">
		<xsl:text>Partially</xsl:text>	
	</xsl:when>
	<xsl:otherwise>
		<xsl:text>Completely</xsl:text>	
	</xsl:otherwise>
</xsl:choose>

<xsl:text> generated code file: </xsl:text><xsl:value-of select="$classname"/><xsl:text>.java</xsl:text>

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

<xsl:variable name="root1">
    <xsl:value-of select="translate($root, '\', '/')"/>
</xsl:variable>

<xsl:variable name="in1">
	<xsl:value-of select="translate($in, '\', '/')"/>
</xsl:variable>
<xsl:variable name="in2">
	<xsl:choose>
		<xsl:when test="$root =''">
			<xsl:value-of select="$in1"/>
		</xsl:when>
		<xsl:otherwise>
		    <xsl:choose>
                <xsl:when test="starts-with($in1, concat($root1, '/'))">
                    <xsl:value-of select="substring-after($in1, concat($root1, '/'))"/>
                </xsl:when>
				<xsl:when test="starts-with($in1, $root1)">
					<xsl:value-of select="substring-after($in1, $root1)"/>
				</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$in1"/>
			</xsl:otherwise>
			</xsl:choose>
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>

<xsl:variable name="style1">
    <xsl:value-of select="translate($style, '\', '/')"/>
</xsl:variable>
<xsl:variable name="style2">
    <xsl:choose>
        <xsl:when test="$root =''">
            <xsl:value-of select="$style1"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:choose>
                <xsl:when test="starts-with($style1, concat($root1, '/'))">
                    <xsl:value-of select="substring-after($style1, concat($root1, '/'))"/>
                </xsl:when>
                <xsl:when test="starts-with($style1, $root1)">
                    <xsl:value-of select="substring-after($style1, $root1)"/>
                </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$style1"/>
            </xsl:otherwise>
            </xsl:choose>
        </xsl:otherwise>
    </xsl:choose>
</xsl:variable>

<xsl:text>
 *
 * Rapid Beans </xsl:text><xsl:value-of select="$kindoftype"/><xsl:text> generator, Copyright Martin Bluemel, 2008
 *
 * generated Java implementation of Rapid Beans </xsl:text><xsl:value-of select="$kindoftype"/><xsl:text> type
 * </xsl:text><xsl:value-of select="$package"/><xsl:text>.</xsl:text><xsl:value-of select="$classname"/><xsl:text>
 * 
 * model:    </xsl:text><xsl:value-of select="$in2"/><xsl:text>
 * template: </xsl:text><xsl:value-of select="$style2"/><xsl:text>
 */</xsl:text>

</xsl:template>

</xsl:stylesheet>

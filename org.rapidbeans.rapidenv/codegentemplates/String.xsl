<?xml version="1.0" encoding="iso-8859-1"?>
<!--
 * Rapid Beans Framework: String.xsl
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
    Helper templates to support String processing.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template name="String.upperFirstCharacter">
	<xsl:param name="string"/>
	<xsl:value-of select="translate(substring($string, 1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
	<xsl:value-of select="substring($string, 2, string-length($string) - 1)"/>
</xsl:template>

<xsl:template name="String.lowerFirstCharacter">
	<xsl:param name="string"/>
	<xsl:value-of select="translate(substring($string, 1, 1), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
	<xsl:value-of select="substring($string, 2, string-length($string) - 1)"/>
</xsl:template>

<xsl:template name="String.escapeHtmlChars">
	<xsl:param name="string"/>
	<xsl:value-of select="$string"/>
	<xsl:call-template name="String.replace">
		<xsl:with-param name="string"><xsl:value-of select="$string"/></xsl:with-param>
		<xsl:with-param name="from"><xsl:value-of select="'&amp;'"/></xsl:with-param>
		<xsl:with-param name="to"><xsl:value-of select="'&amp;amp;'"/></xsl:with-param>
	</xsl:call-template>
</xsl:template>

<!-- *********************************************************************** -->
<!-- * primitive line wrapper                                              * -->
<!-- *********************************************************************** -->
<xsl:template name="String.replace">
	<xsl:param name="string"/>
	<xsl:param name="from"/>
	<xsl:param name="to"/>

	<xsl:choose>
		<xsl:when test="contains($string, $from)">
			<xsl:variable name="before">
				<xsl:value-of select="substring-before($string, $from)"/>
			</xsl:variable>
			<xsl:variable name="after">
				<xsl:value-of select="substring($string, string-length($before) + string-length($from) + 1)"/>
			</xsl:variable>
			<xsl:value-of select="$before"/>
			<xsl:value-of select="$to"/>
			<xsl:call-template name="String.replace">
				<xsl:with-param name="string"><xsl:value-of select="$after"/></xsl:with-param>
				<xsl:with-param name="from"><xsl:value-of select="$from"/></xsl:with-param>
				<xsl:with-param name="to"><xsl:value-of select="$to"/></xsl:with-param>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$string"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- *********************************************************************** -->
<!-- * primitive line wrapper                                              * -->
<!-- *********************************************************************** -->
<xsl:template name="String.wrapLine">
	<xsl:param name="string"/>
	<xsl:param name="linemaxcharcount"/>
	<xsl:param name="conseqlinesindent"/>
	<xsl:param name="firstlineidcount"/>
	<xsl:param name="separateafter"/>
	<xsl:variable name="newline">
		<xsl:text>
</xsl:text>
	</xsl:variable>
	<xsl:choose>
		<xsl:when test="string-length($string) + $firstlineidcount &lt;= $linemaxcharcount">
			<xsl:value-of select="$string"></xsl:value-of>
		</xsl:when>
		<xsl:otherwise>
			<xsl:variable name="line">
				<xsl:value-of select="substring($string, 0, $linemaxcharcount - $firstlineidcount)"/>
			</xsl:variable>
			<xsl:variable name="rest">
				<xsl:value-of select="substring($string, $linemaxcharcount - $firstlineidcount)"/>
			</xsl:variable>
			<xsl:variable name="line1">				
				<xsl:choose>
					<xsl:when test="($separateafter != '') and contains($line, $separateafter) and (substring($line, string-length($line)) != $separateafter)">
						<xsl:call-template name="String.splitAfterLastOccurrenceBefore">
							<xsl:with-param name="string"><xsl:value-of select="$line"/></xsl:with-param>
							<xsl:with-param name="sepchar"><xsl:value-of select="$separateafter"/></xsl:with-param>
							<xsl:with-param name="include">true</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$line"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="rest1">				
				<xsl:choose>
					<xsl:when test="($separateafter != '') and contains($line, $separateafter) and (substring($line, string-length($line)) != $separateafter)">
						<xsl:variable name="splitrest">
							<xsl:call-template name="String.splitAfterLastOccurrenceAfter">
								<xsl:with-param name="string"><xsl:value-of select="$line"/></xsl:with-param>
								<xsl:with-param name="sepchar"><xsl:value-of select="$separateafter"/></xsl:with-param>
								<xsl:with-param name="include">false</xsl:with-param>
							</xsl:call-template>
						</xsl:variable>
						<xsl:value-of select="concat($splitrest, $rest)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$rest"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:value-of select="$line1"/>
			<xsl:value-of select="$newline"/>
			<xsl:value-of select="$conseqlinesindent"/>
			<xsl:call-template name="String.wrapLine">
				<xsl:with-param name="string"><xsl:value-of select="$rest1"/></xsl:with-param>
				<xsl:with-param name="linemaxcharcount"><xsl:value-of select="$linemaxcharcount"/></xsl:with-param>
				<xsl:with-param name="conseqlinesindent"><xsl:value-of select="$conseqlinesindent"/></xsl:with-param>
				<xsl:with-param name="firstlineidcount"><xsl:value-of select="string-length($conseqlinesindent)"/></xsl:with-param>
				<xsl:with-param name="separateafter"><xsl:value-of select="$separateafter"/></xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- *********************************************************************** -->
<!-- * like substring-before but with last occurence                        * -->
<!-- *********************************************************************** -->
<xsl:template name="String.splitAfterLastOccurrenceBefore">
	<xsl:param name="string"/>
	<xsl:param name="sepchar"/>
	<xsl:param name="include"/>
	<xsl:choose>
		<xsl:when test="contains($string, $sepchar) = false">
			<xsl:value-of select="$string"/>
		</xsl:when>
		<xsl:when test="substring($string, string-length($string)) = $sepchar">
			<xsl:choose>
				<xsl:when test="$include = 'true'">
					<xsl:value-of select="$string"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="substring($string, 0, string-length($string))"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="String.splitAfterLastOccurrenceBefore">
				<xsl:with-param name="string"><xsl:value-of select="substring($string, 0, string-length($string))"/></xsl:with-param>
				<xsl:with-param name="sepchar"><xsl:value-of select="$sepchar"/></xsl:with-param>
				<xsl:with-param name="include"><xsl:value-of select="$include"/></xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- *********************************************************************** -->
<!-- * like substring-after but with last occurence                        * -->
<!-- *********************************************************************** -->
<xsl:template name="String.splitAfterLastOccurrenceAfter">
	<xsl:param name="string"/>
	<xsl:param name="rest"/>
	<xsl:param name="sepchar"/>
	<xsl:param name="include"/>
	<xsl:choose>
		<xsl:when test="contains($string, $sepchar) = false">
			<xsl:value-of select="$string"/>
		</xsl:when>
		<xsl:when test="substring($string, string-length($string)) = $sepchar">
			<xsl:choose>
				<xsl:when test="$include = 'true'">
					<xsl:value-of select="concat($sepchar, $rest)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$rest"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="String.splitAfterLastOccurrenceAfter">
				<xsl:with-param name="string"><xsl:value-of select="substring($string, 0, string-length($string))"/></xsl:with-param>
				<xsl:with-param name="rest"><xsl:value-of select="concat(substring($string, string-length($string)), $rest)"/></xsl:with-param>
				<xsl:with-param name="sepchar"><xsl:value-of select="$sepchar"/></xsl:with-param>
				<xsl:with-param name="include"><xsl:value-of select="$include"/></xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="String.removeDuplicateLines">
	<xsl:param name="in"/>
	<xsl:param name="out"/>
	<xsl:variable name="newline">
		<xsl:text>
</xsl:text>		
	</xsl:variable>

	<!-- split up first line and rest -->
	<xsl:variable name="firstLine">
		<xsl:value-of select="substring-before($in, $newline)"/>
	</xsl:variable>
	<xsl:variable name="rest">
		<xsl:value-of select="substring-after($in, $newline)"/>
	</xsl:variable>

	<xsl:variable name="newout">
		<xsl:choose>
			<xsl:when test="contains($out, $firstLine)">
				<xsl:value-of select="$out"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat($out, $firstLine, $newline)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:choose>
		<xsl:when test="$rest = ''">
			<xsl:value-of select="$out"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="String.removeDuplicateLines">
				<xsl:with-param name="in"><xsl:value-of select="$rest"/></xsl:with-param>
				<xsl:with-param name="out"><xsl:value-of select="$newout"/></xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- *********************************************************************** -->
<!-- * extract cell from a csv separated line                              * -->
<!-- *********************************************************************** -->
<!--     line <cell 0>;<cell 1>;<cell 2>; ... -->
<!--             0       1        2   -->
<xsl:template name="String.extractCellFromCsvLine">
	<xsl:param name="line"/>
	<xsl:param name="index"/>

	<!-- split first csv cell -->
	<xsl:variable name="cell">
		<xsl:value-of select="substring-before($line, ';')"/>
	</xsl:variable>
	<xsl:variable name="rest">
		<xsl:value-of select="substring-after($line, ';')"/>
	</xsl:variable>

	<xsl:choose>
		<xsl:when test="$index = 0">
			<xsl:value-of select="$cell"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="String.extractCellFromCsvLine">
				<xsl:with-param name="line"><xsl:value-of select="$rest"/></xsl:with-param>
				<xsl:with-param name="index"><xsl:value-of select="$index - 1"/></xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>

</xsl:template>

</xsl:stylesheet>

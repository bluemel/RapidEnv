<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- new code should be marked with bold and green -->
<xsl:template match="newcode">
  <xsl:text disable-output-escaping="yes"><![CDATA[<b style="color:green">]]></xsl:text>
  <xsl:value-of select="."/>
  <xsl:text disable-output-escaping="yes"><![CDATA[</b>]]></xsl:text>
</xsl:template>

<!-- changed code should be marked with bold and blue -->
<xsl:template match="changedcode">
  <xsl:text disable-output-escaping="yes"><![CDATA[<b style="color:blue">]]></xsl:text>
  <xsl:value-of select="."/>
  <xsl:text disable-output-escaping="yes"><![CDATA[</b>]]></xsl:text>
</xsl:template>

</xsl:stylesheet>

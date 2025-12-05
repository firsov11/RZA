<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
    xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
    xmlns:mml="http://www.w3.org/1998/Math/MathML"
    version="1.0"
    exclude-result-prefixes="m w">

    <xsl:output method="xml" indent="yes"/>

    <!-- Root -->
    <xsl:template match="/">
        <mml:math display="block">
            <xsl:apply-templates/>
        </mml:math>
    </xsl:template>

    <!-- Generic passthrough -->
    <xsl:template match="*">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- Text nodes -->
    <xsl:template match="m:t">
        <mml:mi><xsl:value-of select="."/></mml:mi>
    </xsl:template>

    <xsl:template match="w:t">
        <mml:mi><xsl:value-of select="."/></mml:mi>
    </xsl:template>

    <!-- Fractions -->
    <xsl:template match="m:f">
        <mml:mfrac>
            <xsl:apply-templates select="m:num"/>
            <xsl:apply-templates select="m:den"/>
        </mml:mfrac>
    </xsl:template>

    <xsl:template match="m:num|m:den">
        <mml:mrow>
            <xsl:apply-templates/>
        </mml:mrow>
    </xsl:template>

    <!-- Superscript -->
    <xsl:template match="m:sSup">
        <mml:msup>
            <xsl:apply-templates select="m:e"/>
            <xsl:apply-templates select="m:sup"/>
        </mml:msup>
    </xsl:template>

    <!-- Subscript -->
    <xsl:template match="m:sSub">
        <mml:msub>
            <xsl:apply-templates select="m:e"/>
            <xsl:apply-templates select="m:sub"/>
        </mml:msub>
    </xsl:template>

    <!-- SubSup -->
    <xsl:template match="m:sSubSup">
        <mml:msubsup>
            <xsl:apply-templates select="m:e"/>
            <xsl:apply-templates select="m:sub"/>
            <xsl:apply-templates select="m:sup"/>
        </mml:msubsup>
    </xsl:template>

    <!-- Root -->
    <xsl:template match="m:rad">
        <xsl:choose>
            <xsl:when test="m:deg">
                <mml:mroot>
                    <xsl:apply-templates select="m:e"/>
                    <xsl:apply-templates select="m:deg"/>
                </mml:mroot>
            </xsl:when>
            <xsl:otherwise>
                <mml:msqrt>
                    <xsl:apply-templates select="m:e"/>
                </mml:msqrt>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Operators: sum, int, prod -->
    <xsl:template match="m:limLow">
        <mml:msub>
            <mml:mo>&#x222B;</mml:mo>
            <xsl:apply-templates select="m:lim"/>
        </mml:msub>
    </xsl:template>

    <xsl:template match="m:nary">
        <mml:mrow>
            <mml:mo>
                <xsl:choose>
                    <xsl:when test="m:naryPr/m:chr/@m:val">&#x2211;</xsl:when>
                    <xsl:otherwise>&#x222B;</xsl:otherwise>
                </xsl:choose>
            </mml:mo>
            <xsl:if test="m:limLow">
                <mml:mo><xsl:apply-templates select="m:limLow"/></mml:mo>
            </xsl:if>
            <xsl:apply-templates select="m:e"/>
        </mml:mrow>
    </xsl:template>

    <!-- Matrix -->
    <xsl:template match="m:m">
        <mml:mtable>
            <xsl:for-each select="m:mr">
                <mml:mtr>
                    <xsl:for-each select="m:e">
                        <mml:mtd>
                            <xsl:apply-templates/>
                        </mml:mtd>
                    </xsl:for-each>
                </mml:mtr>
            </xsl:for-each>
        </mml:mtable>
    </xsl:template>

    <!-- Overbar / underline -->
    <xsl:template match="m:bar">
        <mml:mover accent="true">
            <xsl:apply-templates select="m:e"/>
            <mml:mo>&#x00AF;</mml:mo>
        </mml:mover>
    </xsl:template>

    <xsl:template match="m:groupChr">
        <mml:munder accentunder="false">
            <xsl:apply-templates select="m:e"/>
            <mml:mo>&#x23DF;</mml:mo>
        </mml:munder>
    </xsl:template>

</xsl:stylesheet>

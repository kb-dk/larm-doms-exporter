<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foxml="info:fedora/fedora-system:def/foxml#">



    <xsl:strip-space elements="*"/>

    <xsl:param name="timestamp"/>


    <xsl:variable name="asOfTime" select="translate($timestamp,'-:.TZ','')"/>


    <xsl:template match="foxml:digitalObject">
<!--
        <xsl:value-of select="$timestamp"/>
        <xsl:value-of select="$asOfTime"/>
-->
        <xsl:variable name="createdDate" select="
        translate(
        foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#createdDate']/@VALUE,
        '-:.TZ','')"/>
        <xsl:variable name="exists" select=" $asOfTime >= $createdDate"/>

        <xsl:if test="$exists">
            <xsl:copy>
                <xsl:for-each select="@*">
                    <xsl:copy/>
                </xsl:for-each>
                <xsl:apply-templates select="node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>

    <xsl:template match="*">
        <xsl:copy>
            <xsl:for-each select="@*">
                <xsl:copy/>
            </xsl:for-each>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="foxml:objectProperties">
        <xsl:copy-of select="current()"/>
    </xsl:template>


    <xsl:template match="foxml:datastream">

        <xsl:variable name="created" select="$asOfTime >= translate(foxml:datastreamVersion[1]/@CREATED,'-:.TZ','')"/>
        <xsl:if test="$created and @ID != 'AUDIT'">
            <xsl:copy>
                <xsl:for-each select="@*">
                    <xsl:copy/>
                </xsl:for-each>
                <xsl:for-each select="foxml:datastreamVersion">
                    <xsl:if test="$asOfTime >= translate(@CREATED,'-:.TZ','')">
                        <xsl:copy-of select="current()"/>
                    </xsl:if>
                </xsl:for-each>
            </xsl:copy>
        </xsl:if>
    </xsl:template>


</xsl:stylesheet>
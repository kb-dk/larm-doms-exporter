<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foxml="info:fedora/fedora-system:def/foxml#"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:model="info:fedora/fedora-system:def/model#"
                xmlns:dobundle="http://ecm.sourceforge.net/types/digitalobjectbundle/0/2/#"
        >


    <xsl:strip-space elements="*"/>

    <xsl:template match="/dobundle:digitalObjectBundle">
        <BTAdata>
            <xsl:apply-templates select="foxml:digitalObject[1]">
                <xsl:with-param name="type" select="'program'"/>
            </xsl:apply-templates>
        </BTAdata>
    </xsl:template>

    <xsl:template match="foxml:digitalObject">
        <xsl:param name="type"/>
        <xsl:variable name="pid" select="@PID"/>
        <xsl:if test="$type = 'program'">
            <xsl:call-template name="parseProgram">
                <xsl:with-param name="pid" select="$pid"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <xsl:template name="parseProgram">
        <xsl:param name="pid"/>
        <program>
            <xsl:attribute name="id">
                <xsl:value-of select="$pid"/>
            </xsl:attribute>
            <xsl:apply-templates select="foxml:datastream">
                <xsl:with-param name="program" select="'yes'"/>
            </xsl:apply-templates>
        </program>
    </xsl:template>

    <xsl:template name="getContent">
        <xsl:copy-of select="current()/foxml:datastreamVersion[last()]/foxml:xmlContent/*"/>
    </xsl:template>



    <xsl:template match="foxml:datastream[@ID='PBCORE']">
        <xsl:param name="program"/>
        <xsl:if test="$program">
            <pbcore>
                <xsl:call-template name="getContent"/>
            </pbcore>
        </xsl:if>
    </xsl:template>


    <xsl:template match="foxml:datastream">

    </xsl:template>


</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
xmlns:xip="http://www.tessella.com/XIP/v4" xmlns:dc="http://purl.org/dc/elements/1.1/" 
xmlns:oai="http://www.openarchives.org/OAI/2.0/"
                xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:exdc="http://www.preservica.com/export/multi-record-dublin-core/v1"
exclude-result-prefixes="xip oai java javadate javadateParser"

xmlns="http://doms.statsbiblioteket.dk/types/larm/0/1/#" xmlns:PBCoreDescriptionDocument="http://www.pbcore.org/PBCore/PBCoreNamespace.html"
				xmlns:java="http://xml.apache.org/xalan/java"
				xmlns:javadate="http://xml.apache.org/xalan/java/java.util.Date"
				xmlns:javadateParser="http://xml.apache.org/xalan/java/java.text.SimpleDateFormat"
xmlns:pbcore="http://www.pbcore.org/PBCore/PBCoreNamespace.html"  xmlns:ns2="http://www.tessella.com/XIP/v4"
                xmlns:padding="http://kuana.kb.dk/types/padding/0/1/#" xmlns:walltime="http://kuana.kb.dk/types/walltime/0/1/#" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:pidhandle="http://kuana.kb.dk/types/pidhandle/0/1/#" >
                
    <!--
        Preservica XIP to LARM
        LARM documentation: https://sbprojects.statsbiblioteket.dk/display/DRI/Larm+Eksport
    -->
	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">
        <xsl:variable name="id">
            <xsl:value-of select="//record/header/identifier"/>
        </xsl:variable>
        <xsl:variable name="apos">'</xsl:variable>

        <!-- 2017-10-01T16:59:22Z -->
        <xsl:variable name="parser"
                      select="javadateParser:new(concat('yyyy-MM-dd',$apos,'T',$apos,'HH:mm:ssX'))"/>

        <!-- 2017-10-01T19:59:22 , se mulige formater her https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html-->
        <xsl:variable name="printer"
                      select="javadateParser:new(concat('yyyy-MM-dd',$apos,'T',$apos,'HH:mm:ss'))"/>

        <EnvelopeContainer>
			<Envelope>
				<xsl:for-each select=".">
				
			<ObjectEnvelope>
						<UpdateIfExistValue></UpdateIfExistValue>
						<UpdateIfExistField>m00000000-0000-0000-0000-0000df820000_da_all</UpdateIfExistField>
						<FolderID>2043</FolderID>
						<Metadatas>
							<MetadataEnvelope>
								<UpdateIfExist>True</UpdateIfExist>
								<LanguageCode>da</LanguageCode>
								<MetadataSchemaGUID>00000000-0000-0000-0000-0000df820000</MetadataSchemaGUID>
								<MetadataXML>
									<xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
									<Larm.Program>


										<PublicationDateTime>
											<xsl:value-of select="javadateParser:format($printer, javadateParser:parse($parser,//xip:Metadata/PBCoreDescriptionDocument:PBCoreDescriptionDocument/pbcore:pbcoreInstantiation/pbcore:pbcoreDateAvailable/pbcore:dateAvailableStart))"/>
												</PublicationDateTime>
										<PublicationEndDateTime>
											<xsl:value-of select="javadateParser:format($printer, javadateParser:parse($parser,//xip:Metadata/PBCoreDescriptionDocument:PBCoreDescriptionDocument/pbcore:pbcoreInstantiation/pbcore:pbcoreDateAvailable/pbcore:dateAvailableEnd))"/>


										</PublicationEndDateTime>
										<PublicationChannel>
										<xsl:variable name="channel_name" select="//xip:Metadata/PBCoreDescriptionDocument:PBCoreDescriptionDocument/pbcore:pbcorePublisher[pbcore:publisherRole/text() = 'channel_name']/pbcore:publisher"/>
										  <xsl:call-template name="channel_names">
                        <xsl:with-param name="channel" select="$channel_name"/>
                        </xsl:call-template>
                    
										</PublicationChannel>
										<Title>
											<xsl:value-of select="//xip:Metadata/PBCoreDescriptionDocument:PBCoreDescriptionDocument/pbcore:pbcoreTitle[pbcore:titleType/text() = 'titel']/pbcore:title"/>
										</Title>
										<Abstract>
											<xsl:value-of select="//xip:Metadata/PBCoreDescriptionDocument:PBCoreDescriptionDocument/pbcore:pbcoreDescription[pbcore:descriptionType='kortomtale']/pbcore:description"/>
										</Abstract>
										<Description>
											<xsl:value-of select="//xip:Metadata/PBCoreDescriptionDocument:PBCoreDescriptionDocument/pbcore:pbcoreDescription[pbcore:descriptionType='langomtale1']/pbcore:description"/>
											<xsl:value-of select="//xip:Metadata/PBCoreDescriptionDocument:PBCoreDescriptionDocument/pbcore:pbcoreDescription[pbcore:descriptionType='langomtale2']/pbcore:description"/>
										</Description>
										<Publisher>
											<xsl:value-of select="//xip:Metadata/PBCoreDescriptionDocument:PBCoreDescriptionDocument/pbcore:pbcorePublisher[pbcore:publisherRole/text() = 'kanalnavn']/pbcore:publisher"/>
										</Publisher>
										<MajorGenre>
											<xsl:value-of select="substring-after(//xip:Metadata/PBCoreDescriptionDocument:PBCoreDescriptionDocument/pbcore:pbcoreGenre/pbcore:genre[contains(text(), 'hovedgenre')]/text(), 'hovedgenre: ')"/>
										</MajorGenre>
										<MinorGenre>
											<xsl:value-of select="substring-after(//xip:Metadata/PBCoreDescriptionDocument:PBCoreDescriptionDocument/pbcore:pbcoreGenre/pbcore:genre[contains(text(), 'undergenre')]/text(), 'undergenre: ')"/>
										</MinorGenre>
										<Subjects/>
										<Contributors/>
										<Creators/>
										<Locations></Locations>
										<Identifiers>
											<DR.ProductionNumber></DR.ProductionNumber>
											<DR.ArchiveNumber></DR.ArchiveNumber>
											<SB.DomsID><xsl:value-of select="//oai:metadata/xip:DeliverableUnit/xip:DeliverableUnitRef"/></SB.DomsID>
										</Identifiers>
									</Larm.Program>
									<xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
								</MetadataXML>
							</MetadataEnvelope>
							<MetadataEnvelope>
							<UpdateIfExist>True</UpdateIfExist>
								<LanguageCode>da</LanguageCode>
								<MetadataSchemaGUID>00000000-0000-0000-0000-0000dd820000</MetadataSchemaGUID>
								<MetadataXML>
									<xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
									<Larm.FileInfos>
									<xsl:for-each select="//oai:record/oai:metadata/xip:Manifestation"> 
										<xsl:if test="ns2:Active='true' and ns2:TypeRef='2'">
										<xsl:for-each select=".">

										<Larm.FileInfo>
										
										<StartOffSetMS>0</StartOffSetMS>
										 <EndOffSetMS>0</EndOffSetMS>
										<FileName><xsl:value-of select="ns2:ManifestationFile/ns2:FileRef"/></FileName>
										<Index>0</Index>
										<BaseWallTime></BaseWallTime>
										<WalltimeInfo>
										<ChangeDateTime>
										<xsl:value-of select="javadateParser:format($printer, javadateParser:parse($parser,../../oai:header/oai:datestamp))"/>
									</ChangeDateTime>
                    <Walltime><xsl:value-of select="ns2:Metadata/walltime:walltime/walltime:walltimeValue"/></Walltime></WalltimeInfo>
									
										</Larm.FileInfo>
										</xsl:for-each>
										</xsl:if>
										</xsl:for-each>
									</Larm.FileInfos>
									<xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
								</MetadataXML>
							</MetadataEnvelope>
							<MetadataEnvelope>
							<UpdateIfExist>False</UpdateIfExist>
							<LanguageCode>da</LanguageCode>
							<MetadataSchemaGUID>17d59e41-13fb-469a-a138-bb691f13f2ba</MetadataSchemaGUID>
							<MetadataXML>
							<xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text><Larm.Metadata><Title></Title><Description></Description><Genre></Genre><Subjects></Subjects><Tags></Tags><Note></Note><RelatedObjects></RelatedObjects><Contributors /></Larm.Metadata>
							<xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
								</MetadataXML>
							</MetadataEnvelope>
						</Metadatas>
				<Files>
				<xsl:for-each select=".">
				<FileEnvelope>
				
				<DestinationID>90</DestinationID>
				<FolderPath></FolderPath>
				<Filename><xsl:value-of select="//xip:FileRef"/></Filename>
				<OriginalFilename><xsl:value-of select="//xip:FileRef"/></OriginalFilename>
				<FormatID>49</FormatID>
					
				</FileEnvelope>
					</xsl:for-each>
				<FileEnvelope>
				<DestinationID>105</DestinationID>
				<FolderPath></FolderPath>
				<Filename><xsl:variable name="channel_name" select="//xip:Metadata/PBCoreDescriptionDocument:PBCoreDescriptionDocument/pbcore:pbcorePublisher[pbcore:publisherRole/text() = 'channel_name']/pbcore:publisher"/>
										  <xsl:call-template name="channel_logos">
                        <xsl:with-param name="channel" select="$channel_name"/>
                        </xsl:call-template></Filename>
				<OriginalFilename><xsl:variable name="channel_name" select="//xip:Metadata/PBCoreDescriptionDocument:PBCoreDescriptionDocument/pbcore:pbcorePublisher[pbcore:publisherRole/text() = 'channel_name']/pbcore:publisher"/>
										  <xsl:call-template name="channel_logos">
                        <xsl:with-param name="channel" select="$channel_name"/>
                        </xsl:call-template></OriginalFilename>
				<FormatID>50</FormatID>
		
				</FileEnvelope>
				
				</Files>
				
				<ObjectTypeID>69</ObjectTypeID>
				
</ObjectEnvelope>
					<!--</xsl:if>-->
</xsl:for-each>
				</Envelope>
			</EnvelopeContainer>
		</xsl:template>
   <xsl:template name="channel_names">
<xsl:param name="channel"/>

<xsl:for-each select ="$channel">
<xsl:if test=".='drbo'">
<xsl:text>DR Boogieradio</xsl:text>
</xsl:if>
	<xsl:if test=".='drboo'">
<xsl:text>DR Boogieradio</xsl:text>
</xsl:if>
<xsl:if test=".='drpk'">
<xsl:text>DR Klassisk</xsl:text>
</xsl:if>
<xsl:if test=".='drmamara'">
<xsl:text>DR Mama</xsl:text>
</xsl:if>
	<xsl:if test=".='drol'">
<xsl:text>DR Oline</xsl:text>
</xsl:if>
	<xsl:if test=".='drp1'">
<xsl:text>DR P1</xsl:text>
</xsl:if>
	<xsl:if test=".='drp2'">
<xsl:text>DR P2</xsl:text>
</xsl:if>
	<xsl:if test=".='drp3'">
<xsl:text>DR P3</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4b'">
<xsl:text>DR P4 Bornholm</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4blm'">
<xsl:text>DR P4 Bornholm</xsl:text>
</xsl:if>
		<xsl:if test=".='drp4o'">
<xsl:text>DR P4 Fælles</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4f'">
<xsl:text>DR P4 Fyn</xsl:text>
</xsl:if>
			
		<xsl:if test=".='drp4fyn'">
<xsl:text>DR P4 Fyn</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4k94'">
<xsl:text>DR P4 K94</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4k'">
<xsl:text>DR P4 København</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4miv'">
<xsl:text>DR P4 Midt og Vest</xsl:text>
</xsl:if>
		<xsl:if test=".='drp4mv'">
<xsl:text>DR P4 Midt og Vest</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4nj'">
<xsl:text>DR P4 Nordjylland</xsl:text>
</xsl:if>
		<xsl:if test=".='drp4nor'">
<xsl:text>DR P4 Nordjylland</xsl:text>
</xsl:if>
		<xsl:if test=".='drp4o'">
<xsl:text>DR P4 Østjylland</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4re'">
<xsl:text>DR P4 Regional</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4s'">
<xsl:text>DR P4 Syd</xsl:text>
</xsl:if>
		<xsl:if test=".='drp5'">
<xsl:text>DR P5</xsl:text>
</xsl:if>
		<xsl:if test=".='drp5000'">
<xsl:text>DR P5000</xsl:text>
</xsl:if>
	<xsl:if test=".='drp6b'">
<xsl:text>DR P6 Beat</xsl:text>
</xsl:if>
	<xsl:if test=".='drp7m'">
<xsl:text>DR P7 Mix</xsl:text>
</xsl:if>
<xsl:if test=".='drp8j'">
<xsl:text>DR P8 Jazz</xsl:text>
</xsl:if>
		<xsl:if test=".='drram'">
<xsl:text>DR Ramasjang</xsl:text>
</xsl:if>
	<xsl:if test=".='drrar'">
<xsl:text>DR Ramasjang</xsl:text>
</xsl:if>
		<xsl:if test=".='novafm'">
<xsl:text>NOVA fm</xsl:text>
</xsl:if>
<xsl:if test=".='100fm'">
<xsl:text>Radio 100FM</xsl:text>
</xsl:if>
		<xsl:if test=".='radio2'">
<xsl:text>Radio 2</xsl:text>
</xsl:if>
		<xsl:if test=".='24syv'">
<xsl:text>Radio 24syv</xsl:text>
</xsl:if>
	<xsl:if test=".='thevoice'">
<xsl:text>THE VOICE</xsl:text>
</xsl:if>
	<xsl:if test=".='tv2radio'">
<xsl:text>TV2 Radio</xsl:text>
</xsl:if>

			   <!-- these are tv channels -->
	<xsl:if test=".='7eren'">
<xsl:text>7'eren</xsl:text>
</xsl:if>
<xsl:if test=".='canal9'">
<xsl:text>Canal 9</xsl:text>
</xsl:if>
	<xsl:if test=".='dk4'">
<xsl:text>DK 4</xsl:text>
</xsl:if>
	<xsl:if test=".='drhd'">
<xsl:text>DR HD</xsl:text>
</xsl:if>
	<xsl:if test=".='drultra'">
<xsl:text>DR Ultra</xsl:text>
</xsl:if>
	<xsl:if test=".='drup'">
<xsl:text>DR Update</xsl:text>
</xsl:if>
	<xsl:if test=".='dr1'">
<xsl:text>DR 1</xsl:text>
</xsl:if>
			<xsl:if test=".='dr2'">
<xsl:text>DR 2</xsl:text>
</xsl:if>
		<xsl:if test=".='dr3'">
<xsl:text>DR 3</xsl:text>
</xsl:if>
	<xsl:if test=".='folketinget'">
<xsl:text>Folketingskanalen</xsl:text>
</xsl:if>
	<xsl:if test=".='kanal4'">
<xsl:text>Kanal 4</xsl:text>
</xsl:if>
	<xsl:if test=".='kanal5'">
<xsl:text>Kanal 5</xsl:text>
</xsl:if>
	<xsl:if test=".='kanal6'">
<xsl:text>6'eren</xsl:text>
</xsl:if>
	<xsl:if test=".='kanaloestjylland'">
<xsl:text>Kanal Østjylland</xsl:text>
</xsl:if>
		<xsl:if test=".='ksport'">
<xsl:text>Kanal Sport</xsl:text>
</xsl:if>
	<xsl:if test=".='sbsnet'">
<xsl:text>SBS Net</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2s'">
<xsl:text>TV Syd</xsl:text>
</xsl:if>
	<xsl:if test=".='tv2d'">
<xsl:text>TV2</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2born'">
<xsl:text>TV2 Bornholm</xsl:text>
</xsl:if>
	<xsl:if test=".='tv2c'">
<xsl:text>TV2 Charlie</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2fri'">
<xsl:text>TV2 Fri</xsl:text>
</xsl:if>
		<xsl:if test=".='tvfyn'">
<xsl:text>TV2 Fyn</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2l'">
<xsl:text>TV2 Lorry</xsl:text>
</xsl:if>
	<xsl:if test=".='tv2news'">
<xsl:text>TV2 News</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2n'">
<xsl:text>TV2 Nord</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2ost'">
<xsl:text>TV2 Øst</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2sport'">
<xsl:text>TV2 Sport</xsl:text>
</xsl:if>
			<xsl:if test=".='tv2z'">
<xsl:text>TV 2 Zulu</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2oj'">
<xsl:text>TV2 Østjylland</xsl:text>
</xsl:if>
	<xsl:if test=".='tv3'">
<xsl:text>TV3</xsl:text>
</xsl:if>
		<xsl:if test=".='tv3puls'">
<xsl:text>TV3 Puls</xsl:text>
</xsl:if>
			<xsl:if test=".='tv3p'">
<xsl:text>TV3+</xsl:text>
</xsl:if>
		<xsl:if test=".='tv3sport1hd'">
<xsl:text>TV3 sport 1 hd</xsl:text>
</xsl:if>
		<xsl:if test=".='tv3sport2hd'">
<xsl:text>TV3 sport 2 hd</xsl:text>
</xsl:if>
		<xsl:if test=".='tvdk'">
<xsl:text>TV Danmark</xsl:text>
</xsl:if>
		<xsl:if test=".='vh1'">
<xsl:text>VH1</xsl:text>
</xsl:if>
		<xsl:if test=".='voicetv'">
<xsl:text>Voice TV</xsl:text>
</xsl:if>
</xsl:for-each>
</xsl:template>

   <xsl:template name="channel_logos">
<xsl:param name="channel"/>
<xsl:for-each select ="$channel">
<xsl:if test=".='drbo'">
<xsl:text>Unknown_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drboo'">
<xsl:text>Unknown_logo.png</xsl:text>
</xsl:if>
<xsl:if test=".='drpk'">
<xsl:text>drpk_logo.png</xsl:text>
</xsl:if>
<xsl:if test=".='drmamara'">
<xsl:text>DRmama_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drol'">
<xsl:text>DRol_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp1'">
<xsl:text>P1_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp2'">
<xsl:text>P2_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp3'">
<xsl:text>P3_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4b'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4blm'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='drp4o'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4f'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='drp4fyn'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4k94'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4k'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4miv'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='drp4mv'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4nj'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='drp4nor'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='drp4o'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4re'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp4s'">
<xsl:text>P4_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='drp5'">
<xsl:text>drp5_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='drp5000'">
<xsl:text>drp5000_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp6b'">
<xsl:text>drp6b_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drp7m'">
<xsl:text>drp7m_logo.png</xsl:text>
</xsl:if>
<xsl:if test=".='drp8j'">
<xsl:text>drp8j_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='drram'">
<xsl:text>drram_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drrar'">
<xsl:text>drrar_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='novafm'">
<xsl:text>novafm_logo.png</xsl:text>
</xsl:if>
<xsl:if test=".='100fm'">
<xsl:text>Radio100FM_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='radio2'">
<xsl:text>radio2_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='24syv'">
<xsl:text>Radio24syv_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='thevoice'">
<xsl:text>voice_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='tv2radio'">
<xsl:text>tv2radio_logo.png</xsl:text>
</xsl:if>

			   <!-- these are tv channels -->
	<xsl:if test=".='7eren'">
<xsl:text>7eren_logo.png</xsl:text>
</xsl:if>
<xsl:if test=".='canal9'">
<xsl:text>canal9_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='dk4'">
<xsl:text>dk4_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drhd'">
<xsl:text>drhd_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drultra'">
<xsl:text>drultra_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='drup'">
<xsl:text>drup_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='dr1'">
<xsl:text>dr1_logo.png</xsl:text>
</xsl:if>
			<xsl:if test=".='dr2'">
<xsl:text>dr2_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='dr3'">
<xsl:text>dr3_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='folketinget'">
<xsl:text>folketinget_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='kanal4'">
<xsl:text>kanal4_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='kanal5'">
<xsl:text>kanal5_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='kanal6'">
<xsl:text>kanal6_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='kanaloestjylland'">
<xsl:text>kanaloestjylland_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='ksport'">
<xsl:text>ksport_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='sbsnet'">
<xsl:text>sbsnet_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2s'">
<xsl:text>tv2s_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='tv2d'">
<xsl:text>tv2d_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2born'">
<xsl:text>tv2born_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2c'">
<xsl:text>tv2c_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2fri'">
<xsl:text>tv2fri_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tvfyn'">
<xsl:text>tvfyn_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2l'">
<xsl:text>tv2l_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='tv2news'">
<xsl:text>tv2news_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2n'">
<xsl:text>tv2n_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2ost'">
<xsl:text>tv2ost_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2sport'">
<xsl:text>tv2sport_logo.png</xsl:text>
</xsl:if>
			<xsl:if test=".='tv2z'">
<xsl:text>tv2z_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tv2oj'">
<xsl:text>tv2oj_logo.png</xsl:text>
</xsl:if>
	<xsl:if test=".='tv3'">
<xsl:text>tv3_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tv3puls'">
<xsl:text>tv3puls_logo.png</xsl:text>
</xsl:if>
			<xsl:if test=".='tv3p'">
<xsl:text>tv3p_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tv3sport1hd'">
<xsl:text>tv3sport1hd_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tv3sport2hd'">
<xsl:text>tv3sport2hd_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='tvdk'">
<xsl:text>tvdk_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='vh1'">
<xsl:text>vh1_logo.png</xsl:text>
</xsl:if>
		<xsl:if test=".='voicetv'">
<xsl:text>voicetv_logo.png</xsl:text>
</xsl:if>
</xsl:for-each>

</xsl:template>
       <xsl:template name="channels">
       <!--- this is the master template, values copied to two other templates -->
		   <channels>
			   <!-- these are radio channels -->
			   <channel>
				   <sb_channel_name>drbo</sb_channel_name>
				   <chaos_display_name>DR Boogieradio</chaos_display_name>
				   <chaos_logo>Unknown_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drboo</sb_channel_name>
				   <chaos_display_name>DR Boogieradio</chaos_display_name>
				   <chaos_logo>Unknown_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drpk</sb_channel_name>
				   <chaos_display_name>DR Klassisk</chaos_display_name>
				   <chaos_logo>drpk_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drmamara</sb_channel_name>
				   <chaos_display_name>DR Mama</chaos_display_name>
				   <chaos_logo>DRmama_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drol</sb_channel_name>
				   <chaos_display_name>DR Oline</chaos_display_name>
				   <chaos_logo>DRol_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp1</sb_channel_name>
				   <chaos_display_name>DR P1</chaos_display_name>
				   <chaos_logo>P1_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp2</sb_channel_name>
				   <chaos_display_name>DR P2</chaos_display_name>
				   <chaos_logo>P2_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp3</sb_channel_name>
				   <chaos_display_name>DR P3</chaos_display_name>
				   <chaos_logo>P3_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4b</sb_channel_name>
				   <chaos_display_name>DR P4 Bornholm</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4blm</sb_channel_name>
				   <chaos_display_name>DR P4 Bornholm</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4o</sb_channel_name>
				   <chaos_display_name>DR P4 Fælles</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4f</sb_channel_name>
				   <chaos_display_name>DR P4 Fyn</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4fyn</sb_channel_name>
				   <chaos_display_name>DR P4 Fyn</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4k94</sb_channel_name>
				   <chaos_display_name>DR P4 K94</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4k</sb_channel_name>
				   <chaos_display_name>DR P4 København</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4miv</sb_channel_name>
				   <chaos_display_name>DR P4 Midt og Vest</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4mv</sb_channel_name>
				   <chaos_display_name>DR P4 Midt og Vest</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4nj</sb_channel_name>
				   <chaos_display_name>DR P4 Nordjylland</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4nor</sb_channel_name>
				   <chaos_display_name>DR P4 Nordjylland</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4o</sb_channel_name>
				   <chaos_display_name>DR P4 Østjylland</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4re</sb_channel_name>
				   <chaos_display_name>DR P4 Regional</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp4s</sb_channel_name>
				   <chaos_display_name>DR P4 Syd</chaos_display_name>
				   <chaos_logo>P4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp5</sb_channel_name>
				   <chaos_display_name>DR P5</chaos_display_name>
				   <chaos_logo>drp5_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp5000</sb_channel_name>
				   <chaos_display_name>DR P5000</chaos_display_name>
				   <chaos_logo>drp5000_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp6b</sb_channel_name>
				   <chaos_display_name>DR P6 Beat</chaos_display_name>
				   <chaos_logo>drp6b_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp7m</sb_channel_name>
				   <chaos_display_name>DR P7 Mix</chaos_display_name>
				   <chaos_logo>drp7m_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drp8j</sb_channel_name>
				   <chaos_display_name>DR P8 Jazz</chaos_display_name>
				   <chaos_logo>drp8j_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drram</sb_channel_name>
				   <chaos_display_name>DR Ramasjang</chaos_display_name>
				   <chaos_logo>drram_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drrar</sb_channel_name>
				   <chaos_display_name>DR Ramasjang</chaos_display_name>
				   <chaos_logo>drrar_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>novafm</sb_channel_name>
				   <chaos_display_name>NOVA fm</chaos_display_name>
				   <chaos_logo>novafm_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>100fm</sb_channel_name>
				   <chaos_display_name>Radio 100FM</chaos_display_name>
				   <chaos_logo>Radio100FM_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>radio2</sb_channel_name>
				   <chaos_display_name>Radio 2</chaos_display_name>
				   <chaos_logo>radio2_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>24syv</sb_channel_name>
				   <chaos_display_name>Radio 24syv</chaos_display_name>
				   <chaos_logo>Radio24syv_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>thevoice</sb_channel_name>
				   <chaos_display_name>THE VOICE</chaos_display_name>
				   <chaos_logo>voice_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv2radio</sb_channel_name>
				   <chaos_display_name>TV2 Radio</chaos_display_name>
				   <chaos_logo>tv2radio_logo.png</chaos_logo>
			   </channel>

			   <!-- these are tv channels -->
			   <channel>
				   <sb_channel_name>7eren</sb_channel_name>
				   <chaos_display_name>7'eren</chaos_display_name>
				   <chaos_logo>7eren_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>canal9</sb_channel_name>
				   <chaos_display_name>Canal 9</chaos_display_name>
				   <chaos_logo>canal9_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>dk4</sb_channel_name>
				   <chaos_display_name>DK 4</chaos_display_name>
				   <chaos_logo>dk4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drhd</sb_channel_name>
				   <chaos_display_name>DR HD</chaos_display_name>
				   <chaos_logo>drhd_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drultra</sb_channel_name>
				   <chaos_display_name>DR Ultra</chaos_display_name>
				   <chaos_logo>drultra_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>drup</sb_channel_name>
				   <chaos_display_name>DR Update</chaos_display_name>
				   <chaos_logo>drup_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>dr1</sb_channel_name>
				   <chaos_display_name>DR 1</chaos_display_name>
				   <chaos_logo>dr1_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>dr2</sb_channel_name>
				   <chaos_display_name>DR 2</chaos_display_name>
				   <chaos_logo>dr2_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>dr3</sb_channel_name>
				   <chaos_display_name>DR 3</chaos_display_name>
				   <chaos_logo>dr3_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>folketinget</sb_channel_name>
				   <chaos_display_name>Folketingskanalen</chaos_display_name>
				   <chaos_logo>folketinget_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>kanal4</sb_channel_name>
				   <chaos_display_name>Kanal 4</chaos_display_name>
				   <chaos_logo>kanal4_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>kanal5</sb_channel_name>
				   <chaos_display_name>Kanal 5</chaos_display_name>
				   <chaos_logo>kanal5_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>kanal6</sb_channel_name>
				   <chaos_display_name>6'eren</chaos_display_name>
				   <chaos_logo>kanal6_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>kanaloestjylland</sb_channel_name>
				   <chaos_display_name>Kanal Østjylland</chaos_display_name>
				   <chaos_logo>kanaloestjylland_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>ksport</sb_channel_name>
				   <chaos_display_name>Kanal Sport</chaos_display_name>
				   <chaos_logo>ksport_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>sbsnet</sb_channel_name>
				   <chaos_display_name>SBS Net</chaos_display_name>
				   <chaos_logo>sbsnet_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv2s</sb_channel_name>
				   <chaos_display_name>TV Syd</chaos_display_name>
				   <chaos_logo>tv2s_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv2d</sb_channel_name>
				   <chaos_display_name>TV2</chaos_display_name>
				   <chaos_logo>tv2d_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv2born</sb_channel_name>
				   <chaos_display_name>TV2 Bornholm</chaos_display_name>
				   <chaos_logo>tv2born_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv2c</sb_channel_name>
				   <chaos_display_name>TV2 Charlie</chaos_display_name>
				   <chaos_logo>tv2c_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv2fri</sb_channel_name>
				   <chaos_display_name>TV2 Fri</chaos_display_name>
				   <chaos_logo>tv2fri_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tvfyn</sb_channel_name>
				   <chaos_display_name>TV2 Fyn</chaos_display_name>
				   <chaos_logo>tvfyn_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv2l</sb_channel_name>
				   <chaos_display_name>TV2 Lorry</chaos_display_name>
				   <chaos_logo>tv2l_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv2news</sb_channel_name>
				   <chaos_display_name>TV2 News</chaos_display_name>
				   <chaos_logo>tv2news_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv2n</sb_channel_name>
				   <chaos_display_name>TV2 Nord</chaos_display_name>
				   <chaos_logo>tv2n_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv2ost</sb_channel_name>
				   <chaos_display_name>TV2 Øst</chaos_display_name>
				   <chaos_logo>tv2ost_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv2sport</sb_channel_name>
				   <chaos_display_name>TV2 Sport</chaos_display_name>
				   <chaos_logo>tv2sport_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv2z</sb_channel_name>
				   <chaos_display_name>TV 2 Zulu</chaos_display_name>
				   <chaos_logo>tv2z_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv2oj</sb_channel_name>
				   <chaos_display_name>TV2 Østjylland</chaos_display_name>
				   <chaos_logo>tv2oj_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv3</sb_channel_name>
				   <chaos_display_name>TV3</chaos_display_name>
				   <chaos_logo>tv3_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv3puls</sb_channel_name>
				   <chaos_display_name>TV3 Puls</chaos_display_name>
				   <chaos_logo>tv3puls_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv3p</sb_channel_name>
				   <chaos_display_name>TV3+</chaos_display_name>
				   <chaos_logo>tv3p_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv3sport1hd</sb_channel_name>
				   <chaos_display_name>TV3 sport 1 hd</chaos_display_name>
				   <chaos_logo>tv3sport1hd_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tv3sport2hd</sb_channel_name>
				   <chaos_display_name>TV3 sport 2 hd</chaos_display_name>
				   <chaos_logo>tv3sport2hd_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>tvdk</sb_channel_name>
				   <chaos_display_name>TV Danmark</chaos_display_name>
				   <chaos_logo>tvdk_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>vh1</sb_channel_name>
				   <chaos_display_name>VH1</chaos_display_name>
				   <chaos_logo>vh1_logo.png</chaos_logo>
			   </channel>
			   <channel>
				   <sb_channel_name>voicetv</sb_channel_name>
				   <chaos_display_name>Voice TV</chaos_display_name>
				   <chaos_logo>voicetv_logo.png</chaos_logo>
			   </channel>
		   </channels>
	   </xsl:template>

	</xsl:stylesheet>

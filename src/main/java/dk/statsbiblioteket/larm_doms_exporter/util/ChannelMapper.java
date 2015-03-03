package dk.statsbiblioteket.larm_doms_exporter.util;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;
import dk.statsbiblioteket.larm_doms_exporter.consumer.processors.DoExportProcessor;

/**
 * Class to map channel names to the logo-file names supplied by Geckon.
 */
public class ChannelMapper {
    private static Logger logger = LoggerFactory.getLogger(DoExportProcessor.class);
    
    private static ChannelMapper channelMapper = null;

    /**
     * key in hashmap is the SB channel_name, found in digitv database for ritzau channelmapping table
     */
    private static HashMap<String, ChaosChannelEntity> chaosChannelMapping;
	private final static String sb_unknown_channel = "unknown";
	private final static String chaos_unknown_channel = "Ukendt";
	private final static String chaos_unknown_logo = "Unknown_logo.png";

    private ChannelMapper(){};
    
    public static ChannelMapper getChannelMapper(ExportContext context) throws Exception{
    	if(channelMapper == null)
    		channelMapper = new ChannelMapper();
    	
    	channelMapper.initialise(context.getChaosChannelMappingConfigFile());
    	
    	return channelMapper;
    }

    private void initialise(File chaosChannelMappingConfigFile) throws Exception{
    	chaosChannelMapping = new ChannelMapper().parseChaosChannelMapping(chaosChannelMappingConfigFile);
    	
    	if(chaosChannelMapping.get(sb_unknown_channel) == null){
    		//log that unknown entity is missing
    		logger.warn("Did not find channel entity 'unknown' in configuration file; channel will be added to internal map");

    		//create unknown entity
    		chaosChannelMapping.put(sb_unknown_channel, new ChaosChannelEntity(chaos_unknown_channel, chaos_unknown_logo));
    	}
    }

    public String getChaosChannel(String sbChannel) {
        ChaosChannelEntity chaosChannelEntity = chaosChannelMapping.get(sbChannel);
        if(chaosChannelEntity == null)
        	chaosChannelEntity = chaosChannelMapping.get(sb_unknown_channel);

        return chaosChannelEntity.getDisplayName();
    }

    public String getPublisher(String sbChannel) {
        if (sbChannel.contains("dr")) {
            return "DR";
        } else {
            return getChaosChannel(sbChannel);
        }
    }

    public String getLogoFileName(String sbChannel) {
        ChaosChannelEntity chaosChannelEntity = chaosChannelMapping.get(sbChannel);
        if(chaosChannelEntity == null)
        	chaosChannelEntity = chaosChannelMapping.get(sb_unknown_channel);
        
        return chaosChannelEntity.getLogoFilename();
    }
    
    public HashMap<String, ChaosChannelEntity> parseChaosChannelMapping(File configFile) throws Exception {
    	final HashMap<String, ChaosChannelEntity> chaosChannelMapping = new HashMap<String, ChaosChannelEntity>();
    	SAXParserFactory factory = SAXParserFactory.newInstance();

    	try {
			SAXParser parser = factory.newSAXParser();
			
			parser.parse(configFile, new DefaultHandler(){
				private StringBuffer buffer = new StringBuffer();
				private String sb_channel_name = null;
				private ChaosChannelEntity chaosChannelEntity = null;

				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					if("channel".equals(qName)){
						//new channel entity
						chaosChannelEntity = new ChaosChannelEntity();
					}
				}

				@Override
				public void characters(char[] ch, int start, int length) throws SAXException {
					buffer.append(ch, start, length);
				}

				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {
					if("channel".equals(qName)){
						if(sb_channel_name == null || "".equals(sb_channel_name))
							throw new RuntimeException("sb_channel_name not present");
						else if(chaosChannelEntity.getDisplayName() == null || "".equals(chaosChannelEntity.getDisplayName()))
							throw new RuntimeException("Chaos display name not found for " + sb_channel_name);
						else if(chaosChannelEntity.getLogoFilename() == null || "".equals(chaosChannelEntity.getLogoFilename()))
							throw new RuntimeException("Chaos logo filename not found for " + sb_channel_name);
						else
							chaosChannelMapping.put(sb_channel_name, chaosChannelEntity); //store values
						
						//reset all variables
						sb_channel_name = null;
						buffer.delete(0, buffer.length()); //probably not necessary
					}
					else if("sb_channel_name".equals(qName)){
						sb_channel_name = buffer.toString().trim();
						buffer.delete(0, buffer.length());
					}
					else if("chaos_display_name".equals(qName)){
						chaosChannelEntity.setDisplayName(buffer.toString().trim());
						buffer.delete(0, buffer.length());
					}
					else if("chaos_logo".equals(qName)){
						chaosChannelEntity.setLogoFilename(buffer.toString().trim()); 
						buffer.delete(0, buffer.length());
					}
				}
				
			});
		} catch (Exception e) {
			    logger.error("Error occured during parsing", e);
			    throw e;
		}
    	
    	return chaosChannelMapping;
    }
    
    public class ChaosChannelEntity {
    	private String displayName = null;
    	private String logoFilename = null;
    	
    	public ChaosChannelEntity() {}
    	
    	public ChaosChannelEntity(String displayName, String logo) {
    		this.displayName = displayName;
    		this.logoFilename = logo;
    	}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public String getLogoFilename() {
			return logoFilename;
		}

		public void setLogoFilename(String logoFilename) {
			this.logoFilename = logoFilename;
		}
    }
}

package dk.statsbiblioteket.larm_doms_exporter.cli;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 */
public class ExportOptionsParser extends AbstractOptionsParser {

    protected static final Option BEHAVIOURAL_CONFIG_FILE_OPTION = new Option("behavioural_configfile", true, "The behavioural config file");
    protected static final Option INFRASTRUCTURE_CONFIG_FILE_OPTION = new Option("infrastructure_configfile", true, "The infrastructure config file");
    protected static final Option LDE_HIBERNATE_CFG_OPTION = new Option("lde_hibernate_configfile", true, "The hibernate config file");
    protected static final Option BTA_HIBERNATE_CFG_OPTION = new Option("bta_hibernate_configfile", true, "The hibernate config file");

    private ExportContext context;

    public ExportOptionsParser() {
        super();
        context = new ExportContext();
        getOptions().addOption(BEHAVIOURAL_CONFIG_FILE_OPTION);
        getOptions().addOption(INFRASTRUCTURE_CONFIG_FILE_OPTION);
        getOptions().addOption(LDE_HIBERNATE_CFG_OPTION);
        getOptions().addOption(BTA_HIBERNATE_CFG_OPTION);
    }


    public ExportContext parseOptions(String[] args) throws OptionParseException, UsageException {
        CommandLineParser parser = new PosixParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(getOptions(), args);
        } catch (ParseException e) {
            parseError(e.toString());
            throw new OptionParseException(e.getMessage(), e);
        }
        parseUsageOption(cmd);
        parseInfrastructureConfigFileOption(cmd);
        parseBehaviouralConfigFileOption(cmd);
        parseLDEHibernateConfigFileOption(cmd);
        parseBTAHibernateConfigFileOption(cmd);
        try {
            readBehaviouralProperties(context);
        } catch (IOException e) {
            throw new OptionParseException("Error reading behavioural properies", e);
        }
        try {
            readInfrastructureProperties(context);
        } catch (IOException e) {
            throw new OptionParseException("Error reading infrastructure properies", e);
        }
        return context;
    }

    private void readBehaviouralProperties(ExportContext context) throws IOException, OptionParseException {
        Properties props = new Properties();
        props.load(new FileInputStream(context.getBehaviourConfigFile()));
        context.setDomsViewAngle(readStringProperty("domsViewAngle", props));
        context.setSeedTimestamp(readLongProperty("seedTimestamp", props));
        context.setInProductionTimestamp(readLongProperty("inProductionTimestamp", props));
        context.setEarliestExportBroadcastTimestamp(readLongProperty("earliestExportBroadcastTimestamp", props));
    }

    private void readInfrastructureProperties(ExportContext context) throws IOException, OptionParseException {
        Properties props = new Properties();
        props.load(new FileInputStream(context.getInfrastructreConfigFile()));
        context.setDomsCentralWebserviceUrl(readStringProperty("domsWSAPIEndpointUrl", props));
        context.setDomsUsername(readStringProperty("domsUsername", props));
        context.setDomsPassword(readStringProperty("domsPassword", props));
        context.setOutputDirectory(readFileProperty("fileOutputDirectory", props));
    }

    protected void parseBehaviouralConfigFileOption(CommandLine cmd) throws OptionParseException {
        String configFileString = cmd.getOptionValue(BEHAVIOURAL_CONFIG_FILE_OPTION.getOpt());
        if (configFileString == null) {
            parseError(BEHAVIOURAL_CONFIG_FILE_OPTION.toString());
            throw new OptionParseException(BEHAVIOURAL_CONFIG_FILE_OPTION.toString());
        }
        File configFile = new File(configFileString);
        if (!configFile.exists() || configFile.isDirectory()) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        }
        context.setBehaviourConfigFile(configFile);
    }

    protected void parseInfrastructureConfigFileOption(CommandLine cmd) throws OptionParseException {
        String configFileString = cmd.getOptionValue(INFRASTRUCTURE_CONFIG_FILE_OPTION.getOpt());
        if (configFileString == null) {
            parseError(INFRASTRUCTURE_CONFIG_FILE_OPTION.toString());
            throw new OptionParseException(INFRASTRUCTURE_CONFIG_FILE_OPTION.toString());
        }
        File configFile = new File(configFileString);
        if (!configFile.exists() || configFile.isDirectory()) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        }
        context.setInfrastructreConfigFile(configFile);
    }

    protected void parseLDEHibernateConfigFileOption(CommandLine cmd) throws OptionParseException {
        String configFileString = cmd.getOptionValue(LDE_HIBERNATE_CFG_OPTION.getOpt());
        if (configFileString == null) {
            parseError(LDE_HIBERNATE_CFG_OPTION.toString());
            throw new OptionParseException(LDE_HIBERNATE_CFG_OPTION.toString());
        }
        File configFile = new File(configFileString);
        if (!configFile.exists() || configFile.isDirectory()) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        }
        context.setLdeHibernateConfigurationFile(configFile);
    }

    protected void parseBTAHibernateConfigFileOption(CommandLine cmd) throws OptionParseException {
           String configFileString = cmd.getOptionValue(BTA_HIBERNATE_CFG_OPTION.getOpt());
           if (configFileString == null) {
               parseError(BTA_HIBERNATE_CFG_OPTION.toString());
               throw new OptionParseException(BTA_HIBERNATE_CFG_OPTION.toString());
           }
           File configFile = new File(configFileString);
           if (!configFile.exists() || configFile.isDirectory()) {
               throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
           }
           context.setBtaHibernateConfigurationFile(configFile);
       }

}

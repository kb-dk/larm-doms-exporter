package dk.statsbiblioteket.larm_doms_exporter.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.File;
import java.util.Properties;

public abstract class AbstractOptionsParser {
    private final Options options;
    protected static final Option USAGE_OPTION = new Option("u", false, "Usage");


    public AbstractOptionsParser() {
        options = new Options();
        options.addOption(USAGE_OPTION);


    }

    protected File readFileProperty(String propName, Properties props)  throws OptionParseException {
        String prop = props.getProperty(propName);
        if (prop == null || "".equals(prop)) {
            throw new OptionParseException("Property " + propName + " not set.");
        }
        return new File(prop);
    }

    protected File readExistingDirectoryProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        if (prop == null || "".equals(prop)) {
            throw new OptionParseException("Property " + propName + " not set.");
        }
        File dir = new File(prop);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new OptionParseException(dir.getAbsolutePath() + " must be a pre-existing directory");
        }
        return dir;
    }

    protected String readStringProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        if (prop == null || "".equals(prop)) {
            throw new OptionParseException("Property " + propName + " not set.");
        }
        return prop;
    }

    protected int readIntegerProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        try {
            Integer result = Integer.parseInt(prop);
            return result;
        } catch (NumberFormatException e) {
            throw new OptionParseException("Cannot parse " + prop + " as an integer.");
        }
    }

    protected float readFloatProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        try {
            Float result = Float.parseFloat(prop);
            return result;
        } catch (NumberFormatException e) {
            throw new OptionParseException("Cannot parse " + prop + " as a float.");
        }
    }

    protected long readLongProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        try {
            Long result = Long.parseLong(prop);
            return result;
        } catch (NumberFormatException e) {
            throw new OptionParseException("Cannot parse " + prop + " as a long.");
        }
    }

    protected boolean readBooleanProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        try {
            boolean result = Boolean.parseBoolean(prop);
            return result;
        } catch (NumberFormatException e) {
            throw new OptionParseException("Cannot parse " + prop + " as a boolean.");
        }
    }

    protected void parseError(String message) {
        System.err.println("Error parsing arguments");
        System.err.println(message);
        printUsage();
    }
    protected void printUsage() {
        final HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printHelp(this.getClass().getName(), getOptions(), true);
    }

    public Options getOptions() {
        return options;
    }


    protected void parseUsageOption(CommandLine cmd) throws UsageException {
        if (cmd.hasOption(USAGE_OPTION.getOpt())) {
            printUsage();
            throw new UsageException();
        }
    }
}
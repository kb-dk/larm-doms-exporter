package dk.statsbiblioteket.larm_doms_exporter.cli;

public class OptionParseException extends Exception {
    public OptionParseException(String message) {
           super(message);
       }

       public OptionParseException(String message, Throwable cause) {
           super(message, cause);
       }
}

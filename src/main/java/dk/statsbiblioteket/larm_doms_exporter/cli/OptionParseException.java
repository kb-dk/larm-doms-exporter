package dk.statsbiblioteket.larm_doms_exporter.cli;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/24/12
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class OptionParseException extends Exception {
    public OptionParseException(String message) {
           super(message);
       }

       public OptionParseException(String message, Throwable cause) {
           super(message, cause);
       }
}

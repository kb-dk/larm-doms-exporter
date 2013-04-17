package dk.statsbiblioteket.larm_doms_exporter.consumer;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 17/04/13
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */
public class ProcessorException extends Exception {

    public ProcessorException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

}

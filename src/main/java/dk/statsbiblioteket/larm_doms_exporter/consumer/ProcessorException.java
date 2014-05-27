package dk.statsbiblioteket.larm_doms_exporter.consumer;

public class ProcessorException extends Exception {

    public ProcessorException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

}

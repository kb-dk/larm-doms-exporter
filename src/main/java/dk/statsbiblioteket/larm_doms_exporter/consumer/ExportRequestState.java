package dk.statsbiblioteket.larm_doms_exporter.consumer;

import org.w3c.dom.Document;

import java.util.Date;

/**
 * This is a bean class which is used to store information relevant to the export of a given record.
 */
public class ExportRequestState {

    private String pbcoreString;
    private Document pbcoreDocument;
    private Date walltime;
    private Long outputFileTimeStamp;

    /* ---------------------------- */

    public Document getPbcoreDocument() {
        return pbcoreDocument;
    }

    public void setPbcoreDocument(Document pbcoreDocument) {
        this.pbcoreDocument = pbcoreDocument;
    }

    public String getPbcoreString() {
        return pbcoreString;
    }

    public void setPbcoreString(String pbcoreString) {
        this.pbcoreString = pbcoreString;
    }

    public Date getWalltime() {
        return walltime;
    }

    public void setWalltime(Date walltime) {
        this.walltime = walltime;
    }

    public Long getOutputFileTimeStamp() {
        return outputFileTimeStamp;
    }

    public void setOutputFileTimeStamp(Long outputFileTimeStamp) {
        this.outputFileTimeStamp = outputFileTimeStamp;
    }
}

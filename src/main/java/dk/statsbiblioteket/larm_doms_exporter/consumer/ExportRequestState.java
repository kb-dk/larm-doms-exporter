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
    private String mediaFileName;
    private boolean isRadio; //or tv
    private Date programStart;
    private String programBroadcast;
    
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

	public String getMediaFileName() {
		return mediaFileName;
	}

	public void setMediaFileName(String mediaFileName) {
		this.mediaFileName = mediaFileName;
	}

    public boolean isRadio() {
        return isRadio;
    }

    public void setRadio(boolean isRadio) {
        this.isRadio = isRadio;
    }

    public Date getProgramStart() {
        return programStart;
    }

    public void setProgramStart(Date programStart) {
        this.programStart = programStart;
    }
    
    public void setProgramBroadcast(String programBroadcast) {
        this.programBroadcast = programBroadcast;
    }
    
    public String getProgramBroadcast() {
        return programBroadcast;
    }
}

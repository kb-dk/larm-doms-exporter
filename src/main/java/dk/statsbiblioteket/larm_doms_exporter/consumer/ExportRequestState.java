package dk.statsbiblioteket.larm_doms_exporter.consumer;

import org.w3c.dom.Document;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 17/04/13
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
public class ExportRequestState {

    private String pbcoreString;
    private Document pbcoreDocument;

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
}

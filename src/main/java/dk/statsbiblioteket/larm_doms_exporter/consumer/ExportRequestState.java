package dk.statsbiblioteket.larm_doms_exporter.consumer;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 17/04/13
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
public class ExportRequestState {

    private String pbcoreString;

    /* ---------------------------- */

    public String getPbcoreString() {
        return pbcoreString;
    }

    public void setPbcoreString(String pbcoreString) {
        this.pbcoreString = pbcoreString;
    }
}
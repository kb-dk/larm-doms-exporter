package dk.statsbiblioteket.larm_doms_exporter.util;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 4/22/13
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChannelMapper {

    private ChannelMapper(){};


    public static String getChaosChannel(final String sbChannel) {
        if (sbChannel.equals("drp1")) return "DR P1";
        if (sbChannel.equals("drp2")) return "DR P2";
        if (sbChannel.equals("drp3")) return "DR P3";
        if (sbChannel.equals("drp40")) return "DR P4 Østjylland";
        if (sbChannel.equals("100fm")) return "Radio 100FM";
        if (sbChannel.equals("novafm")) return "Nova fm";
        if (sbChannel.equals("drp4s")) return "DR P4 Syd";
        if (sbChannel.equals("drp4k")) return "DR P4 København";
        if (sbChannel.equals("tv2radio")) return "TV2 Radio";
        return "Ukendt";

    }


}

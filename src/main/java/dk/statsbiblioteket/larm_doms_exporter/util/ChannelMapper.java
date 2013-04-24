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

    public enum ChannelEnum {

       drp1("DR P1","P1_logo.png"),
       drp2("DR P2","P2_logo.png"),
        drp3("DR P3","P3_logo.png"),
        drp4o("DR P4 Østjylland","P4_logo.png"),

        drp4b("DR P4 Bornholm","P4_logo.png"),
        drp4blm("DR P4 Bornholm","P4_logo.png"),
        drp4f("DR P4 Fyn","P4_logo.png"),
        drp4fyn("DR P4 Fyn","P4_logo.png"),
        drp4k("DR P4 København","P4_logo.png"),
        drp4k94("DR P4 K94","P4_logo.png"),
        drp4miv("DR P4 Midt og Vest","P4_logo.png"),
        drp4mv("DR P4 Midt og Vest","P4_logo.png"),
        drp4nj("DR P4 Nordjylland","P4_logo.png"),
        drp4nor("DR P4 Nordjylland","P4_logo.png"),
        drp4re("DR P4 Regional","P4_logo.png"),
        drp4s("DR P4 Syd","P4_logo.png"),
        r100fm("Radio 100FM","Radio100FM_logo.png"),
        novafm("NOVA fm","novafm_logo.png"),

        tv2radio("TV2 Radio","tv2radio_logo.png"),
        drp5("DR P5","drp5_logo.png"),
        voice("THE VOICE","voice_logo.png"),
        drp5000("DR P5000","drp5000_logo.png"),
        drrar("DR Ramasjang","drrar_logo.png"),
        drp6b("DR P6 Beat","drp6b_logo.png"),
        drp8j("DR P8 Jazz","drp8j_logo.png"),


        drpxyz("",""),



        ukendt("Ukendt","Unknown_logo.png")

        ;

        private String chaosName;
        private String logoFileName;

        public String getLogoFileName() {
            return logoFileName;
        }

        private ChannelEnum(String chaosName, String logoFileName) {

            this.chaosName = chaosName;
            this.logoFileName = logoFileName;
        }

        public String getChaosName() {
            return chaosName;
        }

        private ChannelEnum(String chaosName) {

            this.chaosName = chaosName;
        }
    }

    public static String getChaosChannel(String sbChannel) {
        if (sbChannel.equals("100fm")) {
            sbChannel = "r100fm";
        }
         ChannelEnum channelEnum;
        try {
           channelEnum = ChannelEnum.valueOf(sbChannel);
        } catch (IllegalArgumentException e) {
           channelEnum = ChannelEnum.valueOf("ukendt");
        }
      return channelEnum.getChaosName();
    }

    public static String getPublisher(String sbChannel) {
        if (sbChannel.equals("100fm")) {
            sbChannel = "r100fm";
        }
        if (sbChannel.contains("dr")) {
            return "DR";
        } else {
            return getChaosChannel(sbChannel);
        }
    }

    public static String getLogoFileName(String sbChannel) {
        if (sbChannel.equals("100fm")) {
            sbChannel = "r100fm";
        }
           ChannelEnum channelEnum;
        try {
           channelEnum = ChannelEnum.valueOf(sbChannel);
        } catch (IllegalArgumentException e) {
           channelEnum = ChannelEnum.valueOf("ukendt");
        }
        return channelEnum.getLogoFileName();
    }

}

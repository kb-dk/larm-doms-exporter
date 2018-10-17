package dk.statsbiblioteket.larm_doms_exporter.cli;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.larm_doms_exporter.persistence.dao.DomsExportRecordDAO;

import java.io.File;

/**
 * Simple bean class containing getter/setter methods for all the required configuration
 * parameters.
 */
public class ExportContext {

    private File ldeHibernateConfigurationFile;
    private File btaHibernateConfigurationFile;
    private File behaviourConfigFile;
    private File infrastructreConfigFile;
    private File chaosChannelMappingConfigFile;
    private File whitelistedChannelsFile;
    private File blacklistedChannelsFile;
    private String domsCentralWebserviceUrl;
    private String domsUsername;
    private String domsPassword;
    private Long seedTimestamp;
    private String domsViewAngle;
    private File outputDirectory;
    private CentralWebservice domsCentralWebservice;
    private DomsExportRecordDAO domsExportRecordDAO;
    private boolean skipSignificantChangeCheck;

    private Long inProductionTimestamp;
    private Long earliestExportBroadcastTimestamp;

    private Long maxExportsPerRun;
    private Long numExports = 0L;

    private int geckonStreamingserverDestinationId;
    private String geckonStreamingserverFolderpath;

    private String unknownChannelPage;
    private File btaRecordIdsFile;

    /* etc */

    public Long getNumExports() {
        return numExports;
    }

    public void setNumExports(Long numExports) {
        this.numExports = numExports;
    }

    public void incrementNumExports() {
        numExports++;
    }

    public Long getMaxExportsPerRun() {
        return maxExportsPerRun;
    }

    public void setMaxExportsPerRun(Long maxExportsPerRun) {
        this.maxExportsPerRun = maxExportsPerRun;
    }

    public Long getInProductionTimestamp() {
        return inProductionTimestamp;
    }

    public void setInProductionTimestamp(Long inProductionTimestamp) {
        this.inProductionTimestamp = inProductionTimestamp;
    }

    public Long getEarliestExportBroadcastTimestamp() {
        return earliestExportBroadcastTimestamp;
    }

    public void setEarliestExportBroadcastTimestamp(Long earliestExportBroadcastTimestamp) {
        this.earliestExportBroadcastTimestamp = earliestExportBroadcastTimestamp;
    }

    public File getBtaHibernateConfigurationFile() {
        return btaHibernateConfigurationFile;
    }

    public void setBtaHibernateConfigurationFile(File btaHibernateConfigurationFile) {
        this.btaHibernateConfigurationFile = btaHibernateConfigurationFile;
    }

    public File getChaosChannelMappingConfigFile() {
		return chaosChannelMappingConfigFile;
	}

	public void setChaosChannelMappingConfigFile(File chaosChannelMappingConfigFile) {
		this.chaosChannelMappingConfigFile = chaosChannelMappingConfigFile;
	}

    public File getWhitelistedChannelsFile() {
		return whitelistedChannelsFile;
	}

	public void setWhitelistedChannelsFile(File whitelistedChannelsFile) {
		this.whitelistedChannelsFile = whitelistedChannelsFile;
	}

    public File getBlacklistedChannelsFile() {
		return blacklistedChannelsFile;
	}

	public void setBlacklistedChannelsFile(File blacklistedChannelsFile) {
		this.blacklistedChannelsFile = blacklistedChannelsFile;
	}

	public DomsExportRecordDAO getDomsExportRecordDAO() {
        return domsExportRecordDAO;
    }

    public void setDomsExportRecordDAO(DomsExportRecordDAO domsExportRecordDAO) {
        this.domsExportRecordDAO = domsExportRecordDAO;
    }

    public CentralWebservice getDomsCentralWebservice() {
        return domsCentralWebservice;
    }

    public void setDomsCentralWebservice(CentralWebservice domsCentralWebservice) {
        this.domsCentralWebservice = domsCentralWebservice;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getDomsUsername() {
        return domsUsername;
    }

    public void setDomsUsername(String domsUsername) {
        this.domsUsername = domsUsername;
    }

    public String getDomsPassword() {
        return domsPassword;
    }

    public void setDomsPassword(String domsPassword) {
        this.domsPassword = domsPassword;
    }

    public Long getSeedTimestamp() {
        return seedTimestamp;
    }

    public void setSeedTimestamp(Long seedTimestamp) {
        this.seedTimestamp = seedTimestamp;
    }

    public String getDomsViewAngle() {
        return domsViewAngle;
    }

    public void setDomsViewAngle(String domsViewAngle) {
        this.domsViewAngle = domsViewAngle;
    }

    public File getBehaviourConfigFile() {
        return behaviourConfigFile;
    }

    public void setBehaviourConfigFile(File behaviourConfigFile) {
        this.behaviourConfigFile = behaviourConfigFile;
    }

    public File getInfrastructreConfigFile() {
        return infrastructreConfigFile;
    }

    public void setInfrastructreConfigFile(File infrastructreConfigFile) {
        this.infrastructreConfigFile = infrastructreConfigFile;
    }

    public File getLdeHibernateConfigurationFile() {
        return ldeHibernateConfigurationFile;
    }

    public void setLdeHibernateConfigurationFile(File ldeHibernateConfigurationFile) {
        this.ldeHibernateConfigurationFile = ldeHibernateConfigurationFile;
    }

    public String getDomsCentralWebserviceUrl() {
        return domsCentralWebserviceUrl;
    }

    public void setDomsCentralWebserviceUrl(String domsCentralWebserviceUrl) {
        this.domsCentralWebserviceUrl = domsCentralWebserviceUrl;
    }

    public int getGeckonStreamingserverDestinationId() {
        return geckonStreamingserverDestinationId;
    }

    public void setGeckonStreamingserverDestinationId(int geckonStreamingserverDestinationId) {
        this.geckonStreamingserverDestinationId = geckonStreamingserverDestinationId;
    }

    public String getGeckonStreamingserverFolderpath() {
        return geckonStreamingserverFolderpath;
    }

    public void setGeckonStreamingserverFolderpath(String geckonStreamingserverFolderpath) {
        this.geckonStreamingserverFolderpath = geckonStreamingserverFolderpath;
    }

    public String getUnknownChannelPage() {
        return unknownChannelPage;
    }

    public void setUnknownChannelPage(String unknownChannelPage) {
        this.unknownChannelPage = unknownChannelPage;
    }

    public File getBtaRecordIdsFile() {
        return btaRecordIdsFile;
    }

    public void setBtaRecordIdsFile(File btaRecordIdsFile) {
        this.btaRecordIdsFile = btaRecordIdsFile;
    }

    public boolean skipSignificantChangeCheck() {
        return skipSignificantChangeCheck;
    }

    public void setSkipSignificantChangeCheck(boolean skipSignificantChangeCheck) {
        this.skipSignificantChangeCheck = skipSignificantChangeCheck;
    }

    @Override
    public String toString() {
        return "ExportContext{" +
                "hibernateConfigurationFile=" + ldeHibernateConfigurationFile +
                ", behaviourConfigFile=" + behaviourConfigFile +
                ", infrastructreConfigFile=" + infrastructreConfigFile +
                ", domsCentralWebserviceUrl='" + domsCentralWebserviceUrl + '\'' +
                ", domsUsername='" + domsUsername + '\'' +
                ", domsPassword='" + "***********" + '\'' +
                ", seedTimestamp=" + seedTimestamp +
                ", domsViewAngle='" + domsViewAngle + '\'' +
                ", outputDirectory=" + outputDirectory +
                ", unknownChannelPage=" + unknownChannelPage +
                ", skipSignificantChangeCheck=" + skipSignificantChangeCheck +
                (btaRecordIdsFile != null ? ", btaRecordIdsFile=" + btaRecordIdsFile : "") +
                '}';
    }
}

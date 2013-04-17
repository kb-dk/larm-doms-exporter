package dk.statsbiblioteket.larm_doms_exporter.cli;

import dk.statsbiblioteket.doms.central.CentralWebservice;

import java.io.File;

/**
 * Simple bean class containing getter/setter methods for all the required configuration
 * parameters.
 */
public class ExportContext {

    private File hibernateConfigurationFile;
    private File behaviourConfigFile;
    private File infrastructreConfigFile;
    private String domsCentralWebserviceUrl;
    private String domsUsername;
    private String domsPassword;
    private Long seedTimestamp;
    private String domsViewAngle;
    private File outputDirectory;
    private CentralWebservice domsCentralWebservice;

    /* etc */

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

    public File getHibernateConfigurationFile() {
        return hibernateConfigurationFile;
    }

    public void setHibernateConfigurationFile(File hibernateConfigurationFile) {
        this.hibernateConfigurationFile = hibernateConfigurationFile;
    }

    public String getDomsCentralWebserviceUrl() {
        return domsCentralWebserviceUrl;
    }

    public void setDomsCentralWebserviceUrl(String domsCentralWebserviceUrl) {
        this.domsCentralWebserviceUrl = domsCentralWebserviceUrl;
    }

    @Override
    public String toString() {
        return "ExportContext{" +
                "hibernateConfigurationFile=" + hibernateConfigurationFile +
                ", behaviourConfigFile=" + behaviourConfigFile +
                ", infrastructreConfigFile=" + infrastructreConfigFile +
                ", domsCentralWebserviceUrl='" + domsCentralWebserviceUrl + '\'' +
                ", domsUsername='" + domsUsername + '\'' +
                ", domsPassword='" + domsPassword + '\'' +
                ", seedTimestamp=" + seedTimestamp +
                ", domsViewAngle='" + domsViewAngle + '\'' +
                ", outputDirectory=" + outputDirectory +
                '}';
    }
}

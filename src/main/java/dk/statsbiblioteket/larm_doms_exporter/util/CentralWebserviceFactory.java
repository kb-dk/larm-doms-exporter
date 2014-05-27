package dk.statsbiblioteket.larm_doms_exporter.util;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.larm_doms_exporter.cli.ExportContext;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Class encapsulating the logic for getting a DOMS CentralWebservice instance,
 */
public class CentralWebserviceFactory {
    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName(
            "http://central.doms.statsbiblioteket.dk/",
            "CentralWebserviceService");


    private static void initialise(ExportContext context) {
        if (context.getDomsCentralWebservice() != null){
            return;
        }
        URL domsWSAPIEndpoint;
        try {
            domsWSAPIEndpoint = new URL(context.getDomsCentralWebserviceUrl());
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL to DOMS not configured correctly. Was: " + context.getDomsCentralWebserviceUrl(), e);
        }
        CentralWebservice serviceInstance = new CentralWebserviceService(domsWSAPIEndpoint, CENTRAL_WEBSERVICE_SERVICE).getCentralWebservicePort();
        Map<String, Object> domsAPILogin = ((BindingProvider) serviceInstance).getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, context.getDomsUsername());
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, context.getDomsPassword());
        context.setDomsCentralWebservice(serviceInstance);
    }

    public static CentralWebservice getServiceInstance(ExportContext context) {
        initialise(context);
        return context.getDomsCentralWebservice();
    }

}

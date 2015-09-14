package it.alfrescoinaction.lab.awsi.service;

import it.alfrescoinaction.lab.awsi.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class AlfrescoRemoteConnection implements RemoteConnection {

    private static Logger logger = LoggerFactory.getLogger(AlfrescoRemoteConnection.class);

    @Value("${alf.username}") private String username;
    @Value("${alf.password}") private String password;
    @Value("${alf.alfrescoUrl}") private String alfrescoUrl;
    @Value("${alf.cmisEntryPoint}") private String cmisEntryPoint;
    @Value("${alf.basePath}") private String alfrescoBasePath;

    private Session session = null;


    @Override
    @PostConstruct
    public void openSession() {
        Map<String, String> parameter = new HashMap<>();

        parameter.put(SessionParameter.BROWSER_URL, alfrescoUrl+cmisEntryPoint);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
        parameter.put(SessionParameter.USER,username);
        parameter.put(SessionParameter.PASSWORD,password);

        try {
            SessionFactory factory = SessionFactoryImpl.newInstance();
            session = factory.getRepositories(parameter).get(0).createSession();
        }
        catch (CmisBaseException ex) {
            logger.debug("Exception"+ ex.getMessage());
            throw new CmisConnectionException();
        }
    }

    public Session getSession() {

        if (session == null) {

            try {
                openSession();
            }
            catch (CmisBaseException e) {
                return null;
            }
        }

        return session;
    }

}

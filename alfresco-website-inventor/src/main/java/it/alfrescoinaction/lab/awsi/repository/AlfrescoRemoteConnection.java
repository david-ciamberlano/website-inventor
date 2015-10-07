package it.alfrescoinaction.lab.awsi.repository;

import it.alfrescoinaction.lab.awsi.exceptions.ConnectionException;
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

    @Value("${alfresco.username}") private String username;
    @Value("${alfresco.password}") private String password;
    @Value("${alfresco.serverProtocol}") private String alfrescoServerProtocol;
    @Value("${alfresco.serverUrl}") private String alfrescoServerUrl;
    @Value("${alfresco.cmisEntryPoint}") private String cmisEntryPoint;

    private Session session = null;


    @Override
    @PostConstruct
    public void openSession() {
        Map<String, String> parameter = new HashMap<>();

        String alfrescoCmisUrl = alfrescoServerProtocol + "://" + alfrescoServerUrl +cmisEntryPoint;
        parameter.put(SessionParameter.BROWSER_URL, alfrescoCmisUrl);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
        parameter.put(SessionParameter.USER,username);
        parameter.put(SessionParameter.PASSWORD,password);

        try {
            SessionFactory factory = SessionFactoryImpl.newInstance();
            session = factory.getRepositories(parameter).get(0).createSession();
        }
        catch (CmisBaseException ex) {
            logger.debug("Exception"+ ex.getMessage());
            throw new ConnectionException();
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

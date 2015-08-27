package it.alfrescoinaction.lab.awsi.service;

import it.alfrescoinaction.lab.awsi.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AlfrescoRemoteConnection implements RemoteConnection {

    private static Logger logger = LoggerFactory.getLogger(AlfrescoRemoteConnection.class);

    private String username;
    private String password;
    private String alfrescoUrl;
    private String cmisEntryPoint;

    private Session session = null;


    @Override
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAlfrescoUrl(String alfrescoUrl) {
        this.alfrescoUrl = alfrescoUrl;
    }

    public void setCmisEntryPoint(String cmisEntryPoint) {
        this.cmisEntryPoint = cmisEntryPoint;
    }

}

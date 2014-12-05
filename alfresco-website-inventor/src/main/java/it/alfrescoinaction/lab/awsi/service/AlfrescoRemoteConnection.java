package it.alfrescoinaction.lab.awsi.service;

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

public class AlfrescoRemoteConnection implements RemoteConnection {

    private static Logger logger = LoggerFactory.getLogger(AlfrescoRemoteConnection.class);

    private String username;
    private String password;
    private String alfrescoUrl;
    private String cmisEntryPoint;
    private String accessToken;

    private Session session;


    @Override
    public boolean openSession() {

        Map<String, String> parameter = new HashMap<String, String>();

        parameter.put(SessionParameter.ATOMPUB_URL, cmisEntryPoint);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameter.put(SessionParameter.AUTH_HTTP_BASIC, "false");
        parameter.put(SessionParameter.HEADER + ".0", "Authorization:Bearer " + accessToken);
        parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");


        //parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");

        try {
            SessionFactory factory = SessionFactoryImpl.newInstance();
            session = factory.getRepositories(parameter).get(0).createSession();
        }
        catch (CmisBaseException ex) {
            //todo
            logger.debug("Exception"+ ex.getMessage());
        }
        return false;
    }

    @Override
    public String test() {
        return username;
    }

    // getter & setter

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAlfrescoUrl() {
        return alfrescoUrl;
    }

    public void setAlfrescoUrl(String alfrescoUrl) {
        this.alfrescoUrl = alfrescoUrl;
    }

    public String getCmisEntryPoint() {
        return cmisEntryPoint;
    }

    public void setCmisEntryPoint(String cmisEntryPoint) {
        this.cmisEntryPoint = cmisEntryPoint;
    }

    public Session getSession() {
        return session;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

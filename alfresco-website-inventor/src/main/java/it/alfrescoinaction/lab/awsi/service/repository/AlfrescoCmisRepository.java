package it.alfrescoinaction.lab.awsi.service.repository;

import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.service.AlfrescoRemoteConnection;
import it.alfrescoinaction.lab.awsi.service.RemoteConnection;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class AlfrescoCmisRepository implements CmisRepository {

    @Autowired
    private RemoteConnection connection;

    private String alfrescoHomePath;

    /**
     * Build the domain object representing a webpage
     * @param id the id of the page to build
     * @return the WebPage object
     * @throws CmisObjectNotFoundException
     */
    public WebPage buildWebPage (String id) throws CmisObjectNotFoundException{

        WebPage wp = new WebPage();

        Optional<Session> session = connection.getSession();

        if (!session.isPresent()) {
            throw new CmisObjectNotFoundException();
        }

        if (id.equals("home")) {
            id = session.get().getObjectByPath(alfrescoHomePath).getId();
        }

        CmisObject obj = session.get().getObject(id);

        if (obj.getType().getId().equals("cmis:folder")){

            Folder folder = (Folder)obj;
            wp.setId(id);

            // retrieve the parent id
            wp.setParentId(folder.getParentId());

            wp.setTitle(obj.getName());
            ItemIterable<CmisObject> children = folder.getChildren();
            for (CmisObject cmiso : children) {
                // path relative to the homepage

                if (cmiso.getType().getId().equals("cmis:folder")) {
                    wp.addPage(cmiso.getName(),cmiso.getId());
                }
                else if (cmiso.getType().getId().equals("cmis:document")) {
                    Document doc = (Document)cmiso;
                    //cmis:contentStreamMimeType;
                    wp.addContent(doc);
                }


            }

        }

        return wp;
    }


    //***** getter/setter *****

    public RemoteConnection getConnection() {
        return connection;
    }

    public void setConnection(AlfrescoRemoteConnection connection) {
        this.connection = connection;
    }

    public String getAlfrescoHomePath() {
        return alfrescoHomePath;
    }

    public void setAlfrescoHomePath(String alfrescoHomePath) {
        this.alfrescoHomePath = alfrescoHomePath;
    }
}

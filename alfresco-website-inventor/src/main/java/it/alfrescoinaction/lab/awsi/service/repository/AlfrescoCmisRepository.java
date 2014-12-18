package it.alfrescoinaction.lab.awsi.service.repository;

import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.service.AlfrescoRemoteConnection;
import it.alfrescoinaction.lab.awsi.service.RemoteConnection;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Repository
public class AlfrescoCmisRepository implements CmisRepository {

    @Autowired
    private RemoteConnection connection;

    private String alfrescoHomePath;

    /**
     * Build the domain object representing a webpage
     * @param path the path of the page to build
     * @return the WebPage object
     * @throws CmisObjectNotFoundException
     */
    public WebPage buildWebPage (String path) throws CmisObjectNotFoundException{

        WebPage wp = new WebPage();

        Session session = connection.getSession();

        CmisObject obj = session.getObjectByPath(alfrescoHomePath + path);

        if (obj.getType().getId().equals("cmis:folder")){

            Folder folder = (Folder)obj;
            wp.setPath(path);

            // retrieve the parent path
            String ppath = Paths.get (path).getParent().toString().replace("\\","/");
            wp.setParentPath(ppath);

            wp.setTitle(obj.getName());
            ItemIterable<CmisObject> children = folder.getChildren();
            for (CmisObject cmiso : children) {
                // path relative to the homepage

                if (cmiso.getType().getId().equals("cmis:folder")) {
                    wp.addPage(cmiso.getName(), wp.getPath()+ cmiso.getName());
                }
                else if (cmiso.getType().getId().equals("cmis:document")) {

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

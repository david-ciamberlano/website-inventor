package it.alfrescoinaction.lab.awsi.service.repository;

import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.service.AlfrescoRemoteConnection;
import it.alfrescoinaction.lab.awsi.service.RemoteConnection;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class AlfrescoCmisRepository implements CmisRepository {

    @Autowired
    private RemoteConnection connection;

    public WebPage buildWebPage (String path) {

        WebPage wp = new WebPage();

        Session session = connection.getSession();

        CmisObject obj = session.getObjectByPath(path);

        if (obj.getType().getId().equals("cmis:folder")){
            Folder folder = (Folder)obj;
            wp.setPath(path);
            wp.setTitle(obj.getName());
            ItemIterable<CmisObject> children = folder.getChildren();
            for (CmisObject cmiso : children) {
                wp.addPage(cmiso.getName(), wp.getPath()+"/"+cmiso.getName());
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
}

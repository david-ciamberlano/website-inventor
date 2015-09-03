package it.alfrescoinaction.lab.awsi.service.repository;

import it.alfrescoinaction.lab.awsi.service.AlfrescoRemoteConnection;
import it.alfrescoinaction.lab.awsi.service.RemoteConnection;
import org.apache.chemistry.opencmis.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;


@Repository
public class AlfrescoCmisRepository implements CmisRepository {

    @Autowired
    private RemoteConnection connection;

    @Value("${alf.homepage}")
    private String alfrescoHomePath;

    public ItemIterable<CmisObject> getCategories() {
        Session session = connection.getSession();
        CmisObject obj = session.getObjectByPath(alfrescoHomePath);

        // procceed only if the node is a folder
        if (obj.getType().getId().equals("cmis:folder")){
            Folder folder = (Folder)obj;
            ItemIterable<CmisObject> children = folder.getChildren();

            return children;
        }
        else {
            throw new NoSuchElementException("Home folder not found: " + alfrescoHomePath);
        }
    }

    public Folder getFolderById(String id) throws NoSuchElementException {
        Session session = connection.getSession();

        if (id.equals("home")) {
            id = session.getObjectByPath(alfrescoHomePath).getId();
        }

        CmisObject obj = session.getObject(id);

        // procceed only if the node is a folder
        if (obj.getType().getId().equals("cmis:folder")){
            Folder folder = (Folder)obj;
            return folder;
        }
        else {
            throw new NoSuchElementException("Folder not found: "  + id);
        }
    }

    public Document getDocumentById(String id) throws NoSuchElementException {
        Session session = connection.getSession();

        CmisObject obj = session.getObject(id);

        if (obj.getType().getId().equals("cmis:document") || obj.getType().getId().equals("D:cm:thumbnail")){
            Document doc = (Document)obj;
            return doc;
        }
        else {
            throw new NoSuchElementException("Document not found: "  + id);
        }
    }

    public ItemIterable<CmisObject> getChildren (Folder folder) {
        Session session = connection.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");
        ItemIterable<CmisObject> children = folder.getChildren(oc);

        return children;
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
